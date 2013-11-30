package org.hexahedron.collision;

import org.hexahedron.cube.CubeGrid;
import org.hexahedron.geom.Vector3i;
import org.hexahedron.geom.Vector3iDefault;
import org.hexahedron.util.VectorUtils;

import com.jme3.math.Vector3f;

/**
 *	A single point that can be moved through a {@link CubeGrid}, implementing
 *traced collision with grids in the cube. This is very similar to {@link OctoBox},
 *but is optimised/restricted for a single point. Can be used for ray tracing,
 *picking etc. as well as for moving pointlike objects like debris, missiles etc.
 */
public class OctoPoint implements CollisionGeom {

	/**
	 * The position of the point in octree space - octree
	 * cubes are assumed to cover exact units to simplify calculations
	 */
	private Vector3f position = new Vector3f();

	/**
	 * The index of the cube the point is considered to lie in currently.
	 * This is used to find (for example) the next position at which
	 * collisions need to be checked. Note that this is more authoritative
	 * than the {@link Vector3f} position - for example if the float position
	 * is exactly 2 in an axis, we may be in either cube 1 or cube 2 on that
	 * axis - the float position just tells us that the point is exactly on
	 * the boundary. This int position tells us whether we are considered to
	 * be "in" cube 1 or cube 2. If we are in cube 1, we can move in the negative
	 * direction without the possibility of "instant" collision, if we are
	 * in cube 2 we can move in the positive direction. This is a similar distinction
	 * to positive/negative zero, and allows for perfectly precise collisions.
	 * {@link OctoBox} has a very similar concept with its integer bounds.
	 */
	private Vector3i iPosition = new Vector3iDefault();

	/**
	 * Indicates whether the point is touching a cube in the cubegrid in each
	 * direction.
	 * 
	 * The first index is 0 for the negative axis direction, 1 for the positive direction
	 * The second index is the axis
	 * 
	 * So for example if bounds[0][2] is true, the point is touching a cube below
	 * 
	 * This is very closely related to "aligned" - a point can only be touching
	 * if it is aligned, but may be aligned without touching - it depends whether
	 * there are actually any cubes on the far side of the aligned plane at the
	 * position of the point
	 */
	private boolean[][] touching = new boolean[2][3];

	/**
	 * Indicates whether the point is EXACTLY aligned to the cubegrid in each
	 * direction and axis. 
	 * 
	 * The first index is the direction we are aligned in - 0 if we are aligned by being
	 * exactly on the negative-axis-direction side of the cube we are in, 1 if we are
	 * on the positive-axis-side.
	 * The index is the relevant axis.
	 * 
	 * So for example if bounds[0][2] is true, the point is EXACTLY
	 * aligned with a dividing plane of the cube grid in the z axis, by being
	 * at the very minimum edge of the cube it lies in, in the grid.
	 * 
	 * This is very closely related to "touching" - a point can only be touching
	 * if it is aligned, but may be aligned without touching - it depends whether
	 * there are actually any cubes on the far side of the aligned plane, within
	 * the position of the point
	 * 
	 */
	private boolean[][] aligned = new boolean[2][3];

	private CubeGrid grid;
	
	/**
	 * Heading - this is a temp vector that gives the direction of
	 * motion in each axis - 1 for increasing, -1 for decreasing,
	 * 0 if velocity is exactly 0
	 */
	private Vector3i heading = new Vector3iDefault();

	/**
	 * Temp value for the next unit boundaries we will pass through
	 */
	private Vector3i nextBoundaries = new Vector3iDefault();

	/**
	 * Temp position of the cube we will search
	 */
	private Vector3i searchPosition = new Vector3iDefault();

	/**
	 * {@link CollisionReceiver} used in {@link #slideAlong(Vector3f, float)}
	 */
	private NextCollisionReceiver r = new NextCollisionReceiver();
	
	/**
	 * Temporary velocity used in {@link #slideAlong(Vector3f, float)}
	 */
	private Vector3f slideV = new Vector3f();

	/**
	 * Create an {@link OctoPoint} with integer position derived from float position.
	 * The int position in each axis will be given by flooring the float position.
	 * You might want to use the constructor giving an explicit iPosition, if the
	 * point is positioned exactly on an integer position in an axis, in which case it may
	 * have iPosition either the same as or one less than the position.
	 * @param position
	 * 		The float position
	 * 
	 * @param grid
	 * 		The {@link CubeGrid} the point moves through
	 */
	public OctoPoint(CubeGrid grid, Vector3f position) {
		this(grid, position, Vector3iDefault.createFloor(position));
	}

	/**
	 * Create an {@link OctoPoint}.
	 * @param position
	 * 		The float position
	 * @param iPosition
	 * 		The integer position - MUST be compatible with the float position. Compatibility
	 * is NOT checked - if it was, we would require that in each axis, given float position
	 * f and int position i, we have:
	 * 
	 * i <= f <= (i + 1)
	 * 
	 * @param grid
	 * 		The {@link CubeGrid} the point moves through
	 */
	public OctoPoint(CubeGrid grid, Vector3f position, Vector3i iPosition) {
		super();
		this.position = position;
		this.iPosition = iPosition;
		this.grid = grid;
		
		//FIXME is it worth working out aligned and touching when we start?
		for (int axis = 0; axis < 3; axis++) {
			for (int direction = 0; direction < 2; direction++) {
				aligned[direction][axis] = false; 
				touching[direction][axis] = false;
			}
		}
	}

	private void updateHeading(Vector3f velocity) {
		for (int i = 0; i < 3; i++) {
			int val = 0;
			double vComp = velocity.get(i);
			if (vComp > 0) {
				val = 1;
			} else if (vComp < 0) {
				val = -1;
			}
			heading.set(i, val);
		}
	}


	//Call only when heading is correct
	private void moveAndUpdate(Vector3f velocity, float time) {
		
		//If time is zero, do nothing
		if (time == 0) return;
		
		//Move to our position at given time
		for (int j = 0; j < 3; j++){
			position.set(j, position.get(j) + time * velocity.get(j));
		}
		
		//Track changes to touching/aligned due to movement normal to the touching/aligned faces
		for (int j = 0; j < 3; j++){
			//If we have actual movement on this axis, we need to update bounds, alignment and touching
			if (heading.get(j) != 0) {

				//If we make any movement along an axis, the faces perpendicular to that
				//axis will no longer be aligned or touching
				//Note that if we are moving into a new aligned/touching position this will be
				//detected elsewhere
				//Moving in either direction breaks touching - we are only touching
				//if we are just exactly aligned in contact, NOT penetrating, at least
				//according to the integer bounds etc.
				touching[0][j] = false;
				touching[1][j] = false;
				aligned[0][j] = false;
				aligned[1][j] = false;
			}
		}
		
		//Now finalise changes to touching/aligned due to movement
		//in the plane of the touching/aligned faces
		//On all axes that are aligned, update whether we are touching
		for (int j = 0; j < 3; j++){
			for (int direction = 0; direction < 1; direction++) {
				if (aligned[direction][j]) {
					
					//Check if there are any cubes across the aligned cubegrid boundary,
					//if so we are touching
					//We know that we have set aligned false for each axis 
					//if we have any movement on that axis, so here we are checking
					//what happens as we slide exactly along a plane, possibly in and
					//out of contact with boxes on the other side of it
					
					//Work out the cube grid coord for cubes on the far side of the
					//aligned plane
					int boundary = iPosition.get(j);
					if (direction==0) {
						boundary--;
					} else {
						boundary++;
					}
					
					//Work out cube grid position to check
					searchPosition.set(iPosition);
					searchPosition.set(j, boundary);
					
					//Now scan on that position - iff there is a cube we are touching
					touching[direction][j] = grid.getPresence(searchPosition);
				}
			}
		}		
		
	}
	
	/**
	 * Trace a point through the {@link CubeGrid}, sending notifications
	 * of collisions to a {@link CollisionReceiver}. The point moves at the
	 * defined velocity, for up to maxTime
	 * @param velocity
	 * 		The velocity of the point
	 * @param maxTime
	 * 		The maximum time for which point is traced
	 * @param receiver
	 * 		To be notified of collisions
	 */
	public void slide(Vector3f velocity, float maxTime, CollisionReceiver receiver) {
		
		float elapsedTime = 0;
		
		updateHeading(velocity);

		//Keep scanning for more collisions until we leave the CubeGrid,
		//run out of time, or are told to stop scanning by the receiver
		for (int repeats = 0; repeats < 100000; repeats++) {

			//If velocity is zero, stop
			if (VectorUtils.isZero(velocity)) return;

			//FIXME If we are in an empty cube of the grid, we can
			//check if the void we are in is more than just one cube - that is,
			//whether we are in an empty octant of the octree at some level. If we are, we
			//can find the size of this empty octant, and use its boundaries instead of
			//the boundaries of the cube we are in. This should improve speed, as long as
			//our means of finding empty octants and getting their bounds is relatively fast,
			//and the cubegrid is relatively empty.
			
			//Find the integer positions of the next unit planes to be reached
			//by the extreme point - these are selected from the current 
			//integer bounds, according to heading
			for (int i = 0; i < 3; i++) {
				int h = heading.get(i);
				if (h == 1) {
					nextBoundaries.set(i, iPosition.get(i) + 1); 
				} else {
					//Note that if h == 0 we don't really mind which plane we pick
					nextBoundaries.set(i, iPosition.get(i)); 				
				}
			}
			
			//Now we find which unit plane will be passed through first, 
			//by the point, since we only get collisions when 
			//this happens (we don't necessarily always get a collision though!)
			float minTime = Float.MAX_VALUE;	//Note we can't end up with this after checks, since we know we have some movement in at least one direction
			int minAxis = 0;
			for (int i = 0; i < 3; i++) {
				float vi = velocity.get(i);
				if (vi != 0) {
					float t = (nextBoundaries.get(i) - position.get(i))/vi;
					if (t < minTime) {
						minTime = t;
						minAxis = i;
					}
				}
			}
			
			//If we are out of time before next collision
			if (elapsedTime + minTime > maxTime) {
				//Move to our position at maxTime
				moveAndUpdate(velocity, maxTime - elapsedTime);
				//We're done, no more collisions, and box is at final position
				return;
			}
			
			//Translate to the (possible) collision time 
			//(update position and elapsed time)
			moveAndUpdate(velocity, minTime);
			elapsedTime += minTime;
	
			int headingMinAxis = heading.get(minAxis);
			
			//work out the coordinate in the cube grid for the plane
			//of cubes on the far side of the unit plane we are passing through
			int collisionPlaneCubeIndex = iPosition.get(minAxis) + headingMinAxis;
			
			//Work out the cube position we might collide with
			searchPosition.set(iPosition);
			searchPosition.set(minAxis, collisionPlaneCubeIndex);
			
			//Check for cube
			boolean collided = grid.getPresence(searchPosition);
			
			if (collided) {
				
				//We are now touching and aligned on the collided bounds, in the direction we
				//are headed on the colliding axis
				touching[headingMinAxis > 0 ? 1 : 0][minAxis] = true;
				aligned[headingMinAxis > 0 ? 1 : 0][minAxis] = true;
				
				//Let collision receiver know
				if (!receiver.acceptCollision(elapsedTime, position, minAxis, position)) {
					return;
				}
				
			//If we had no collision, we have passed JUST through the plane in
			//terms of our integer bounds, so we can no collide with cubes on the
			//other side of the plane, and will not check for collision with the
			//same plane again (unless we go past it the other way first)
			} else {
				//Now we move the next integer position along on the axis we will check
				//for collisions, so we will not check it again
				iPosition.set(minAxis, iPosition.get(minAxis) + headingMinAxis);
			}
	
		}
		
	}
	
	/**
	 * Slide the point through a grid, making sure we slide along
	 * any cubes we hit in the grid.
	 * @param v
	 * 		The velocity at which to move
	 * @param maxTime
	 * 		The maximum time for which to move
	 * @param grid
	 * 		The grid we are moving through
	 */
	public void slideAlong(Vector3f v, float maxTime) {
		float elapsedTime = 0;
		slideV.set(v);
		
		while ((elapsedTime < maxTime) && (!VectorUtils.isZero(slideV))) {			
			r.reset();			
			slide(slideV, maxTime - elapsedTime, r);
			//If we had no collision we are done
			if (!r.collided()) return;
			
			//We had a collision, so advance time and
			//kill velocity in the collision axis
			elapsedTime += r.getElapsedTime();
			slideV.set(r.getCollisionAxis(), 0);
		}
	}

	public boolean[][] getTouching() {
		return touching;
	}

	public boolean[][] getAligned() {
		return aligned;
	}

	public Vector3f getPosition() {
		return position;
	}

	public Vector3i getIPosition() {
		return iPosition;
	}

	public CubeGrid getGrid() {
		return grid;
	}
	
}

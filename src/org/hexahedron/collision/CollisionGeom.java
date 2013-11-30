package org.hexahedron.collision;

import org.hexahedron.cube.CubeGrid;

import com.jme3.math.Vector3f;

/**
 * A collision geometry, this can be slid around a cubegrid,
 * tracking whether it collides with, aligns with or touches
 * cubes in the cubegrid.
 */
public interface CollisionGeom {

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
	public void slide(Vector3f velocity, float maxTime, CollisionReceiver receiver);

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
	public void slideAlong(Vector3f v, float maxTime);

	/**
	 * Indicates whether the geom is touching a cube in the cubegrid in each
	 * direction.
	 * 
	 * The first index is 0 for the negative axis direction, 1 for the positive direction
	 * The second index is the axis
	 * 
	 * So for example if bounds[0][2] is true, the geom is touching a cube below
	 * 
	 * This is very closely related to "aligned" - a geom can only be touching
	 * if it is aligned, but may be aligned without touching - it depends whether
	 * there are actually any cubes on the far side of the aligned plane within the
	 * extents of the geom
	 */
	public boolean[][] getTouching();

	/**
	 * Indicates whether the geometry is EXACTLY aligned to the cubegrid in each
	 * direction and axis. 
	 * 
	 * The first index is the direction geom is aligned in - 0 if the geom's negative
	 * facing extent is aligned 
	 * exactly with the negative-axis-direction side of the cube the geom is in, 
	 * 1 if the positive-axis extents are aligned.
	 * The index is the relevant axis.
	 * 
	 * So for example if bounds[0][2] is true, the geom "bottom" is EXACTLY
	 * aligned with a dividing plane of the cube grid in the z axis, by being
	 * at the very minimum edge of the cube it lies in, in the grid.
	 * 
	 * This is very closely related to "touching" - a geom can only be touching
	 * if it is aligned, but may be aligned without touching - it depends whether
	 * there are actually any cubes on the far side of the aligned plane, within
	 * the extents of the geom
	 */
	public boolean[][] getAligned();

	/**
	 * The position of the box center in octree space - octree
	 * cubes are assumed to cover exact units to simplify calculations
	 * @return position
	 */
	public Vector3f getPosition();

	/**
	 * The grid we are moving through
	 */
	public CubeGrid getGrid();

}

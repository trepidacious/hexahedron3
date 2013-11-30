/*
 *  $Id: AFace.java,v 1.6 2008/06/29 00:00:39 shingoki Exp $
 *
 * 	Copyright (c) 2005-2006 shingoki
 *
 *  This file is part of AirCarrier, see http://aircarrier.dev.java.net/
 *
 *    AirCarrier is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.

 *    AirCarrier is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with AirCarrier; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.hexahedron.cube;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.hexahedron.occlusion.Transform;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;

/**
 * <code>AFaceMesh</code> is a Mesh forming one face of a cube with the correct geometry and methods to
 * be a leaf in an Actree.
 * The face has an odd geometry, with 4 tris , meeting at an extra vert in the
 * center of the face. This allows for diagonal shadows to be cast across the
 * face in either direction, as well as for slightly more detail in vertex lighting.
 * The faces have distinct vertices at the corners to allow for sharp edged shadows
 * via vertex colours. 
 * 
 * General systems used for coordinates etc. in this class:
 * 
 * The cardinal directions are:
 * 0	+Z		Up		Top
 * 1	+X		East	Right
 * 2	+Y		North	Back
 * 3	-X		West	Left
 * 4	-Y		South	Front
 * 5	-Z		Down	Bottom
 * 
 * The "Top" etc. descriptions refer to the faces of the cube that
 * have their normal along the cardinal direction.
 * 
 * So for example, the cube's faces are numbered in this order
 *
 * On each face (for example for the purposes of texture mapping), there
 * is a face-local "up" or "local Y" vector, this is as given below:
 * 
 * 0	Top		+Y
 * 1	Right	+Z
 * 2	Back	+Z
 * 3	Left	+Z
 * 4	Front	+Z
 * 5	Bottom	-Y
 * 
 * So the faces containing the Z axis have Z as the "up" or "local Y" vector. The top and bottom
 * have "up" so that if the cube is rotated around the X axis in order to point the
 * face towards a viewer looking along the Y axis, the local "up" vector will then
 * be aligned with the Z axis.
 * 
 * The "right" or "local X" vector for a face is as expected - it is the "up" vector crossed
 * with the face normal, so that the up, right and out vectors form a right handed set.
 * 
 * When the four tris of a cube face are indexed, they are indexed as:
 * 
 * 0	Right
 * 1	Up
 * 2	Left
 * 3	Down
 * 
 * 	in the face local directions.
 * 
 * Finally, when 2D cardinal and diagonal directions are required (e.g. for occlusion), we use:
 * 
 * 0	+X		East			Right
 * 1	+X+Y	North-East		Up-Right
 * 2	+Y		North			Up
 * 3	-X+Y	North-West		Up-Left
 * 4	-X		West			Left
 * 5	-X-Y	South-West		Down-Left
 * 6	-Y		South			Down
 * 7	+X-Y	South-East		Down-Right
 * 
 * When dealing with directions relative to a cube face, X and Y are obviously the face-local X and Y (right and up)
 * 
 * @author shingoki
 * @version $Id: AFace.java,v 1.6 2008/06/29 00:00:39 shingoki Exp $
 */
public class AFaceMesh extends Mesh {

	/**
	 * The index of this face within a cube
	 */
	private final int face;

	//FIXME can we share some of this? E.g. every face with the same index has the
	//same positionBuffer and normalBuffer, every face has the same indexBuffer,
	//I think every face has the same tex2Buffer, and every face with the same
	//UV transformation has the same tex1Buffer? cbufs probably need to be separate.
	//Buffers for mesh data
	private final FloatBuffer positionBuffer;
	private final FloatBuffer normalBuffer;
	private final IntBuffer indexBuffer;
	private final FloatBuffer tex1Buffer;
	private final FloatBuffer tex2Buffer;
	private final FloatBuffer cbuf;
	
	/**
	 * The order in which the verts are used in each tri of the
	 * cube face, indexed as:
	 * 		vertOrder[triIndex][vertIndex]
	 * where triIndex is the index of the triangle within the cube
	 * face, and vertIndex is the index of the vert in that triangle.
	 */
	final static int[][] vertOrder = 
		new int[][]
		          {
					{0, 1, 4},
					{1, 2, 4},
					{2, 3, 4},
					{3, 0, 4},
		          };

	

	/**
	 * Constructor creates a new <code>AFace</code> object, which is a unit cube
	 * centered on 0,0,0
	 * 
	 * @param face The index of this face within a cube
	 */
	public AFaceMesh(int face) {
		this.face = face;
		
        //Face has 4 entirely separate tris, with 3 vertices each
		int vertexCount = 4 * 3;
		
		//We need enough vertices in the buffer
		positionBuffer = BufferUtils.createVector3Buffer(vertexCount);
		setBuffer(Type.Position, 3, positionBuffer);
				
		//One normal per vertex
		normalBuffer = BufferUtils.createVector3Buffer(vertexCount);
		setBuffer(Type.Normal, 3, normalBuffer);
	
		//One UV position per vertex, in the first two tex coords
		tex1Buffer = BufferUtils.createVector2Buffer(vertexCount);
		setBuffer(Type.TexCoord, 2, tex1Buffer);
		
		tex2Buffer = BufferUtils.createVector2Buffer(vertexCount);
		setBuffer(Type.TexCoord2, 2, tex2Buffer);

	    //Each vertex is only used once, so one index per vertex
		indexBuffer = BufferUtils.createIntBuffer(vertexCount);
	    setBuffer(Type.Index, 3, indexBuffer);

	    //Set up the indices first - we just run through the verts in order, since
	    //there are no shared verts, and verts are added in the order they are used in triangles
	    for (int i = 0; i < vertexCount; i++) {
			indexBuffer.put(i, i);
	    }
	    
	    cbuf = BufferUtils.createFloatBuffer(4 * getVertexCount());
	    setBuffer(Type.Color, 4, cbuf);
	    
	    setFaceColor(new ColorRGBA(244f/255f, 236f/255f, 222f/255f, 1f));

	    //Set up each face in turn, this also fills out faceLocalAxes
	    buildCubeFace(face);
	    
	    updateBound();
	}

	/**
	 * The index of this face within its cube
	 * @return
	 * 		face index
	 */
	public int getFace() {
		return face;
	}

	/**
	 * Add a face to the triangle and UV buffers
	 * 	@param face
	 * 		The direction of face to add
	 */
	private void buildCubeFace(int face) {

		//Build each Tri
		for (int i = 0; i < 4; i++) {
			buildTri(face, i);			
		}

	}
	
	/**
	 * Put entries in the vertex and normal buffers of the batch (at current position)
	 * in order to build a tri according to the current contents of faceVerts
	 * @param face
	 * 		The cube face being built
	 * @param i
	 * 		The tri being built
	 */
	private void buildTri(int face, int i) {
		
		Vector3f normal = AFace.faceLocalAxes[face][2];

		for (int j = 0; j < 3; j++) {
			
			//The index we use into the faceVerts and faceUVs arrays 
			int vertIndex = vertOrder[i][j];
			
			//Get the vert position from faceVerts array
			Vector3f faceVert = AFace.faceVerts[face][vertIndex];
			positionBuffer.put(faceVert.x).put(faceVert.y).put(faceVert.z);
			
			//Get the UV positions from faceUVs array
			Vector2f faceUV = AFace.faceUVs[vertIndex]; 
			tex1Buffer.put(faceUV.x).put(faceUV.y);
			tex2Buffer.put(faceUV.x).put(faceUV.y);
			
			//Put the normal directly as specified
			normalBuffer.put(normal.x).put(normal.y).put(normal.z);
		}		
	}
	
	/**
	 * Set the color of a given triangle of the face
	 * @param tri
	 * 		The triangle
	 * @param color
	 * 		The color
	 */
	public void setTriColor(int tri, ColorRGBA color) {
		//There are 4 floats per vertex, and 3 vertices per tri
		cbuf.position(4 * 3 * tri);
		
		//Put the color into each of the three vertices, as r,g,b,a
		cbuf.put(color.r);
		cbuf.put(color.g);
		cbuf.put(color.b);
		cbuf.put(color.a);
		
		cbuf.put(color.r);
		cbuf.put(color.g);
		cbuf.put(color.b);
		cbuf.put(color.a);
		
		cbuf.put(color.r);
		cbuf.put(color.g);
		cbuf.put(color.b);
		cbuf.put(color.a);
	}

	/**
	 * Set the color of each triangle in the face
	 * @param color
	 * 		The color
	 */
	public void setFaceColor(ColorRGBA color) {
		for (int i = 0; i < 4; i++) {
			setTriColor(i, color);
		}
	}
	


	/**
	 * Set the UV positions to apply a transform to the display
	 * of the occlusion texture on the face
	 * @param transform
	 * 		The transform required for the occlusion texture
	 */
	public void setUVTransform(Transform transform){
		tex1Buffer.rewind();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				
				//The index we use into the faceUVs array, given no 
				//transform
				int vertIndex = vertOrder[i][j];

				//Now work out where we would be if transformed
				
				//Index 4 is never moved, since it is at the center
				//of the face
				if (vertIndex != 4) {
					
					//Flip vertically
					if (transform.getFlip()) {
						if (vertIndex == 0) {
							vertIndex = 1;
						} else if (vertIndex == 1) {
							vertIndex = 0;
						} else if (vertIndex == 2) {
							vertIndex = 3;
						} else { // 3
							vertIndex = 2;
						}
					}
					
					//Rotate mod 4
					//Use negative rotation since we are rotating the positions,
					//so the image rotates the other way, unless we are flipped,
					//in which case go the other way
					int r = transform.getRotate();
					if (transform.getFlip()) r = -r;
					vertIndex = (vertIndex - r) % 4;
					if (vertIndex < 0) vertIndex += 4;
				}
				
				//Get the UV positions from faceUVs array
				Vector2f faceUV = AFace.faceUVs[vertIndex]; 
				tex1Buffer.put(faceUV.x).put(faceUV.y);				
			}
		}
	}
	
}

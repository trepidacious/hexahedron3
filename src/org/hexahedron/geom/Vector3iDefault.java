package org.hexahedron.geom;

import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

public class Vector3iDefault implements Vector3i {

	int[] coords;

	public Vector3iDefault() {
		super();
		coords = new int[]{0, 0, 0};
	}

	public Vector3iDefault(int x, int y, int z) {
		super();
		coords = new int[]{x, y, z};
	}

	public Vector3iDefault(Vector3i toClone) {
		this();
		set(toClone);
	}

	public Vector3iDefault(Vector3f toClone) {
		this();
		set(toClone);
	}

	/**
	 * Create a new {@link Vector3iDefault} where each axis
	 * is set from the floor of the corresponding
	 * axis in toFloor
	 * @param toFloor
	 * 		The vector to floor
	 * @return
	 * 		The new floor vector
	 */
	public static Vector3iDefault createFloor(Vector3f toFloor) {
		Vector3iDefault v = new Vector3iDefault();
		for (int i = 0; i < 3; i++) {
			v.set(i, (int)FastMath.floor(toFloor.get(i)));
		}
		return v;
	}

	public Vector3iDefault setFloor(Vector3f toFloor) {
		for (int i = 0; i < 3; i++) {
			set(i, (int)FastMath.floor(toFloor.get(i)));
		}
		return this;
	}

	public int getX() {
		return coords[0];
	}

	public void set(int x, int y, int z) {
		coords[0] = x;
		coords[1] = y;
		coords[2] = z;
	}

	public void setX(int x) {
		coords[0] = x;
	}

	public int getY() {
		return coords[1];
	}

	public void setY(int y) {
		coords[1] = y;
	}

	public int getZ() {
		return coords[2];
	}

	public void setZ(int z) {
		coords[2] = z;
	}

	public int get(int i) {
		return coords[i];
	}

	public Vector3iDefault addLocalMult(Vector3i toAdd, int mult) {
		for (int i = 0; i < 3; i++) {
			coords[i] += toAdd.get(i) * mult;
		}
		return this;
	}

	public Vector3iDefault addLocal(Vector3i toAdd) {
		for (int i = 0; i < 3; i++) {
			coords[i] += toAdd.get(i);
		}
		return this;
	}

	public Vector3iDefault subtractLocal(Vector3i toAdd) {
		for (int i = 0; i < 3; i++) {
			coords[i] -= toAdd.get(i);
		}
		return this;
	}
	
	public Vector3iDefault multLocal(Vector3i toAdd) {
		for (int i = 0; i < 3; i++) {
			coords[i] *= toAdd.get(i);
		}
		return this;
	}

	public Vector3iDefault divideLocal(Vector3i toDivide) {
		for (int i = 0; i < 3; i++) {
			coords[i] /= toDivide.get(i);
		}
		return this;
	}
	
	public int lengthSquared() {
		int ls = 0;
		for (int i = 0; i < 3; i++) {
			ls += coords[i] * coords[i]; 
		}
		return ls;
	}

	public void set(int i, int val) {
		coords[i] = val;
	}

	public void set(Vector3i toSet) {
		for (int i = 0; i < 3; i++) {
			coords[i] = toSet.get(i);
		}		
	}

	public void set(Vector3f toSet) {
		for (int i = 0; i < 3; i++) {
			coords[i] = Math.round(toSet.get(i));
		}		
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		
		if (obj instanceof Vector3i) {
			Vector3i v = (Vector3i) obj;
			return (v.getX() == getX() && v.getY() == getY() && v.getZ() == getZ());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getX() + getY() * 31 + getZ() * 19;
	}

	@Override
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ")";
	}
}

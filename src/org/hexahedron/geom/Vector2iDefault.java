package org.hexahedron.geom;

import com.jme3.math.Vector2f;

public class Vector2iDefault implements Vector2i {

	int[] coords;

	public Vector2iDefault() {
		super();
		coords = new int[]{0, 0};
	}

	public Vector2iDefault(int x, int y) {
		super();
		coords = new int[]{x, y};
	}

	public Vector2iDefault(Vector2i toClone) {
		this();
		set(toClone);
	}

	public Vector2iDefault(Vector2f toClone) {
		this();
		set(toClone);
	}

	public void setComponents(int x, int y) {
		coords[0] = x;
		coords[1] = y;
	}

	public int getX() {
		return coords[0];
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

	public int get(int i) {
		return coords[i];
	}

	public Vector2iDefault addLocalMult(Vector2i toAdd, int mult) {
		for (int i = 0; i < 2; i++) {
			coords[i] += toAdd.get(i) * mult;
		}
		return this;
	}

	public Vector2iDefault addLocal(Vector2i toAdd) {
		for (int i = 0; i < 2; i++) {
			coords[i] += toAdd.get(i);
		}
		return this;
	}

	public Vector2iDefault subtractLocal(Vector2i toAdd) {
		for (int i = 0; i < 2; i++) {
			coords[i] -= toAdd.get(i);
		}
		return this;
	}
	
	public Vector2iDefault multLocal(Vector2i toAdd) {
		for (int i = 0; i < 2; i++) {
			coords[i] *= toAdd.get(i);
		}
		return this;
	}

	public Vector2iDefault divideLocal(Vector2i toDivide) {
		for (int i = 0; i < 2; i++) {
			coords[i] /= toDivide.get(i);
		}
		return this;
	}
	
	public int lengthSquared() {
		int ls = 0;
		for (int i = 0; i < 2; i++) {
			ls += coords[i] * coords[i]; 
		}
		return ls;
	}

	public void set(int i, int val) {
		coords[i] = val;
	}

	public void set(Vector2i toSet) {
		for (int i = 0; i < 2; i++) {
			coords[i] = toSet.get(i);
		}		
	}

	public void set(Vector2f toSet) {
		coords[0] = Math.round(toSet.x);
		coords[1] = Math.round(toSet.y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		
		if (obj instanceof Vector2i) {
			Vector2i v = (Vector2i) obj;
			return (v.getX() == getX() && v.getY() == getY());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return getX() + getY() * 31;
	}

	@Override
	public String toString() {
		return "(" + getX() + ", " + getY() + ")";
	}
}

package org.hexahedron.collision;

import com.jme3.math.Vector3f;

public class PrintingCollisionReceiver implements CollisionReceiver {

	@Override
	public boolean acceptCollision(float elapsedTime, Vector3f position,
			int collisionAxis, Vector3f collisionCenter) {
		System.out.println("Collision at " + elapsedTime + "s, box at " + position + ", collision axis " + collisionAxis + ", center " + collisionCenter);
		return false;
	}

}

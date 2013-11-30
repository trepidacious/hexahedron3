package org.hexahedron.util;

import com.jme3.scene.Spatial;

/**
 * A spatial action which calls lockMesh on spatials
 * @author goki
 */
public class SpatialMeshLocker implements SpatialAction {

	public void actOnSpatial(Spatial spatial) {
		spatial.lockMeshes();
	}

	public void actOnSpatial(Spatial spatial, int level) {
		actOnSpatial(spatial);
	}

}

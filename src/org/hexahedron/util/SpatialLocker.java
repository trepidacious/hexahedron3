package org.hexahedron.util;

import com.jme3.scene.Spatial;

/**
 * A spatial action which locks spatials
 * @author goki
 */
public class SpatialLocker implements SpatialAction {

	public void actOnSpatial(Spatial spatial) {
		spatial.lock();
	}

	public void actOnSpatial(Spatial spatial, int level) {
		actOnSpatial(spatial);
	}

}

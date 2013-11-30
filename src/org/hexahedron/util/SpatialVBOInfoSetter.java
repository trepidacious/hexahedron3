package org.hexahedron.util;

import com.jme3.scene.Spatial;

/**
 * A spatial action which sets a new VBOInfo on any
 * TriMeshes, with a specified "enabled" value.
 * @author goki
 */
public class SpatialVBOInfoSetter implements SpatialAction {

	boolean enabled;
	
	public SpatialVBOInfoSetter(boolean enabled) {
		this.enabled = enabled;
	}
	
	public void actOnSpatial(Spatial spatial) {
		if (spatial instanceof TriMesh) {
			((TriMesh)spatial).setVBOInfo(new VBOInfo(enabled));
			//System.out.println("Set TriMesh " + spatial + " VBOInfo: " + enabled);
		}
	}

	public void actOnSpatial(Spatial spatial, int level) {
		actOnSpatial(spatial);
	}

}

package org.hexahedron.test;

import org.hexahedron.cube.ACube;
import org.hexahedron.cube.AFace;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
 
public class ACubeTest extends SimpleApplication {
	
    public static void main(String[] args) {
    	new ACubeTest().start();
    }
     
    @Override
    public void simpleInitApp() {
    	
    	ACube cube = new ACube();
    	for (int i = 0; i < 6; i++) {
    		AFace aFace = new AFace(i);
    		cube.attachFace(aFace);
    		aFace.setFaceColor(ColorRGBA.Orange);
    	}
    	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    	mat.setBoolean("VertexColor", true);
        cube.setMaterial(mat);
        rootNode.attachChild(cube);
        mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);

        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
    }
}

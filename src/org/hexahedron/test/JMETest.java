package org.hexahedron.test;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.math.ColorRGBA;
 
public class JMETest extends SimpleApplication {
	
    public static void main(String[] args) {
    	new JMETest().start();
    }
     
    @Override
    public void simpleInitApp() {
    	Box b = new Box(Vector3f.ZERO, Vector3f.UNIT_XYZ);
    	Geometry geom = new Geometry("Box", b);
    	Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);
    }
}

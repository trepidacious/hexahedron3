package org.hexahedron.test;

import java.util.Random;

import org.hexahedron.cube.AFace;
import org.hexahedron.cube.CubeGrid;
import org.hexahedron.cube.Octode;
import org.hexahedron.geom.Vector3iDefault;
import org.hexahedron.occlusion.OcclusionTextures;

import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.scene.Spatial;
import com.jme3.util.SkyFactory;
 
public class CubeGridTest extends SimpleApplication {
	
    public static void main(String[] args) {
    	new CubeGridTest().start();
    }
     
    @Override
    public void simpleInitApp() {
    	
        flyCam.setMoveSpeed(10f); // odd to set this here but it did it before

    	OcclusionTextures occlusionTextures = new OcclusionTextures(assetManager);
    	CubeGrid grid = new CubeGrid(assetManager, occlusionTextures, 8);
		Random r = new Random(101);
		Vector3iDefault bv = new Vector3iDefault(0,0,0);
		for (int i = 0; i < 2000; i++) {
			int direction = r.nextInt(6);
			bv.addLocal(AFace.intThreeDCardinalDirections[direction]);
			for (int j = 0; j < 3; j++) {
				if (bv.get(j) < 0) bv.set(j, 0);
				if (bv.get(j) >= grid.size(j)) bv.set(j, grid.size(j)-1);
			}
			grid.setPresence(bv, true);
		}
		
		grid.buildAllCubes();
		
		
//		for (int x = 0; x < 20; x++) {
//			for (int y = 0; y < 20; y++) {
//				for (int z = 0; z < 20; z++) {
//					for (int f = 0; f < 6; f++) {
//						ACube cube = grid.getCube(new Vector3iDefault(x, y, z));
//						if (cube != null) {
//							AFace face = cube.getFace(f);
//							if (face != null) {
//								TextureState ts = (TextureState)face.getRenderState(RenderState.RS_TEXTURE);
//								ts.setTexture(sss, 1);
//							}
//						}
//					}
//				}
//			}
//		}

		
		Octode gridRoot = grid.getOctode();

		//FIXME reinstate
//		setupFog(gridRoot);
//		gridRoot.updateRenderState();

		grid.shade(false, CubeGrid.DEFAULT_BASE_COLOR, CubeGrid.DEFAULT_DARK_COLOR, CubeGrid.DEFAULT_FACE_COLORS);
		
		rootNode.attachChild(gridRoot);
		gridRoot.updateModelBound();
		
		//FIXME is this still needed? it's from jme2
//        gridRoot.updateWorldBound(); // We do this to allow the camera setup access to the world bound in our setup code.

		//FIXME Can we do this in jme3? Code from jme2
//        gridRoot.lock();

        //FIXME reinstate
//		TextureState ts = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
//		ts.setTexture(crete, 0);
//        box = new Box("box", new Vector3f(), 0.4f, 0.9f, 0.4f);
//        box.setRenderState(ts);
//		rootNode.attachChild(box);
//
//		WireframeState wfState = DisplaySystem.getDisplaySystem().getRenderer().createWireframeState();
//		wfState.setLineWidth(3);
//		MaterialState mState = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
//		mState.setDiffuse(ColorRGBA.blue);
//		TextureState ts2 = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
//		ts2.setTexture(crete, 0);
		
		/*
		corners = new Box[8];
		
		for (int i = 0; i < corners.length; i++) {
			corners[i] = new Box("corner " + i, new Vector3f(), 0.5f, 0.5f, 0.5f);
			rootNode.attachChild(corners[i]);
			corners[i].setRenderState(wfState);
			corners[i].setRenderState(mState);
			corners[i].setRenderState(ts2);
		}
		*/
		
        //FIXME jme2->3?
//		rootNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
	
		//FIXME reinstate
//		octoBox = new OctoBox(grid, new Vector3f(0.4f, 0.9f, 0.4f), new Vector3f(10, 32, 10));
		
    	
    	
//    	
//    	ACube cube = new ACube();
//    	for (int i = 0; i < 6; i++) {
//    		AFace aFace = new AFace(i);
//    		cube.attachFace(aFace);
//    		aFace.setFaceColor(ColorRGBA.Orange);
//    	}
		
		Spatial skyBox = SkyFactory.createSky(assetManager, 
        		assetManager.loadTexture("resources/bskyW.jpg"),
        		assetManager.loadTexture("resources/bskyE.jpg"),
        		assetManager.loadTexture("resources/bskyN.jpg"),
        		assetManager.loadTexture("resources/bskyS.jpg"),
        		assetManager.loadTexture("resources/bskyU.jpg"),
        		assetManager.loadTexture("resources/bskyD.jpg")
        		);
        rootNode.attachChild(skyBox);
		Quaternion skyQ = new Quaternion(new float[]{0,FastMath.PI * -0.25f,0});
		skyBox.setLocalRotation(skyQ);
		
        viewPort.setBackgroundColor(ColorRGBA.DarkGray);
    }
    
//	private void setupFog(Node node) {
//		ColorRGBA skyColor = new ColorRGBA(0.956862745f, 0.945098039f, 0.917647058f, 0.5f);
//		FogState fs = display.getRenderer().createFogState();
//		fs.setDensity(1f);
//		fs.setEnabled(true);
//		fs.setColor(skyColor);
//		fs.setEnd(35);
//		fs.setStart(2);
//		fs.setDensityFunction(DensityFunction.Linear);
//		fs.setQuality(Quality.PerVertex);
//		node.setRenderState(fs);		
//	}
}

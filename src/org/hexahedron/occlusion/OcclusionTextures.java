package org.hexahedron.occlusion;

import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

public class OcclusionTextures {

//	private final AssetManager assetManager;

	private final Texture[] occlusionTextures = new Texture[51];
	
	public OcclusionTextures(AssetManager assetManager) {
		super();
//		this.assetManager = assetManager;
		
		for (int i = 0; i < 51; i++) {
			String s = Integer.toString(i+1);
			while (s.length() < 4) s = "0" + s;
			occlusionTextures[i] = assetManager.loadTexture("resources/occlusion/" + s + ".png");
			
			//FIXME We might be able to do better here
			occlusionTextures[i].setWrap(WrapMode.EdgeClamp);
		}
	}
	
	public Texture occlusionTexture(int i) {
		return occlusionTextures[i];
	}
	
}

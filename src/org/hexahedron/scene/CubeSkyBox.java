/*
 *  $Id: CarrierSkyBox.java,v 1.5 2007/05/30 22:31:04 shingoki Exp $
 *
 * 	Copyright (c) 2005-2006 shingoki
 *
 *  This file is part of AirCarrier, see http://aircarrier.dev.java.net/
 *
 *    AirCarrier is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.

 *    AirCarrier is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.

 *    You should have received a copy of the GNU General Public License
 *    along with AirCarrier; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package org.hexahedron.scene;

import org.hexahedron.util.TextureLoader;

import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Texture;

/**
 * 
 * A skybox with textures loaded from carrier resources
 * 
 * @author shingoki
 * 
 */
public class CubeSkyBox extends Skybox {
	private static final long serialVersionUID = -5939987896538415224L;

	/**
	 * Create a skybox
	 * 
	 * @param name
	 *            Name for the node
	 * @param resourceBase
	 *            Base name for texture resources
	 * @param resourceExtension
	 *            Extension for texture resources
	 */
	public CubeSkyBox(String name, String resourceBase,
			String resourceExtension) {
		super(name, 300, 300, 300);

		Texture u = TextureLoader.loadUncompressedTexture(resourceBase + "U" + resourceExtension);
		Texture n = TextureLoader.loadUncompressedTexture(resourceBase + "N" + resourceExtension);
		Texture e = TextureLoader.loadUncompressedTexture(resourceBase + "E" + resourceExtension);
		Texture s = TextureLoader.loadUncompressedTexture(resourceBase + "S" + resourceExtension);
		Texture w = TextureLoader.loadUncompressedTexture(resourceBase + "W" + resourceExtension);
		Texture d = TextureLoader.loadUncompressedTexture(resourceBase + "D" + resourceExtension);

		setTexture(Skybox.Face.Up, u);
		setTexture(Skybox.Face.North, n);
		setTexture(Skybox.Face.East, e);
		setTexture(Skybox.Face.South, s);
		setTexture(Skybox.Face.West, w);
		setTexture(Skybox.Face.Down, d);
		
		preloadTextures();
		
		CullState cullState = DisplaySystem.getDisplaySystem().getRenderer().createCullState();
		cullState.setCullFace(com.jme.scene.state.CullState.Face.None);
		cullState.setEnabled( true );
		setRenderState( cullState );

		ZBufferState zState = DisplaySystem.getDisplaySystem().getRenderer().createZBufferState();
		zState.setEnabled( false );
		setRenderState( zState );

		FogState fs = DisplaySystem.getDisplaySystem().getRenderer().createFogState();
		fs.setEnabled( false );
		setRenderState( fs );

		setLightCombineMode(LightCombineMode.Off);
		setCullHint(CullHint.Never);

		setTextureCombineMode(TextureCombineMode.Replace);
		updateRenderState();

		//lockBounds();
		//lockMeshes();
		
		
	}

}

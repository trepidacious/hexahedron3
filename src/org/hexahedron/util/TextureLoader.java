/*
 *  $Id: TextureLoader.java,v 1.3 2007/02/18 20:24:20 shingoki Exp $
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

package org.hexahedron.util;

import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;

public class TextureLoader {

	/**
	 * Loads a texture with reasonable defaults, from a carrier resource
	 * @param resourceName
	 * 		Name of the resource to load
	 * @return
	 * 		A texture with:
	 * 			MM_LINEAR_LINEAR
	 * 			FM_LINEAR
	 * 			Guessed format
	 */
	public static Texture loadTexture(String resourceName) {
		return TextureManager.loadTexture(
				TextureLoader.class.getClassLoader().getResource(resourceName),
				MinificationFilter.Trilinear,
				MagnificationFilter.Bilinear);
	}

	public static Texture loadUncompressedTexture(String resourceName) {
		return TextureManager.loadTexture(
				TextureLoader.class.getClassLoader().getResource(resourceName), 
				MinificationFilter.Trilinear,
				MagnificationFilter.Bilinear,
				//Make sure we don't use compression, which makes skyboxes look awful
				Format.GuessNoCompression,		 
				1f, 
				true);
	}
}

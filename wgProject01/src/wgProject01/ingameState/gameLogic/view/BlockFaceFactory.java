package wgProject01.ingameState.gameLogic.view;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;

/**
 * Provides static methods for creating block faces (JME3 geometries).
 * 
 * @author oli
 * 
 */
public class BlockFaceFactory {

	/**
	 * Returns a wooden Block face (Quad Geometry).
	 */
	public static Geometry getWoodenFace(AssetManager assetManager) {
		return getFaceWithTexture("./assets/Textures/Wood/wood.png",
				assetManager);
	}

	/**
	 * Returns a stony Block face (Quad Geometry).
	 */
	public static Geometry getStoneFace(AssetManager assetManager) {
		return getFaceWithTexture("./assets/Textures/Pond/Pond.jpg",
				assetManager);
	}

	/**
	 * Creates an returns the geometry with a texture from the given file path.
	 * 
	 * @param filePath
	 *            may for example be "./assets/Textures/Wood/wood.png"
	 */
	private static Geometry getFaceWithTexture(String filePath,
			AssetManager assetManager) {
		Geometry result = getOpaqueGeometry();
		Material material = getMaterialWithTexture(filePath, assetManager);
		result.setMaterial(material);
		return result;
	}

	/**
	 * Creates an returns the geometry without a material of an opaque
	 * BlockFace.
	 */
	private static Geometry getOpaqueGeometry() {
		float sideLength = 1f;
		Quad mesh = new Quad(sideLength, sideLength);
		Geometry result = new Geometry("Quad", mesh);
		return result;
	}

	/**
	 * Creates and returns a material with the texture from the given path
	 * attached.
	 * 
	 * @param filePath
	 *            may for example be "./assets/Textures/Wood/wood.png"
	 */
	private static Material getMaterialWithTexture(String filePath,
			AssetManager assetManager) {
		Material material = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		Texture cube1Tex = assetManager.loadTexture(filePath);
		material.setTexture("ColorMap", cube1Tex);
		return material;
	}

}

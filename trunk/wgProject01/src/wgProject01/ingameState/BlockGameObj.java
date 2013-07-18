package wgProject01.ingameState;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;

public class BlockGameObj {

	/**
	 * The Node to which this Block will be attached to and detached from.
	 */
	private Node node;
	
	/**
	 * The box geometry of this block.
	 */
	private Geometry geometry;

	/**
	 * Creates a new Block game object which will attach to the given node as
	 * soon as placed via the {@link #doHandlePlacementAt(int, int, int)}
	 * method.
	 * 
	 * @param node
	 */
	BlockGameObj(Node node, AssetManager assetManager) {
		this.node = node;
		
		Box shape = new Box(0.5f, 0.5f, 0.5f);
		geometry = new Geometry("Block", shape);

		TangentBinormalGenerator.generate(shape);
		Material sphereMat = new Material(assetManager,
				"assets/Materials/Lighting/Lighting.j3md");
		sphereMat.setTexture("DiffuseMap",
				assetManager.loadTexture("Textures/Pond/Pond.jpg"));
		sphereMat.setTexture("NormalMap",
				assetManager.loadTexture("Textures/Pond/Pond_normal.png"));
		sphereMat.setBoolean("UseMaterialColors", true);
		sphereMat.setColor("Diffuse", ColorRGBA.White);
		sphereMat.setColor("Specular", ColorRGBA.White);
		sphereMat.setFloat("Shininess", 64f); // [0,128]
		geometry.setMaterial(sphereMat);
	}

	/**
	 * Informs the block about a changed neighbor block in the current
	 * direction. This block acts accordingly by turning on and of it's mathes.
	 */
	void doHandleNeighborChangeAt(int i, int j, int k, BlockGameObj newNeighbor) {
		// TODO 1.
	}

	/**
	 * Informs the block that it was placed at the given position.
	 */
	void doHandlePlacementAt(int x, int y, int z) {
		geometry.setLocalTranslation(x, y, z);
		node.attachChild(geometry);
	}

	/**
	 * Informs the block that it was removed from it's position.
	 */
	void doHandleRemovementFrom() {
		geometry.removeFromParent();
	}

}

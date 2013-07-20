package wgProject01.ingameState;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
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

	private Box mesh;

	/**
	 * Creates a new Block game object which will attach to the given node as
	 * soon as placed via the {@link #doHandlePlacementAt(int, int, int)}
	 * method.
	 * 
	 * @param node
	 */
	BlockGameObj(Node node, AssetManager assetManager) {
		this.node = node;

		mesh = new Box(0.5f, 0.5f, 0.5f);
		geometry = new Geometry("Block", mesh);
		geometry.setQueueBucket(Bucket.Transparent);
		TangentBinormalGenerator.generate(mesh);
		Material shinyStoneMat = new Material(assetManager,
				"assets/Materials/Lighting/Lighting.j3md");
		shinyStoneMat.setTexture("DiffuseMap",
				assetManager.loadTexture("Textures/Pond/Pond.jpg"));
		shinyStoneMat.setTexture("NormalMap",
				assetManager.loadTexture("Textures/Pond/Pond_normal.png"));
		shinyStoneMat.setBoolean("UseMaterialColors", true);
		shinyStoneMat.setColor("Diffuse", ColorRGBA.White);
		shinyStoneMat.setColor("Specular", ColorRGBA.White);
		shinyStoneMat.setFloat("Shininess", 64f); // [0,128]

		Material debugMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		ColorRGBA randomColor = ColorRGBA.randomColor();
		randomColor.a = 0.5f;
		debugMaterial.setColor("Color", randomColor);
		debugMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // !

		geometry.setMaterial(debugMaterial);
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
//		mesh.getBound().setCenter(new Vector3f(x, y, z));
		node.attachChild(geometry);
	}

	/**
	 * Informs the block that it was removed from it's position.
	 */
	void doHandleRemovementFrom() {
		geometry.removeFromParent();
	}

	BoundingBox getBoundingBox() {
		return (BoundingBox) mesh.getBound();
	}
}

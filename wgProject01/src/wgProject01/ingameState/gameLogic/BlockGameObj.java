package wgProject01.ingameState.gameLogic;

import wgProject01.Settings;
import wgProject01.ingameState.gameLogic.components.BlockPositionComponent;
import wgProject01.ingameState.gameLogic.view.BlockView;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;

/**
 * Objects of this class represent the logic of fixed blocks in the terrain.
 * 
 * They are managed by the {@link BlockManager}. They need to be informed,
 * whenever they are moved or their neighborhood changed, using this classes
 * methods.
 * 
 * @author oli
 * 
 */
public class BlockGameObj {

	/**
	 * The View of this block.
	 */
	private BlockView blockView;

	/**
	 * The position of this block.
	 */
	public BlockPositionComponent blockPositionComponent = new BlockPositionComponent();

	/**
	 * Creates a new Block game object which will attach a spatial to the given
	 * node as soon as placed via the
	 * {@link #doHandlePlacementAt(int, int, int)} method.
	 */
	BlockGameObj(Node node, AssetManager assetManager) {
		Geometry geometry;
		if (Settings.debugMode < 3) {
			// in release mode create a normal textured block
			Box mesh = new Box(0.5f, 0.5f, 0.5f);
			geometry = new Geometry("Block", mesh);
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
			geometry.setMaterial(shinyStoneMat);
		} else {
			// in debug mode create a transparent block
			Box mesh = new Box(0.5f, 0.5f, 0.5f);
			geometry = new Geometry("Block", mesh);
			geometry.setQueueBucket(Bucket.Transparent);
			Material debugMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			ColorRGBA randomColor = ColorRGBA.randomColor();
			randomColor.a = 0.5f;
			debugMaterial.setColor("Color", randomColor);
			debugMaterial.getAdditionalRenderState().setBlendMode(
					BlendMode.Alpha); // !
			geometry.setMaterial(debugMaterial);
		}
		blockView = new BlockView(this, geometry, node);
	}

	/**
	 * Informs the block about a changed neighbor block in the current
	 * direction. This block acts accordingly by turning on and of it's surfaces
	 * meshes.
	 */
	void doHandleNeighborChangeAt(int i, int j, int k, BlockGameObj newNeighbor) {
		blockView.informBlockChange();
	}

	/**
	 * Informs the block that it was placed at the given position. The block
	 * updates its model and view accordingly.
	 */
	void doHandlePlacementAt(int x, int y, int z) {
		this.blockPositionComponent.x = x;
		this.blockPositionComponent.y = y;
		this.blockPositionComponent.z = z;
		this.blockPositionComponent.placed = true;
		blockView.informBlockChange();
	}

	/**
	 * Informs the block that it was removed. The block updates its model and
	 * view accordingly.
	 */
	void doHandleRemovementFrom() {
		this.blockPositionComponent.placed = false;
		blockView.informBlockChange();
	}
}

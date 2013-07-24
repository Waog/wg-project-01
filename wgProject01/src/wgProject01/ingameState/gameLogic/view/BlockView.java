package wgProject01.ingameState.gameLogic.view;

import jm3Utils.Jme3Utils;
import wgProject01.Settings;
import wgProject01.ingameState.gameLogic.BlockGameObj;
import wgProject01.ingameState.gameLogic.BlockManager;

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
 * <p>
 * Wraps a {@link BlockGameObj Block} object and is attached to a visual model
 * (a JME spatial). Renders the spatial, to represent the wrapped block best.
 * </p>
 * 
 * <p>
 * When using this class the BlockView is the passive component, which is
 * informed about the infrequent changes of it's Block object.
 * </p>
 * 
 * @author oli
 * 
 */
public class BlockView {

	/**
	 * The wrapped block, which is visualized by this view.
	 */
	private BlockGameObj block;

	/**
	 * The wrapped JME3 Node which represents the block of this view.
	 */
	private Node blockNode;

	/**
	 * The spatial of one face of this block.
	 */
	private Geometry facePositiveX, facePositiveY, facePositiveZ,
			faceNegativeX, faceNegativeY, faceNegativeZ;

	/**
	 * The scene graph Node to which this Blocks spatial will be attached to and
	 * detached from.
	 */
	private Node parentNode;

	/**
	 * Constructs a new View for the given block and spatial.
	 * 
	 * @param blockNode
	 *            {@inheritDoc #node} The scene graph Node to which this Blocks
	 *            spatial will be attached to and detached from.
	 */
	public BlockView(BlockGameObj block, Node parentNode,
			AssetManager assetManager) {
		this.parentNode = parentNode;
		this.block = block;
		initFaceSpatials(assetManager);

		Geometry boxGeometry;
		if (Settings.debugMode < 3) {
			// in release mode create a normal textured block
			Box mesh = new Box(0.5f, 0.5f, 0.5f);
			boxGeometry = new Geometry("Block", mesh);
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
			boxGeometry.setMaterial(shinyStoneMat);
		} else {
			// in debug mode create a transparent block
			Box mesh = new Box(0.5f, 0.5f, 0.5f);
			boxGeometry = new Geometry("Block", mesh);
			boxGeometry.setQueueBucket(Bucket.Transparent);
			Material debugMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			ColorRGBA randomColor = ColorRGBA.randomColor();
			randomColor.a = 0.5f;
			debugMaterial.setColor("Color", randomColor);
			debugMaterial.getAdditionalRenderState().setBlendMode(
					BlendMode.Alpha); // !
			boxGeometry.setMaterial(debugMaterial);
		}
		this.blockNode = new Node();
		this.blockNode.attachChild(boxGeometry);
	}

	/**
	 * Informs this view about an update to the block object. This view then
	 * renders the wrapped block according to the new state in the rendering
	 * thread.
	 */
	public void informBlockChange() {
		// TODO 2: move this code, to be executed in the render thread somehow.
		this.blockNode.setLocalTranslation(block.blockPositionComponent.x,
				block.blockPositionComponent.y, block.blockPositionComponent.z);
		if (block.blockPositionComponent.placed) {
			this.parentNode.attachChild(blockNode);
		} else {
			this.blockNode.removeFromParent();
		}

		// update each face depending on the blocks neighbor
		checkNeighbor(1, 0, 0, facePositiveX);
		checkNeighbor(0, 1, 0, facePositiveY);
		checkNeighbor(0, 0, 1, facePositiveZ);
		checkNeighbor(-1, 0, 0, faceNegativeX);
		checkNeighbor(0, -1, 0, faceNegativeY);
		checkNeighbor(0, 0, -1, faceNegativeZ);
	}

	/**
	 * Checks if there is a block at the given relative position and attaches
	 * the given geometry to the blockNode if so or detached it, if not.
	 */
	private void checkNeighbor(int xOffset, int yOffset, int zOffset,
			Geometry dependentGeometry) {
		BlockGameObj checkedBlock = BlockManager.getInstance().getBlock(
				block.blockPositionComponent.x + xOffset,
				block.blockPositionComponent.y + yOffset,
				block.blockPositionComponent.z + zOffset);
		if (checkedBlock == null) {
			blockNode.attachChild(dependentGeometry);
		} else {
			dependentGeometry.removeFromParent();
		}
	}

	/**
	 * Creates a spatial for each side of the block and stores them in the
	 * instance variables.
	 */
	private void initFaceSpatials(AssetManager assetManager) {
		facePositiveX = Jme3Utils.getCubeGeom(.1f, assetManager);
		facePositiveX.setLocalTranslation(.5f, 0, 0);
		facePositiveY = Jme3Utils.getCubeGeom(.1f, assetManager);
		facePositiveY.setLocalTranslation(0, .5f, 0);
		facePositiveZ = Jme3Utils.getCubeGeom(.1f, assetManager);
		facePositiveZ.setLocalTranslation(0, 0, .5f);
		faceNegativeX = Jme3Utils.getCubeGeom(.1f, assetManager);
		faceNegativeX.setLocalTranslation(-.5f, 0, 0);
		faceNegativeY = Jme3Utils.getCubeGeom(.1f, assetManager);
		faceNegativeY.setLocalTranslation(0, -.5f, 0);
		faceNegativeZ = Jme3Utils.getCubeGeom(.1f, assetManager);
		faceNegativeZ.setLocalTranslation(0, 0, -.5f);
	}
}

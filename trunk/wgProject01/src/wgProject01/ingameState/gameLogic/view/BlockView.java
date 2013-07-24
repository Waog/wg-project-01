package wgProject01.ingameState.gameLogic.view;

import wgProject01.Settings;
import wgProject01.ingameState.gameLogic.BlockGameObj;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
	 * The Component which stores the position of this
	 */

	/**
	 * The wrapped block, which is visualized by this view.
	 */
	private BlockGameObj block;

	/**
	 * The wrapped spatial which represents the block of this view.
	 */
	private Spatial spatial;

	/**
	 * The scene graph Node to which this Blocks spatial will be attached to and
	 * detached from.
	 */
	private Node node;

	/**
	 * Constructs a new View for the given block and spatial.
	 * 
	 * @param blockNode
	 *            {@inheritDoc #node} The scene graph Node to which this Blocks
	 *            spatial will be attached to and detached from.
	 */
	public BlockView(BlockGameObj block, Node blockNode, AssetManager assetManager) {
		if (Settings.debugMode < 3) {
			// in release mode create a normal textured block
			Box mesh = new Box(0.5f, 0.5f, 0.5f);
			spatial = new Geometry("Block", mesh);
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
			spatial.setMaterial(shinyStoneMat);
		} else {
			// in debug mode create a transparent block
			Box mesh = new Box(0.5f, 0.5f, 0.5f);
			spatial = new Geometry("Block", mesh);
			spatial.setQueueBucket(Bucket.Transparent);
			Material debugMaterial = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			ColorRGBA randomColor = ColorRGBA.randomColor();
			randomColor.a = 0.5f;
			debugMaterial.setColor("Color", randomColor);
			debugMaterial.getAdditionalRenderState().setBlendMode(
					BlendMode.Alpha); // !
			spatial.setMaterial(debugMaterial);
		}
		
		this.block = block;
		this.node = blockNode;
	}

	/**
	 * Informs this view about an update to the block object. This view then
	 * renders the wrapped block according to the new state in the rendering
	 * thread.
	 */
	public void informBlockChange() {
		// TODO 2: move this code, to be executed in the render thread somehow.
		this.spatial.setLocalTranslation(block.blockPositionComponent.x,
				block.blockPositionComponent.y, block.blockPositionComponent.z);
		if (block.blockPositionComponent.placed) {
			this.node.attachChild(spatial);
		} else {
			this.spatial.removeFromParent();
		}
	}
}

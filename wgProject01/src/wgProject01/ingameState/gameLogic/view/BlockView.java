package wgProject01.ingameState.gameLogic.view;

import wgProject01.ingameState.gameLogic.BlockGameObj;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

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
	public BlockView(BlockGameObj block, Spatial spatial, Node blockNode) {
		this.block = block;
		this.spatial = spatial;
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

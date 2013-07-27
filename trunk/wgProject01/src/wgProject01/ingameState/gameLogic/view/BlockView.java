package wgProject01.ingameState.gameLogic.view;

import wgProject01.ingameState.gameLogic.BlockGameObj;
import wgProject01.ingameState.gameLogic.BlockManager;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

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

		this.blockNode = new Node();
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
		float blockRadius = .5f;
		
		String blockType = block.blockPositionComponent.getType();

		facePositiveX = BlockFaceFactory.getFaceForType(blockType, assetManager);
		facePositiveX.rotate(0, (float) Math.PI / 2f, 0);
		facePositiveX.setLocalTranslation(blockRadius, -blockRadius,
				blockRadius);

		facePositiveY = BlockFaceFactory.getFaceForType(blockType, assetManager);
		facePositiveY.rotate((float) -Math.PI / 2f, 0, 0);
		facePositiveY.setLocalTranslation(-blockRadius, blockRadius,
				blockRadius);

		facePositiveZ = BlockFaceFactory.getFaceForType(blockType, assetManager);
		facePositiveZ.rotate(0, 0, 0);
		facePositiveZ.setLocalTranslation(-blockRadius, -blockRadius,
				blockRadius);

		faceNegativeX = BlockFaceFactory.getFaceForType(blockType, assetManager);
		faceNegativeX.rotate(0, (float) -Math.PI / 2f, 0);
		faceNegativeX.setLocalTranslation(-blockRadius, -blockRadius,
				-blockRadius);

		faceNegativeY = BlockFaceFactory.getFaceForType(blockType, assetManager);
		faceNegativeY.rotate((float) Math.PI / 2f, 0, 0);
		faceNegativeY.setLocalTranslation(-blockRadius, -blockRadius,
				-blockRadius);

		faceNegativeZ = BlockFaceFactory.getFaceForType(blockType, assetManager);
		faceNegativeZ.rotate((float) Math.PI, 0, 0);
		faceNegativeZ.setLocalTranslation(-blockRadius, blockRadius,
				-blockRadius);
	}
}

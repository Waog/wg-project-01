package wgProject01.ingameState;

import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import utils.BetterArray3D;

/**
 * Manages all the {@link BlockGameObj Blocks}. Places them or takes them out of
 * the game. Informs the neighbors of changed blocks about the change, so that
 * they may change their (render) state.
 * 
 * Uses the <a
 * href="http://en.wikipedia.org/wiki/Singleton_pattern">singleton</a> design
 * pattern.
 * 
 * @author oli
 * 
 */
public class BlockManager {

	/**
	 * The singleton instance of the block manager.
	 */
	private static BlockManager singletonInstance;

	/**
	 * The 3 dimensional automatically scaling array of blocks.
	 */
	private BetterArray3D<BlockGameObj> blockArray = new BetterArray3D<BlockGameObj>();

	/**
	 * The Node to which all Blocks have to be attached.
	 */
	private Node node;

	/**
	 * The asset manager of the application.
	 */
	private AssetManager assetManager;

	/**
	 * Returns the singleton instance of the block manager. The
	 * {@link #initData(Node, AssetManager)} method has to be called on this
	 * instance once before using it.
	 */
	static BlockManager getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new BlockManager();
		}

		return singletonInstance;
	}

	/**
	 * Private Constructor to ensure the singleton pattern. Use
	 * {@link #getInstance()} to access an object of this class. Initializes the
	 * block manager.
	 */
	private BlockManager() {
		// nothing
	}

	/**
	 * Initializes the Block Manager with it's needed data. This method needs to
	 * be called once before using the instance of the block manager.
	 * 
	 * @param blockNode
	 *            the Node to which all blocks of the BlockManager shall be
	 *            attached.
	 */
	void initData(Node blockNode, AssetManager assetManager) {
		this.node = blockNode;
		this.assetManager = assetManager;
	}

	/**
	 * Creates and returns a new {@link BlockGameObj Block} game object.
	 */
	BlockGameObj getBlockGameObj() {
		return new BlockGameObj(this.node, this.assetManager);
	}

	/**
	 * Places the given Block at the given coordinates. Removes any Block which
	 * was possibly at the given position before placing the new Block. Informs
	 * all neighbor blocks about the change.
	 */
	void setBlock(int x, int y, int z, BlockGameObj newBlock) {
		// inform the old block, that it's removed
		BlockGameObj oldBlock = blockArray.get(x, y, z);
		if (oldBlock != null) {
			oldBlock.doHandleRemovementFrom();
		}

		// place the block and inform it, that it's placed
		blockArray.set(x, y, z, newBlock);
		newBlock.doHandlePlacementAt(x, y, z);

		// inform the neighbors of the block about the change
		// and the block about it's neighbors
		// loop into all 3 dimmensions...
		for (int xOffset = -1; xOffset <= 1; xOffset++) {
			for (int yOffset = -1; yOffset <= 1; yOffset++) {
				for (int zOffset = -1; zOffset <= 1; zOffset++) {
					// ... check if the current iteration is a direct neighbor
					// or a diagonal neighbor...
					Boolean directNeighbor = ((Math.abs(xOffset)
							+ Math.abs(yOffset) + Math.abs(zOffset)) == 1);
					if (directNeighbor) {
						// ... check if the neighbor exists ...
						BlockGameObj curNeighbor = blockArray.get(x + xOffset,
								y + yOffset, z + zOffset);
						// ... inform the new block anyway about the neighbor...
						newBlock.doHandleNeighborChangeAt(xOffset, yOffset,
								zOffset, curNeighbor);
						if (curNeighbor != null) {
							// ... and inform it if so.
							curNeighbor.doHandleNeighborChangeAt(-xOffset,
									-yOffset, -zOffset, newBlock);
						}
					}
				}
			}
		}
	}

	/**
	 * Returns the block at the given position or null if the given position is
	 * empty.
	 */
	BlockGameObj getBlock(int x, int y, int z) {
		return blockArray.get(x, y, z);

	}

	/**
	 * Like {@link #getBlock(int, int, int)} but with a vector as parameter.
	 * The given vectors coordinates are casted to integers.
	 */
	BlockGameObj getBlock(Vector3f pos) {
		return getBlock((int) pos.x, (int) pos.y, (int) pos.z);

	}
}

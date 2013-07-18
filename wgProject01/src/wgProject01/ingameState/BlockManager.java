package wgProject01.ingameState;

import java.util.ArrayList;
import java.util.List;

import utils.BetterArray3D;

/**
 * Manages all the Blocks. Places them or takes them out of the game. Informs
 * the neighbors of changed blocks about the change, so that they may change
 * their (render) state.
 * 
 * Uses the singleton design pattern.
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
	 * The 3 dimensional automatically scaling Array of blocks.
	 */
	private BetterArray3D<BlockGameObj> blockArray = new BetterArray3D<BlockGameObj>();

	/**
	 * Returns the singleton instance of the block manager.
	 */
	BlockManager getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new BlockManager();
		}

		return singletonInstance;
	}

	/**
	 * Initializes the block manager.
	 */
	private BlockManager() {
	}

	/**
	 * Places the given block at the given coordinates. Overrides any block
	 * which was possibly at the given position. informs all neighbor blocks
	 * about the change.
	 */
	void setBlock(int x, int y, int z, BlockGameObj newBlock) {
		blockArray.set(x, y, z, newBlock);

		// inform the neighbors of the block about the change
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
}

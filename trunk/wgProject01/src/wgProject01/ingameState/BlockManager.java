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
	BetterArray3D<BlockGameObj> blockArray = new BetterArray3D<BlockGameObj>();
	
	/**
	 * Returns the singleton instance of the block manager.
	 */
	public BlockManager getInstance() {
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
}

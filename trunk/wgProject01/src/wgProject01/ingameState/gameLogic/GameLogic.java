package wgProject01.ingameState.gameLogic;

import wgProject01.ingameState.BlockGameObj;
import wgProject01.ingameState.BlockManager;
import wgProject01.ingameState.gameLogic.control.BlockCollisionSystem;
import wgProject01.ingameState.gameLogic.control.SimpleWalkingAiSystem;
import wgProject01.ingameState.gameLogic.utils.EntityFactory;

import com.artemis.World;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

public class GameLogic {
	
	public final static int FLOOR_RADIUS = 10;

	private final static float MAX_SECONDS_PER_UPDATE = 1.0f / 20.0f;

	private World world;

	private Node blockNode;

	private AssetManager assetManager;

	public void doInit(Node blockNode, AssetManager assetManager) {
		this.blockNode = blockNode;
		this.assetManager = assetManager;
		
		initBlockManager();
		initFloor();
		
		world = new World();

		world.setSystem(new SimpleWalkingAiSystem());
		world.setSystem(new BlockCollisionSystem());

		EntityFactory.createEnemy(world, new Vector3f());

		world.initialize();
	}

	public void doCleanup() {
		world.deleteSystem(world.getSystem(SimpleWalkingAiSystem.class));
	}

	public void doUpdate(float secondsDelta) {
		float leftDeltaToProcess = secondsDelta;

		while (leftDeltaToProcess > 0) {
			float curDeltaToProcess = Math.min(leftDeltaToProcess,
					MAX_SECONDS_PER_UPDATE);
			leftDeltaToProcess -= curDeltaToProcess;

			world.setDelta(curDeltaToProcess);
			world.process();
		}
	}
	
	/**
	 * Initializes the block Manager and some blocks for testing.
	 */
	private void initBlockManager() {
		// initialize the block manager
		BlockManager blockManager = BlockManager.getInstance();
		// rootNode.attachChild(blockNode);
		blockManager.initData(blockNode, assetManager);
	}
	
	/**
	 * initializes a quadratic floor consisting of blocks, FLOOR_RADIUS defines
	 * its size
	 */
	private void initFloor() {
		for (int x = -FLOOR_RADIUS; x <= FLOOR_RADIUS; x++) {
			for (int z = -FLOOR_RADIUS; z <= FLOOR_RADIUS; z++) {
				addBlockAt(x, -2, z);
				addBlockAt(x, -3, z);
				// addBlockAt(x, -2, z);

				if (Math.abs(x) >= FLOOR_RADIUS - 2
						|| Math.abs(z) >= FLOOR_RADIUS - 1) {
					addBlockAt(x, -1, z);
					addBlockAt(x, 0, z);
					addBlockAt(x, 1, z);
				}
			}
		}
	}
	
	/**
	 * adds a block at the specific position (x,y,z)
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @param z
	 *            the z-coordinate
	 */
	private void addBlockAt(int x, int y, int z) {
		BlockGameObj newBlock = BlockManager.getInstance().getBlockGameObj();
		BlockManager.getInstance().setBlock(x, y, z, newBlock);
	}
}

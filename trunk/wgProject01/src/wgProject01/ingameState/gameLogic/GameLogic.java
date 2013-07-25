package wgProject01.ingameState.gameLogic;

import wgProject01.Settings;
import wgProject01.ingameState.gameLogic.systems.BlockCollisionSystem;
import wgProject01.ingameState.gameLogic.systems.GravitationSystem;
import wgProject01.ingameState.gameLogic.systems.PlayerControlSystem;
import wgProject01.ingameState.gameLogic.systems.OrbitingSystem;
import wgProject01.ingameState.gameLogic.systems.SimpleWalkingAiSystem;
import wgProject01.ingameState.gameLogic.utils.EntityFactory;

import com.artemis.World;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 * Class which manages the global game logic, initializes the world and entity
 * systems.
 * 
 * @author oli
 * 
 */
public class GameLogic {

	/**
	 * The "radius" of the initial Block generation.
	 */
	public final static int FLOOR_RADIUS = 10;

	/**
	 * This value defins the maximal time delta which game logic updates have to
	 * handle.
	 * 
	 * Greater time deltas are automatically splitted up into multiple smaller
	 * update calls by this class.
	 */
	private final static float MAX_SECONDS_PER_UPDATE = 1.0f / 20.0f;

	/**
	 * The {@link World} object, to globally manage our entity system framework.
	 */
	private World world;

	/**
	 * The JME3 {@link Node} to which blocks are attached.
	 */
	private Node blockNode;

	/**
	 * The JME3 {@link AssetManager}.
	 */
	private AssetManager assetManager;

	private Node rootNode;

	/**
	 * Initializes the game logic and entity systems.
	 */
	public void doInit(Node rootNode, Node blockNode, AssetManager assetManager) {
		this.rootNode = rootNode;
		this.blockNode = blockNode;
		this.assetManager = assetManager;

		initBlockManager();
		initFloor();

		world = new World();
		
		world.setSystem(new PlayerControlSystem());
		world.setSystem(new SimpleWalkingAiSystem());
		world.setSystem(new OrbitingSystem());
		world.setSystem(new GravitationSystem());
		world.setSystem(new BlockCollisionSystem());

		EntityFactory.createEnemy(this.rootNode, world, new Vector3f(0, 5, 0));
		EntityFactory.createPlayer(this.rootNode, world, new Vector3f(1,10,1));
		for (int i = 0; i <= Settings.debugMode; i++) {
			EntityFactory.createSun(this.rootNode, world);
		}

		world.initialize();
		initTestingStuff();
	}

	/**
	 * Cleans up the game logic and entity systems. Not properly tested
	 */
	public void doCleanup() {
		world.deleteSystem(world.getSystem(SimpleWalkingAiSystem.class));
		world.deleteSystem(world.getSystem(BlockCollisionSystem.class));
		world.deleteSystem(world.getSystem(GravitationSystem.class));
		world.deleteSystem(world.getSystem(OrbitingSystem.class));
		world.deleteSystem(world.getSystem(PlayerControlSystem.class));
	}

	/**
	 * This method updates the whole game logic. The given secondsDelta is the
	 * ingame time the game logic shall be progressed.
	 * 
	 * Internally the given time delta may be split into smaller parts and
	 * entity system is updated multiple time with the small time deltas.
	 */
	public void doUpdate(float secondsDelta) {
		long time = 0;
		if(Settings.debugMode > 0 )  time = System.nanoTime();
		float leftDeltaToProcess = secondsDelta;

		while (leftDeltaToProcess > 0) {
			float curDeltaToProcess = Math.min(leftDeltaToProcess,
					MAX_SECONDS_PER_UPDATE);
			leftDeltaToProcess -= curDeltaToProcess;

			if (Settings.debugMode >= 1) {
				System.out.println("update with delta: " + curDeltaToProcess);
			}
			world.setDelta(curDeltaToProcess);
			world.process();
		}
		if(Settings.debugMode > 0 ) System.out.println("GameLogic update total used time: " + (System.nanoTime() - time) * 0.000000001);
	}

	/**
	 * Initializes the block Manager.
	 */
	private void initBlockManager() {
		// initialize the block manager
		BlockManager blockManager = BlockManager.getInstance();
		// rootNode.attachChild(blockNode);
		blockManager.initData(blockNode, assetManager);
	}

	/**
	 * Initializes a quadratic floor and walls consisting of blocks.
	 * {@link GameLogic#FLOOR_RADIUS} defines its size.
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
	 * for testing
	 */
	private void initTestingStuff() {
		if (Settings.debugMode < 2) {
			return;
		}
		
		// nothing
	}

	/**
	 * Adds a block at the specific position (x,y,z), using the
	 * {@link BlockManager}.
	 */
	private void addBlockAt(int x, int y, int z) {
		BlockGameObj newBlock = BlockManager.getInstance().getBlockGameObj();
		BlockManager.getInstance().setBlock(x, y, z, newBlock);
	}
}

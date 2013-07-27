package wgProject01.ingameState.gameLogic.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Pair;
import wgProject01.ingameState.gameLogic.BlockGameObj;
import wgProject01.ingameState.gameLogic.BlockManager;
import wgProject01.ingameState.gameLogic.components.DirectionComponent;
import wgProject01.ingameState.gameLogic.components.PlayerControlComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;
import wgProject01.ingameState.gameLogic.utils.EntityFactory;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.bulletphysics.collision.dispatch.PairCachingGhostObject;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * An entity (processing) system handling the given user commands. The commands
 * are managed with a {@link Map} from Strings to boolean flags.
 * 
 * @author Mirco
 * 
 */
public class PlayerControlSystem extends EntityProcessingSystem {

	/** names for commands */
	/** the digital commands */
	/** for moving left */
	public static final String LEFT = "Left";
	/** for moving right */
	public static final String RIGHT = "Right";
	/** for moving down */
	public static final String BACK = "Back";
	/** for moving up */
	public static final String FORWARD = "Forward";

	/** the analogue commands */
	/** mouse movement in negative x-direction, i.e. to the left */
	public static final String MOUSE_LEFT = "MouseLeft";
	/** mouse movement in positive x-direction, i.e. to the right */
	public static final String MOUSE_RIGHT = "MouseRight";
	/** mouse movement in positive y-direction, i.e. upwards */
	public static final String MOUSE_UP = "MouseUp";
	/** mouse movement in negative x-direction, i.e. downwards */
	public static final String MOUSE_DOWN = "MouseDown";

	public static float turnHorizontal = 0;
	public static float turnVertical = 0;

	/** the {@link Map} mapping the keys given as Strings to boolean values */
	public static Map<String, Boolean> mapper = new HashMap<String, Boolean>();

	/**
	 * Automagical creation of a ComponentMapper to extract a component from the
	 * entities.
	 */
	@Mapper
	ComponentMapper<PositionComponent> positionManager;

	/**
	 * Automagical creation of a ComponentMapper to extract a component from the
	 * entities.
	 */
	@Mapper
	ComponentMapper<PlayerControlComponent> playerControlManager;
	/**
	 * 
	 * Automagical creation of a ComponentMapper to extract a component from the
	 * entities.
	 */
	@Mapper
	ComponentMapper<DirectionComponent> directionComponentManager;
	
	//TODO 2 remove debug crab
	private Entity smallDebugCube;

	/**
	 * constructs a new PlayerControlSystem for all Entities that have a
	 * InputReactionComponent and a PositionComponent
	 */
	@SuppressWarnings("unchecked")
	public PlayerControlSystem() {
		super(Aspect.getAspectForAll(PlayerControlComponent.class,
				PositionComponent.class, DirectionComponent.class));
	}
	
	@Override
	protected void initialize() {
		super.initialize();
		this.smallDebugCube = EntityFactory.createSmallCube( world, new Vector3f());
	}

	/**
	 * <p>
	 * The Artemis framework calls this method automatically once every time
	 * {@link World#process()} is called.
	 * </p>
	 * 
	 * <p>
	 * Works off the Commands given to the entity
	 * </p>
	 */
	@Override
	protected void process(Entity e) {
		// prepare some variables
		float delta = world.getDelta();
		PositionComponent positionComponent = positionManager.get(e);
		DirectionComponent directionComponent = directionComponentManager
				.get(e);
		PlayerControlComponent playerComponent = playerControlManager.get(e);

		// handle movement and rotation
		doHandleTranslation(delta, positionComponent, directionComponent,
				playerComponent);
		doHandleRotation(directionComponent);

		// determine blocks on player ray
		// pairs of lengthes of dir'n and collision points with blocks
		List<Pair<Float, Vector3f>> collisionPointList = new ArrayList<Pair<Float, Vector3f>>();
		float lengthOfRay = 6;
		Vector3f dir = directionComponent.getSwitchedCartesianDirection();

		Vector3f endOfRay = positionComponent.pos.add(dir.mult(lengthOfRay));
		fillCollisionPointList(positionComponent, collisionPointList, dir,
				endOfRay, new Vector3f(1,0,0));

		fillCollisionPointList(positionComponent, collisionPointList, dir,
				endOfRay, new Vector3f(0,1,0));

		fillCollisionPointList(positionComponent, collisionPointList, dir,
				endOfRay, new Vector3f(0,0,1));

		// find closest collision point
		Pair<Float, Vector3f> closestPair = null;
		if (collisionPointList.size() != 0) {
			closestPair = collisionPointList.get(0);

		}
		for (Pair<Float, Vector3f> pair : collisionPointList) {
			if (pair.first < closestPair.first) {
				closestPair = pair;
			}
		}
		BlockGameObj collidingBlock = null;
		if (closestPair != null) {
			collidingBlock = BlockManager.getInstance().getBlock(
					closestPair.second);
			smallDebugCube.getComponent(PositionComponent.class).pos = closestPair.second;
		}
		
	}

	/**
	 * @param positionComponent
	 * @param blocksOnRayList
	 * @param dir
	 * @param endOfRay
	 */
	private void fillCollisionPointList(PositionComponent positionComponent,
			List<Pair<Float, Vector3f>> blocksOnRayList, Vector3f dir,
			Vector3f endOfRay, Vector3f axis) {
		float minCoord = Math.min(positionComponent.pos.dot(axis), endOfRay.dot(axis));
		float maxCoord = Math.max(positionComponent.pos.dot(axis), endOfRay.dot(axis));

		int roundedMinCoord = Math.round(minCoord);
		int roundedMaxCoord = Math.round(maxCoord);

		// calculates collision points and length from player to collision point
		for (int curRoundedCoord = roundedMinCoord; curRoundedCoord < roundedMaxCoord; curRoundedCoord++) {
			float curBorderCoord = curRoundedCoord + 0.5f;
			float walkParameter = (curBorderCoord - positionComponent.pos.dot(axis))
					/ dir.dot(axis);

			// define collision point
			Vector3f collisionPoint = new Vector3f();
			collisionPoint.x = positionComponent.pos.x + walkParameter * dir.x;
			collisionPoint.y = positionComponent.pos.y + walkParameter * dir.y;
			collisionPoint.z = positionComponent.pos.z + walkParameter * dir.z;
			// move the collision point slightly to leave block border
			collisionPoint.addLocal(dir.mult(0.0001f));
			BlockGameObj collidingBlock = BlockManager.getInstance().getBlock(
					collisionPoint);
			if (collidingBlock != null) {
				Pair<Float, Vector3f> blockOnRayPair = new Pair<Float, Vector3f>(
						walkParameter, collisionPoint);
				blocksOnRayList.add(blockOnRayPair);
			}
		}
	}

	/**
	 * changes the direction according to the corresponding turning commands.
	 */
	private void doHandleRotation(DirectionComponent directionComponent) {
		if (turnHorizontal != 0) {
			Vector2f curDirection = directionComponent.getSphericalDirection();
			Vector2f newDirection = new Vector2f(curDirection.x, curDirection.y
					- turnHorizontal);
			directionComponent.setSphericalDirection(newDirection);
			turnHorizontal = 0; // reset the turning
		}
		if (turnVertical != 0) {
			Vector2f curDirection = directionComponent.getSphericalDirection();
			float newTheta = curDirection.x - turnVertical;
			newTheta = Math.max(newTheta, 0.0001f);
			newTheta = Math.min(newTheta, (float) Math.PI - 0.0001f);
			Vector2f newDirection = new Vector2f(newTheta, curDirection.y);
			directionComponent.setSphericalDirection(newDirection);
			turnVertical = 0; // reset the turning
		}
	}

	/**
	 * changes position according to the corresponding command flags.
	 */
	private void doHandleTranslation(float delta,
			PositionComponent positionComponent,
			DirectionComponent directionComponent,
			PlayerControlComponent playerComponent) {
		if (getMappedValue(LEFT)) {
			Vector3f directionXZ = directionComponent
					.getSwitchedCatesianProjectedDirectionXZ();
			Vector3f directionLeft = directionXZ.cross(new Vector3f(0, 1, 0))
					.mult(-1f);
			directionLeft.normalizeLocal();
			Vector3f positionDelta = directionLeft.mult(playerComponent.speed
					* delta);
			positionComponent.pos.addLocal(positionDelta);
		}
		if (getMappedValue(RIGHT)) {
			Vector3f directionXZ = directionComponent
					.getSwitchedCatesianProjectedDirectionXZ();
			Vector3f directionRight = directionXZ.cross(new Vector3f(0, 1, 0));
			directionRight.normalizeLocal();
			Vector3f positionDelta = directionRight.mult(playerComponent.speed
					* delta);
			positionComponent.pos.addLocal(positionDelta);
		}
		if (getMappedValue(BACK)) {
			Vector3f positionDelta = directionComponent
					.getSwitchedCatesianProjectedDirectionXZ().mult(
							-1 * playerComponent.speed * delta);
			positionComponent.pos.addLocal(positionDelta);
		}
		if (getMappedValue(FORWARD)) {
			Vector3f positionDelta = directionComponent
					.getSwitchedCatesianProjectedDirectionXZ().mult(
							playerComponent.speed * delta);
			positionComponent.pos.addLocal(positionDelta);
		}
	}

	/**
	 * returns the mapped value for the given key from the mapper variable or
	 * false if the given key is not defined
	 */
	private boolean getMappedValue(String key) {
		return (mapper.get(key) != null && mapper.get(key));
	}
}

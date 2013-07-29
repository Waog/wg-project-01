package wgProject01.ingameState.gameLogic.systems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/** The reach of the player in meters. */
	private static final float PLAYER_RANGE = 7f;

	// names for commands
	// the digital commands
	/** command for moving left */
	public static final String LEFT = "LEFT";
	/** command for moving right */
	public static final String RIGHT = "RIGHT";
	/** command for moving down */
	public static final String BACK = "BACK";
	/** command for moving up */
	public static final String FORWARD = "FORWARD";
	/** command to pick focused block */
	public static final String PICK_BLOCK = "PICK_BLOCK";
	/** command to place a block onto the focused block's face */
	public static final String PLACE_BLOCK = "PLACE_BLOCK";

	/**
	 * command for turning players direction horizontally - negative values
	 * indicate turning left, positive values indicate turning right.
	 */
	public static float turnHorizontal = 0;
	/**
	 * command for turning players direction vertically - negative values
	 * indicate turning downwards, positive values indicate turning upwards.
	 */
	public static float turnVertical = 0;

	/**
	 * the {@link Map} mapping the keys given as Strings to boolean values. This
	 * class should use the method
	 * {@link PlayerControlSystem#getMappedValue(String)} to access the values
	 * of the Map.
	 */
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

	/**
	 * The entity which is attached to the currently focused face of a block.
	 */
	private Entity hightlightEntity;

	/**
	 * constructs a new PlayerControlSystem for all Entities that have a
	 * {@link PlayerControlComponent}, a {@link PositionComponent} and a
	 * {@link DirectionComponent}.
	 */
	@SuppressWarnings("unchecked")
	public PlayerControlSystem() {
		super(Aspect.getAspectForAll(PlayerControlComponent.class,
				PositionComponent.class, DirectionComponent.class));
	}

	/**
	 * Creates the highlighter of focused blocks as an entity.
	 */
	@Override
	protected void initialize() {
		super.initialize();
		this.hightlightEntity = EntityFactory.createBlockFaceHighlight(world);
	}

	/**
	 * <p>
	 * The Artemis framework calls this method automatically once every time
	 * {@link World#process()} is called.
	 * </p>
	 * 
	 * <p>
	 * Works off the Commands given to this system.
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

		// pick block if command is set
		doPickBlock(positionComponent, directionComponent, playerComponent);

		// place block from inventory stack (if possible) if command is set
		doPlaceBlock(positionComponent, directionComponent, playerComponent);

		// highlight focused block face
		doHighlightBlockFace(positionComponent, directionComponent);
	}

	/**
	 * checks if the key of the {@link #PLACE_BLOCK} command in the
	 * {@link mapper} is true and if there is a block in the inventory, places a
	 * block at the focused block face if within reach.
	 */
	private void doPlaceBlock(PositionComponent positionComponent,
			DirectionComponent directionComponent,
			PlayerControlComponent playerComponent) {
		if (getMappedValue(PLACE_BLOCK)
				&& playerComponent.inventoryStack.size() > 0) {
			Pair<Float, Vector3f> closestRayCollisionPair = getClosestRayCollision(
					positionComponent, directionComponent);
			if (closestRayCollisionPair != null) {
				Vector3f collisionPointDirectionFromPlayer = directionComponent
						.getSwitchedCartesianDirection().mult(
								closestRayCollisionPair.first - 0.0001f);
				Vector3f newBlockPositionNonRounded = positionComponent.pos
						.add(collisionPointDirectionFromPlayer);
				if (BlockManager.getInstance().getBlock(
						newBlockPositionNonRounded) == null) {
					BlockManager.getInstance().setBlock(
							newBlockPositionNonRounded,
							playerComponent.inventoryStack.pop());
				}
			}
		}
		mapper.put(PLACE_BLOCK, false);
	}

	/**
	 * checks if the key of the {@link #PICK_BLOCK} command in the
	 * {@link mapper} is true and picks the focused block to the inventory if
	 * there is one within reach.
	 * 
	 */
	private void doPickBlock(PositionComponent positionComponent,
			DirectionComponent directionComponent,
			PlayerControlComponent playerComponent) {
		if (getMappedValue(PICK_BLOCK)) {
			// determine blocks on player ray
			Pair<Float, Vector3f> closestRayCollisionPair = getClosestRayCollision(
					positionComponent, directionComponent);
			if (closestRayCollisionPair != null) {
				BlockGameObj focusedBlock = BlockManager.getInstance()
						.getBlock(closestRayCollisionPair.second);
				playerComponent.inventoryStack.push(focusedBlock);
				BlockManager.getInstance().removeBlockFrom(
						closestRayCollisionPair.second);
			}
		}
		mapper.put(PICK_BLOCK, false);
	}

	/**
	 * @return First value of the pair is the exact distances of the player to
	 *         the real collision points. The second value of the pair is the
	 *         collision point vector slightly moved into the block the ray is
	 *         colliding with.
	 */
	private Pair<Float, Vector3f> getClosestRayCollision(
			PositionComponent positionComponent,
			DirectionComponent directionComponent) {
		// pairs of lengthes of dir'n and collision points with blocks
		List<Pair<Float, Vector3f>> collisionPointList = new ArrayList<Pair<Float, Vector3f>>();
		doFillCollisionPointList(collisionPointList, new Vector3f(1, 0, 0),
				positionComponent, directionComponent);

		doFillCollisionPointList(collisionPointList, new Vector3f(0, 1, 0),
				positionComponent, directionComponent);

		doFillCollisionPointList(collisionPointList, new Vector3f(0, 0, 1),
				positionComponent, directionComponent);

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

		return closestPair;
	}

	/**
	 * Calculates the collision points with blocks (only those in the
	 * perpendicular plane to the given axis) and the distance of it to the
	 * player and adds the corresponding pair to the given list (see return
	 * value).
	 * 
	 * @param collisionPointList
	 *            The list to be filled with pairs of the distance to collision
	 *            point and the collision point with block.
	 * @param axis
	 *            One of the three standard basis vectors ((1,0,0) , (0,1,0) or
	 *            (0,0,1)).
	 * @param positionComponent
	 *            The position component of the entity.
	 * @param directionComponent
	 *            The direction component of the entity.
	 * @return First value of the pairs are the exact distances of the player to
	 *         the real collision points. The second value of the pairs are the
	 *         collision point vectors slightly moved into the block the ray is
	 *         colliding with.
	 * 
	 */
	private void doFillCollisionPointList(
			List<Pair<Float, Vector3f>> collisionPointList, Vector3f axis,
			PositionComponent positionComponent,
			DirectionComponent directionComponent) {
		Vector3f dir = directionComponent.getSwitchedCartesianDirection();

		Vector3f endOfRay = positionComponent.pos.add(dir.mult(PLAYER_RANGE));

		float minCoord = Math.min(positionComponent.pos.dot(axis),
				endOfRay.dot(axis));
		float maxCoord = Math.max(positionComponent.pos.dot(axis),
				endOfRay.dot(axis));

		int roundedMinCoord = Math.round(minCoord);
		int roundedMaxCoord = Math.round(maxCoord);

		// calculates collision points and length from player to collision point
		for (int curRoundedCoord = roundedMinCoord; curRoundedCoord < roundedMaxCoord; curRoundedCoord++) {
			float curBorderCoord = curRoundedCoord + 0.5f;
			float walkParameter = (curBorderCoord - positionComponent.pos
					.dot(axis)) / dir.dot(axis);

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
				collisionPointList.add(blockOnRayPair);
			}
		}
	}

	/**
	 * Changes the direction according to the corresponding turning commands.
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
	 * Changes position according to the corresponding command flags.
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
	 * Returns the mapped value for the given key from the {@link mapper} or
	 * false if the given key is not defined.
	 */
	private boolean getMappedValue(String key) {
		return (mapper.get(key) != null && mapper.get(key));
	}

	/**
	 * Highlights the focused block face if the face is within
	 * {@link #PLAYER_RANGE} by translating and turning and making (in-)visible
	 * the highlight entity.
	 */
	private void doHighlightBlockFace(PositionComponent positionComponent,
			DirectionComponent directionComponent) {
		Pair<Float, Vector3f> closestCollisionPair = getClosestRayCollision(
				positionComponent, directionComponent);
		PositionComponent highlightPosComp = hightlightEntity
				.getComponent(PositionComponent.class);
		DirectionComponent highlightDirComp = hightlightEntity
				.getComponent(DirectionComponent.class);
		if (closestCollisionPair == null) {
			highlightPosComp.visible = false;
			return;
		} else {
			highlightPosComp.visible = true;
		}

		Vector3f collisionPoint = closestCollisionPair.second;

		// get the geometry of the hit spatial that is closest to the player
		BlockGameObj hitBlock = BlockManager.getInstance().getBlock(
				collisionPoint);
		Vector3f hitBlockPos = hitBlock.blockPositionComponent.getPosVec3f();

		Vector3f vectorFromBlockPosToFacePos = getVectorFromBlockPosToFacePos(
				hitBlockPos, collisionPoint);

		highlightPosComp.pos = hitBlockPos.add(vectorFromBlockPosToFacePos);
		highlightDirComp
				.setSwitchedCartesianDirection(vectorFromBlockPosToFacePos);
	}

	/**
	 * Returns the vector pointing from the center of the block at the given
	 * position to the center of the block's face containing the given collision
	 * point.
	 * 
	 * @param hitBlockPos
	 *            Center of the focused block.
	 * @param collisionPoint
	 *            First intersection point of the ray with the focused block's
	 *            face.
	 */
	private Vector3f getVectorFromBlockPosToFacePos(Vector3f hitBlockPos,
			Vector3f collisionPoint) {
		// calculate unit vectors manually, because the
		// JME3s are not constant for some reason...
		Vector3f UNIT_X = new Vector3f(1, 0, 0);
		Vector3f UNIT_Y = new Vector3f(0, 1, 0);
		Vector3f UNIT_Z = new Vector3f(0, 0, 1);

		// calculate the vector pointing from the middle of the block to
		// the contactPoint
		Vector3f centerToHitPointVec = collisionPoint.subtract(hitBlockPos);

		Vector3f projectedX = centerToHitPointVec.project(UNIT_X);
		Vector3f projectedY = centerToHitPointVec.project(UNIT_Y);
		Vector3f projectedZ = centerToHitPointVec.project(UNIT_Z);

		// get longest of the three projections
		if (projectedX.length() > projectedY.length()
				&& projectedX.length() > projectedZ.length()) {
			return projectedX;
		} else if (projectedY.length() > projectedX.length()
				&& projectedY.length() > projectedZ.length()) {
			return projectedY;
		} else {
			return projectedZ;
		}
	}
}

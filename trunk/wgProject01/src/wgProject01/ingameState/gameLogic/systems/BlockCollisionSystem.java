package wgProject01.ingameState.gameLogic.systems;

import wgProject01.ingameState.BlockGameObj;
import wgProject01.ingameState.BlockManager;
import wgProject01.ingameState.gameLogic.components.CollisionBoxComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.jme3.math.Vector3f;

/**
 * <p>
 * This is an entity system to handle collision of entities with the terrain
 * {@link BlockGameObj Blocks} with very high performance.
 * </p>
 * 
 * <p>
 * A {@link BlockManager} must be initialized before using this class.
 * </p>
 * 
 * @author oli
 * 
 */
public class BlockCollisionSystem extends EntityProcessingSystem {

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
	ComponentMapper<CollisionBoxComponent> collisionBoxManager;

	/**
	 * <p>
	 * Create a new BlockCollision instance, which handles block collision of
	 * the entities if appropriate.
	 * </p>
	 * <p>
	 * Like all EntitySystems the constructed instance must be attached to a
	 * {@link World} to work.
	 * </p>
	 * 
	 * <p>
	 * A {@link BlockManager} must be initialized before using this class.
	 * </p>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public BlockCollisionSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class,
				CollisionBoxComponent.class));
	}

	/**
	 * <p>
	 * The Artemis framework calls this method automatically once every time
	 * {@link World#process()} is called.
	 * </p>
	 * 
	 * <p>
	 * Checks if the entities collision box is colliding with any blocks of the
	 * {@link BlockManager} and "pushes" the entity out of them, if so.
	 * </p>
	 */
	@Override
	protected void process(Entity e) {
		// extract needed components from entity
		PositionComponent positionComponent = positionManager.get(e);
		CollisionBoxComponent collisionBoxComponent = collisionBoxManager
				.get(e);

		// handle up to 3 block collisions or abort if no collision occured.
		// Attention: to the loops break condition.
		Boolean collisionDedected = true;
		for (int i = 1; i <= 3 && collisionDedected; i++) {
			collisionDedected = false;
			Vector3f spatialPos = positionComponent.pos;

			// calculate the minimal and maximal block positions of the
			// collision test.
			int minX = (int) Math.floor(spatialPos.x
					- collisionBoxComponent.radii.x + .5f);
			int maxX = (int) Math.ceil(spatialPos.x
					+ collisionBoxComponent.radii.x - .5f);
			int minY = (int) Math.floor(spatialPos.y
					- collisionBoxComponent.radii.y + .5f);
			int maxY = (int) Math.ceil(spatialPos.y
					+ collisionBoxComponent.radii.y - .5f);
			int minZ = (int) Math.floor(spatialPos.z
					- collisionBoxComponent.radii.z + .5f);
			int maxZ = (int) Math.ceil(spatialPos.z
					+ collisionBoxComponent.radii.z - .5f);

			// find the block with the maximal intersection volume with the
			// spatial.
			float maxIntersectionVolum = 0f;
			int maxIntersectionIndexX = 0;
			int maxIntersectionIndexY = 0;
			int maxIntersectionIndexZ = 0;
			for (int curX = minX; curX <= maxX; curX++) {
				for (int curY = minY; curY <= maxY; curY++) {
					for (int curZ = minZ; curZ <= maxZ; curZ++) {
						float curIntersectionVol = getIntersectionVolumWithBlockAt(
								positionComponent, collisionBoxComponent, curX,
								curY, curZ);
						if (curIntersectionVol > maxIntersectionVolum) {
							maxIntersectionVolum = curIntersectionVol;
							maxIntersectionIndexX = curX;
							maxIntersectionIndexY = curY;
							maxIntersectionIndexZ = curZ;
						}
					}
				}
			}

			// handle the collision with the maximal volume, if one occured.
			if (maxIntersectionVolum > 0f) {
				handleCollisionAt(positionComponent, collisionBoxComponent,
						maxIntersectionIndexX, maxIntersectionIndexY,
						maxIntersectionIndexZ);
				collisionDedected = true;
			}
		}
	}

	/**
	 * Returns the intersection volume of the entites collision box and the
	 * block at the given position. Returns 0 if there is no block or no
	 * collision.
	 */
	private float getIntersectionVolumWithBlockAt(
			PositionComponent positionComponent,
			CollisionBoxComponent collisionBoxComponent, int x, int y, int z) {
		BlockGameObj block = BlockManager.getInstance().getBlock(x, y, z);

		// return 0 if there is no Block at the given position.
		if (block == null) {
			return 0;
		}

		// initialize some variables.
		Vector3f blockPos = new Vector3f(x, y, z);
		Vector3f lowerBlockBorders = blockPos.subtract(new Vector3f(.5f, .5f,
				.5f));
		Vector3f upperBlockBorders = blockPos.add(new Vector3f(.5f, .5f, .5f));
		Vector3f lowerSpatialBorders = positionComponent.pos
				.subtract(collisionBoxComponent.radii);
		Vector3f upperSpatialBorders = positionComponent.pos
				.add(collisionBoxComponent.radii);

		// calculate the intersections in each direction seperatly.
		float xIntersect = Math.max(
				0,
				Math.min(upperBlockBorders.x, upperSpatialBorders.x)
						- Math.max(lowerBlockBorders.x, lowerSpatialBorders.x));
		float yIntersect = Math.max(
				0,
				Math.min(upperBlockBorders.y, upperSpatialBorders.y)
						- Math.max(lowerBlockBorders.y, lowerSpatialBorders.y));
		float zIntersect = Math.max(
				0,
				Math.min(upperBlockBorders.z, upperSpatialBorders.z)
						- Math.max(lowerBlockBorders.z, lowerSpatialBorders.z));

		// multiply and return the three 1D-intersections
		return xIntersect * yIntersect * zIntersect;
	}

	/**
	 * Moves the entity, so that it's collision box is not colliding with the
	 * block at the given position anymore.
	 * <p>
	 * The entity is moved the shortest possible way possible to reach a non
	 * colliding position.
	 * </p>
	 */
	private void handleCollisionAt(PositionComponent positionComponent,
			CollisionBoxComponent collisionBoxComponent, int x, int y, int z) {
		// determine the shortest way out of the block ...
		float shortestDistOut = Float.MAX_VALUE;
		Vector3f finalLocalTranslation = positionComponent.pos.clone();

		// collision with higher x coordinate
		float spatialX = positionComponent.pos.x;
		float lowerSpatialX = spatialX - collisionBoxComponent.radii.x;
		if (lowerSpatialX < x + .5f && spatialX > x) {
			Vector3f curLocalTranslation = positionComponent.pos.clone();
			Vector3f newLocalTranslation = positionComponent.pos.clone();
			newLocalTranslation.x = x + .5f + collisionBoxComponent.radii.x;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}
		// collision with lower x coordinate
		spatialX = positionComponent.pos.x;
		float upperSpatialX = spatialX + collisionBoxComponent.radii.x;
		if (upperSpatialX > x - .5f && spatialX < x) {
			Vector3f curLocalTranslation = positionComponent.pos.clone();
			Vector3f newLocalTranslation = positionComponent.pos.clone();
			newLocalTranslation.x = x - .5f - collisionBoxComponent.radii.x;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}

		// collision with higher y coordinate
		float spatialY = positionComponent.pos.y;
		float lowerSpatialY = spatialY - collisionBoxComponent.radii.y;
		if (lowerSpatialY < y + .5f && spatialY > y) {
			Vector3f curLocalTranslation = positionComponent.pos.clone();
			Vector3f newLocalTranslation = positionComponent.pos.clone();
			newLocalTranslation.y = y + .5f + collisionBoxComponent.radii.y;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}

		// collision with lower y coordinate
		spatialY = positionComponent.pos.y;
		float upperSpatialY = spatialY + collisionBoxComponent.radii.y;
		if (upperSpatialY > y - .5f && spatialY < y) {

			Vector3f curLocalTranslation = positionComponent.pos.clone();
			Vector3f newLocalTranslation = positionComponent.pos.clone();
			newLocalTranslation.y = y - .5f - collisionBoxComponent.radii.y;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}
		// collision with higher z coordinate
		float spatialZ = positionComponent.pos.z;
		float lowerSpatialZ = spatialZ - collisionBoxComponent.radii.z;
		if (lowerSpatialZ < z + .5f && spatialZ > z) {
			Vector3f curLocalTranslation = positionComponent.pos.clone();
			Vector3f newLocalTranslation = positionComponent.pos.clone();
			newLocalTranslation.z = z + .5f + collisionBoxComponent.radii.z;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}

		// collision with lower z coordinate
		spatialZ = positionComponent.pos.z;
		float upperSpatialZ = spatialZ + collisionBoxComponent.radii.z;
		if (upperSpatialZ > z - .5f && spatialZ < z) {
			Vector3f curLocalTranslation = positionComponent.pos.clone();
			Vector3f newLocalTranslation = positionComponent.pos.clone();
			newLocalTranslation.z = z - .5f - collisionBoxComponent.radii.z;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}
		positionComponent.pos.set(finalLocalTranslation);
	}
}
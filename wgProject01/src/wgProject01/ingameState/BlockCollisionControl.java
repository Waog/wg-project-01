package wgProject01.ingameState;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * This is a {@link com.jme3.scene.control.Control Control} to handle collision
 * with the terrain {@link BlockGameObj Blocks} in a very performant way.
 * 
 * <p>
 * Attach it to any spatial which is not allowed to intersect with blocks. A
 * {@link BlockManager} must be initialized before using this class.
 * </p>
 * 
 * @author oli
 * 
 */
class BlockCollisionControl extends AbstractControl {

	/**
	 * The size of the spatials collision box. For example the spatials
	 * collision area in x direction is from</br>
	 * <code>(spatial.getLocalTranslation() - radii.x)</code> to</br>
	 * <code>(spatial.getLocalTranslation() + radii.x)</code>.
	 */
	private Vector3f radii;

	/**
	 * Creates a new control with a collision box of the given size.
	 * <p>
	 * A {@link BlockManager} must be initialized before using this class.
	 * </p>
	 * 
	 */
	BlockCollisionControl(Vector3f spacialCollisionSize) {
		this.radii = spacialCollisionSize.mult(0.5f);
	}

	/**
	 * JME3 calls this method automatically if the control is attached to a
	 * spatial. Initializes the Control.
	 */
	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
	}

	/**
	 * JME3 calls this method automatically every frame. Checks if the spatials
	 * collision box is colliding with any blocks of the {@link BlockManager}
	 * and "pushes" the spatial out of them, if so.
	 */
	@Override
	protected void controlUpdate(float tpf) {
		// handle up to 3 block collisions or abort if no collision occured.
		// Attention: to the loops break condition.
		Boolean collisionDedected = true;
		for (int i = 1; i <= 3 && collisionDedected; i++) {
			collisionDedected = false;
			Vector3f spatialPos = spatial.getWorldTranslation();

			// calculate the minimal and maximal block positions of the
			// collision test.
			int minX = (int) Math.floor(spatialPos.x - radii.x + .5f);
			int maxX = (int) Math.ceil(spatialPos.x + radii.x - .5f);
			int minY = (int) Math.floor(spatialPos.y - radii.y + .5f);
			int maxY = (int) Math.ceil(spatialPos.y + radii.y - .5f);
			int minZ = (int) Math.floor(spatialPos.z - radii.z + .5f);
			int maxZ = (int) Math.ceil(spatialPos.z + radii.z - .5f);

			// find the block with the maximal intersection volum with the
			// spatial.
			float maxIntersectionVolum = 0f;
			int maxIntersectionIndexX = 0;
			int maxIntersectionIndexY = 0;
			int maxIntersectionIndexZ = 0;
			for (int curX = minX; curX <= maxX; curX++) {
				for (int curY = minY; curY <= maxY; curY++) {
					for (int curZ = minZ; curZ <= maxZ; curZ++) {
						float curIntersectionVol = getIntersectionVolumWithBlockAt(
								curX, curY, curZ);
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
				handleCollisionAt(maxIntersectionIndexX, maxIntersectionIndexY,
						maxIntersectionIndexZ);
				collisionDedected = true;
			}
		}
	}

	/**
	 * Returns the intersection volume of the spatials collision box and the
	 * block at the given position. Returns 0 if there is no block or no
	 * collision.
	 */
	private float getIntersectionVolumWithBlockAt(int x, int y, int z) {
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
		Vector3f lowerSpatialBorders = spatial.getLocalTranslation().subtract(
				radii);
		Vector3f upperSpatialBorders = spatial.getLocalTranslation().add(radii);

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
	 * Moves the spatial, so that it's collision box is not colliding with the
	 * block at the given position anymore.
	 * <p>
	 * The spatial is moved the shortest possible way possible to reach a non
	 * colliding position.
	 * </p>
	 */
	private void handleCollisionAt(int x, int y, int z) {
		// determine the shortest way out of the block ...
		float shortestDistOut = Float.MAX_VALUE;
		Vector3f finalLocalTranslation = spatial.getLocalTranslation().clone();

		// collision with higher x coordinate
		float spatialX = spatial.getLocalTranslation().x;
		float lowerSpatialX = spatialX - radii.x;
		if (lowerSpatialX < x + .5f && spatialX > x) {
			Vector3f curLocalTranslation = spatial.getLocalTranslation()
					.clone();
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.x = x + .5f + radii.x;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}
		// collision with lower x coordinate
		spatialX = spatial.getLocalTranslation().x;
		float upperSpatialX = spatialX + radii.x;
		if (upperSpatialX > x - .5f && spatialX < x) {
			Vector3f curLocalTranslation = spatial.getLocalTranslation()
					.clone();
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.x = x - .5f - radii.x;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}

		// collision with higher y coordinate
		float spatialY = spatial.getLocalTranslation().y;
		float lowerSpatialY = spatialY - radii.y;
		if (lowerSpatialY < y + .5f && spatialY > y) {
			Vector3f curLocalTranslation = spatial.getLocalTranslation()
					.clone();
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.y = y + .5f + radii.y;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}

		// collision with lower y coordinate
		spatialY = spatial.getLocalTranslation().y;
		float upperSpatialY = spatialY + radii.y;
		if (upperSpatialY > y - .5f && spatialY < y) {

			Vector3f curLocalTranslation = spatial.getLocalTranslation()
					.clone();
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.y = y - .5f - radii.y;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}
		// collision with higher z coordinate
		float spatialZ = spatial.getLocalTranslation().z;
		float lowerSpatialZ = spatialZ - radii.z;
		if (lowerSpatialZ < z + .5f && spatialZ > z) {
			Vector3f curLocalTranslation = spatial.getLocalTranslation()
					.clone();
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.z = z + .5f + radii.z;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}

		// collision with lower z coordinate
		spatialZ = spatial.getLocalTranslation().z;
		float upperSpatialZ = spatialZ + radii.z;
		if (upperSpatialZ > z - .5f && spatialZ < z) {
			Vector3f curLocalTranslation = spatial.getLocalTranslation()
					.clone();
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.z = z - .5f - radii.z;
			float curDist = newLocalTranslation.distance(curLocalTranslation);
			if (curDist < shortestDistOut) {
				shortestDistOut = curDist;
				finalLocalTranslation = newLocalTranslation;
			}
		}

		spatial.setLocalTranslation(finalLocalTranslation);
	}

	/**
	 * JME3 calls this method automatically every frame. Does nothing.
	 */
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}
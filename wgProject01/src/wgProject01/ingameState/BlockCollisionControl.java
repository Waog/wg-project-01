package wgProject01.ingameState;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class BlockCollisionControl extends AbstractControl {

	private Vector3f radii;

	public BlockCollisionControl(Vector3f spacialSize) {
		this.radii = spacialSize.mult(0.5f);
	}

	/**
	 * This is your init method. Optionally, you can modify the spatial from
	 * here (transform it, initialize userdata, etc).
	 */
	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
	}

	@Override
	protected void controlUpdate(float tpf) {
		Boolean collisionDedected = true;
		for (int i = 1; i <= 3 && collisionDedected; i++) {
			collisionDedected = false;
			Vector3f spatialPos = spatial.getWorldTranslation();

			int minX = (int) Math.floor(spatialPos.x - radii.x + .5f);
			int maxX = (int) Math.ceil(spatialPos.x + radii.x - .5f);
			int minY = (int) Math.floor(spatialPos.y - radii.y + .5f);
			int maxY = (int) Math.ceil(spatialPos.y + radii.y - .5f);
			int minZ = (int) Math.floor(spatialPos.z - radii.z + .5f);
			int maxZ = (int) Math.ceil(spatialPos.z + radii.z - .5f);

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

			if (maxIntersectionVolum > 0f) {
				handleCollisionAt(maxIntersectionIndexX, maxIntersectionIndexY,
						maxIntersectionIndexZ);
				collisionDedected = true;
			}
		}
	}

	/**
	 * Returns 0 if there is no block at the given pos.
	 */
	private float getIntersectionVolumWithBlockAt(int x, int y, int z) {
		BlockGameObj block = BlockManager.getInstance().getBlock(x, y, z);
		if (block == null) {
			return 0;
		}

		Vector3f blockPos = new Vector3f(x, y, z);
		Vector3f lowerBlockBorders = blockPos.subtract(new Vector3f(.5f, .5f,
				.5f));
		Vector3f upperBlockBorders = blockPos.add(new Vector3f(.5f, .5f, .5f));
		Vector3f lowerSpatialBorders = spatial.getLocalTranslation().subtract(
				radii);
		Vector3f upperSpatialBorders = spatial.getLocalTranslation().add(radii);

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

		return xIntersect * yIntersect * zIntersect;
	}

	private void handleCollisionAt(int x, int y, int z) {
		// determine the shortest way out of the block
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

		// spatial.setLocalTranslation(finalLocalTranslation);
		spatial.setLocalTranslation(finalLocalTranslation);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}
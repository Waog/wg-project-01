package wgProject01.ingameState;

import com.jme3.bounding.BoundingBox;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class BlockCollisionControl extends AbstractControl {

	private Vector3f radii;
	private boolean collisionDeteced;

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
		do {
			collisionDeteced = false;
			Vector3f spatialPos = spatial.getWorldTranslation();

			int minX = (int) (spatialPos.x - radii.x + .5f);
			int maxX = (int) Math.ceil(spatialPos.x + radii.x - .5f);
			int minY = (int) (spatialPos.y - radii.y + .5f);
			int maxY = (int) Math.ceil(spatialPos.y + radii.y - .5f);
			int minZ = (int) (spatialPos.z - radii.z + .5f);
			int maxZ = (int) Math.ceil(spatialPos.z + radii.z - .5f);

			// attention: this are hacked loops:
			// they only loop until the first collision is dedected.
			for (int curX = minX; curX <= maxX && ! collisionDeteced; curX++) {
				for (int curY = minY; curY <= maxY && ! collisionDeteced; curY++) {
					for (int curZ = minZ; curZ <= maxZ && ! collisionDeteced; curZ++) {
						handleBlockAt(curX, curY, curZ);
					}
				}
			}

		} while (collisionDeteced);
	}

	private void handleBlockAt(int x, int y, int z) {
		BlockGameObj block = BlockManager.getInstance().getBlock(x, y, z);

		if (block != null) {
			// TODO 3: debug code:
//			System.out.println("DEBUG: in block");
//			BoundingBox boundingBox = block.getBoundingBox();
//			int collisionCount2 = spatial.collideWith(boundingBox,
//					new CollisionResults());
//			if (collisionCount2 > 0) {
//				System.out.println("DEBUG: box collision dedected");
//			}

			handleCollisionAt(x, y, z);
			collisionDeteced = true;
		}

	}

	private void handleCollisionAt(int x, int y, int z) {
		// collision with higher x coordinate
		float spatialX = spatial.getLocalTranslation().x;
		float lowerSpatialX = spatialX - radii.x;
		if (lowerSpatialX < x + .5f && spatialX > x) {
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.x = x + .5f + radii.x;
			spatial.setLocalTranslation(newLocalTranslation);
		}
		// collision with lower x coordinate
		spatialX = spatial.getLocalTranslation().x;
		float upperSpatialX = spatialX + radii.x;
		if (upperSpatialX > x - .5f && spatialX < x) {
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.x = x - .5f - radii.x;
			spatial.setLocalTranslation(newLocalTranslation);
		}

		// collision with higher y coordinate
		float spatialY = spatial.getLocalTranslation().y;
		float lowerSpatialY = spatialY - radii.y;
		if (lowerSpatialY < y + .5f && spatialY > y) {
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.y = y + .5f + radii.y;
			spatial.setLocalTranslation(newLocalTranslation);
		}

		// collision with lower y coordinate
		spatialY = spatial.getLocalTranslation().y;
		float upperSpatialY = spatialY + radii.y;
		if (upperSpatialY > y - .5f && spatialY < y) {
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.y = y - .5f - radii.y;
			spatial.setLocalTranslation(newLocalTranslation);
		}
		// collision with higher z coordinate
		float spatialZ = spatial.getLocalTranslation().z;
		float lowerSpatialZ = spatialZ - radii.z;
		if (lowerSpatialZ < z + .5f && spatialZ > z) {
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.z = z + .5f + radii.z;
			spatial.setLocalTranslation(newLocalTranslation);
		}

		// collision with lower z coordinate
		spatialZ = spatial.getLocalTranslation().z;
		float upperSpatialZ = spatialZ + radii.z;
		if (upperSpatialZ > z - .5f && spatialZ < z) {
			Vector3f newLocalTranslation = spatial.getLocalTranslation()
					.clone();
			newLocalTranslation.z = z - .5f - radii.z;
			spatial.setLocalTranslation(newLocalTranslation);
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}
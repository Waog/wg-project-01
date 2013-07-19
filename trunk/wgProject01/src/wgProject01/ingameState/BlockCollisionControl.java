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
		Vector3f spatialPos = spatial.getWorldTranslation();

		BlockGameObj block = BlockManager.getInstance().getBlock(spatialPos);

		if (block != null) {
			System.out.println("in block");
			BoundingBox boundingBox = block.getBoundingBox();
			
			int collisionCount2 = spatial.collideWith(boundingBox,
					new CollisionResults());
			if (collisionCount2 > 0) {
				System.out.println("box collision dedected");
			}
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}
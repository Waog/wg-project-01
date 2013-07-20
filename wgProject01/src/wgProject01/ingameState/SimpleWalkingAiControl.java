package wgProject01.ingameState;

import java.util.Random;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class SimpleWalkingAiControl extends AbstractControl {

	private Vector3f curDirection = new Vector3f(0, 0, 0);
	private float leftSecs = -1;
	private float speed = 1;
	private float maxSecondsToOneDirection;

	SimpleWalkingAiControl() {
	}

	void setSpeed(float walkSpeed) {
		this.speed = walkSpeed;
	}
	
	void setSwitchDirectionInterval(float maxSecondsToOneDirection) {
		this.maxSecondsToOneDirection = maxSecondsToOneDirection;
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
		// TODO 1: Bad workaround: set down the tpf for movement, to prevent wall slipping
		tpf = Math.min(tpf, 0.2f);
		
		if (leftSecs < 0) {
			Random random = new Random();
			leftSecs = random.nextFloat() * maxSecondsToOneDirection;
			curDirection.x = random.nextFloat() - 0.5f;
			curDirection.y = 0;
			curDirection.z = random.nextFloat() - 0.5f;
			curDirection.normalizeLocal();
		}
		
		Vector3f moveOffset = curDirection.mult(tpf).mult(speed);
		spatial.move(moveOffset);
		leftSecs -= tpf;
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}
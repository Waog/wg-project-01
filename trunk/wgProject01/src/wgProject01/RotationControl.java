package wgProject01;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

public class RotationControl extends AbstractControl {

	Vector3f center = new Vector3f(0, 0, 0);

	Vector3f radii = new Vector3f(1, 1, 1);
	Vector3f speeds = new Vector3f(1, 1, 1);

	/**
	 * Each coordinate is counted cyclic from 0 to 2*PI.
	 */
	Vector3f curPosCount = new Vector3f(0, 0, 0);

	/**
	 * the calculated current position.
	 * Calculated each time from curPosCount.
	 */
	Vector3f curCalculatedPos = new Vector3f(0, 0, 0);

	public RotationControl() {
	}

	public RotationControl(int radiusX, int radiusY, int radiusZ) {
		super();
		radii = new Vector3f(radiusX, radiusY, radiusZ);
	}

	public void setSpeeds(Vector3f speeds) {
		this.speeds = speeds;
	}

	/**
	 * This is your init method. Optionally, you can modify the spatial from
	 * here (transform it, initialize userdata, etc).
	 */
	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		this.center = spatial.getLocalTranslation().clone();
	}

	@Override
	protected void controlUpdate(float tpf) {
		
		this.curPosCount = this.curPosCount.add(speeds.mult(tpf));
		System.out.println("tpf:" + tpf);
		System.out.println("speeds:" + speeds);
		System.out.println("speeds.mult(tpf):" + speeds.mult(tpf));
		System.out.println(this.curPosCount);
		System.out.println("center: " + center);
		if (this.curPosCount.x > Math.PI * 2) {
			this.curPosCount.x -= Math.PI * 2;
		}
		if (this.curPosCount.y > Math.PI * 2) {
			this.curPosCount.y -= Math.PI * 2;
		}
		if (this.curPosCount.z > Math.PI * 2) {
			this.curPosCount.z -= Math.PI * 2;
		}
		
		curCalculatedPos.x = (float) (center.x + radii.x * Math.sin(curPosCount.x));
		curCalculatedPos.y = (float) (center.y + radii.y * Math.cos(curPosCount.y));
		curCalculatedPos.z = (float) (center.z + radii.z * Math.cos(curPosCount.z));
		
		spatial.setLocalTranslation(curCalculatedPos);
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}

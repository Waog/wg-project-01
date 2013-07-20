package wgProject01.ingameState;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * This is a {@link com.jme3.scene.control.Control Control} to handle rotational
 * movement around a central point.
 * 
 * <p>
 * Attach it to any spatial which shall move around a center point.
 * </p>
 * 
 * @author oli
 * 
 */
public class RotationControl extends AbstractControl {

	/**
	 * The center of the rotation, derived from an initial call to the spatials
	 * {@link Spatial#getLocalTranslation()} method.
	 */
	Vector3f center = new Vector3f(0, 0, 0);

	/**
	 * The radius around each axis. For example (1, 0, 1) means rotation in the
	 * x-z-plane.
	 */
	Vector3f radii = new Vector3f(1, 1, 1);

	/**
	 * The rotation speed around each axis. For example (1,2,3) means that
	 * translation speed along the z-axis oscillates between -3 and 3.
	 */
	Vector3f speeds = new Vector3f(1, 1, 1);

	/**
	 * In this variable each coordinate is counted cyclic from 0 to 2*PI, to
	 * have parameters for the sin() and cos() functions.
	 */
	Vector3f curPosCount = new Vector3f(0, 0, 0);

	/**
	 * The calculated current position. Calculated each time from CurPosCount.
	 */
	Vector3f curCalculatedPos = new Vector3f(0, 0, 0);

	/**
	 * Creates a default rotation control, with default speeds and radii. The
	 * center of the rotation is the position of the spatial, while this control
	 * is attached to it.
	 */
	public RotationControl() {
	}

	/**
	 * Like {@link #RotationControl()}, but uses custom radii.
	 */
	public RotationControl(float radiusX, float radiusY, float radiusZ) {
		super();
		radii = new Vector3f(radiusX, radiusY, radiusZ);
	}

	/**
	 * Sets the rotation speed around each axis. For example (1,2,3) means that
	 * translation speed along the z-axis oscillates between -3 and 3.
	 */
	public void setSpeeds(Vector3f speeds) {
		this.speeds = speeds;
	}

	/**
	 * JME3 calls this method automatically if the control is attached to a
	 * spatial. Initializes the Control.
	 * <p>
	 * Initializes the rotation center to be
	 * {@link Spatial#getLocalTranslation()}.
	 * </p>
	 */
	@Override
	public void setSpatial(Spatial spatial) {
		super.setSpatial(spatial);
		this.center = spatial.getLocalTranslation().clone();
	}

	/**
	 * JME3 calls this method automatically every frame.
	 * 
	 * Updates the position of the spatial. Currently just sets a new position
	 * instead of moving the spatial, so it still can pass walls.
	 */
	@Override
	protected void controlUpdate(float tpf) {

		this.curPosCount = this.curPosCount.add(speeds.mult(tpf));
		if (this.curPosCount.x > Math.PI * 2) {
			this.curPosCount.x -= Math.PI * 2;
		}
		if (this.curPosCount.y > Math.PI * 2) {
			this.curPosCount.y -= Math.PI * 2;
		}
		if (this.curPosCount.z > Math.PI * 2) {
			this.curPosCount.z -= Math.PI * 2;
		}

		curCalculatedPos.x = (float) (center.x + radii.x
				* Math.sin(curPosCount.x));
		curCalculatedPos.y = (float) (center.y + radii.y
				* Math.cos(curPosCount.y));
		curCalculatedPos.z = (float) (center.z + radii.z
				* Math.cos(curPosCount.z));

		spatial.setLocalTranslation(curCalculatedPos);
	}

	/**
	 * JME3 calls this method automatically every frame. Does nothing.
	 */
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}

package wgProject01.ingameState;

import java.util.Random;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * This is a {@link com.jme3.scene.control.Control Control} to handle simple
 * random walking. Walking means translation on the x-z-plane.
 * 
 * <p>
 * Attach it to any spatial which shall randomly walk around.
 * </p>
 * 
 * @author oli
 * 
 */
public class SimpleWalkingAiControl extends AbstractControl {

	/**
	 * The current walking direction. Must be a normalized vector.
	 */
	private Vector3f curDirection = new Vector3f(1, 0, 0);

	/**
	 * The seconds left, until the control switches the walking direction the
	 * next time.
	 */
	private float leftSecs = -1;

	/**
	 * The walking speed.
	 */
	private float speed = 1;

	/**
	 * The maximum number of seconds the spatial shall move into one direction.
	 */
	private float maxSecondsToOneDirection = 5;

	/**
	 * Creates a default walking control.
	 */
	SimpleWalkingAiControl() {
	}

	/**
	 * Sets the walking speed.
	 */
	void setSpeed(float walkSpeed) {
		this.speed = walkSpeed;
	}

	/**
	 * Sets the maximum number of seconds the spatial shall move into one
	 * direction.
	 */
	void setSwitchDirectionInterval(float maxSecondsToOneDirection) {
		this.maxSecondsToOneDirection = maxSecondsToOneDirection;
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
	 * JME3 calls this method automatically every frame.
	 * 
	 * Moves the spatial into one direction. If moved long enough, randomly
	 * defines a new walking direction and walking duration.
	 */
	@Override
	protected void controlUpdate(float tpf) {
		// TODO 1: Bad workaround: set down the tpf for movement, to prevent
		// wall slipping
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

	/**
	 * JME3 calls this method automatically every frame. Does nothing.
	 */
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}

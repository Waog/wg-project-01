package wgProject01.ingameState.gameLogic.model;

import com.artemis.Component;
import com.jme3.math.Vector3f;

public class WalkingAiComponent extends Component {
	/**
	 * The current walking direction. Must be a normalized vector.
	 */
	public Vector3f curDirection = new Vector3f(1, 0, 0);

	/**
	 * The seconds left, until the control switches the walking direction the
	 * next time.
	 */
	public float leftSecs = -1;

	/**
	 * The walking speed.
	 */
	public float speed = 6;

	/**
	 * The maximum number of seconds the spatial shall move into one direction.
	 */
	public float maxSecondsToOneDirection = 5;
	
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
}
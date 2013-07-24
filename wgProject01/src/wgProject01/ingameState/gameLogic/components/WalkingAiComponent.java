package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.Vector3f;

/**
 * A {@link Component} (pure data structure) to describe and store the walking
 * behavior (of an entity).
 * 
 * @author oli
 * 
 */
public class WalkingAiComponent extends Component {
	
	/**
	 * The current walking direction. Must be a normalized vector.
	 */
	public Vector3f curDirection = new Vector3f(1, 0, 0);

	/**
	 * The seconds left, until the walking direction is switched the
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
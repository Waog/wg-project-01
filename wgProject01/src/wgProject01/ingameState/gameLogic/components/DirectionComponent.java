package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.Vector3f;

/**
 * A {@link Component} (pure data structure) to describe the direction the
 * entity is pointing at.
 * 
 * @author mirco
 * 
 */
public class DirectionComponent extends Component {
	/** a unit vector describing the direction the entity is pointing at */
	private Vector3f direction = new Vector3f(1, 0, 0);

	/** @return a unit vector describing the direction the entity is pointing at */
	public Vector3f getDirection() {
		return direction;
	}

	/** sets the direction vector to the given direction and normalizes it */
	public void setDirection(Vector3f direction) {
		this.direction.set(direction);
		this.direction.normalizeLocal();
	}
}

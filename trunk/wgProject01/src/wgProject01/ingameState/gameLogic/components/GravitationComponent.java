package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;

/**
 * A {@link Component} (pure data structure) to describe and store the falling
 * behavior (of an entity).
 * 
 * @author Mirco
 * 
 */
public class GravitationComponent extends Component {
	
	/** the meters an entity falls in one second */
	private static final float FALL_VELOCITY = 8;

	/** @return the meters an entity falls in one second */
	public float getFallVelocity() {
		return FALL_VELOCITY;
	}
}

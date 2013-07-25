package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;

/**
 *  A {@link Component} (pure data structure) to describe and mark that the entity is reacting to user inputs.
 * 
 * @author Mirco
 *
 */
public class PlayerControlComponent extends Component {

	/** the meters an entity moves */
	public float speed = 20;
	
}

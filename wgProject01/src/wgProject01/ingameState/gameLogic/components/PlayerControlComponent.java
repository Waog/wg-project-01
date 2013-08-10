package wgProject01.ingameState.gameLogic.components;

import java.util.Stack;

import utils.typeModels.IntegerModel;
import wgProject01.ingameState.gameLogic.BlockGameObj;

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

	/** inventory block stack */
	public Stack<BlockGameObj> inventoryStack = new Stack<BlockGameObj>();
	
	/** count of inventory blocks */
	public IntegerModel itemCount = new IntegerModel(0);
}

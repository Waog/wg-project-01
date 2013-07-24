package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;

/**
 * A {@link Component} (pure data structure) to describe a position of a block.
 * 
 * @author oli
 * 
 */
public class BlockPositionComponent extends Component {
	/**
	 * The position of the block in the world.
	 * Is only of use if {@link #placed} is true.
	 */
	public int x, y, z;
	
	/**
	 * Flag: Is the block placed somewhere in the world?
	 */
	public Boolean placed;
}
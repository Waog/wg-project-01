package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.Vector3f;

/**
 * A {@link Component} (pure data structure) to describe a position of a block.
 * 
 * @author oli
 *
 */
public class BlockPositionComponent extends Component {
        public int x, y, z;
        public Boolean placed;
}
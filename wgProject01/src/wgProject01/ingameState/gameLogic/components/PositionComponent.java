package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.Vector3f;

/**
 * A {@link Component} (pure data structure) to describe a position (of an entity).
 * 
 * @author oli
 *
 */
public class PositionComponent extends Component {
		/**
		 * Flag: is the entity visible? 
		 */
		public boolean visible = true;
	
        public Vector3f pos = new Vector3f();
}
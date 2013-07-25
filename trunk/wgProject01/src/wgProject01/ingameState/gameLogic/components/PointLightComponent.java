package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.ColorRGBA;

/**
 * A {@link Component} (pure data structure) to describe a position (of an entity).
 * 
 * @author oli
 *
 */
public class PointLightComponent extends Component {
	public ColorRGBA color = ColorRGBA.randomColor();
	
	/**
	 * A Radius of 0 means, no restriction for the light.
	 */
	public float radius = 0;
}
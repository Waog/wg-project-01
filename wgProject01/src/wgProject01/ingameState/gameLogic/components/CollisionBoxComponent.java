package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.Vector3f;

/**
 * A {@link Component} (pure data structure) to describe a collision box (of an entity).
 * 
 * @author oli
 *
 */
public class CollisionBoxComponent extends Component {
	/**
	 * The size of the spatial's collision box. For example the spatial's
	 * collision area in x direction is from</br>
	 * <code>(spatial.getLocalTranslation() - radii.x)</code> to</br>
	 * <code>(spatial.getLocalTranslation() + radii.x)</code>.
	 */
	public Vector3f radii;

	/**
	 * Creates a new component with a collision box of the given size.
	 */
	public CollisionBoxComponent(Vector3f radii) {
		super();
		this.radii = radii;
	}
}
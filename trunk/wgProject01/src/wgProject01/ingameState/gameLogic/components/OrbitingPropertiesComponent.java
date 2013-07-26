package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * A {@link Component} (pure data structure) to describe a position (of an
 * entity).
 * 
 * @author oli
 * 
 */
public class OrbitingPropertiesComponent extends Component {
	/**
	 * The center of the rotation, derived from an initial call to the spatials
	 * {@link Spatial#getLocalTranslation()} method.
	 */
	public Vector3f center = new Vector3f(0, 0, 0);

	/**
	 * The radius around each axis. For example (1, 0, 1) means rotation in the
	 * x-z-plane.
	 */
	public float radius = 1;

	/**
	 * The rotation speed around each axis. For example (2,3) means that
	 * translation speed around the z-axis is 3 circulations per second and the
	 * rotation perpendicular to the first rotation is at a speed of 2
	 * circulations per second.
	 */
	public Vector2f speeds = new Vector2f(1, 1);

	/**
	 * Internal variable to remember if phi has to be raised or lowered the
	 * next frame.
	 */
	public boolean raiseTheta = true;
}
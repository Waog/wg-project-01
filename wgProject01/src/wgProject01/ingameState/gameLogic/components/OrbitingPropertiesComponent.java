package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * A {@link Component} (pure data structure) to describe how an entity is
 * orbiting around something.
 * 
 * @author oli
 * 
 */
public class OrbitingPropertiesComponent extends Component {
	/**
	 * The center point of the rotation.
	 */
	public Vector3f center = new Vector3f(0, 0, 0);

	/**
	 * The radius of the sphere the entity shall move on.
	 */
	public float radius = 1;

	/**
	 * The rotation speeds (theta, phi) in spherical coordinates in circulations
	 * per second. For example (2,3) means that translation speed around the
	 * z-axis is 3 circulations per second and the rotation perpendicular to the
	 * first rotation is at a speed of 2 circulations per second.
	 * 
	 * (TODO: Documentation still up to date? since x-y-z may be switched
	 * somehow in the direction component. We should use a unified convenion
	 * everywhere in the code.)
	 */
	public Vector2f speeds = new Vector2f(1, 1);

	/**
	 * Internal variable to remember if theta has to be raised or lowered the next
	 * frame.
	 */
	public boolean raiseTheta = true;
}
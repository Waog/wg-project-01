package wgProject01.ingameState.gameLogic.components;

import com.artemis.Component;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 * A {@link Component} (pure data structure) to describe a position (of an entity).
 * 
 * @author oli
 *
 */
public class RotationPropertiesComponent extends Component {
	/**
	 * The center of the rotation, derived from an initial call to the spatials
	 * {@link Spatial#getLocalTranslation()} method.
	 */
	public Vector3f center = new Vector3f(0, 0, 0);

	/**
	 * The radius around each axis. For example (1, 0, 1) means rotation in the
	 * x-z-plane.
	 */
	public Vector3f radii = new Vector3f(1, 1, 1);

	/**
	 * The rotation speed around each axis. For example (1,2,3) means that
	 * translation speed along the z-axis oscillates between -3 and 3.
	 */
	public Vector3f speeds = new Vector3f(1, 1, 1);

	/**
	 * In this variable each coordinate is counted cyclic from 0 to 2*PI, to
	 * have parameters for the sin() and cos() functions.
	 */
	public Vector3f curPosCount = new Vector3f(0, 0, 0);

	/**
	 * The calculated current position. Calculated each time from CurPosCount.
	 */
	public Vector3f curCalculatedPos = new Vector3f(0, 0, 0);
}
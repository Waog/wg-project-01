package wgProject01.ingameState.gameLogic.components;

import JSci.maths.CoordinateMath;

import com.artemis.Component;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;

/**
 * A {@link Component} (pure data structure) to describe the direction the
 * entity is pointing at.
 * 
 * @author mirco
 * 
 */
public class DirectionComponent extends Component {

	private static final float radius = 1f;

	/**
	 * a 2d vector describing the direction the entity is pointing at in
	 * spherical coordinates (theta, phi).
	 * <p>
	 * theta in [0, pi]
	 * </p>
	 * <p>
	 * phi in [-pi, pi]
	 * </p>
	 */
	private Vector2f direction = new Vector2f(0, 0); // points in z-direction

	/**
	 * returns the direction vector in spherical coordinates (theta, phi).
	 * <p>
	 * theta in [0, pi]
	 * </p>
	 * <p>
	 * phi in [-pi, pi]
	 * </p>
	 */
	public Vector2f getSphericalDirection() {
		return direction;
	}

	/**
	 * sets the direction vector to the given direction in spherical coordinates
	 * (theta, phi).
	 * <p>
	 * theta in [0, pi]
	 * </p>
	 * <p>
	 * phi in [-pi, pi]
	 * </p>
	 */
	public void setSphericalDirection(Vector2f direction) {
		this.direction = direction;
	}

	/**
	 * @return a unit vector describing the direction the entity is pointing at
	 *         in cartesian coordinates.
	 */
	public Vector3f getCartesianDirection() {
		double[] cartesianArray = CoordinateMath.sphericalToCartesian(radius,
				direction.x, direction.y);
		Vector3f result = new Vector3f((float) cartesianArray[0],
				(float) cartesianArray[1], (float) cartesianArray[2]);
		return result;
	}

	/**
	 * sets the direction vector to the given direction in cartesian coordinates
	 * and normalizes it
	 */
	public void setCartesianDirection(Vector3f direction) {
		double[] sphericalArray = CoordinateMath.cartesianToSpherical(
				direction.x, direction.y, direction.z);
		Vector2f result = new Vector2f((float) sphericalArray[1],
				(float) sphericalArray[2]);
		this.direction.set(result);
	}

	/**
	 * Returns the current direction in cartesian coordinates projected to the
	 * XZ plane and normalized.
	 */
	public Vector3f getCatesianProjectedDirectionXZ() {
		Vector3f projectedDirectionXZ = new Vector3f();
		projectedDirectionXZ.set(getCartesianDirection());
		projectedDirectionXZ.y = 0;
		projectedDirectionXZ.normalizeLocal();
		return projectedDirectionXZ;
	}

}

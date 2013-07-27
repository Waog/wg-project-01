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
	private Vector2f direction = new Vector2f((float) Math.PI / 2f, 0); // points
																		// in
																		// z-direction

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
		this.direction.set(direction);
		normalizeToSphericalParams(this.direction);
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
	 * ATTENTION: switches y and z axis
	 * 
	 * @return a unit vector describing the direction the entity is pointing at
	 *         in cartesian coordinates with y and z axis switched.
	 */
	public Vector3f getSwitchedCartesianDirection() {
		Vector3f nonSwitchedDir = getCartesianDirection();
		return new Vector3f(nonSwitchedDir.x, nonSwitchedDir.z,
				nonSwitchedDir.y);
	}

	/**
	 * ATTENTION: switches y and z axis sets the direction vector to the given
	 * direction in cartesian coordinates with y and z coordinate switched and
	 * normalizes it,
	 */
	public void setSwitchedCartesianDirection(Vector3f switchedCartesianDir) {
		Vector3f nonSwitchedCartesianDir = new Vector3f(switchedCartesianDir.x,
				switchedCartesianDir.z, switchedCartesianDir.y);
		setCartesianDirection(nonSwitchedCartesianDir);
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
		normalizeToSphericalParams(this.direction);
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

	/**
	 * Returns the current z-y-switched direction in cartesian coordinates
	 * projected to the XZ plane and normalized.
	 */
	public Vector3f getSwitchedCatesianProjectedDirectionXZ() {
		Vector3f projectedDirectionXZ = new Vector3f();
		projectedDirectionXZ.set(getSwitchedCartesianDirection());
		projectedDirectionXZ.y = 0;
		projectedDirectionXZ.normalizeLocal();
		return projectedDirectionXZ;
	}

	/**
	 * Normalizes a given spherical coordinate (theta, phi) to match:
	 * <p>
	 * theta in [0, pi]
	 * </p>
	 * <p>
	 * phi in [-pi, pi]
	 * </p>
	 */
	private void normalizeToSphericalParams(Vector2f sphereCoord) {
		// normalize the angles theta into [0, PI] and phi into [-PI, PI]
		// normalize theta
		while (sphereCoord.x < 0) {
			if (sphereCoord.x > 2 * Math.PI) {
				sphereCoord.x -= 2 * Math.PI;
			} else if (sphereCoord.x > Math.PI) {
				// mirror theta at PI and turn phi
				float distToPi = sphereCoord.x - ((float) Math.PI);
				sphereCoord.x = ((float) Math.PI) - distToPi;
				sphereCoord.y += Math.PI;
			} else {
				System.out.println("this shouldn't happen, check "
						+ this.getClass().getName());
			}
		}
		while (sphereCoord.x > Math.PI) {
			if (sphereCoord.x < -2 * Math.PI) {
				sphereCoord.x += 2 * Math.PI;
			} else if (sphereCoord.x < Math.PI) {
				sphereCoord.x += 2 * Math.PI;
			} else if (sphereCoord.x < 0) {
				// mirror theta at 0 and turn phi
				sphereCoord.x = Math.abs(sphereCoord.x);
				sphereCoord.y += Math.PI;
			} else {
				System.out.println("this shouldn't happen, check "
						+ this.getClass().getName());
			}
		}

		// normalize phi
		while (sphereCoord.y < -Math.PI) {
			sphereCoord.y += 2 * Math.PI;
		}
		while (sphereCoord.y > Math.PI) {
			sphereCoord.y -= 2 * Math.PI;
		}
	}
}

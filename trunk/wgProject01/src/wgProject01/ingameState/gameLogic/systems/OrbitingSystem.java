package wgProject01.ingameState.gameLogic.systems;

import jm3Utils.Jme3Utils;
import wgProject01.ingameState.gameLogic.components.OrbitingPropertiesComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.jme3.math.Vector3f;

/**
 * <p>
 * This is an entity system to handle rotational movement around a central
 * point.
 * </p>
 * 
 * <p>
 * it doen't change the direction of the entities. It just changes the position,
 * to slowly adopt the orbit path.
 * </p>
 * 
 * @author oli
 * 
 */
public class OrbitingSystem extends EntityProcessingSystem {

	/**
	 * Automagical creation of a ComponentMapper to extract a component from the
	 * entities.
	 */
	@Mapper
	ComponentMapper<PositionComponent> positionManager;

	/**
	 * Automagical creation of a ComponentMapper to extract a component from the
	 * entities.
	 */
	@Mapper
	ComponentMapper<OrbitingPropertiesComponent> orbitingManager;

	/**
	 * <p>
	 * Create a new Entity System instance, which lets the appropriate entities
	 * orbit around, like specified in their {@link OrbitingPropertiesComponent}
	 * .
	 * </p>
	 * 
	 * <p>
	 * Like all EntitySystems the constructed instance must be attached to a
	 * {@link World} to work.
	 * </p>
	 * 
	 */
	@SuppressWarnings("unchecked")
	public OrbitingSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class,
				OrbitingPropertiesComponent.class));
	}

	/**
	 * <p>
	 * The Artemis framework calls this method automatically once every time
	 * {@link World#process()} is called.
	 * </p>
	 * 
	 * <p>
	 * Moves the entities position to slowly adopt it's specified orbit.
	 * <p>
	 */
	@Override
	protected void process(Entity e) {
		// extract needed components from entity
		PositionComponent positionComponent = positionManager.get(e);
		OrbitingPropertiesComponent orbitingComponent = orbitingManager.get(e);
		// extract the time delta
		float timeDelta = world.getDelta();

		Vector3f curLocalCartesianPos = positionComponent.pos
				.subtract(orbitingComponent.center);

		// calculating sphere coordinates for the zero vector fails, so prevent
		// it.
		if (curLocalCartesianPos.length() == 0f) {
			curLocalCartesianPos.x += 0.000001f;
		}

		Vector3f curSphericalPos = Jme3Utils
				.getSphericalCoord(curLocalCartesianPos);

		// determine the desired new position by ...
		Vector3f newDesiredSphericalPos = new Vector3f();
		// ... trying to converge the current radius to the desired radius
		// slowly.
		newDesiredSphericalPos.x = (curSphericalPos.x + orbitingComponent.radius) / 2f;
		// ... trying to rotate along the theta angle with the defined speed.
		float thetaDelta = timeDelta * orbitingComponent.speeds.x * 2f
				* ((float) Math.PI);
		if (orbitingComponent.raiseTheta) {
			newDesiredSphericalPos.y = curSphericalPos.y + thetaDelta;
		} else {
			newDesiredSphericalPos.y = curSphericalPos.y - thetaDelta;
		}
		// ... trying to rotate along the phi angle with the defined speed.
		newDesiredSphericalPos.z = curSphericalPos.z + timeDelta
				* orbitingComponent.speeds.y * 2f * ((float) Math.PI);

		// normalize the angles theta into [0, PI] and phi into [-PI, PI]
		// normalize theta
		if (newDesiredSphericalPos.y >= Math.PI) {
			newDesiredSphericalPos.y = (float) Math.PI - 0.000001f;
			newDesiredSphericalPos.z += Math.PI;
			orbitingComponent.raiseTheta = false;
		} else if (newDesiredSphericalPos.y <= 0) {
			newDesiredSphericalPos.y = 0.000001f;
			newDesiredSphericalPos.z += Math.PI;
			orbitingComponent.raiseTheta = true;
		}
		// normalize phi
		if (newDesiredSphericalPos.z >= Math.PI) {
			newDesiredSphericalPos.z -= 2 * Math.PI;
		}

		// the desired new position in Cartesian coordinates
		Vector3f newDesiredLocalCartesianPos = Jme3Utils
				.getCartesianCoord(newDesiredSphericalPos);
		Vector3f newDesiredGlobalCartesianPos = newDesiredLocalCartesianPos
				.add(orbitingComponent.center);

		// potentially determine a final position which differs from the desired
		// position here (for example due to cartesian speed limitations).
		// nothing yet

		// set the entity to it's new position
		positionComponent.pos = newDesiredGlobalCartesianPos;
	}
}

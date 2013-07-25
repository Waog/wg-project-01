package wgProject01.ingameState.gameLogic.systems;

import wgProject01.ingameState.gameLogic.components.PositionComponent;
import wgProject01.ingameState.gameLogic.components.OrbitingPropertiesComponent;
import JSci.maths.CoordinateMath;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.jme3.math.Vector3f;

/**
 * This is a {@link com.jme3.scene.control.Control Control} to handle rotational
 * movement around a central point.
 * 
 * <p>
 * Attach it to any spatial which shall move around a center point.
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
	 * Create a new SimpleWalkingAiSystem instance, which lets the appropriate
	 * entities walk randomly around in the x-z-plane.
	 * </p>
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
	 * JME3 calls this method automatically every frame.
	 * 
	 * Updates the position of the spatial. Currently just sets a new position
	 * instead of moving the spatial, so it still can pass walls.
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

		Vector3f curSphericalPos = getSphericalCoord(curLocalCartesianPos);

		// determine the desired new position
		Vector3f newDesiredSphericalPos = new Vector3f();
		// try to converge the current radius to the desired radius slowly.
		newDesiredSphericalPos.x = (curSphericalPos.x + orbitingComponent.radius) / 2f;
		// try to rotate along the theta angle with the defined speed.
		newDesiredSphericalPos.y = curSphericalPos.y + timeDelta
				* orbitingComponent.speeds.x * 2f * ((float) Math.PI);
		// try to rotate along the phi angle with the defined speed.
		newDesiredSphericalPos.z = curSphericalPos.z + timeDelta
				* orbitingComponent.speeds.y * 2f * ((float) Math.PI);
		
//		if (newDesiredSphericalPos.y >= 2 * Math.PI) {
//			newDesiredSphericalPos.y -= 2 * Math.PI;
//		}
//		if (newDesiredSphericalPos.z >= 2 * Math.PI) {
//			newDesiredSphericalPos.z -= 2 * Math.PI;
//		}

		// the desired new position in Cartesian coordinates
		Vector3f newDesiredLocalCartesianPos = getCartesianCoord(newDesiredSphericalPos);
		Vector3f newDesiredGlobalCartesianPos = newDesiredLocalCartesianPos.add(orbitingComponent.center);

		// potentially determine a final position which differs from the desired
		// position here (for example due to speed limitations).

		// set the entity to it's new position
		positionComponent.pos = newDesiredGlobalCartesianPos;
		
		System.out.println("--- DEBUG: orbiting system frame: ---");
		System.out.println("curSphericalPos            : " + curSphericalPos);
		System.out.println("newDesiredSphericalPos     : " + newDesiredSphericalPos);
		System.out.println("curLocalCartesianPos       : " + curLocalCartesianPos);
		System.out.println("newDesiredLocalCartesianPos: " + newDesiredLocalCartesianPos);
	}

	private Vector3f getSphericalCoord(Vector3f cartesianCoord) {
		double[] sphericalArray = CoordinateMath.cartesianToSpherical(
				cartesianCoord.x, cartesianCoord.y, cartesianCoord.z);
		Vector3f result = new Vector3f((float) sphericalArray[0],
				(float) sphericalArray[1], (float) sphericalArray[2]);
		return result;
	}

	private Vector3f getCartesianCoord(Vector3f sphericalCoord) {
		double[] cartesianArray = CoordinateMath.sphericalToCartesian(
				sphericalCoord.x, sphericalCoord.y, sphericalCoord.z);
		Vector3f result = new Vector3f((float) cartesianArray[0],
				(float) cartesianArray[1], (float) cartesianArray[2]);
		return result;
	}
}

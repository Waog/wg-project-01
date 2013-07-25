package wgProject01.ingameState.gameLogic.systems;

import wgProject01.ingameState.gameLogic.components.PositionComponent;
import wgProject01.ingameState.gameLogic.components.RotationPropertiesComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;

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
public class RotationSystem extends EntityProcessingSystem {

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
	ComponentMapper<RotationPropertiesComponent> rotationManager;

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
	public RotationSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class,
				RotationPropertiesComponent.class));
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
		RotationPropertiesComponent rotationComponent = rotationManager.get(e);
		// extract the time delta
		float timeDelta = world.getDelta();

		rotationComponent.curPosCount = rotationComponent.curPosCount.add(rotationComponent.speeds.mult(timeDelta));
		if (rotationComponent.curPosCount.x > Math.PI * 2) {
			rotationComponent.curPosCount.x -= Math.PI * 2;
		}
		if (rotationComponent.curPosCount.y > Math.PI * 2) {
			rotationComponent.curPosCount.y -= Math.PI * 2;
		}
		if (rotationComponent.curPosCount.z > Math.PI * 2) {
			rotationComponent.curPosCount.z -= Math.PI * 2;
		}

		rotationComponent.curCalculatedPos.x = (float) (rotationComponent.center.x + rotationComponent.radii.x
				* Math.sin(rotationComponent.curPosCount.x));
		rotationComponent.curCalculatedPos.y = (float) (rotationComponent.center.y + rotationComponent.radii.y
				* Math.cos(rotationComponent.curPosCount.y));
		rotationComponent.curCalculatedPos.z = (float) (rotationComponent.center.z + rotationComponent.radii.z
				* Math.cos(rotationComponent.curPosCount.z));

		positionComponent.pos.set(rotationComponent.curCalculatedPos);
		
		System.out.println("moved the sun");
	}
}

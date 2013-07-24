package wgProject01.ingameState.gameLogic.systems;

import java.util.Random;

import wgProject01.ingameState.gameLogic.components.PositionComponent;
import wgProject01.ingameState.gameLogic.components.WalkingAiComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.jme3.math.Vector3f;

/**
 * <p>
 * This is an entity system to handle simple random walking. Walking means
 * translation on the x-z-plane.
 * </p>
 * 
 * @author oli
 * 
 */
public class SimpleWalkingAiSystem extends EntityProcessingSystem {

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
	ComponentMapper<WalkingAiComponent> walkingAiManager;

	/**
	 * Used for randomly generating walking directions and times.
	 */
	Random random = new Random();

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
	public SimpleWalkingAiSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class,
				WalkingAiComponent.class));
	}

	/**
	 * <p>
	 * The Artemis framework calls this method automatically once every time
	 * {@link World#process()} is called.
	 * </p>
	 * 
	 * <p>
	 * Moves the entity into one direction. If moved long enough, randomly
	 * defines a new walking direction and walking duration.
	 * </p>
	 */
	@Override
	protected void process(Entity e) {
		// extract needed components from entity
		PositionComponent positionComponent = positionManager.get(e);
		WalkingAiComponent walkingAiComponent = walkingAiManager.get(e);
		// extract the time delta
		float timeDelta = world.getDelta();

		if (walkingAiComponent.leftSecs < 0) {
			walkingAiComponent.leftSecs = random.nextFloat()
					* walkingAiComponent.maxSecondsToOneDirection;
			walkingAiComponent.curDirection.x = random.nextFloat() - 0.5f;
			walkingAiComponent.curDirection.y = 0;
			walkingAiComponent.curDirection.z = random.nextFloat() - 0.5f;
			walkingAiComponent.curDirection.normalizeLocal();
		}

		Vector3f moveOffset = walkingAiComponent.curDirection.mult(timeDelta)
				.mult(walkingAiComponent.speed);
		positionComponent.pos.addLocal(moveOffset);
		walkingAiComponent.leftSecs -= timeDelta;
	}
}

package wgProject01.ingameState.control;

import java.util.Random;

import wgProject01.ingameState.model.PositionComponent;
import wgProject01.ingameState.model.WalkingAiComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.jme3.math.Vector3f;

/**
 * This is a Control to handle simple random walking. Walking means translation
 * on the x-z-plane.
 * 
 * <p>
 * Attach it to any entity which shall randomly walk around.
 * </p>
 * 
 * @author oli
 * 
 */
public class SimpleWalkingAiSystem extends EntityProcessingSystem {

	@Mapper
	ComponentMapper<PositionComponent> positionManager;
	@Mapper
	ComponentMapper<WalkingAiComponent> walkingAiManager;

	/**
	 * Creates a default walking control.
	 */
	@SuppressWarnings("unchecked")
	SimpleWalkingAiSystem() {
		super(Aspect.getAspectForAll(PositionComponent.class,
				WalkingAiComponent.class));
	}

	/**
	 * TODO: This method should be called automatically every frame.
	 * 
	 * Moves the entity into one direction. If moved long enough, randomly
	 * defines a new walking direction and walking duration.
	 */
	@Override
	protected void process(Entity e) {
		// extract needed components from entity
		PositionComponent positionComponent = positionManager.get(e);
		WalkingAiComponent walkingAiComponent = walkingAiManager.get(e);

		// TODO 1: Bad workaround: set down the tpf for movement, to prevent
		// wall slipping
		float timeDelta = world.getDelta();

		if (walkingAiComponent.leftSecs < 0) {
			Random random = new Random();
			walkingAiComponent.leftSecs = random.nextFloat()
					* walkingAiComponent.maxSecondsToOneDirection;
			walkingAiComponent.curDirection.x = random.nextFloat() - 0.5f;
			walkingAiComponent.curDirection.y = 0;
			walkingAiComponent.curDirection.z = random.nextFloat() - 0.5f;
			walkingAiComponent.curDirection.normalizeLocal();
		}

		Vector3f moveOffset = walkingAiComponent.curDirection.mult(timeDelta)
				.mult(walkingAiComponent.speed);
		positionComponent.x += moveOffset.x;
		positionComponent.y += moveOffset.y;
		positionComponent.z += moveOffset.z;
		walkingAiComponent.leftSecs -= timeDelta;
	}
}

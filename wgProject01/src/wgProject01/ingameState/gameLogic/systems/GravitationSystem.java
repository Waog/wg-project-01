package wgProject01.ingameState.gameLogic.systems;

import wgProject01.ingameState.gameLogic.components.GravitationComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;


/**
 * This is an entity system handling falling of an entity. Falling means translation in negative y-direction.
 * @author Mirco
 */
public class GravitationSystem extends EntityProcessingSystem {

	/**
	 * Automagical creation of a ComponentMapper to extract a component from the
	 * entities.
	 */
	@Mapper
	ComponentMapper<GravitationComponent> gravitationManager;
	
	/**
	 * Automagical creation of a ComponentMapper to extract a component from the
	 * entities.
	 */
	@Mapper
	ComponentMapper<PositionComponent> positionManager;

	/**
	 * constructs a new GravitationSystem for all Entities that have a
	 * GravitationComponent and a PositionComponent
	 */
	@SuppressWarnings("unchecked")
	public GravitationSystem() {
		super(Aspect.getAspectForAll(GravitationComponent.class,
				PositionComponent.class));
	}

	/**
	 * <p>
	 * The Artemis framework calls this method automatically once every time
	 * {@link World#process()} is called.
	 * </p>
	 * 
	 * <p>
	 * translates the entity into negative y-direction with fall velocity of the entity
	 * </p>
	 */
	@Override
	protected void process(Entity e) {
		PositionComponent positionComponent = positionManager.get(e);
		GravitationComponent gravitationComponent = gravitationManager.get(e);

		float fallVelocity = gravitationComponent.getFallVelocity();

		// get the time passed since last cycle
		float timeDelta = world.getDelta();

		// calculate how many meters the entity fell in this cycle
		float fallenMeters = timeDelta * fallVelocity;

		// substract the fallen meters from the y-coordinate of the positionComponent
		positionComponent.pos.addLocal(0, -fallenMeters, 0);
	}
}

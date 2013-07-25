package wgProject01.ingameState.gameLogic.systems;

import wgProject01.ingameState.gameLogic.components.InputReactingComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.jme3.math.Vector3f;

/**
 * An entity system handling the given user inputs. The inputs commands are
 * stored in a queue.
 * 
 * @author Mirco
 * 
 */
public class InputHandlingSystem extends EntityProcessingSystem {

	/** flag marking if entity shall move to left, set by external class */
	public static boolean moveLeftActivated;
	/** flag marking if entity shall move to right, set by external class */
	public static boolean moveRightActivated;
	/** flag marking if entity shall move to down, set by external class */
	public static boolean moveBackActivated;
	/** flag marking if entity shall move to up, set by external class */
	public static boolean moveForwardActivated;

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
	ComponentMapper<InputReactingComponent> inputReactingManager;

	/**
	 * constructs a new InputHandlingSystem for all Entities that have a
	 * InputReactionComponent and a PositionComponent
	 */
	@SuppressWarnings("unchecked")
	public InputHandlingSystem() {
		super(Aspect.getAspectForAll(InputReactingComponent.class,
				PositionComponent.class));
	}

	@Override
	protected void process(Entity e) {
		float delta = world.getDelta();
		PositionComponent positionComponent = positionManager.get(e);
		InputReactingComponent inputReactingComponent = inputReactingManager
				.get(e);
		if (moveLeftActivated) {
			positionComponent.pos.x -= inputReactingComponent.speed * delta;
		}
		if(moveRightActivated){
			positionComponent.pos.x += inputReactingComponent.speed * delta;
		}
		if(moveBackActivated){
			positionComponent.pos.z -= inputReactingComponent.speed * delta;
		}
		if(moveForwardActivated){
			positionComponent.pos.z += inputReactingComponent.speed * delta;
		}
		

	}
}

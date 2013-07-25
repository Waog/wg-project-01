package wgProject01.ingameState.gameLogic.systems;

import java.util.HashMap;
import java.util.Map;

import wgProject01.ingameState.gameLogic.components.InputReactingComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;

/**
 * An entity system handling the given user inputs. The inputs commands are
 * stored in a queue.
 * 
 * @author Mirco
 * 
 */
public class InputHandlingSystem extends EntityProcessingSystem {
	
	/** names for commands */
	/** for moving left */ 
	public static final String LEFT = "Left";
	/** for moving right */
	public static final String RIGHT = "Right";
	/** for moving down */
	public static final String BACK = "Back";
	/** for moving up */
	public static final String FORWARD = "Forward";

	/** the map mapping the keys given as Strings into boolean values */
	public static Map<String, Boolean> mapper = new HashMap<String, Boolean>();;
	
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
		if(getMappedValue(LEFT)){
			positionComponent.pos.x -= inputReactingComponent.speed * delta;
		}
		if(getMappedValue(RIGHT)){
			positionComponent.pos.x += inputReactingComponent.speed * delta;
		}
		if(getMappedValue(BACK)){
			positionComponent.pos.z -= inputReactingComponent.speed * delta;
		}
		if(getMappedValue(FORWARD)){
			positionComponent.pos.z += inputReactingComponent.speed * delta;
		}
		
	}
	
	/**
	 * returns the mapped value for the given key from the mapper variable or false if the given key is not defined 
	 */
	private boolean getMappedValue(String key){
		return (mapper.get(key) != null && mapper.get(key));
	}
}

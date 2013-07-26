package wgProject01.ingameState.gameLogic.systems;

import java.util.HashMap;
import java.util.Map;

import wgProject01.ingameState.gameLogic.components.DirectionComponent;
import wgProject01.ingameState.gameLogic.components.PlayerControlComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.World;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;

/**
 * An entity (processing) system handling the given user commands. The commands are managed with a {@link Map} from Strings to boolean flags. 
 * 
 * @author Mirco
 * 
 */
public class PlayerControlSystem extends EntityProcessingSystem {
	
	/** names for commands */
	/** the digital commands */
	/** for moving left */ 
	public static final String LEFT = "Left";
	/** for moving right */
	public static final String RIGHT = "Right";
	/** for moving down */
	public static final String BACK = "Back";
	/** for moving up */
	public static final String FORWARD = "Forward";
	
	/** the analogue commands */
	/** mouse movement in negative x-direction, i.e. to the left */
	public static final String MOUSE_LEFT  = "MouseLeft";
	/** mouse movement in positive x-direction, i.e. to the right */
	public static final String MOUSE_RIGHT  = "MouseRight";
	/** mouse movement in positive y-direction, i.e. upwards */
	public static final String MOUSE_UP = "MouseUp";
	/** mouse movement in negative x-direction, i.e. downwards */
	public static final String MOUSE_DOWN  = "MouseDown";
	
	/** the {@link Map} mapping the keys given as Strings to boolean values */
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
	ComponentMapper<PlayerControlComponent> playerControlManager;
	/**
	 * 
	 * Automagical creation of a ComponentMapper to extract a component from the
	 * entities.
	 */
	@Mapper
	ComponentMapper<DirectionComponent> directionComponentManager;

	/**
	 * constructs a new PlayerControlSystem for all Entities that have a
	 * InputReactionComponent and a PositionComponent
	 */
	@SuppressWarnings("unchecked")
	public PlayerControlSystem() {
		super(Aspect.getAspectForAll(PlayerControlComponent.class,
				PositionComponent.class));
	}

	/**
	 * <p>
	 * The Artemis framework calls this method automatically once every time
	 * {@link World#process()} is called.
	 * </p>
	 * 
	 * <p>
	 * Works off the Commands given to the entity
	 * </p>
	 */
	@Override
	protected void process(Entity e) {
		float delta = world.getDelta();
		PositionComponent positionComponent = positionManager.get(e);
		PlayerControlComponent inputReactingComponent = playerControlManager
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

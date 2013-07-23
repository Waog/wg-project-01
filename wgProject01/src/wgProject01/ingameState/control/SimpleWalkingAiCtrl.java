package wgProject01.ingameState.control;

import java.util.Random;

import wgProject01.ingameState.model.PositionComponent;

import com.artemis.Aspect;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.annotations.Mapper;
import com.artemis.systems.EntityProcessingSystem;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * This is a Control to handle simple
 * random walking. Walking means translation on the x-z-plane.
 * 
 * <p>
 * Attach it to any entity which shall randomly walk around.
 * </p>
 * 
 * @author oli
 * 
 */
public class SimpleWalkingAiCtrl extends EntityProcessingSystem {
	
	@Mapper ComponentMapper<PositionComponent> positionManager;

	/**
	 * The current walking direction. Must be a normalized vector.
	 */
	private Vector3f curDirection = new Vector3f(1, 0, 0);

	/**
	 * The seconds left, until the control switches the walking direction the
	 * next time.
	 */
	private float leftSecs = -1;

	/**
	 * The walking speed.
	 */
	private float speed = 1;

	/**
	 * The maximum number of seconds the spatial shall move into one direction.
	 */
	private float maxSecondsToOneDirection = 5;

	/**
	 * Creates a default walking control.
	 */
	@SuppressWarnings("unchecked")
	SimpleWalkingAiCtrl() {
		super(Aspect.getAspectForAll(PositionComponent.class));
	}

	/**
	 * Sets the walking speed.
	 */
	void setSpeed(float walkSpeed) {
		this.speed = walkSpeed;
	}

	/**
	 * Sets the maximum number of seconds the spatial shall move into one
	 * direction.
	 */
	void setSwitchDirectionInterval(float maxSecondsToOneDirection) {
		this.maxSecondsToOneDirection = maxSecondsToOneDirection;
	}

	/**
	 * TODO: This method should be called automatically every frame.
	 * 
	 * Moves the entity into one direction. If moved long enough, randomly
	 * defines a new walking direction and walking duration.
	 */
	@Override
	protected void process(Entity e) {
		// TODO 1: Bad workaround: set down the tpf for movement, to prevent
		// wall slipping
		float timeDelta = world.getDelta();

		if (leftSecs < 0) {
			Random random = new Random();
			leftSecs = random.nextFloat() * maxSecondsToOneDirection;
			curDirection.x = random.nextFloat() - 0.5f;
			curDirection.y = 0;
			curDirection.z = random.nextFloat() - 0.5f;
			curDirection.normalizeLocal();
		}

		Vector3f moveOffset = curDirection.mult(timeDelta).mult(speed);
		PositionComponent position = positionManager.get(e);
		position.x += moveOffset.x;
		position.y += moveOffset.y;
		position.z += moveOffset.z;
		leftSecs -= timeDelta;
	}
}

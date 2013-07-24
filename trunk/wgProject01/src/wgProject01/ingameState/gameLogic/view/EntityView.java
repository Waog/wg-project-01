package wgProject01.ingameState.gameLogic.view;

import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Entity;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * Wraps an (Artemis) Entity object and is attached to a visual model (a JME
 * spatial). Renders the spatial, to represent the wrapped entity best.
 * 
 * @author oli
 * 
 */
public class EntityView extends AbstractControl {

	/**
	 * The wrapped entity, which is visualized by this view.
	 */
	private Entity entity;

	/**
	 * Constructs a new View for the given entity.
	 */
	public EntityView(Entity entity) {
		this.entity = entity;
	}

	/**
	 * <p>
	 * JME3 calls this method automatically every frame.
	 * <p>
	 * 
	 * <p>
	 * Updates the spatial to which this view is attached to best match the
	 * wrapped entity.
	 * <p>
	 */
	@Override
	protected void controlUpdate(float tpf) {
		// set the position according to the entities position if it has one.
		PositionComponent positionComponent = entity
				.getComponent(PositionComponent.class);
		if (positionComponent != null) {
			spatial.setLocalTranslation(positionComponent.pos);
		}
	}

	/**
	 * JME3 calls this method automatically every frame. Does nothing.
	 */
	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}

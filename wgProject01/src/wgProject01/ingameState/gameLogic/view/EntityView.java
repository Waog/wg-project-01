package wgProject01.ingameState.gameLogic.view;

import wgProject01.ingameState.gameLogic.model.PositionComponent;

import com.artemis.Entity;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Wraps an (Artemis) Entity object and is attached to a visual model (a JME
 * spatial). Renders the spatial, to best represent the wrapped entity best.
 * 
 * @author oli
 * 
 */
public class EntityView extends AbstractControl {

	private Entity entity;

	public EntityView(Entity entity) {
		this.entity = entity;
	}

	@Override
	protected void controlUpdate(float tpf) {
		System.out.println("entity update");
		
		// set the position according to the entities position if it has one.
		PositionComponent positionComponent = entity
				.getComponent(PositionComponent.class);
		if (positionComponent != null) {
			System.out.println("entity position update");
			spatial.setLocalTranslation(positionComponent.x,
					positionComponent.y, positionComponent.z);
		}
	}

	@Override
	protected void controlRender(RenderManager rm, ViewPort vp) {
		// nothing
	}

}

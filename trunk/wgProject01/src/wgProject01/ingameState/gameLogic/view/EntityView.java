package wgProject01.ingameState.gameLogic.view;

import jm3Utils.Jme3Utils;
import wgProject01.Settings;
import wgProject01.ingameState.gameLogic.components.CollisionBoxComponent;
import wgProject01.ingameState.gameLogic.components.DirectionComponent;
import wgProject01.ingameState.gameLogic.components.PlayerControlComponent;
import wgProject01.ingameState.gameLogic.components.PointLightComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Entity;
import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.LightControl;

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

	/** the AssetManager of the simpleApplication */
	AssetManager assetManager;

	/**
	 * the Node containing mostly the entities, but now also the component box
	 * of it
	 */
	Node entityNode;

	/** for testing: geometry representing the collision box */
	private Geometry collisionBoxGeometry;

	/** for testing: line representing the direction of the entity */
	private Spatial directionLineSpatial;

	private PointLight pointLightSource;

	private Node rootNode;

	/**
	 * Constructs a new View for the given entity.
	 */
	public EntityView(Entity entity, Node rootNode) {
		this.entity = entity;
		this.rootNode = rootNode;
	}

	/** initializes the entity viewers datafields */
	public void init(AssetManager assetManager, Node entityNode) {
		this.assetManager = assetManager;
		this.entityNode = entityNode;

		// TODO 2 set the right debug mode
		if (Settings.debugMode >= 2) {
			PositionComponent positionComponent = entity
					.getComponent(PositionComponent.class);

			CollisionBoxComponent collisionBoxComponent = entity
					.getComponent(CollisionBoxComponent.class);
			if (collisionBoxComponent != null && positionComponent != null) {
				collisionBoxGeometry = Jme3Utils.getCuboid(
						collisionBoxComponent.radii, assetManager);
				collisionBoxGeometry.getMaterial().getAdditionalRenderState()
						.setWireframe(true);
				collisionBoxGeometry.setLocalTranslation(positionComponent.pos);
				entityNode.attachChild(collisionBoxGeometry);
			}
			DirectionComponent directionComponent = entity
					.getComponent((DirectionComponent.class));
			if (directionComponent != null && positionComponent != null) {
				directionLineSpatial = Jme3Utils.drawLine(new Vector3f(),
						new Vector3f(0, 0, 3), entityNode, assetManager);
			}
		}
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

			if (!positionComponent.visible) {
				// TODO: find a better architectural solution
				// removing spatial from the scene graph causes this control to
				// not receive update-calls anymore.
				// make an entity invisible by moving it far away.
				spatial.setLocalTranslation(Float.MAX_VALUE / 2,
						Float.MAX_VALUE / 2, Float.MAX_VALUE / 2);
			}
		}
		DirectionComponent directionComponent = entity
				.getComponent((DirectionComponent.class));
		PlayerControlComponent playerControlComponent = entity
				.getComponent(PlayerControlComponent.class);

		// TODO: better architecture!!!
		// case that the entity is a block face highlight
		if (directionComponent != null && positionComponent != null
				&& playerControlComponent == null
				&& spatial.getName().equals("block face highlight")) {
			spatial.rotateUpTo(directionComponent.getSwitchedCartesianDirection());
//			spatial.lookAt(positionComponent.pos.add(directionComponent
//					.getSwitchedCartesianDirection()), new Vector3f(0, 1, 0));
		}

		// case that the entity is a npc
		if (directionComponent != null && positionComponent != null
				&& playerControlComponent == null) {
			spatial.lookAt(positionComponent.pos.add(directionComponent
					.getCatesianProjectedDirectionXZ()), new Vector3f(0, 1, 0));
		}
		// TODO 2 is this extra case neccesary
		// case that the entity is a playable character
		// oli: possibly yes, since we dont want to display the player at all
		// from ego perspective.
		else if (directionComponent != null && positionComponent != null
				&& playerControlComponent != null) {
			Vector3f spatialDirection = directionComponent
					.getSwitchedCatesianProjectedDirectionXZ();
			spatial.lookAt(positionComponent.pos.add(spatialDirection),
					new Vector3f(0, 1, 0));

		}

		PointLightComponent pointLightComponent = entity
				.getComponent((PointLightComponent.class));
		if (pointLightComponent != null && positionComponent != null) {
			if (this.pointLightSource == null) {
				// the point light
				this.pointLightSource = new PointLight();
				pointLightSource.setColor(pointLightComponent.color);
				pointLightSource.setRadius(pointLightComponent.radius);
				rootNode.addLight(pointLightSource);
				LightControl lightControl = new LightControl(pointLightSource);
				spatial.addControl(lightControl); // this spatial controls the
				// position of this light.
			}
			pointLightSource.setColor(pointLightComponent.color);
			pointLightSource.setRadius(pointLightComponent.radius);
		}

		// TODO 2 set the right debug mode
		if (Settings.debugMode >= 2) {
			// show the collision box
			CollisionBoxComponent collisionBoxComponent = entity
					.getComponent(CollisionBoxComponent.class);
			if (collisionBoxComponent != null && positionComponent != null
					&& collisionBoxGeometry != null) {
				collisionBoxGeometry.setLocalTranslation(positionComponent.pos);
				entityNode.attachChild(collisionBoxGeometry);
			}
		}

		if (Settings.debugMode >= 2) {
			if (directionComponent != null && positionComponent != null) {
				directionLineSpatial.setLocalTranslation(positionComponent.pos);

				Vector3f direction = directionComponent
						.getSwitchedCartesianDirection();
				directionLineSpatial.lookAt(positionComponent.pos
						.add(direction), new Vector3f(0, 1, 0));

			}
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

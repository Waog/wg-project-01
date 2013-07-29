package wgProject01.ingameState.gameLogic.view;

import jm3Utils.Jme3Utils;
import wgProject01.Settings;
import wgProject01.ingameState.gameLogic.components.CollisionBoxComponent;
import wgProject01.ingameState.gameLogic.components.DirectionComponent;
import wgProject01.ingameState.gameLogic.components.HighlightComponent;
import wgProject01.ingameState.gameLogic.components.PlayerControlComponent;
import wgProject01.ingameState.gameLogic.components.PointLightComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;

import com.artemis.Entity;
import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
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

	/** The AssetManager of the simpleApplication. */
	public static AssetManager assetManager;

	/** The jme3 camera object. */
	public static Camera cam;

	/**
	 * The parent node containing a spatial of each entity.
	 */
	Node entityNode;

	/** For viewing in debug mode: geometry representing the collision box */
	private Geometry collisionBoxGeometry;

	/** For viewing in debug mode: line representing the direction of the entity */
	private Spatial directionLineSpatial;

	/**
	 * The JME3 {@link PointLight} which is moves with the wrapped entity, if it
	 * has a {@link PointLightComponent}, or just null for all other entities.
	 */
	private PointLight pointLightView;

	/**
	 * The JME3 root node. Currently only used to attach a
	 * {@link #pointLightView} to it.
	 */
	public static Node rootNode;

	/**
	 * Constructs a new View for the given entity.
	 */
	public EntityView(Entity entity, Node entityNode) {
		this.entity = entity;
		this.entityNode = entityNode;
		initDebugVisuals();
	}

	/** Initializes debug visuals (collision box and direction line). */
	public void initDebugVisuals() {
		if (Settings.debugMode >= 2) {
			// init the collision box
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

			// init the direction line
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
		// extract the components from the entity (may be null)
		PositionComponent positionComponent = entity
				.getComponent(PositionComponent.class);
		DirectionComponent directionComponent = entity
				.getComponent((DirectionComponent.class));
		PlayerControlComponent playerControlComponent = entity
				.getComponent(PlayerControlComponent.class);
		HighlightComponent highlightComponent = entity
				.getComponent(HighlightComponent.class);
		PointLightComponent pointLightComponent = entity
				.getComponent((PointLightComponent.class));

		// ---- POSITION ----
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

		// ------ DIRECTION of entities ------
		if (directionComponent != null) {
			Quaternion rotQuaternion = new Quaternion();
			rotQuaternion.lookAt(directionComponent
					.getSwitchedCatesianProjectedDirectionXZ(), new Vector3f(0,
					1, 0));
			spatial.setLocalRotation(rotQuaternion);
		}

		// ------ DIRECTION & CAMERA of player-entity ------
		if (directionComponent != null && positionComponent != null
				&& playerControlComponent != null) {
			Vector3f spatialDirection = directionComponent
					.getSwitchedCatesianProjectedDirectionXZ();
			spatial.lookAt(positionComponent.pos.add(spatialDirection),
					new Vector3f(0, 1, 0));

			cam.setLocation(positionComponent.pos);
			Quaternion rotQuaternion = new Quaternion();
			rotQuaternion.lookAt(
					directionComponent.getSwitchedCartesianDirection(),
					new Vector3f(0, 1, 0));
			cam.setRotation(rotQuaternion);
		}

		// ------ DIRECTION of highlight entity ------
		if (directionComponent != null && positionComponent != null
				&& highlightComponent != null) {
			Vector3f highlightDir = directionComponent
					.getSwitchedCartesianDirection();
			Vector3f yDirection = new Vector3f(highlightDir.z, highlightDir.x,
					highlightDir.y);
			spatial.lookAt(positionComponent.pos.add(highlightDir), yDirection);
		}

		// ------ POINT LIGHT ------
		if (pointLightComponent != null && positionComponent != null) {
			if (this.pointLightView == null) {
				this.pointLightView = new PointLight();
				rootNode.addLight(pointLightView);
				LightControl lightControl = new LightControl(pointLightView);
				spatial.addControl(lightControl); // this spatial controls the
				// position of this light.
			}
			pointLightView.setColor(pointLightComponent.color);
			pointLightView.setRadius(pointLightComponent.radius);
		}

		// ------- COLLISION BOX (for debugging) -----
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

		// ------- DIRECTION LINE (for debugging) -----
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

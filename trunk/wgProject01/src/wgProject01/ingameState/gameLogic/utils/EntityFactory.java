package wgProject01.ingameState.gameLogic.utils;

import java.util.Random;

import jm3Utils.Jme3Utils;
import wgProject01.ingameState.gameLogic.GameLogic;
import wgProject01.ingameState.gameLogic.components.CollisionBoxComponent;
import wgProject01.ingameState.gameLogic.components.DirectionComponent;
import wgProject01.ingameState.gameLogic.components.GravitationComponent;
import wgProject01.ingameState.gameLogic.components.HighlightComponent;
import wgProject01.ingameState.gameLogic.components.PlayerControlComponent;
import wgProject01.ingameState.gameLogic.components.PointLightComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;
import wgProject01.ingameState.gameLogic.components.OrbitingPropertiesComponent;
import wgProject01.ingameState.gameLogic.components.WalkingAiComponent;
import wgProject01.ingameState.gameLogic.view.EntityView;

import com.artemis.Entity;
import com.artemis.World;
import com.jme3.asset.AssetManager;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

/**
 * TODO: totally refactor this class...
 * 
 * Class which provides static methods, to generate new entities, generates their
 * views, attach them to the world and to the scene graph with a single call.
 * 
 * Uses the singleton pattern.
 * 
 * @author oli
 * 
 */
public class EntityFactory {

	/**
	 * The singleton instance of this class.
	 */
	private static EntityFactory singletonInstance;

	/**
	 * The JME3 {@link Node} to which all entity visuals are attached.
	 */
	private static Node entityNode;

	/**
	 * The JME3 {@link AssetManager} to load the assets of the created entities.
	 */
	private static AssetManager assetManager;

	/**
	 * Private constructor to enforce the use of the {@link #getInstance()}
	 * method.
	 */
	private EntityFactory() {
	}

	/**
	 * Returns the singleton instance of the block manager. The
	 * {@link #initData(Node, AssetManager)} method has to be called on this
	 * instance once before using it.
	 */
	static EntityFactory getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new EntityFactory();
		}

		return singletonInstance;
	}

	/**
	 * Initializes the EntityFactory with it's needed data. This method needs to
	 * be called once before using the instance of the block manager.
	 * 
	 * @param blockNode
	 *            the Node to which all blocks of the BlockManager shall be
	 *            attached.
	 */
	public static void initData(Node entityNode, AssetManager assetManager,
			Node lightNode) {
		// TODO architecture: demand rootNode as parameter, instead of 2 subnodes.
		EntityFactory.entityNode = entityNode;
		EntityFactory.assetManager = assetManager;
	}

	/**
	 * Initializes a randomly colored sun, with a random rotation speed, which
	 * uses the {@link OrbitingSystem} to fly around and acts as a point light
	 * of it's color source as well.
	 * 
	 * @see PointLightComponent
	 * @see PointLight
	 */
	public static Entity createSun(World world) {
		// some properties which determine the suns appearance and behavior.
		float innerRadius = 20;
		float outerRadius = 30;
		float rotationRadius = GameLogic.FLOOR_RADIUS + 2 * outerRadius;
		ColorRGBA innerColor = ColorRGBA.randomColor();
		ColorRGBA outerColor = innerColor.clone();
		outerColor.a = .5f;
		Random rand = new Random();
		float randomSpeedTheta = 0.1f * rand.nextFloat();
		float randomSpeedPhi = 0.1f * rand.nextFloat();

		// Creates the entity + components, adds it to the world and returns it.
		Entity e = world.createEntity();

		PositionComponent position = new PositionComponent();
		position.pos = new Vector3f(0, 0, 0);
		e.addComponent(position);

		OrbitingPropertiesComponent rotationPropertiesComponent = new OrbitingPropertiesComponent();
		rotationPropertiesComponent.center = new Vector3f(0, 0, 0);
		rotationPropertiesComponent.radius = rotationRadius;
		rotationPropertiesComponent.speeds = new Vector2f(randomSpeedTheta,
				randomSpeedPhi);
		e.addComponent(rotationPropertiesComponent);

		PointLightComponent pointLightComponent = new PointLightComponent();
		pointLightComponent.color.set(innerColor);
		e.addComponent(pointLightComponent);

		e.addToWorld();

		// create the spatials and attach them to each other
		Node sunNode = new Node("sun node");
		sunNode.setName("sun");
		entityNode.attachChild(sunNode);

		// inner non-transparent sphere
		Mesh sphereMesh = new Sphere(20, 20, innerRadius);
		Spatial innerSpacial = new Geometry("Sphere", sphereMesh);
		innerSpacial.setName("suns inner sphere");
		Material innerMat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		innerMat.setColor("Color", innerColor);
		innerSpacial.setMaterial(innerMat);
		sunNode.attachChild(innerSpacial);

		// outer semi transparent sphere
		Sphere sphereMesh2 = new Sphere(20, 20, outerRadius);
		Spatial outerSpacial = new Geometry("Sphere", sphereMesh2);
		innerSpacial.setName("suns outer sphere");
		outerSpacial.setQueueBucket(Bucket.Transparent);
		Material outerMat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		outerMat.setColor("Color", outerColor);
		outerMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // !
		outerSpacial.setMaterial(outerMat);
		sunNode.attachChild(outerSpacial);

		// make it visible (connect model and view)
		EntityView entityView = new EntityView(e, entityNode);
		sunNode.addControl(entityView);

		return e;
	}

	/**
	 * Creates a new Enemy at the given position.
	 */
	public static Entity createEnemy(World world, Vector3f pos) {
		// Creates the entity + components, adds it to the world and returns it.
		Entity e = world.createEntity();

		PositionComponent position = new PositionComponent();
		position.pos.set(pos);
		e.addComponent(position);

		WalkingAiComponent walkingAiComponent = new WalkingAiComponent();
		e.addComponent(walkingAiComponent);

		Vector3f collisionBoxRadii = new Vector3f(1f, 2.35f, 1f);
		CollisionBoxComponent collisionBoxComponent = new CollisionBoxComponent(
				collisionBoxRadii);
		e.addComponent(collisionBoxComponent);

		// add Gravitation
		GravitationComponent gravitationComponent = new GravitationComponent();
		e.addComponent(gravitationComponent);

		// add direction
		DirectionComponent directionComponent = new DirectionComponent();
		e.addComponent(directionComponent);

		e.addToWorld();

		// creates the view for this enemy and attaches the entity to it.
		Spatial spatial = assetManager
				.loadModel("./assets/Models/Oto/Oto.mesh.xml");
		spatial.setName("enemy");
		spatial.scale(0.5f);
		entityNode.attachChild(spatial);

		// make it visible (connect model and view)
		EntityView entityView = new EntityView(e, entityNode);
		spatial.addControl(entityView);

		return e;
	}

	/**
	 * TODO: write comment
	 */
	public static Entity createBlockFaceHighlight(World world) {
		// Creates the entity + components, adds it to the world.
		Entity e = world.createEntity();
		PositionComponent position = new PositionComponent();
		e.addComponent(position);
		DirectionComponent direction = new DirectionComponent();
		e.addComponent(direction);
		HighlightComponent highlight = new HighlightComponent();
		e.addComponent(highlight);
		e.addToWorld();

		// creates a spatial for the entity
		Geometry geometry = Jme3Utils.getCuboid(new Vector3f(.5f, .5f, 0.01f),
				assetManager);
		geometry.setName("block face highlight");
//		geometry.getMaterial().getAdditionalRenderState().setWireframe(true);
		entityNode.attachChild(geometry);

		// make it visible (connect model and view)
		EntityView entityView = new EntityView(e, entityNode);
		geometry.addControl(entityView);

		return e;
	}

	/**
	 * Creates a small visible cubic entity at the given position.
	 */
	public static Entity createSmallCube(World world, Vector3f pos) {
		// Creates the entity + components, adds it to the world and returns it.
		Entity e = world.createEntity();

		// add position
		PositionComponent position = new PositionComponent();
		position.pos.set(pos);
		e.addComponent(position);

		e.addToWorld();

		Spatial spatial = Jme3Utils.getCubeGeom(0.1f, assetManager);
		spatial.setName("small cube");
		entityNode.attachChild(spatial);

		// make it visible (connect model and view)
		EntityView entityView = new EntityView(e, entityNode);
		spatial.addControl(entityView);

		return e;
	}

	/**
	 * Creates a new Enemy at the given position.
	 */
	public static Entity createPlayer(World world, Vector3f pos) {
		// Creates the entity + components, adds it to the world and returns it.
		Entity e = world.createEntity();

		// add position
		PositionComponent position = new PositionComponent();
		position.pos.set(pos);
		e.addComponent(position);

		// add collision
		Vector3f collisionBoxRadii = new Vector3f(1f, 2.35f, 1f);
		CollisionBoxComponent collisionBoxComponent = new CollisionBoxComponent(
				collisionBoxRadii);
		e.addComponent(collisionBoxComponent);

		// add gravitation
		GravitationComponent gravitationComponent = new GravitationComponent();
		e.addComponent(gravitationComponent);

		// add direction
		DirectionComponent directionComponent = new DirectionComponent();
		e.addComponent(directionComponent);

		// add input handling
		PlayerControlComponent playerControlComponent = new PlayerControlComponent();
		e.addComponent(playerControlComponent);

		e.addToWorld();

		Spatial spatial = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
		spatial.setName("player");
		spatial.scale(0.5f);
		entityNode.attachChild(spatial);

		// make it visible (connect model and view)
		EntityView entityView = new EntityView(e, entityNode);
		spatial.addControl(entityView);

		return e;
	}
}

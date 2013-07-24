package wgProject01.ingameState.gameLogic.utils;

import wgProject01.ingameState.gameLogic.components.CollisionBoxComponent;
import wgProject01.ingameState.gameLogic.components.GravitationComponent;
import wgProject01.ingameState.gameLogic.components.PositionComponent;
import wgProject01.ingameState.gameLogic.components.WalkingAiComponent;
import wgProject01.ingameState.gameLogic.view.EntityView;

import com.artemis.Entity;
import com.artemis.World;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 * Class which provies static methods, to generate new entities, generates their
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
	public static void initData(Node entityNode, AssetManager assetManager) {
		EntityFactory.entityNode = entityNode;
		EntityFactory.assetManager = assetManager;
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

		Vector3f collisionBoxRadii = new Vector3f(0.5f, 1.5f, 0.5f);
		CollisionBoxComponent collisionBoxComponent = new CollisionBoxComponent(
				collisionBoxRadii);
		e.addComponent(collisionBoxComponent);
		
		// add Gravitation
		GravitationComponent gravitationComponent = new GravitationComponent();
		e.addComponent(gravitationComponent);

		e.addToWorld();

		Spatial golem = assetManager.loadModel("Models/Oto/Oto.mesh.xml");
		golem.scale(0.5f);
		// creates the view for this enemy and attaches the entity to it.
	/*	Box mesh = new Box(collisionBoxRadii.x, collisionBoxRadii.y,
				collisionBoxRadii.z);
		Geometry geometry = new Geometry("Block", mesh);

		Material enemyMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		enemyMaterial.setColor("Color", ColorRGBA.Red);
		geometry.setMaterial(enemyMaterial);
		geometry.setLocalTranslation(0, -1, 0);
		entityNode.attachChild(geometry);*/
		entityNode.attachChild(golem);

		// make it walk
		EntityView entityView = new EntityView(e);
		entityView.init(assetManager, entityNode);
		golem.addControl(entityView);

		return e;
	}
}

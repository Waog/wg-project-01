package wgProject01.ingameState;

import java.util.Random;

import jm3Utils.Jm3Utils;
import wgProject01.GameApplication;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;

public class IngameState extends AbstractAppState implements ActionListener {

	private GameApplication app;
	private Node rootNode, guiNode;
	private AssetManager assetManager;
	private AppStateManager stateManager;
	private InputManager inputManager;
	private ViewPort viewPort;
	private Camera cam;

	private static final String PLACE_BLOCK = "PlaceBlock";
	private static final String JUMP = "Jump";
	private static final String DOWN = "Down";
	private static final String UP = "Up";
	private static final String RIGHT = "Right";
	private static final String LEFT = "Left";
	private static final String MINE_BLOCK = "MineBlock";
	private static final String SHOOTABLES = "Shootables";
	private static final String MINEABLES = "Mineables";
	private static final String INVENTORY_NODE = "InventoryNode";
	private static final float RAY_LIMIT = 6; // defines how long the shot ray
												// for picking and placing is
	protected Geometry player;
	private Geometry highlightedBlockFace = new Geometry();
	private Material matGrid;
	Boolean isRunning = true;
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;

	private CharacterControl playerPhys;
	private BulletAppState bulletAppState;
	private double sunPosition = 0.0f;
	private CollisionResults results = new CollisionResults();

	private Ray ray = new Ray();

	// Temporary vectors used on each frame.
	// They here to avoid instanciating new vectors on each frame
	private Vector3f camDir = new Vector3f();
	private Vector3f camLeft = new Vector3f();
	private Vector3f newSunPos = new Vector3f();
	private Vector3f faceVector = new Vector3f();
	private Vector3f faceVectorNegated = new Vector3f();
	private Vector3f projectedVector = new Vector3f();

	private Node shootables; // contains all spatials onto which a block can be
								// set
	private Node mineables; // contains all spatials that can be picked up e.g.
							// not the floor
	private Node inventoryNode; // this one handles the ingame inventory
								// spatials
	private FlyByCamera flyCam;
	private final int FLOOR_RADIUS = 10;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		this.app = (GameApplication) app; // cast to a more specific class
		this.rootNode = this.app.getRootNode();
		this.assetManager = this.app.getAssetManager();
		this.stateManager = this.app.getStateManager();
		this.inputManager = this.app.getInputManager();
		this.viewPort = this.app.getViewPort();
		this.stateManager.getState(BulletAppState.class);
		this.cam = this.app.getCamera();
		this.flyCam = this.app.getFlyByCamera();
		this.guiNode = this.app.getGuiNode();

		initBlockManager();

		// TODO 1: remove debug code:
		// draw the coordinate system:
		Jm3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0),
				rootNode, assetManager);
		Jm3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0),
				rootNode, assetManager);
		Jm3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1),
				rootNode, assetManager);

		// init stuff that is independent of whether state is PAUSED or RUNNING
		guiNode.addLight(new AmbientLight());

		initNodes();
		initCrossHairs();
		initPhysics();
		initFloor();
		// initOneBlockFloor(); // take either one of the floor initializations
		initKeys(); // load my custom keybinding
		initGeneralLights();
		initRandomSun();
		initMaterials();

		highlightedBlockFace.setMaterial(matGrid);
		highlightedBlockFace.setMesh(new Box(0.5f, 0f, 0.5f));
		highlightedBlockFace.setLocalTranslation(0, 2, 0);
		rootNode.attachChild(highlightedBlockFace);
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f)); // makes
																		// the
																		// background
																		// somewhat
																		// blue
		// TODO 2 the movementspeed setting does not work at all
		flyCam.setMoveSpeed(2500);

		initTestBlock();
		initTestEnemy();
	}

	private void initTestEnemy() {
		Box mesh = new Box(0.5f, 0.5f, 0.5f);
		Geometry geometry = new Geometry("Block", mesh);

		Material enemyMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		enemyMaterial.setColor("Color", ColorRGBA.Red);
		geometry.setMaterial(enemyMaterial);
		geometry.setLocalTranslation(0, 5.5f, 0);
		rootNode.attachChild(geometry);

		// make it walk
		SimpleWalkingAiControl walkControl = new SimpleWalkingAiControl();
		walkControl.setSpeed(4);
		geometry.addControl(walkControl);

		// make it colide with blocks
		BlockCollisionControl blockCollisionControl = new BlockCollisionControl(
				new Vector3f(1, 1, 1));
		geometry.addControl(blockCollisionControl);
	}

	private void initTestBlock() {
		Box mesh = new Box(0.5f, 0.5f, 0.5f);
		Geometry geometry = new Geometry("Block", mesh);
		geometry.setQueueBucket(Bucket.Transparent);

		Material debugMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		ColorRGBA randomColor = ColorRGBA.randomColor();
		randomColor.a = 0.8f;
		debugMaterial.setColor("Color", randomColor);
		debugMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // !

		geometry.setMaterial(debugMaterial);

		geometry.setLocalTranslation(0, 10, 0);
		rootNode.attachChild(geometry);

		// make it rotate
		// the rotational movement
		RotationControl rotationControl = new RotationControl(8, 8, 8);
		rotationControl.setSpeeds(new Vector3f(1, 1, 1));
		geometry.addControl(rotationControl);

		// make it colide with blocks
		BlockCollisionControl blockCollisionControl = new BlockCollisionControl(
				new Vector3f(1, 1, 1));
		geometry.addControl(blockCollisionControl);
	}

	/**
	 * Initializes the block Manager and some blocks for testing.
	 */
	private void initBlockManager() {
		// initialize the block manager
		BlockManager blockManager = BlockManager.getInstance();
		Node blockNode = new Node();
		rootNode.attachChild(blockNode);
		blockManager.initData(blockNode, assetManager);

		int POS_OFFSET = 0;

		// initialize blocks using the block manager
		for (int x = -FLOOR_RADIUS; x <= FLOOR_RADIUS; x++) {
			for (int z = -FLOOR_RADIUS; z <= FLOOR_RADIUS; z++) {
				BlockGameObj newBlock = blockManager.getBlockGameObj();
				blockManager.setBlock(x + POS_OFFSET, 4, z + POS_OFFSET,
						newBlock);

				if (Math.abs(x) >= FLOOR_RADIUS - 1
						|| Math.abs(z) >= FLOOR_RADIUS - 1) {
					BlockGameObj newBlock2 = blockManager.getBlockGameObj();
					blockManager.setBlock(x + POS_OFFSET, 5, z + POS_OFFSET,
							newBlock2);
					BlockGameObj newBlock3 = blockManager.getBlockGameObj();
					blockManager.setBlock(x + POS_OFFSET, 6, z + POS_OFFSET,
							newBlock3);
				}
			}
		}
	}

	/**
	 * Initializes a randomly colored sun, with a random rotation speed, which
	 * uses the rotation control to fly around and acts as a point light of it's
	 * color source as well.
	 */
	private void initRandomSun() {
		float geometryRadius = 20;
		float geometryRadius2 = 30;
		float rotationRadius = FLOOR_RADIUS + 2 * geometryRadius2;
		ColorRGBA innerColor = ColorRGBA.randomColor();
		ColorRGBA outerColor = innerColor.clone();
		outerColor.a = .5f;
		Random rand = new Random();
		float randomSpeedX = rand.nextFloat();
		float randomSpeedY = rand.nextFloat();
		float randomSpeedZ = rand.nextFloat();

		Node sunNode = new Node();
		rootNode.attachChild(sunNode);

		// inner non-transparent sphere
		Mesh sphereMesh = new Sphere(20, 20, geometryRadius);
		Spatial sphereSpacial = new Geometry("Sphere", sphereMesh);
		Material sphereMat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		sphereMat.setColor("Color", innerColor);
		sphereSpacial.setMaterial(sphereMat);
		sunNode.attachChild(sphereSpacial);

		// outer semi transparent sphere
		Sphere sphereMesh2 = new Sphere(20, 20, geometryRadius2);
		Spatial sphereSpacial2 = new Geometry("Sphere", sphereMesh2);
		sphereSpacial2.setQueueBucket(Bucket.Transparent);
		Material mat_aSun2 = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat_aSun2.setColor("Color", outerColor);
		mat_aSun2.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // !
		sphereSpacial2.setMaterial(mat_aSun2);
		sunNode.attachChild(sphereSpacial2);

		// the point light
		PointLight myLight = new PointLight();
		myLight.setColor(innerColor);
		rootNode.addLight(myLight);
		LightControl lightControl = new LightControl(myLight);
		sphereSpacial.addControl(lightControl); // this spatial controls the
												// position of this light.

		// the rotational movement
		RotationControl rotationControl = new RotationControl(rotationRadius,
				rotationRadius, rotationRadius);
		rotationControl.setSpeeds(new Vector3f(randomSpeedX, randomSpeedY,
				randomSpeedZ));
		sunNode.addControl(rotationControl);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		// unregister all my listeners, detach all my nodes, etc...
		// nothing
	}

	@Override
	public void setEnabled(boolean enabled) {
		// Pause and unpause
		super.setEnabled(enabled);
		if (enabled) {
			// init stuff that is in use while this state is RUNNING
			// nothing
		} else {
			// take away everything not needed while this state is PAUSED
			// nothing
		}
	}

	// Note that update is only called while the state is both attached and
	// enabled.
	@Override
	public void update(float tpf) {
		// do the following while game is RUNNING
		camDir.set(cam.getDirection()).multLocal(0.6f);
		camLeft.set(cam.getLeft()).multLocal(0.4f);
		walkDirection.set(0, 0, 0);
		if (left) {
			walkDirection.addLocal(camLeft);
		}
		if (right) {
			walkDirection.addLocal(camLeft.negate());
		}
		if (up) {
			walkDirection.addLocal(camDir);
		}
		if (down) {
			walkDirection.addLocal(camDir.negate());
		}
		playerPhys.setWalkDirection(walkDirection);
		cam.setLocation(playerPhys.getPhysicsLocation());
		highlightBlockFace();
	}

	private void highlightBlockFace() {

		results.clear();
		results = shootRay(RAY_LIMIT);
		if (results.size() > 0) {
			// Vector3f viewPoint = calculateBlockLocation(results);
			// Box shape = new Box(0f, 0.5f, 100f);
			// Geometry geometry = new Geometry("Block", shape);
			CollisionResult closest = results.getClosestCollision();
			Geometry geom = closest.getGeometry();
			Vector3f selectedBlockPos = geom.getWorldTranslation();
			Vector3f vectorToNeighbor = getVectorToNeighbor(geom,
					closest.getContactPoint());
			Vector3f halfVecToNeigh = vectorToNeighbor.mult(0.51f);

			// this is supposed to be a bit in front of the last block in a
			// local coordinate system relative to the mid point of the hit
			// geometry
			highlightedBlockFace.setLocalTranslation(selectedBlockPos
					.add(halfVecToNeigh));

			highlightedBlockFace.rotateUpTo(vectorToNeighbor);
			rootNode.attachChild(highlightedBlockFace);
		} else {
			highlightedBlockFace.removeFromParent();
		}
	}

	/**
	 * initializes the most important nodes and attaches them to their specific
	 * position
	 */
	private void initNodes() {
		// contains all nodes and geometries where player is able to put things
		// at, or to mine it
		shootables = new Node(SHOOTABLES);
		rootNode.attachChild(shootables);

		// the node containing the blocks that can be picked up
		mineables = new Node(MINEABLES);
		shootables.attachChild(mineables);

		// the node containing the geometries in the inventory
		inventoryNode = new Node(INVENTORY_NODE); // set up the inventory
		guiNode.attachChild(inventoryNode);
		inventoryNode.setLocalTranslation(cam.getWidth() / 2,
				cam.getHeight() / 2 - 20, 0);
		inventoryNode.scale(20);
	}

	/** initializes a "floor" made of one big box - this box is not mineable */
	private void initOneBlockFloor() {
		Box shape = new Box(100f, 0.5f, 100f);
		Geometry geometry = new Geometry("Block", shape);
		// Material mat = new Material(assetManager,
		// "Common/MatDefs/Misc/Unshaded.j3md");
		// mat.setColor("Color", ColorRGBA.randomColor());
		// geometry.setMaterial(mat);

		TangentBinormalGenerator.generate(shape);
		Material sphereMat = new Material(assetManager,
				"assets/Materials/Lighting/Lighting.j3md");

		Texture texture = assetManager.loadTexture("Textures/Pond/Pond.jpg");
		texture.setWrap(WrapMode.Repeat);
		sphereMat.setTexture("DiffuseMap", texture);
		sphereMat.setTexture("NormalMap",
				assetManager.loadTexture("Textures/Pond/Pond_normal.png"));
		sphereMat.setBoolean("UseMaterialColors", true);
		sphereMat.setColor("Diffuse", ColorRGBA.White);
		sphereMat.setColor("Specular", ColorRGBA.White);
		sphereMat.setFloat("Shininess", 64f); // [0,128]
		geometry.setMaterial(sphereMat);

		geometry.setLocalTranslation(new Vector3f(0, 0, 0));

		// the block physics:
		RigidBodyControl blockPhy = new RigidBodyControl(0);
		geometry.addControl(blockPhy);
		blockPhy.setKinematic(true);
		bulletAppState.getPhysicsSpace().add(blockPhy);

		shootables.attachChild(geometry);
	}

	/**
	 * 
	 */
	private void initMaterials() {
		matGrid = new Material(assetManager,
				"assets/Materials/Unshaded/Unshaded.j3md");
		matGrid.setColor("Color", ColorRGBA.LightGray);
		matGrid.getAdditionalRenderState().setWireframe(true);
	}

	/**
	 * initializes a quadratic floor consisting of blocks, FLOOR_RADIUS defines
	 * its size
	 */
	private void initFloor() {
		for (int x = -FLOOR_RADIUS; x <= FLOOR_RADIUS; x++) {
			for (int z = -FLOOR_RADIUS; z <= FLOOR_RADIUS; z++) {
				addBlockAt(x, -2, z);
				addBlockAt(x, -3, z);
				// addBlockAt(x, -2, z);

				if (Math.abs(x) >= FLOOR_RADIUS - 2
						|| Math.abs(z) >= FLOOR_RADIUS - 1) {
					addBlockAt(x, -1, z);
					addBlockAt(x, 0, z);
					addBlockAt(x, 1, z);
				}
			}
		}
	}

	/**
	 * initializes three lights to have a general enlightening setting any
	 * materials visible
	 */
	private void initGeneralLights() {
		// Add an ambient light to make everything visible.
		AmbientLight ambientLight = new AmbientLight();
		ambientLight.setColor(ColorRGBA.Pink);
		rootNode.addLight(ambientLight);

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(1, 0, -1).normalizeLocal());
		sun.setColor(ColorRGBA.DarkGray);
		rootNode.addLight(sun);

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun2 = new DirectionalLight();
		sun2.setDirection(new Vector3f(-1, 1, 0).normalizeLocal());
		sun2.setColor(ColorRGBA.DarkGray);
		rootNode.addLight(sun2);

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun3 = new DirectionalLight();
		sun3.setDirection(new Vector3f(0, -1, 1).normalizeLocal());
		sun3.setColor(ColorRGBA.DarkGray);
		rootNode.addLight(sun3);

	}

	/** A centered plus sign to help the player aim. */
	private void initCrossHairs() {
		app.setDisplayStatView(false);
		BitmapFont guiFont = assetManager
				.loadFont("Interface/Fonts/Default.fnt");
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		ch.setText("+"); // crosshairs
		ch.setLocalTranslation(
		// center
				cam.getWidth() / 2 - ch.getLineWidth() / 2, cam.getHeight() / 2
						+ ch.getLineHeight() / 2, 0);
		guiNode.attachChild(ch);
	}

	/**
	 * Initializes the usage of physics and makes the player/camera collidable.
	 */
	private void initPhysics() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		// bulletAppState.getPhysicsSpace().enableDebug(assetManager);

		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f,
				1.8f, 1);
		playerPhys = new CharacterControl(capsuleShape, 0.05f);
		playerPhys.setJumpSpeed(10f);
		playerPhys.setFallSpeed(30);
		playerPhys.setGravity(0);
		playerPhys.setPhysicsLocation(new Vector3f(0, 5, 0));

		// We attach the scene and the player to the rootnode and the physics
		// space,
		// to make them appear in the game world.
		bulletAppState.getPhysicsSpace().add(playerPhys);
	}

	/**
	 * We over-write some navigational key mappings here, so we can add
	 * physics-controlled walking and jumping:
	 */
	private void initKeys() {
		inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(UP, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(DOWN, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping(PLACE_BLOCK, new MouseButtonTrigger(
				MouseInput.BUTTON_RIGHT));
		inputManager.addMapping(MINE_BLOCK, new MouseButtonTrigger(
				MouseInput.BUTTON_LEFT));
		inputManager.addListener(this, LEFT);
		inputManager.addListener(this, RIGHT);
		inputManager.addListener(this, UP);
		inputManager.addListener(this, DOWN);
		inputManager.addListener(this, JUMP);
		inputManager.addListener(this, PLACE_BLOCK);
		inputManager.addListener(this, MINE_BLOCK);

		// Add the names to the action listener.
	}

	/**
	 * currently picks up a block and puts it into the inventory
	 */
	private void mineBlock() {
		CollisionResults results = shootRay(RAY_LIMIT);
		if (results.size() > 0) {
			CollisionResult closest = results.getClosestCollision();
			Geometry geom = closest.getGeometry();
			Node node = geom.getParent();
			bulletAppState.getPhysicsSpace().remove(geom); // removes the
															// geometry
															// from the
															// physicsSpace
			if (node.getName().equals(MINEABLES)) {
				inventoryNode.attachChild(geom);
			}
			// this one may be important for non-block spatials
			else {
				inventoryNode.attachChild(node); //
			}
		}
	}

	/**
	 * takes a spatial from the inventory and puts it onto the right place
	 */
	private void placeBlockFromInv() {
		if (inventoryNode.getQuantity() > 0) { // check for empty inventoryNode
			CollisionResults results = shootRay(RAY_LIMIT);
			if (results.size() > 0) { // check if there is a hit at all
				Vector3f blockLocation = calculateBlockLocation(results);
				Spatial spaten = inventoryNode.detachChildAt(0);
				spaten.setLocalTranslation(blockLocation);

				addBlockPhysics(spaten, 0);
				mineables.attachChild(spaten); // TODO 3 is there anything that
												// can be set but is not
												// mineable?
				// TODO 2 stop blocks from being placed when player is in the
				// way
			}
		}
	}

	/**
	 * places a block onto the targetted physical object relative to the
	 * targetted face of the block. This method does not use the inventory
	 * instead generates new blocks every time called.
	 */
	private void placeBlock() {
		CollisionResults results = shootRay(RAY_LIMIT);
		if (results.size() > 0) {
			Vector3f blockLocation = calculateBlockLocation(results);

			addBlockAt((int) blockLocation.x, (int) blockLocation.y,
					(int) blockLocation.z);
			// TODO 2 stop blocks from being placed when player is in the way
		}
	}

	/**
	 * calculates the position where the block shall be set and returns it
	 * 
	 * @param results
	 *            the set of results calculated
	 * @return the position of the block to be placed
	 */
	private Vector3f calculateBlockLocation(CollisionResults results) {
		CollisionResult closest = results.getClosestCollision();
		Geometry selectedGeom = closest.getGeometry();
		Vector3f vectorToNeighbor = getVectorToNeighbor(selectedGeom,
				closest.getContactPoint());
		// System.out.println(blockLocation.toString()); // for Testing
		// System.out.println(geom.getLocalTranslation().toString());
		Vector3f blockLocation = selectedGeom.getWorldTranslation().add(
				vectorToNeighbor);
		Jm3Utils.drawLine(selectedGeom.getWorldTranslation(), blockLocation,
				rootNode, assetManager);
		return blockLocation;
	}

	/**
	 * calculates which of the six faces of a block was hit by projecting it to
	 * the axes and
	 * 
	 * @param geom
	 *            the geometry of the hitted block
	 * @param contactPoint
	 *            the closest intersection point of the ray with the block
	 * @return a signed unit coordinate vector corresponding to the face of the
	 *         hit block
	 */
	private Vector3f getVectorToNeighbor(Geometry geom, Vector3f contactPoint) {
		// calculate the vector pointing from the middle of geom to contactPoint
		projectedVector.set(contactPoint.add(geom.getWorldTranslation()
				.negate()));

		float xProjectionLength = projectedVector.dot(Vector3f.UNIT_X); // scalar
																		// product
																		// of
		// tmp with the
		// unit
		// vector in x-dir'n
		float yProjectionLength = projectedVector.dot(Vector3f.UNIT_Y);
		float zProjectionLength = projectedVector.dot(Vector3f.UNIT_Z);
		// get maximum of the three projections
		float projectionMax = Math.max(Math.abs(xProjectionLength),
				Math.abs(yProjectionLength));
		projectionMax = Math.max(projectionMax, Math.abs(zProjectionLength));
		if (projectionMax == xProjectionLength
				|| projectionMax == -xProjectionLength) {
			return Vector3f.UNIT_X.multLocal(Math.signum(xProjectionLength));
		} else if (projectionMax == yProjectionLength
				|| projectionMax == -yProjectionLength) {
			return Vector3f.UNIT_Y.multLocal(Math.signum(yProjectionLength));
		} else {
			return Vector3f.UNIT_Z.multLocal(Math.signum(zProjectionLength));
		}

	}

	/**
	 * shoots a ray of length >limit< in camera direction and returns a list of
	 * results where the ray intersects with elements of the node shootables
	 * 
	 * @param limit
	 *            the maximum length of the ray, may be infinity
	 * @return results the list of CollisionResults
	 */
	private CollisionResults shootRay(float limit) {
		results.clear();
		ray.setOrigin(cam.getLocation());
		ray.setDirection(cam.getDirection());
		ray.setLimit(limit);
		shootables.collideWith(ray, results);
		// Print the results for Testing
		/*
		 * System.out.println("----- Collisions? " + results.size() + "-----");
		 * for (int i = 0; i < results.size(); i++) { // For each hit, we know
		 * // distance, impact point, // name of geometry. float dist =
		 * results.getCollision(i).getDistance(); Vector3f pt =
		 * results.getCollision(i).getContactPoint(); String hit =
		 * results.getCollision(i).getGeometry().getName();
		 * System.out.println("* Collision #" + i);
		 * System.out.println("  You shot " + hit + " at " + pt + ", " + dist +
		 * " wu away."); }
		 */
		return results;
	}

	/**
	 * adds a block at the specific position (x,y,z)
	 * 
	 * @param x
	 *            the x-coordinate
	 * @param y
	 *            the y-coordinate
	 * @param z
	 *            the z-coordinate
	 */
	private void addBlockAt(int x, int y, int z) {
		Box shape = new Box(0.5f, 0.5f, 0.5f);
		Geometry geometry = new Geometry("Block", shape);
		// Material mat = new Material(assetManager,
		// "Common/MatDefs/Misc/Unshaded.j3md");
		// mat.setColor("Color", ColorRGBA.randomColor());
		// geometry.setMaterial(mat);

		TangentBinormalGenerator.generate(shape);
		Material sphereMat = new Material(assetManager,
				"assets/Materials/Lighting/Lighting.j3md");
		sphereMat.setTexture("DiffuseMap",
				assetManager.loadTexture("Textures/Pond/Pond.jpg"));
		sphereMat.setTexture("NormalMap",
				assetManager.loadTexture("Textures/Pond/Pond_normal.png"));
		sphereMat.setBoolean("UseMaterialColors", true);
		sphereMat.setColor("Diffuse", ColorRGBA.White);
		sphereMat.setColor("Specular", ColorRGBA.White);
		sphereMat.setFloat("Shininess", 64f); // [0,128]
		geometry.setMaterial(sphereMat);

		geometry.setLocalTranslation(new Vector3f(x, y, z));

		addBlockPhysics(geometry, 0);

		mineables.attachChild(geometry);
	}

	private void addBlockPhysics(Spatial spaten, int mass) {

		RigidBodyControl blockPhy = new RigidBodyControl(mass);
		spaten.addControl(blockPhy);
		blockPhy.setKinematic(true); // TODO 1 why is this true?
		bulletAppState.getPhysicsSpace().add(blockPhy);

	}

	/**
	 * These are our custom actions triggered by key presses. We do not walk
	 * yet, we just keep track of the direction the user pressed.
	 */
	public void onAction(String binding, boolean value, float tpf) {
		if (binding.equals(LEFT)) {
			if (value) {
				left = true;
			} else {
				left = false;
			}
		} else if (binding.equals(RIGHT)) {
			if (value) {
				right = true;
			} else {
				right = false;
			}
		} else if (binding.equals(UP)) {
			if (value) {
				up = true;
			} else {
				up = false;
			}
		} else if (binding.equals(DOWN)) {
			if (value) {
				down = true;
			} else {
				down = false;
			}
		} else if (binding.equals(JUMP)) {
			playerPhys.jump();
		}

		// new if-statement to get the possibility of running and mining or
		// placing at the same time
		if (binding.equals(PLACE_BLOCK) && !value) {
			placeBlockFromInv(); // use blocks from inventory
			// placeBlock(); // infinite block placing
		} else if (binding.equals(MINE_BLOCK) && !value) {
			mineBlock();
		}
	}
}
package wgProject01.ingameState;

import java.util.Random;

import jm3Utils.Jm3Utils;
import wgProject01.GameApplication;
import wgProject01.Settings;
import wgProject01.ingameState.gameLogic.GameLogic;
import wgProject01.ingameState.gameLogic.utils.EntityFactory;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.LightControl;
import com.jme3.scene.shape.Sphere;

public class IngameState extends AbstractAppState {
	
	/**
	 * datafields given by the {@link GameApplication} and the
	 * {@link AssetManager} itself
	 */
	private GameApplication app;
	private Node rootNode, guiNode;
	private AssetManager assetManager;
	private AppStateManager stateManager;
	private ViewPort viewPort;
	private Camera cam;
	private FlyByCamera flyCam;

	/** name for the node Shootable */
	public static final String SHOOTABLES = "Shootables";
	/** name for the node Mineables */
	public static final String MINEABLES = "Mineables";

	/** the geometry representing the players body */
	protected Geometry player;

	/** contains all spatials onto which a block can be set */
	private Node shootables; 
	/** contains all spatials that can be picked up by the player */
	private Node mineables; 

	/** object representing the game logic */
	private GameLogic gameLogic;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		
		this.app = (GameApplication) app; // cast to a more specific class
		this.rootNode = this.app.getRootNode();
		this.assetManager = this.app.getAssetManager();
		this.stateManager = this.app.getStateManager();
		this.app.getInputManager();
		this.viewPort = this.app.getViewPort();
		this.stateManager.getState(BulletAppState.class);
		this.cam = this.app.getCamera();
		this.flyCam = this.app.getFlyByCamera();
		this.guiNode = this.app.getGuiNode();

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
		EntityFactory.initData(rootNode, assetManager);
		
		gameLogic = new GameLogic();
		gameLogic.doInit(mineables, assetManager);
		
		// initOneBlockFloor(); // take either one of the floor initializations
		initGeneralLights();
		for (int i = 0; i <= Settings.debugMode; i++) {
			initRandomSun();
		}
		
		stateManager.attach(new Player());

		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f)); // makes
																		// the
																		// background
																		// somewhat
																		// blue
		// TODO 2 the movementspeed setting does not work at all
		flyCam.setMoveSpeed(10);
	}

	/**
	 * Initializes a randomly colored sun, with a random rotation speed, which
	 * uses the rotation control to fly around and acts as a point light of it's
	 * color source as well.
	 */
	private void initRandomSun() {
		float geometryRadius = 20;
		float geometryRadius2 = 30;
		float rotationRadius = GameLogic.FLOOR_RADIUS + 2 * geometryRadius2;
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
		gameLogic.doCleanup();
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
		gameLogic.doUpdate(tpf);
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

	// private void addBlockAt(Vector3f blockLocation) {
	// BlockGameObj newBlock = BlockManager.getInstance().getBlockGameObj();
	// BlockManager.getInstance().setBlock(blockLocation, newBlock);
	// }
}
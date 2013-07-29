package wgProject01.ingameState;

import jm3Utils.Jme3Utils;
import wgProject01.GameApplication;
import wgProject01.ingameState.gameLogic.GameLogic;
import wgProject01.ingameState.gameLogic.utils.EntityFactory;
import wgProject01.ingameState.gameLogic.view.EntityView;
import wgProject01.ingameState.gameLogic.view.InputHandler;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class IngameState extends AbstractAppState {
	
	/**
	 * datafields given by the {@link GameApplication} and the
	 * {@link AssetManager} itself
	 */
	private GameApplication app;
	private Node rootNode, guiNode;
	private AssetManager assetManager;
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
		this.app.getInputManager();
		this.viewPort = this.app.getViewPort();
		this.cam = this.app.getCamera();
		this.flyCam = this.app.getFlyByCamera();
		this.guiNode = this.app.getGuiNode();

		EntityView.cam = this.cam;
		EntityView.rootNode = this.rootNode;
		EntityView.assetManager = this.assetManager;
		flyCam.setEnabled(false);
		this.app.getInputManager().setCursorVisible(false);
		
		// TODO 1: remove debug code:
		// draw the coordinate system:
		Jme3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0),
				rootNode, assetManager);
		Jme3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0),
				rootNode, assetManager);
		Jme3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1),
				rootNode, assetManager);

		// init stuff that is independent of whether state is PAUSED or RUNNING
		guiNode.addLight(new AmbientLight());
		
		initNodes();
		initCrossHairs();		
		EntityFactory.initData(rootNode, assetManager, rootNode);
		
		gameLogic = new GameLogic();
		gameLogic.doInit(mineables, assetManager);
		
		stateManager.attach(new InputHandler());

		initGeneralLights();
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f)); // makes
																		// the
																		// background
																		// somewhat
																		// blue
		// TODO 2 the movementspeed setting does not work at all
		flyCam.setMoveSpeed(10);
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
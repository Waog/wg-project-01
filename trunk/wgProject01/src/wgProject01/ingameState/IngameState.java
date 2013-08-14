package wgProject01.ingameState;

import java.util.Observable;
import java.util.Observer;

import jm3Utils.Jme3Utils;
import wgProject01.GameApplication;
import wgProject01.ModelAccessor;
import wgProject01.Settings;
import wgProject01.ingameState.gameLogic.GameLogic;
import wgProject01.ingameState.gameLogic.utils.EntityFactory;
import wgProject01.ingameState.gameLogic.view.EntityView;
import wgProject01.ingameState.gameLogic.view.InputHandler;
import wgProject01.ingameState.view.HudController;
import wgProject01.mainMenuState.MainMenuState;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioRenderer;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

public class IngameState extends AbstractAppState implements ActionListener,
		Observer, ScreenController {

	/**
	 * datafields given by the {@link GameApplication} and the
	 * {@link AssetManager} itself
	 */
	private GameApplication app;
	private Node realRootNode, guiNode;
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

	/** object representing the game logic */
	private GameLogic gameLogic;
	private InputManager inputManager;
	private AudioRenderer audioRenderer;
	private ViewPort guiViewPort;
	/** Some Nifty Gui variable... */
	private Nifty nifty;
	private HudController hudController;
	private AppStateManager stateManager;
	private InputHandler inputHandlerSubState;
	private Node ourRootNode;
	private final String SWITCH_TO_MAIN_MENU = "SwitchToMainMenu";;

	public IngameState() {
	}

	/**
	 * Called by JME3 whenever this state is attached to a state manager.
	 * 
	 * Internal: Sets all data fields.
	 */
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);

		// initialize variables
		this.app = (GameApplication) app; // cast to a more specific class
		this.realRootNode = this.app.getRootNode();
		this.assetManager = this.app.getAssetManager();
		this.inputManager = this.app.getInputManager();
		this.viewPort = this.app.getViewPort();
		this.guiViewPort = this.app.getGuiViewPort();
		this.cam = this.app.getCamera();
		this.flyCam = this.app.getFlyByCamera();
		this.guiNode = this.app.getGuiNode();
		this.audioRenderer = this.app.getAudioRenderer();
		this.stateManager = stateManager;

		// initialize an own root node, because detaching lights from the real
		// root node is crap.
		this.ourRootNode = new Node("our Root Node");
		this.realRootNode.attachChild(ourRootNode);

		// init own classes and give them access to necessary data fields
		EntityView.cam = this.cam;
		EntityView.rootNode = this.ourRootNode;
		EntityView.assetManager = this.assetManager;
		EntityFactory.initData(ourRootNode, assetManager, ourRootNode);
		gameLogic = new GameLogic();
		gameLogic.doInit(ourRootNode, assetManager);

		// disable the cursor
		this.app.getInputManager().setCursorVisible(false);

		// draw the coordinate system
		drawCoordinateSystem();

		// init the cross hair
		initCrossHairs();

		// makes the background somewhat blue
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));

		// initialize the HUD
		ModelAccessor.getInstance().itemCount.addObserver(this);
		nifty.gotoScreen("hud");

		// initialize input handling for state changes
		initStateSpecificInputHandling();

		// initialize sub states
		this.inputHandlerSubState = new InputHandler();
		stateManager.attach(inputHandlerSubState);
	}

	private void initStateSpecificInputHandling() {
		inputManager.addMapping(SWITCH_TO_MAIN_MENU, new KeyTrigger(
				KeyInput.KEY_ESCAPE));
		inputManager.addListener(this, SWITCH_TO_MAIN_MENU);
	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals(SWITCH_TO_MAIN_MENU) && !isPressed) {
			gotoMainMenu();
		}
	}

	/**
	 * Switches to the main menu state.
	 */
	public void gotoMainMenu() {
		// initialize and switch to the next state/screen
		ScreenController startScreenController = nifty.getScreen("start")
				.getScreenController();
		MainMenuState mainMenuState = (MainMenuState) startScreenController;
		this.nifty.gotoScreen("start");
		this.stateManager.attach(mainMenuState);
		this.stateManager.detach(this);
	}

	private void drawCoordinateSystem() {
		if (Settings.debugMode < 2) {
			return;
		}
		// draw the coordinate system:
		Jme3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(1, 0, 0),
				ourRootNode, assetManager);
		Jme3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 1, 0),
				ourRootNode, assetManager);
		Jme3Utils.drawLine(new Vector3f(0, 0, 0), new Vector3f(0, 0, 1),
				ourRootNode, assetManager);
	}

	@Override
	public void cleanup() {
		super.cleanup();
		// unregister all my listeners, detach all my nodes, etc...
		gameLogic.doCleanup();
		realRootNode.detachAllChildren();
		this.stateManager.detach(this.inputHandlerSubState);
		inputManager.deleteMapping(SWITCH_TO_MAIN_MENU);
		inputManager.removeListener(this);
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
	 * A centered plus sign to help the player aim. TODO: move the cross hair
	 * code to another class (maybe {@link HudController}).
	 */
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

	@Override
	public void bind(Nifty nifty, Screen screen) {
		this.nifty = nifty;
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Observable o, Object arg) {
		// TODO Auto-generated method stub
		
	}
}
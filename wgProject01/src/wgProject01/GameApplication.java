package wgProject01;

import wgProject01.mainMenuState.MainMenuState;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.niftygui.NiftyJmeDisplay;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * The Game Application.
 * 
 * This is the central control class responsible for putting together the whole
 * game and navigating through different states (for instance menus, ingame
 * state, login screen) of the game.
 */
public class GameApplication extends SimpleApplication {

	/**
	 * JME3 calls this method automatically once. Initializes the game
	 * application.
	 */
	@Override
	public void simpleInitApp() {
		// make our own assets accessible
		assetManager.registerLocator(".", FileLocator.class);

		// delete default input managing
		inputManager.deleteMapping(SimpleApplication.INPUT_MAPPING_EXIT);
		flyCam.setEnabled(false);

		// initialize strange nifty magic stuff
		NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
				inputManager, audioRenderer, guiViewPort);
		/** Create a new NiftyGUI object */
		Nifty nifty = niftyDisplay.getNifty();
		// attach the Nifty display to the gui view port as a processor
		guiViewPort.addProcessor(niftyDisplay);

		// construct all app states / screen controllers from the XMLs
		nifty.fromXml("Interface/start.xml", "start");
		nifty.addXml("Interface/hud.xml");
		
		// initialize the initial state/screen
		ScreenController startScreenController = nifty.getScreen("start")
				.getScreenController();
		MainMenuState mainMenuState = (MainMenuState) startScreenController;
		stateManager.attach(mainMenuState);
	}

	/**
	 * JME3 calls this method automatically every Frame. Does nothing (except
	 * debug stuff).
	 */
	@Override
	public void simpleUpdate(float tpf) {
		// print the TPF in debug mode:
		if (Settings.debugMode >= 4) {
			System.out.println("DEBUG: tpf: " + tpf);
		}
	}
}
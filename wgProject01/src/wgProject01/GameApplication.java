package wgProject01;

import wgProject01.ingameState.IngameState;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;

/**
 * The Game Application.
 * 
 * This is the central control class responsible for putting together the whole
 * game and navigating through different states (for instance menus, ingame
 * state, login screen) of the game.
 */
public class GameApplication extends SimpleApplication {

	/**
	 * JME3 calls this method automatically once.
	 * Initializes the game application.
	 */
	@Override
	public void simpleInitApp() {
		// make our own assets accessible
		assetManager.registerLocator(".", FileLocator.class);

		// initialize the initial app states
		stateManager.attach(new IngameState());
	}

	/**
	 * JME3 calls this method automatically every Frame.
	 * Does nothing (except debug stuff).
	 */
	@Override
	public void simpleUpdate(float tpf) {
		// print the TPF in debug mode:
		if (Settings.debugMode >= 1) {
			System.out.println("DEBUG: tpf: " + tpf);
		}
	}
}
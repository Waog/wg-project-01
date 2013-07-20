package wgProject01;

import wgProject01.ingameState.IngameState;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;

/** Sample 5 - how to map keys and mousebuttons to actions */
public class GameApplication extends SimpleApplication {

	@Override
	public void simpleInitApp() {
		assetManager.registerLocator(".", FileLocator.class);
		
		stateManager.attach(new IngameState());
	}

	/**
	 * This is the main event loop--walking happens here. We check in which
	 * direction the player is walking by interpreting the camera direction
	 * forward (camDir) and to the side (camLeft). The setWalkDirection()
	 * command is what lets a physics-controlled player walk. We also make sure
	 * here that the camera moves with player.
	 */
	@Override
	public void simpleUpdate(float tpf) {
		// nothing
//		System.out.println("DEBUG: tpf: " + tpf);
	}
}
package wgProject01.ingameState.gameLogic.view;

import wgProject01.GameApplication;
import wgProject01.ingameState.gameLogic.systems.PlayerControlSystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
 * the Listener for user inputs. Informs the PlayerControlSystem by settings its
 * boolean flags. This Listener informs the PlayerControlSystem when pushing a
 * key and informs it again when the key is released.
 * 
 * @author Mirco
 * 
 */
public class InputHandler extends AbstractAppState implements ActionListener {

	/**
	 * datafields given by the {@link GameApplication} and the
	 * {@link AssetManager} itself
	 */
	private InputManager inputManager;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		// initialize datafields
		super.initialize(stateManager, app);
		this.inputManager = app.getInputManager();

		initKeys();
	}

	/**
	 * initializes the specific key for a command and adds listeners to this
	 * command
	 */
	private void initKeys() {
		inputManager.addMapping(PlayerControlSystem.LEFT, new KeyTrigger(
				KeyInput.KEY_A));
		inputManager.addMapping(PlayerControlSystem.RIGHT, new KeyTrigger(
				KeyInput.KEY_D));
		inputManager.addMapping(PlayerControlSystem.BACK, new KeyTrigger(
				KeyInput.KEY_S));
		inputManager.addMapping(PlayerControlSystem.FORWARD, new KeyTrigger(
				KeyInput.KEY_W));
		inputManager.addListener(this, PlayerControlSystem.LEFT);
		inputManager.addListener(this, PlayerControlSystem.RIGHT);
		inputManager.addListener(this, PlayerControlSystem.BACK);
		inputManager.addListener(this, PlayerControlSystem.FORWARD);

	}

	/**
	 * sets the PlayerControlSystem by using its Map
	 */
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		PlayerControlSystem.mapper.put(name, isPressed);
	}

}

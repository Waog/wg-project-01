package wgProject01.ingameState.gameLogic.view;

import wgProject01.GameApplication;
import wgProject01.ingameState.gameLogic.systems.InputHandlingSystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

/**
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

	private void initKeys() {
		inputManager.addMapping(InputHandlingSystem.LEFT, new KeyTrigger(
				KeyInput.KEY_A));
		inputManager.addMapping(InputHandlingSystem.RIGHT, new KeyTrigger(
				KeyInput.KEY_D));
		inputManager.addMapping(InputHandlingSystem.BACK, new KeyTrigger(
				KeyInput.KEY_S));
		inputManager.addMapping(InputHandlingSystem.FORWARD, new KeyTrigger(
				KeyInput.KEY_W));
		inputManager.addListener(this, InputHandlingSystem.LEFT);
		inputManager.addListener(this, InputHandlingSystem.RIGHT);
		inputManager.addListener(this, InputHandlingSystem.BACK);
		inputManager.addListener(this, InputHandlingSystem.FORWARD);

	}

	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		InputHandlingSystem.mapper.put(name, isPressed);
	}

}

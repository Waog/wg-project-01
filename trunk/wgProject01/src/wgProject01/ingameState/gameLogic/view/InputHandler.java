package wgProject01.ingameState.gameLogic.view;

import wgProject01.GameApplication;
import wgProject01.ingameState.gameLogic.components.InputReactingComponent;
import wgProject01.ingameState.gameLogic.systems.InputHandlingSystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;

public class InputHandler extends AbstractAppState implements ActionListener {

	/**
	 * datafields given by the {@link GameApplication} and the
	 * {@link AssetManager} itself
	 */
	private InputManager inputManager;
	private GameApplication app;
	
	/** names for actions */
	/** for moving left */ 
	private static final String LEFT = "Left";
	/** for moving right */
	private static final String RIGHT = "Right";
	/** for moving down */
	private static final String DOWN = "Down";
	/** for moving up */
	private static final String UP = "Up";
	
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		// initialize datafields
		super.initialize(stateManager, app);
		this.app = (GameApplication) app;
		this.inputManager = app.getInputManager();
		
		initKeys();
	}
	
	
	
	private void initKeys() {
		inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(DOWN, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(UP, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addListener(this, LEFT);
		inputManager.addListener(this, RIGHT);
		inputManager.addListener(this, DOWN);
		inputManager.addListener(this, UP);
		
	}



	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		if(name.equals(LEFT) && !isPressed) { // reacts only when key was released
			InputHandlingSystem.moveLeftActivated = isPressed;
		}
		if(name.equals(LEFT) && isPressed) { // reacts only when key was pushed down
			InputHandlingSystem.moveLeftActivated = isPressed;
		}
		
	}

	
}

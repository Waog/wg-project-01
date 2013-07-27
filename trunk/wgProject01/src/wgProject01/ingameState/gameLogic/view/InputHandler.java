package wgProject01.ingameState.gameLogic.view;

import java.util.Map;

import wgProject01.GameApplication;
import wgProject01.ingameState.gameLogic.systems.PlayerControlSystem;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;

/**
 * the Listener for user inputs. Informs the {@link PlayerControlSystem} by
 * settings its boolean flags. This Listener informs the
 * {@link PlayerControlSystem} when pushing a key and informs it again when the
 * key is released.
 * 
 * @author Mirco
 * 
 */
public class InputHandler extends AbstractAppState implements ActionListener,
		AnalogListener {

	/**
	 * The mouse sensitivity. currently only used for speed of the player
	 * rotation.
	 */
	private static float mouseSensitivity = 3;

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

		// TODO uncomment both lines
		// initDigitalInputs(); // e.g. mouse motion
		initAnalogueInputs(); // e.g. key strokes
	}

	/**
	 * initializes the specific key for a command given as analogue input
	 * signals and adds listeners to this command.
	 */
	private void initAnalogueInputs() {
		// mouse movement in positive x-direction, i.e. to the right
		inputManager.addMapping(PlayerControlSystem.MOUSE_RIGHT,
				new MouseAxisTrigger(MouseInput.AXIS_X, false));
		inputManager.addListener(this, PlayerControlSystem.MOUSE_RIGHT);

		// mouse movement in negative x-direction, i.e. to the left
		inputManager.addMapping(PlayerControlSystem.MOUSE_LEFT,
				new MouseAxisTrigger(MouseInput.AXIS_X, true));
		inputManager.addListener(this, PlayerControlSystem.MOUSE_LEFT);

		// mouse movement in positive y-direction, i.e. upwards
		inputManager.addMapping(PlayerControlSystem.MOUSE_UP,
				new MouseAxisTrigger(MouseInput.AXIS_Y, false));
		inputManager.addListener(this, PlayerControlSystem.MOUSE_UP);

		// mouse movement in negative y-direction, i.e. upwards
		inputManager.addMapping(PlayerControlSystem.MOUSE_DOWN,
				new MouseAxisTrigger(MouseInput.AXIS_Y, true));
		inputManager.addListener(this, PlayerControlSystem.MOUSE_DOWN);
	}

	/**
	 * initializes the specific key for a command given as digital input signals
	 * and adds listeners to this command.
	 */
	private void initDigitalInputs() {
		// pressed 'A' on keyboard - shall correspond to moving left
		inputManager.addMapping(PlayerControlSystem.LEFT, new KeyTrigger(
				KeyInput.KEY_A));
		inputManager.addListener(this, PlayerControlSystem.LEFT);

		// pressed 'D' on keyboard - shall correspond to moving right
		inputManager.addMapping(PlayerControlSystem.RIGHT, new KeyTrigger(
				KeyInput.KEY_D));
		inputManager.addListener(this, PlayerControlSystem.RIGHT);

		// pressed 'S' on keyboard - shall correspond to moving backwards
		inputManager.addMapping(PlayerControlSystem.BACK, new KeyTrigger(
				KeyInput.KEY_S));
		inputManager.addListener(this, PlayerControlSystem.BACK);

		// pressed 'w' on keyboard - shall correspond to moving forward
		inputManager.addMapping(PlayerControlSystem.FORWARD, new KeyTrigger(
				KeyInput.KEY_W));
		inputManager.addListener(this, PlayerControlSystem.FORWARD);
	}

	/**
	 * for digital actions: sets the {@link PlayerControlSystem} by using its
	 * {@link Map}
	 */
	@Override
	public void onAction(String name, boolean isPressed, float tpf) {
		PlayerControlSystem.mapper.put(name, isPressed);
	}

	/**
	 * for analogue actions: sets the {@link PlayerControlSystem}
	 */
	@Override
	public void onAnalog(String name, float value, float tpf) {
		// TODO: add handling in x-z-plane
		if (name.equals(PlayerControlSystem.MOUSE_LEFT)) {
			PlayerControlSystem.turnHorizontal += value * mouseSensitivity;
		}

		if (name.equals(PlayerControlSystem.MOUSE_RIGHT)) {
			PlayerControlSystem.turnHorizontal -= value * mouseSensitivity;
		}

		// add handling in y-direction
		if (name.equals(PlayerControlSystem.MOUSE_UP)) {
			PlayerControlSystem.turnVertical += value * mouseSensitivity;
		}

		if (name.equals(PlayerControlSystem.MOUSE_DOWN)) {
			PlayerControlSystem.turnVertical -= value * mouseSensitivity;
		}

	}

}

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
import com.jme3.input.controls.MouseButtonTrigger;

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
	private static float mouseSensitivity = 5;

	// the analog input mappings
	/** mouse movement in negative x-direction, i.e. to the left */
	public static final String MOUSE_LEFT = "MOUSE_LEFT";
	/** mouse movement in positive x-direction, i.e. to the right */
	public static final String MOUSE_RIGHT = "MOUSE_RIGHT";
	/** mouse movement in positive y-direction, i.e. upwards */
	public static final String MOUSE_UPWARDS = "MOUSE_UPWARDS";
	/** mouse movement in negative x-direction, i.e. downwards */
	public static final String MOUSE_DOWNWARDS = "MOUSE_DOWNWARDS";

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

		initDigitalInputs(); // e.g. mouse motion
		initAnalogueInputs(); // e.g. key strokes
	}

	/**
	 * initializes the specific key for a command given as analogue input
	 * signals and adds listeners to this command.
	 */
	private void initAnalogueInputs() {
		// mouse movement in positive x-direction, i.e. to the right
		inputManager.addMapping(MOUSE_RIGHT, new MouseAxisTrigger(
				MouseInput.AXIS_X, false));
		inputManager.addListener(this, MOUSE_RIGHT);

		// mouse movement in negative x-direction, i.e. to the left
		inputManager.addMapping(MOUSE_LEFT, new MouseAxisTrigger(
				MouseInput.AXIS_X, true));
		inputManager.addListener(this, MOUSE_LEFT);

		// mouse movement in positive y-direction, i.e. upwards
		inputManager.addMapping(MOUSE_UPWARDS, new MouseAxisTrigger(
				MouseInput.AXIS_Y, false));
		inputManager.addListener(this, MOUSE_UPWARDS);

		// mouse movement in negative y-direction, i.e. upwards
		inputManager.addMapping(MOUSE_DOWNWARDS, new MouseAxisTrigger(
				MouseInput.AXIS_Y, true));
		inputManager.addListener(this, MOUSE_DOWNWARDS);
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

		// pressed left mouse button triggered - shall correspond to picking
		inputManager.addMapping(PlayerControlSystem.PICK_BLOCK,
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(this, PlayerControlSystem.PICK_BLOCK);

		// pressed right mouse button triggered - shall correspond to placing
		inputManager.addMapping(PlayerControlSystem.PLACE_BLOCK,
				new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addListener(this, PlayerControlSystem.PLACE_BLOCK);
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
		if (name.equals(MOUSE_LEFT)) {
			PlayerControlSystem.turnHorizontal += value * mouseSensitivity;
		}

		if (name.equals(MOUSE_RIGHT)) {
			PlayerControlSystem.turnHorizontal -= value * mouseSensitivity;
		}

		// add handling in y-direction
		if (name.equals(MOUSE_UPWARDS)) {
			PlayerControlSystem.turnVertical += value * mouseSensitivity;
		}

		if (name.equals(MOUSE_DOWNWARDS)) {
			PlayerControlSystem.turnVertical -= value * mouseSensitivity;
		}

	}

	@Override
	public void cleanup() {
		super.cleanup();

		// TODO: implement dynamic mechanism to add and remove any amount of
		// input mappings and listeners.

		inputManager.removeListener(this);
		
		// Analog inputs
		inputManager.deleteMapping(MOUSE_RIGHT);
		inputManager.deleteMapping(MOUSE_LEFT);
		inputManager.deleteMapping(MOUSE_UPWARDS);
		inputManager.deleteMapping(MOUSE_DOWNWARDS);

		// digital inputs
		inputManager.deleteMapping(PlayerControlSystem.LEFT);
		inputManager.deleteMapping(PlayerControlSystem.RIGHT);
		inputManager.deleteMapping(PlayerControlSystem.BACK);
		inputManager.deleteMapping(PlayerControlSystem.FORWARD);
		inputManager.deleteMapping(PlayerControlSystem.PICK_BLOCK);
		inputManager.deleteMapping(PlayerControlSystem.PLACE_BLOCK);
	}

}

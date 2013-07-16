package wgProject01;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.util.TangentBinormalGenerator;
import com.jme3.math.ColorRGBA;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/** Sample 5 - how to map keys and mousebuttons to actions */
public class HelloInput extends SimpleApplication {

	public static void main(String[] args) {
		HelloInput app = new HelloInput();
		app.start();
	}

	protected Geometry player;
	Boolean isRunning = true;

	@Override
	public void simpleInitApp() {
		Box b = new Box(1, 1, 1);
		player = new Geometry("Player", b);
		assetManager.registerLocator(".",
				FileLocator.class);
		Material mat = new Material(assetManager,
				"assets/Materials/Unshaded/Unshaded.j3md");
		
		mat.setColor("Color", ColorRGBA.Red);
		player.setMaterial(mat);
		rootNode.attachChild(player);
		initKeys(); // load my custom keybinding

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(1, 0, -1).normalizeLocal());
		sun.setColor(ColorRGBA.White);
		rootNode.addLight(sun);

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun2 = new DirectionalLight();
		sun2.setDirection(new Vector3f(-1, 1, 0).normalizeLocal());
		sun2.setColor(ColorRGBA.Yellow);
		rootNode.addLight(sun2);

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun3 = new DirectionalLight();
		sun3.setDirection(new Vector3f(0, -1, 1).normalizeLocal());
		sun3.setColor(ColorRGBA.LightGray);
		rootNode.addLight(sun3);
	}

	/** Custom Keybinding: Map named actions to inputs. */
	private void initKeys() {
		// You can map one or several inputs to one named action
		inputManager.addMapping("Pause", new KeyTrigger(KeyInput.KEY_P));
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_J));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_K));
		inputManager.addMapping("Rotate", new KeyTrigger(KeyInput.KEY_SPACE),
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		// change size with mouse wheel:
		inputManager.addMapping("ScaleUp", new MouseAxisTrigger(
				MouseInput.AXIS_WHEEL, false));
		inputManager.addMapping("ScaleDown", new MouseAxisTrigger(
				MouseInput.AXIS_WHEEL, true));

		// place blocks with the right mouse button
		inputManager.addMapping("addBlock", new MouseButtonTrigger(
				MouseInput.BUTTON_RIGHT));

		// Add the names to the action listener.
		inputManager.addListener(actionListener, "Pause", "addBlock");
		inputManager.addListener(analogListener, "Left", "Right", "Rotate",
				"ScaleUp", "ScaleDown");
	}

	private ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("Pause") && !keyPressed) {
				isRunning = !isRunning;
			}
			if (name.equals("addBlock") && !keyPressed) {
				addBlock();
			}
		}
	};

	private AnalogListener analogListener = new AnalogListener() {
		public void onAnalog(String name, float value, float tpf) {
			if (isRunning) {
				if (name.equals("Rotate")) {
					player.rotate(0, value * speed, 0);
				}
				if (name.equals("Right")) {
					Vector3f v = player.getLocalTranslation();
					player.setLocalTranslation(v.x + value * speed, v.y, v.z);
				}
				if (name.equals("Left")) {
					Vector3f v = player.getLocalTranslation();
					player.setLocalTranslation(v.x - value * speed, v.y, v.z);
				}
				if (name.equals("ScaleUp")) {
					System.out.println("scale up-value:" + value);
					player.scale(1.0f + 0.05f * value);
				}
				if (name.equals("ScaleDown")) {
					System.out.println("scale down-value:" + value);
					player.scale(1.0f - 0.05f * value);
				}
			} else {
				System.out.println("Press P to unpause.");
			}
		}
	};

	private void addBlock() {
		Box shape = new Box(0.5f, 0.5f, 0.5f);
		Geometry geometry = new Geometry("Block", shape);
		// Material mat = new Material(assetManager,
		// "Common/MatDefs/Misc/Unshaded.j3md");
		// mat.setColor("Color", ColorRGBA.randomColor());
		// geometry.setMaterial(mat);

		TangentBinormalGenerator.generate(shape);
		Material sphereMat = new Material(assetManager,
				"assets/Materials/Lighting/Lighting.j3md");
		sphereMat.setTexture("DiffuseMap",
				assetManager.loadTexture("Textures/Pond/Pond.jpg"));
		sphereMat.setTexture("NormalMap", assetManager
				.loadTexture("Textures/Pond/Pond_normal.png"));
		sphereMat.setBoolean("UseMaterialColors", true);
		sphereMat.setColor("Diffuse", ColorRGBA.White);
		sphereMat.setColor("Specular", ColorRGBA.White);
		sphereMat.setFloat("Shininess", 64f); // [0,128]
		geometry.setMaterial(sphereMat);

		Vector3f blockLocation = cam.getLocation().add(
				cam.getDirection().mult(4f));
		Vector3f roundedLocation = new Vector3f((int) blockLocation.x,
				(int) blockLocation.y, (int) blockLocation.z);

		geometry.setLocalTranslation(roundedLocation);

		rootNode.attachChild(geometry);
		initKeys(); // load my custom keybinding
	}
}
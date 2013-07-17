package wgProject01;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.TangentBinormalGenerator;

/** Sample 5 - how to map keys and mousebuttons to actions */
public class GameApplication extends SimpleApplication implements
		ActionListener {

	private static final String ADD_BLOCK = "addBlock";
	private static final String JUMP = "Jump";
	private static final String DOWN = "Down";
	private static final String UP = "Up";
	private static final String RIGHT = "Right";
	private static final String LEFT = "Left";
	protected Geometry player;
	Boolean isRunning = true;
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;

	private CharacterControl playerPhys;
	private BulletAppState bulletAppState;
	private double sunPosition = 0.0f;

	// Temporary vectors used on each frame.
	// They here to avoid instanciating new vectors on each frame
	private Vector3f camDir = new Vector3f();
	private Vector3f camLeft = new Vector3f();
	private Node sunNode;
	private PointLight sunLight;

	@Override
	public void simpleInitApp() {
		assetManager.registerLocator(".", FileLocator.class);
		
		Mesh sphereMesh = new Sphere(5, 5, 20f);
		Spatial sphereSpacial = new Geometry("Sphere", sphereMesh);
		Material sphereMat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		sphereMat.setColor("Color", ColorRGBA.Red);
		sphereSpacial.setMaterial(sphereMat);
		rootNode.attachChild(sphereSpacial);
		sphereSpacial.setLocalTranslation(0, 10, 0);
		RotationControl rotationControl = new RotationControl(100, 100, 100);
		rotationControl.setSpeeds(new Vector3f(1.1f, 1.2f, 1.3f));
		sphereSpacial.addControl(rotationControl);
		

		initCrossHairs();
		initPhysics();
		// initFloor();
		initOneBlockFloor();
		initSun();

		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		flyCam.setMoveSpeed(25);

		initKeys(); // load my custom keybinding
		setUpKeys();

		// Add a ambient light to make everything visible.
		AmbientLight ambientLight = new AmbientLight();
		ambientLight.setColor(ColorRGBA.Pink);
		rootNode.addLight(ambientLight);

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(1, 0, -1).normalizeLocal());
		sun.setColor(ColorRGBA.DarkGray);
		rootNode.addLight(sun);

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun2 = new DirectionalLight();
		sun2.setDirection(new Vector3f(-1, 1, 0).normalizeLocal());
		sun2.setColor(ColorRGBA.DarkGray);
		rootNode.addLight(sun2);

		/** Must add a light to make the lit object visible! */
		DirectionalLight sun3 = new DirectionalLight();
		sun3.setDirection(new Vector3f(0, -1, 1).normalizeLocal());
		sun3.setColor(ColorRGBA.DarkGray);
		rootNode.addLight(sun3);
	}

	private void initSun() {
			sunNode = new Node("sun");
			
			// a visible point light source:
			Vector3f sunPos = new Vector3f(3, 2, 20);
			sunNode.setLocalTranslation(sunPos);
			ColorRGBA lightColor = ColorRGBA.Orange;
			ColorRGBA lightColorSemiTransparent = lightColor.clone();
			lightColorSemiTransparent.a = 0.5f;

			Sphere sphere = new Sphere(20, 20, 20f);
			Spatial aSun = new Geometry("Sphere", sphere);
			aSun.setQueueBucket(Bucket.Transparent);
			Material mat_aSun = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			mat_aSun.setColor("Color", lightColor);
			mat_aSun.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // !
			aSun.setMaterial(mat_aSun);
			sunNode.attachChild(aSun);

			Sphere sphere2 = new Sphere(20, 20, 30f);
			Spatial aSun2 = new Geometry("Sphere", sphere2);
			aSun2.setQueueBucket(Bucket.Transparent);
			Material mat_aSun2 = new Material(assetManager,
					"Common/MatDefs/Misc/Unshaded.j3md");
			mat_aSun2.setColor("Color", lightColorSemiTransparent);
			mat_aSun2.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // !
			aSun2.setMaterial(mat_aSun2);
			sunNode.attachChild(aSun2);
			rootNode.attachChild(sunNode);

			sunLight = new PointLight();
			sunLight.setPosition(sunPos);
			sunLight.setColor(lightColor);
//			pointlight.setRadius(300);
			rootNode.addLight(sunLight);		
	}

	/**
	 * Initializes the useing of physics and makes the player/camera collidable.
	 */
	private void initPhysics() {
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1f,
				1.8f, 1);
		playerPhys = new CharacterControl(capsuleShape, 0.05f);
		playerPhys.setJumpSpeed(10f);
		playerPhys.setFallSpeed(30);
		playerPhys.setGravity(30);
		playerPhys.setPhysicsLocation(new Vector3f(0, 10, 0));

		// We attach the scene and the player to the rootnode and the physics
		// space,
		// to make them appear in the game world.
		bulletAppState.getPhysicsSpace().add(playerPhys);
	}

	/**
	 * We over-write some navigational key mappings here, so we can add
	 * physics-controlled walking and jumping:
	 */
	private void setUpKeys() {
		inputManager.addMapping(LEFT, new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping(RIGHT, new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping(UP, new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping(DOWN, new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping(JUMP, new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping(ADD_BLOCK, new MouseButtonTrigger(
				MouseInput.BUTTON_RIGHT));
		inputManager.addListener(this, LEFT);
		inputManager.addListener(this, RIGHT);
		inputManager.addListener(this, UP);
		inputManager.addListener(this, DOWN);
		inputManager.addListener(this, JUMP);
		inputManager.addListener(this, ADD_BLOCK);

		// Add the names to the action listener.
	}

	/**
	 * These are our custom actions triggered by key presses. We do not walk
	 * yet, we just keep track of the direction the user pressed.
	 */
	public void onAction(String binding, boolean value, float tpf) {
		if (binding.equals(LEFT)) {
			if (value) {
				left = true;
			} else {
				left = false;
			}
		} else if (binding.equals(RIGHT)) {
			if (value) {
				right = true;
			} else {
				right = false;
			}
		} else if (binding.equals(UP)) {
			if (value) {
				up = true;
			} else {
				up = false;
			}
		} else if (binding.equals(DOWN)) {
			if (value) {
				down = true;
			} else {
				down = false;
			}
		} else if (binding.equals(JUMP)) {
			playerPhys.jump();
		}
		if (binding.equals(ADD_BLOCK) && !value) {
			addBlock();
		}
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
		camDir.set(cam.getDirection()).multLocal(0.6f);
		camLeft.set(cam.getLeft()).multLocal(0.4f);
		walkDirection.set(0, 0, 0);
		if (left) {
			walkDirection.addLocal(camLeft);
		}
		if (right) {
			walkDirection.addLocal(camLeft.negate());
		}
		if (up) {
			walkDirection.addLocal(camDir);
		}
		if (down) {
			walkDirection.addLocal(camDir.negate());
		}
		playerPhys.setWalkDirection(walkDirection);
		cam.setLocation(playerPhys.getPhysicsLocation());
		
		updateSun(tpf);
	}

	private void updateSun(float tpf) {
		double radius = 100;
				
		this.sunPosition += tpf;
		if (this.sunPosition > Math.PI * 2) {
			this.sunPosition -= Math.PI * 2;
		}
		float newSunPosX = (float) (radius * Math.sin(this.sunPosition));
		float newSunPosY = (float) (radius * Math.cos(this.sunPosition));
		
		Vector3f newSunPos = new Vector3f(newSunPosX, newSunPosY, 0);
		
		sunNode.setLocalTranslation(newSunPos);
		sunLight.setPosition(newSunPos);
	}

	/** Custom Keybinding: Map named actions to inputs. */
	private void initKeys() {
	}

	private void addBlock() {
		Vector3f blockLocation = cam.getLocation().add(
				cam.getDirection().mult(4f));
		addBlockAt((int) blockLocation.x, (int) blockLocation.y,
				(int) blockLocation.z);
	}

	private void initOneBlockFloor() {
		Box shape = new Box(100f, 0.5f, 100f);
		Geometry geometry = new Geometry("Block", shape);
		// Material mat = new Material(assetManager,
		// "Common/MatDefs/Misc/Unshaded.j3md");
		// mat.setColor("Color", ColorRGBA.randomColor());
		// geometry.setMaterial(mat);

		TangentBinormalGenerator.generate(shape);
		Material sphereMat = new Material(assetManager,
				"assets/Materials/Lighting/Lighting.j3md");

		Texture texture = assetManager.loadTexture("Textures/Pond/Pond.jpg");
		texture.setWrap(WrapMode.Repeat);
		sphereMat.setTexture("DiffuseMap", texture);
		sphereMat.setTexture("NormalMap",
				assetManager.loadTexture("Textures/Pond/Pond_normal.png"));
		sphereMat.setBoolean("UseMaterialColors", true);
		sphereMat.setColor("Diffuse", ColorRGBA.White);
		sphereMat.setColor("Specular", ColorRGBA.White);
		sphereMat.setFloat("Shininess", 64f); // [0,128]
		geometry.setMaterial(sphereMat);

		geometry.setLocalTranslation(new Vector3f(0, 0, 0));

		// the block physics:
		RigidBodyControl blockPhy = new RigidBodyControl(0);
		geometry.addControl(blockPhy);
		blockPhy.setKinematic(true);
		bulletAppState.getPhysicsSpace().add(blockPhy);

		rootNode.attachChild(geometry);
	}

	private void addBlockAt(int x, int y, int z) {
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
		sphereMat.setTexture("NormalMap",
				assetManager.loadTexture("Textures/Pond/Pond_normal.png"));
		sphereMat.setBoolean("UseMaterialColors", true);
		sphereMat.setColor("Diffuse", ColorRGBA.White);
		sphereMat.setColor("Specular", ColorRGBA.White);
		sphereMat.setFloat("Shininess", 64f); // [0,128]
		geometry.setMaterial(sphereMat);

		geometry.setLocalTranslation(new Vector3f(x, y, z));

		// the block physics:
		RigidBodyControl blockPhy = new RigidBodyControl(0);
		geometry.addControl(blockPhy);
		blockPhy.setKinematic(true);
		bulletAppState.getPhysicsSpace().add(blockPhy);

		rootNode.attachChild(geometry);
	}

	private void initFloor() {
		int FLOOR_RADIUS = 50;
		for (int x = -FLOOR_RADIUS; x <= FLOOR_RADIUS; x++) {
			for (int z = -FLOOR_RADIUS; z <= FLOOR_RADIUS; z++) {
				addBlockAt(x, 0, z);
				addBlockAt(x, -1, z);
				addBlockAt(x, -2, z);

				if (Math.abs(x) >= FLOOR_RADIUS - 2
						|| Math.abs(z) >= FLOOR_RADIUS - 2) {
					addBlockAt(x, 1, z);
					addBlockAt(x, 2, z);
					addBlockAt(x, 3, z);
				}
			}
		}
	}

	/** A centered plus sign to help the player aim. */
	protected void initCrossHairs() {
		setDisplayStatView(false);
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		ch.setText("+"); // crosshairs
		ch.setLocalTranslation(
				// center
				settings.getWidth() / 2 - ch.getLineWidth() / 2,
				settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
		guiNode.attachChild(ch);
	}
}
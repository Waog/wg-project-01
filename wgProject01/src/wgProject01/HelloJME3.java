package wgProject01;

import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Sample 1 - how to get started with the most simple JME 3 application. Display
 * a blue 3D cube and view from all sides by moving the mouse and pressing the
 * WASD keys.
 */
public class HelloJME3 extends SimpleApplication {

	/**
	 * hjkdshjkgh
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		HelloJME3 app = new HelloJME3();
		app.start(); // start the game
	}

	@Override
	public void simpleInitApp() {
		Box b = new Box(1, 1, 1); // create cube shape
		Geometry geom = new Geometry("Box", b); // create cube geometry from the
												// shape
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md"); // create a simple
														// material
		mat.setColor("Color", ColorRGBA.Blue); // set color of material to blue
		geom.setMaterial(mat); // set the cube's material
		rootNode.attachChild(geom); // make the cube appear in the scene

		Box b2 = new Box(0.5f, 2.0f, 0.5f); // create cube shape
		Geometry geom2 = new Geometry("Box2", b2); // create cube geometry from
													// the shape
		Material mat2 = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md"); // create a simple
														// material
		mat2.setColor("Color", ColorRGBA.Red); // set color of material to blue
		geom2.setMaterial(mat2); // set the cube's material
		rootNode.attachChild(geom2); // make the cube appear in the scene
	}
}
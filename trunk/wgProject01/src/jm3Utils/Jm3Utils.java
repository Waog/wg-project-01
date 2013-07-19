package jm3Utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Line;

/**
 * Provides static methods for operating on the jm3 framework.
 * 
 * @author oli
 * 
 */
public class Jm3Utils {

	/**
	 * places a visible line in the coordinate system of the given node, between
	 * the given end points.
	 */
	public static void drawLine(Vector3f start, Vector3f end, Node node, AssetManager assetManager) {
		Line line = new Line(start, end);
		Geometry geometry = new Geometry("Line", line);
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.White);
		geometry.setMaterial(mat);
		node.attachChild(geometry);
	}
}

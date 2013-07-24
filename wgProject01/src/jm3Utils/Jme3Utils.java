package jm3Utils;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Line;

/**
 * Provides static methods for operating on the jm3 framework.
 * 
 * @author oli
 * 
 */
public class Jme3Utils {

	/**
	 * Places a visible line in the coordinate system of the given node, between
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
	
	/**
	 * Returns a random colored semi transparent cube Geometry with the given "radius".
	 */
	public static Geometry getCubeGeom(float radius, AssetManager assetManager) {
		Box mesh = new Box(0.1f, 0.1f, 0.1f);
		Geometry result = new Geometry("Block", mesh);
		result.setQueueBucket(Bucket.Transparent);
		Material debugMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		ColorRGBA randomColor = ColorRGBA.randomColor();
		randomColor.a = 0.5f;
		debugMaterial.setColor("Color", randomColor);
		debugMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha); // !
		result.setMaterial(debugMaterial);
		return result;
	}
}

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
import com.jme3.scene.shape.Quad;

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
	public static void drawLine(Vector3f start, Vector3f end, Node node,
			AssetManager assetManager) {
		Line line = new Line(start, end);
		Geometry geometry = new Geometry("Line", line);
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.White);
		geometry.setMaterial(mat);
		node.attachChild(geometry);
	}

	/**
	 * Returns a random colored semi transparent cube Geometry with the given
	 * "radius".
	 */
	public static Geometry getCubeGeom(float radius, AssetManager assetManager) {
		return getCuboid(new Vector3f(radius, radius, radius), assetManager);
	}

	/**
	 * Returns a random colored semi transparent quad object with the given side
	 * length.
	 */
	public static Geometry getQuad(float sideLength, AssetManager assetManager) {
		Quad mesh = new Quad(sideLength, sideLength);
		Geometry result = new Geometry("Quad", mesh);
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
	
	/**
	 * Returns a random colored semi transparent cuboid Geometry with the given
	 * "radii".
	 */
	public static Geometry getCuboid(Vector3f radii, AssetManager assetManager) {
		Box mesh = new Box(radii.x, radii.y, radii.z);
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

package jm3Utils;

import JSci.maths.CoordinateMath;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
	 * the given end points and returns the corresponding Spatial, which can
	 * easily be rotated around the start point.
	 */
	public static Spatial drawLine(Vector3f start, Vector3f end, Node node,
			AssetManager assetManager) {
		Node result = new Node("line container");
		node.attachChild(result);
		Line line = new Line(start, end);
		Geometry geometry = new Geometry("Line", line);
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.White);
		geometry.setMaterial(mat);
		result.attachChild(geometry);
		return result;
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

	/**
	 * Returns the spherical coordinates (r, theta, phi) for the given cartesian
	 * coordinate (x,y,z).
	 */
	public static Vector3f getSphericalCoord(Vector3f cartesianCoord) {
		double[] sphericalArray = CoordinateMath.cartesianToSpherical(
				cartesianCoord.x, cartesianCoord.y, cartesianCoord.z);
		Vector3f result = new Vector3f((float) sphericalArray[0],
				(float) sphericalArray[1], (float) sphericalArray[2]);
		return result;
	}

	/**
	 * Returns the cartesian coordinate (x,y,z) for the given spherical
	 * coordinates (r, theta, phi).
	 */
	public static Vector3f getCartesianCoord(Vector3f sphericalCoord) {
		double[] cartesianArray = CoordinateMath.sphericalToCartesian(
				sphericalCoord.x, sphericalCoord.y, sphericalCoord.z);
		Vector3f result = new Vector3f((float) cartesianArray[0],
				(float) cartesianArray[1], (float) cartesianArray[2]);
		return result;
	}
}

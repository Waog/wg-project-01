package wgProject01.ingameState;

import java.util.Stack;

import jm3Utils.Jm3Utils;
import wgProject01.GameApplication;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * 
 * @author Mirco
 * 
 */
public class Player extends AbstractAppState implements ActionListener {

	/** defines the shot ray's length */
	private static final float RAY_LIMIT = 6;

	/** like in {@link IngameState} */
	private Node shootables;

	/** the final String for the action PlaceBlock */
	private static final String PLACE_BLOCK = "PlaceBlock";
	/** the final String for the action MineBlock */
	private static final String MINE_BLOCK = "MineBlock";

	/**
	 * datafields to avoid creating new objects on every frame, used especially
	 * in method {@link #shootRay(float)}
	 */
	private CollisionResults results = new CollisionResults();
	private Ray ray = new Ray();
	private Vector3f projectedVector = new Vector3f();

	/** the geometry of the highlighter */
	private Geometry highlightedBlockFace = new Geometry();

	/** the players inventory, currently implemented as a stack */
	private Stack<BlockGameObj> inventory = new Stack<BlockGameObj>();

	/**
	 * datafields given by the {@link GameApplication} and the
	 * {@link AssetManager} itself
	 */
	private InputManager inputManager;
	private Node rootNode;
	private AssetManager assetManager;
	private Camera cam;
	private GameApplication app;

	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		// initialize datafields
		super.initialize(stateManager, app);
		this.app = (GameApplication) app;

		this.rootNode = this.app.getRootNode();
		this.shootables = (Node) this.rootNode.getChild(IngameState.SHOOTABLES);
		;
		this.assetManager = this.app.getAssetManager();
		this.inputManager = this.app.getInputManager();
		this.cam = this.app.getCamera();

		// initialize material for highlighted block face
		Material matGrid = new Material(assetManager,
				"assets/Materials/Unshaded/Unshaded.j3md");
		matGrid.setColor("Color", ColorRGBA.LightGray);
		matGrid.getAdditionalRenderState().setWireframe(true);

		// initialize highlightedBlockFace
		highlightedBlockFace.setMaterial(matGrid);
		highlightedBlockFace.setMesh(new Box(0.5f, 0f, 0.5f));
		highlightedBlockFace.setLocalTranslation(0, 2, 0);

		// make the highlighter visible
		rootNode.attachChild(highlightedBlockFace);

		// initialize the key mappings
		initKeys();

	}

	/**
	 * Highlights the block face the player views at if the face is in the range
	 * of {@link #RAY_LIMIT} meters
	 */
	private void highlightBlockFace() {
		results.clear(); // resets the results
		results = shootRay(RAY_LIMIT);
		if (results.size() > 0) { // case that there is at least a block in the
									// view direction and within the range

			// get the geometry of the hit spatial that is closest to the player
			CollisionResult closest = results.getClosestCollision();
			Geometry geom = closest.getGeometry();
			Vector3f selectedBlockPos = geom.getWorldTranslation();
			Vector3f vectorToNeighbor = getVectorToNeighbor(geom,
					closest.getContactPoint());

			Vector3f halfVecToNeigh = vectorToNeighbor.mult(0.51f);
			// this is supposed to be a bit in front of the last block in a
			// local coordinate system relative to the mid point of the hit
			// geometry

			highlightedBlockFace.setLocalTranslation(selectedBlockPos
					.add(halfVecToNeigh));

			highlightedBlockFace.rotateUpTo(vectorToNeighbor);
			rootNode.attachChild(highlightedBlockFace);
		} else { // case that no block is in range or view direction, then hide
					// the highlighter
			highlightedBlockFace.removeFromParent();
		}
	}

	/**
	 * currently picks up a block and puts it into the inventory
	 */
	private void mineBlock() {
		results.clear(); // resets the results
		results = shootRay(RAY_LIMIT);
		if (results.size() > 0) {
			CollisionResult closest = results.getClosestCollision();
			Geometry geom = closest.getGeometry();
			Node node = geom.getParent();
			if (node.getName().equals(IngameState.MINEABLES)) {
				Vector3f clickedBlockPos = geom.getLocalTranslation();
				BlockGameObj clickedBlock = BlockManager.getInstance()
						.getBlock(clickedBlockPos);
				BlockManager.getInstance().removeBlockFrom(clickedBlockPos);
				inventory.add(clickedBlock);
			}

			// this one may be important for non-block spatials
			else {
			}
		}
	}

	/**
	 * takes a spatial from the inventory and puts it onto the right place
	 */
	private void placeBlockFromInv() {
		results.clear(); // resets the results
		if (inventory.size() > 0) { // check for empty inventoryNode
			results = shootRay(RAY_LIMIT);
			if (results.size() > 0) { // check if there is a hit at all
				Vector3f blockLocation = calculateBlockLocation(results);
				BlockGameObj curBlock = inventory.pop();
				BlockManager.getInstance().setBlock(blockLocation, curBlock);

				// spaten.setLocalTranslation(blockLocation);
				// mineables.attachChild(spaten);
				// TODO 3 is there anything that can be set but is not mineable?
				// TODO 2 stop blocks from being placed when player is in the
				// way
			}
		}
	}

	/**
	 * calculates the position where the block shall be set and returns it
	 * 
	 * @param results
	 *            the set of results calculated
	 * @return the position of the block to be placed
	 */
	private Vector3f calculateBlockLocation(CollisionResults results) {
		CollisionResult closest = results.getClosestCollision();
		Geometry selectedGeom = closest.getGeometry();
		Vector3f vectorToNeighbor = getVectorToNeighbor(selectedGeom,
				closest.getContactPoint());
		// System.out.println(blockLocation.toString()); // for Testing
		// System.out.println(geom.getLocalTranslation().toString());
		Vector3f blockLocation = selectedGeom.getWorldTranslation().add(
				vectorToNeighbor);
		Jm3Utils.drawLine(selectedGeom.getWorldTranslation(), blockLocation,
				rootNode, assetManager);
		return blockLocation;
	}

	/**
	 * calculates which of the six faces of a block was hit by projecting it to
	 * the axes and
	 * 
	 * @param geom
	 *            the geometry of the hitted block
	 * @param contactPoint
	 *            the closest intersection point of the ray with the block
	 * @return a signed unit coordinate vector corresponding to the face of the
	 *         hit block
	 */
	private Vector3f getVectorToNeighbor(Geometry geom, Vector3f contactPoint) {
		// calculate the vector pointing from the middle of geom to contactPoint
		projectedVector.set(contactPoint.add(geom.getWorldTranslation()
				.negate()));

		float xProjectionLength = projectedVector.dot(Vector3f.UNIT_X);
		// scalar product of projectedVector with the unit vector in x-dir'n
		float yProjectionLength = projectedVector.dot(Vector3f.UNIT_Y);
		float zProjectionLength = projectedVector.dot(Vector3f.UNIT_Z);
		// get maximum of the three projections
		float projectionMax = Math.max(Math.abs(xProjectionLength),
				Math.abs(yProjectionLength));
		projectionMax = Math.max(projectionMax, Math.abs(zProjectionLength));
		if (projectionMax == xProjectionLength
				|| projectionMax == -xProjectionLength) {
			return Vector3f.UNIT_X.multLocal(Math.signum(xProjectionLength));
		} else if (projectionMax == yProjectionLength
				|| projectionMax == -yProjectionLength) {
			return Vector3f.UNIT_Y.multLocal(Math.signum(yProjectionLength));
		} else {
			return Vector3f.UNIT_Z.multLocal(Math.signum(zProjectionLength));
		}

	}

	/**
	 * shoots a ray of length >limit< in camera direction and returns a list of
	 * results where the ray intersects with elements of the node shootables
	 * 
	 * @param limit
	 *            the maximum length of the ray, may be infinity
	 * @return results the list of CollisionResults
	 */
	private CollisionResults shootRay(float limit) {
		ray.setOrigin(cam.getLocation());
		ray.setDirection(cam.getDirection());
		ray.setLimit(limit);
		shootables.collideWith(ray, results);

		// Print the results for Testing
		//
		// System.out.println("----- Collisions? " + results.size() + "-----");
		// for (int i = 0; i < results.size(); i++) { // For each hit, we know
		// // distance, impact point, // name of geometry. float dist =
		// results.getCollision(i).getDistance(); Vector3f pt =
		// results.getCollision(i).getContactPoint(); String hit =
		// results.getCollision(i).getGeometry().getName();
		// System.out.println("* Collision #" + i);
		// System.out.println("  You shot " + hit + " at " + pt + ", " + dist +
		// " wu away."); }
		//
		return results;
	}

	/**
	 * We over-write some navigational key mappings here, so we can add
	 * physics-controlled walking and jumping:
	 */
	private void initKeys() {
		inputManager.addMapping(PLACE_BLOCK, new MouseButtonTrigger(
				MouseInput.BUTTON_RIGHT));
		inputManager.addMapping(MINE_BLOCK, new MouseButtonTrigger(
				MouseInput.BUTTON_LEFT));
		inputManager.addListener(this, PLACE_BLOCK);
		inputManager.addListener(this, MINE_BLOCK);
	}

	/**
	 * These are our custom actions triggered by key presses. We do not walk
	 * yet, we just keep track of the direction the user pressed.
	 */
	public void onAction(String binding, boolean value, float tpf) {
		if (binding.equals(PLACE_BLOCK) && !value) {
			placeBlockFromInv();
		} else if (binding.equals(MINE_BLOCK) && !value) {
			mineBlock();
		}
	}

	@Override
	public void update(float tpf) {
		super.update(tpf);
		highlightBlockFace();
	}
}

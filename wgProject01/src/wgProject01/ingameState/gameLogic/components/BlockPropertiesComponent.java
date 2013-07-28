package wgProject01.ingameState.gameLogic.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.artemis.Component;
import com.jme3.math.Vector3f;

/**
 * A {@link Component} (pure data structure) to describe a position of a block.
 * 
 * @author oli
 * 
 */
public class BlockPropertiesComponent extends Component {

	/**
	 * A type which a block can be of.
	 */
	public static final String TYPE_WOOD = "TYPE_WOOD",
			TYPE_STONE = "TYPE_STONE";

	/**
	 * The position of the block in the world. Is only of use if {@link #placed}
	 * is true.
	 */
	public int x, y, z;

	/**
	 * Flag: Is the block placed somewhere in the world?
	 */
	public Boolean placed;

	/**
	 * The type of the Block. The Value must be one of the static constants
	 * defined in {@link BlockPropertiesComponent} with the prefix
	 * <code>TYPE_</code> - for example <code>TYPE_WOOD<code>.
	 */
	private String type = TYPE_WOOD;

	/**
	 * Creates a new BlockPropertiesComponent with the given type.
	 * 
	 * @param type
	 *            The type of the Block. The Value must be one of the static
	 *            constants defined in {@link BlockPropertiesComponent} with the
	 *            prefix <code>TYPE_</code>, for example <code>TYPE_WOOD<code>.
	 */
	public BlockPropertiesComponent(String type) {
		super();
		this.type = type;
	}

	/**
	 * Returns the type of the Block. The Value is one of the static constants
	 * defined in {@link BlockPropertiesComponent} with the prefix
	 * <code>TYPE_</code> - for example <code>TYPE_WOOD<code>.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Returns a random block type.
	 * 
	 * @see BlockPropertiesComponent#getType()
	 * @see BlockPropertiesComponent#type
	 */
	public static String getRandomType() {
		List<String> allTypes = new ArrayList<String>();
		allTypes.add(TYPE_STONE);
		allTypes.add(TYPE_WOOD);

		Collections.shuffle(allTypes);
		return allTypes.get(0);
	}

	/**
	 * Returns the block position as a Vector3f object.
	 */
	public Vector3f getPosVec3f() {
		return new Vector3f(x, y, z);
	}
}
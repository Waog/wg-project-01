package wgProject01.ingameState.gameLogic.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.artemis.Component;

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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((placed == null) ? 0 : placed.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + x;
		result = prime * result + y;
		result = prime * result + z;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockPropertiesComponent other = (BlockPropertiesComponent) obj;
		if (placed == null) {
			if (other.placed != null)
				return false;
		} else if (!placed.equals(other.placed))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		if (z != other.z)
			return false;
		return true;
	}
}
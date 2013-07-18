package utils;

/**
 * Is like the {@link BetterArray}-class, except this class is 3 dimensional,
 * i.e. you always need three indices to access it.
 * 
 * @author oli
 * 
 */
public class BetterArray3D<T> {

	/**
	 * Build an array of better 2D-arrays to establish 2 dimensions. The x
	 * coordinate is the index of the inner array, while the y and z coordinate
	 * are the x and y indices of the values inside the inner array
	 * respectively.
	 */
	private BetterArray<BetterArray2D<T>> space = new BetterArray<BetterArray2D<T>>();

	/**
	 * Returns the element at the specified position in this Array.
	 */
	public T get(int x, int y, int z) {
		BetterArray2D<T> innerArray = space.get(x);
		if (innerArray == null) {
			return null;
		}

		return innerArray.get(y, z);
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 */
	public void set(int x, int y, int z, T element) {
		BetterArray2D<T> innerArray = space.get(x);
		if (innerArray == null) {
			innerArray = new BetterArray2D<T>();
			space.set(x, innerArray);
		}
		innerArray.set(y, z, element);
	}

}

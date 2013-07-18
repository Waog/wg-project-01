package utils;

/**
 * Is like the {@link BetterArray}-class, except this class is 2 dimensional,
 * i.e. you always need two indices to access it.
 * 
 * @author oli
 * 
 */
public class BetterArray2D<T> {

	/**
	 * Build an array of better arrays to establish 2 dimensions. The x
	 * coordinate is the index of the inner array, while the y coordinate is the
	 * index of the value inside the inner array.
	 */
	private BetterArray<BetterArray<T>> space = new BetterArray<BetterArray<T>>();

	/**
	 * Returns the element at the specified position in this Array.
	 */
	public T get(int x, int y) {
		BetterArray<T> innerArray = space.get(x);
		if (innerArray == null) {
			return null;
		}

		return innerArray.get(y);
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 */
	public void set(int x, int y, T element) {
		BetterArray<T> innerArray = space.get(x);
		if (innerArray == null) {
			innerArray = new BetterArray<T>();
			space.set(x, innerArray);
		}
		innerArray.set(y, element);
	}

}

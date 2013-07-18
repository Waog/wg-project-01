package utils;

import java.util.ArrayList;

/**
 * This class works like an array, except that: - you need to use method calls,
 * instead of the []-operator. - it scales automatically, so you can insert
 * objects at any index. - you can use negative indices as well.
 * 
 * @author oli
 * 
 */
public class BetterArray<T> {
	/**
	 * Contains the elements of the positive indices and zero.
	 */
	ArrayList<T> list = new ArrayList<T>();

	/**
	 * Returns the element at the specified position in this Array.
	 */
	public T get(int index) {
		int positiveIndex = getPositiveIndex(index);
		checkIndex(positiveIndex);
		return list.get(positiveIndex);
	}

	/**
	 * Replaces the element at the specified position in this list with the
	 * specified element.
	 */
	public void set(int index, T element) {
		int positiveIndex = getPositiveIndex(index);
		checkIndex(positiveIndex);
		list.set(positiveIndex, element);
	}

	/**
	 * Transforms the given original index, to a always positive index like
	 * this: (given Index->returned index), (0->0), (1->1), (-1->2), (2->3),
	 * (-2->4), ...
	 */
	private int getPositiveIndex(int originalIndex) {
		if (originalIndex > 0) {
			return 2 * originalIndex - 1;
		} else {
			return -2 * originalIndex;
		}
	}

	/**
	 * Transforms the given positive index, to the original index, reversing the
	 * effect of {@link #getPositiveIndex(int)}.
	 */
	private int getOriginalIndex(int positiveIndex) {
		if (positiveIndex % 2 == 0) { // even
			return positiveIndex / -2;
		} else {
			return (positiveIndex + 1) / 2;
		}
	}

	/**
	 * Checks if the given positive index is existant in the internal list and
	 * fills the list with "null"-objects until this index if not.
	 */
	private void checkIndex(int positiveIndex) {
		while (list.size() - 1 < positiveIndex) {
			list.add(null);
		}
	}
}

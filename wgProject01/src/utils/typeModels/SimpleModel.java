package utils.typeModels;

import java.util.Observable;

/**
 * See {@link utils.typeModels package description} on how to use this class.
 */
public class SimpleModel<T> extends Observable {
	private T internal;

	public SimpleModel(T initialValue) {
		super();
		this.internal = initialValue;
	}

	public T get() {
		return internal;
	}

	public void set(T internal) {
		this.internal = internal;
		super.setChanged();
		super.notifyObservers();
	}
}

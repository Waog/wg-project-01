package utils.typeModels;

/**
 * See {@link utils.typeModels package description} on how to use this class.
 */
public class IntegerModel extends SimpleModel<Integer> {
	public IntegerModel(int initialValue) {
		super(initialValue);
	}

	public void increment() {
		add(1);
	}
	
	public void decrement() {
		add(-1);
	}
	
	public void add(int summand) {
		Integer integer = super.get();
		int sum = integer.intValue() + summand;
		super.set(sum);
	}
	
	public void subtract(int subtrahend) {
		add(-subtrahend);
	}
}

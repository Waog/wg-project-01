package wgProject01;

import utils.typeModels.IntModel;


public class ModelAccessor {
	
	// =========== singleton stuff ===============
	
	/**
	 * The singleton instance of this class.
	 */
	private static ModelAccessor singletonInstance;

	/**
	 * Returns the singleton instance of this class. The
	 */
	public static ModelAccessor getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new ModelAccessor();
		}

		return singletonInstance;
	}

	/**
	 * Private Constructor to ensure the singleton pattern. Use
	 * {@link #getInstance()} to access an object of this class. Initializes
	 * the singleton object of this class.
	 */
	private ModelAccessor() {
		// nothing
	}
	
	// ================ content =============
	
	// TODO: if annoying, implement generic mechanism to add new models/components.
	public IntModel itemCount = new IntModel(0);
}

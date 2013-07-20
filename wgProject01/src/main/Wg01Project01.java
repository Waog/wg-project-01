package main;

import wgProject01.GameApplication;

/**
 * The Class with (only) the main() method. Starts the Application.
 * 
 * @author oli
 * 
 */
public class Wg01Project01 {

	/**
	 * Initializes a new {@link GameApplication} object and calls its
	 * {@link GameApplication#start() start()} method.
	 * 
	 * @param args not used at all.
	 */
	public static void main(String[] args) {
		GameApplication app = new GameApplication();
		app.start();
	}

}

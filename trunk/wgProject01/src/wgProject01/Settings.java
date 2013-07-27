package wgProject01;

/**
 * <p>
 * Contains globally accessible setting variables. Anyone may write and read the
 * variables at will.
 * </p>
 * 
 * <p>
 * This is probably not the best architectural solution, but it works for the
 * moment, especially to activate and deactive debug mode at a central point.
 * </p>
 * 
 * @author oli
 * 
 */
public class Settings {

	/**
	 * <p>
	 * What debug information shall be printed or rendered? If this variable is
	 * set to a high value anything is rendered. If set to 0 no debug stuff
	 * shall be rendered at all.
	 * </p>
	 * 
	 * possible values are:
	 * <ul>
	 * <li> <code>debugMode = 0</code>: nothing is rendered</li>
	 * <li> <code>debugMode = 1</code>: still acceptable for playing, only global
	 * console debug information, like TPF.</li>
	 * <li> <code>debugMode = 2</code>: debug rendering allowed, max 1 console
	 * line per frame per problem domain.</li>
	 * <li> <code>debugMode = 3</code>: all debugging stuff is allowed as long as
	 * it doesn't slow down the game too hard</li>
	 * <li> <code>debugMode = 4</code>: anything allowed</li>
	 * <ul>
	 */
	public static int debugMode = 3;
}

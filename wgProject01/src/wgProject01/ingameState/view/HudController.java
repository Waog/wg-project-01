package wgProject01.ingameState.view;

import java.util.Observable;
import java.util.Observer;

import utils.typeModels.IntModel;
import wgProject01.ModelAccessor;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * See {@link wgProject01.ingameState.view package description} for general
 * information on how to use controller classes.
 */
public class HudController implements Observer, ScreenController {

	/**
	 * Flag: was the corresponding nifty GUI XML file already read?
	 */
	private static boolean readXmlOnce = false;
	
	private Nifty nifty;

	public HudController(Nifty nifty) {
		super();
		this.nifty = nifty;
		IntModel itemCount = ModelAccessor.getInstance().itemCount;
		itemCount.addObserver(this);

		createHud();

		//update(this.itemCount, null);
	}

	@Override
	public void update(Observable o, Object arg) {
		IntModel itemCount = ModelAccessor.getInstance().itemCount;
		if (o == itemCount) {
			String newText = "itemCount: " + itemCount.get();

			// find old text
			Element niftyElement = nifty.getScreen("hud").findElementByName(
					"updatedText");
			// swap old with new text
			niftyElement.getRenderer(TextRenderer.class).setText(newText);
		}
	}

	private void createHud() {
		nifty.registerScreenController(this);
		if (! readXmlOnce) {
			/** Read your XML and initialize your custom ScreenController */
			nifty.addXml("Interface/hud.xml");
			readXmlOnce = true;
		}
	
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onEndScreen() {
		nifty.unregisterScreenController(this);
		IntModel itemCount = ModelAccessor.getInstance().itemCount;
		itemCount.deleteObserver(this);
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
	}

}

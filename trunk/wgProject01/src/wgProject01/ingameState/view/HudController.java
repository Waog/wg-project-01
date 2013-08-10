package wgProject01.ingameState.view;

import java.util.Observable;
import java.util.Observer;

import utils.typeModels.IntModel;
import wgProject01.ModelAccessor;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.TextRenderer;

/**
 * See {@link wgProject01.ingameState.view package description} for general
 * information on how to use controller classes.
 */
public class HudController implements Observer {

	private IntModel itemCount;
	private Nifty nifty;

	public HudController(Nifty nifty) {
		super();
		this.nifty = nifty;
		this.itemCount = ModelAccessor.getInstance().itemCount;
		this.itemCount.addObserver(this);

		createHud();
		
		update(this.itemCount, null);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o == itemCount) {
			String newText = "itemCount: " + itemCount.get();

			// find old text
			Element niftyElement = nifty.getCurrentScreen().findElementByName(
					"updatedText");
			// swap old with new text
			niftyElement.getRenderer(TextRenderer.class).setText(newText);
		}
	}

	private void createHud() {
		/** Read your XML and initialize your custom ScreenController */
		nifty.fromXml("Interface/hud.xml", "start");
	}

}

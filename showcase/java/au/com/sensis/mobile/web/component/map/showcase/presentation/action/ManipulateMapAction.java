package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;

/**
 * Demonstrates how to manipulate (eg. pan or zoom) an existing map using the
 * {@link #getMapDelegate()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ManipulateMapAction extends AbstractMapAction {

    private ManipulateMapForm manipulateMapForm;

    /**
     * Executes this action and returns a result name.
     * @return result name.
     */
    public String execute() {
        // Example of how to use the MapDelegate to pan/zoom an existing map.
        final Map map =
                getMapDelegate().manipulateMap(getModel().getOrignalMapCentre(),
                        getModel().getMapUrl(), getModel().getMapLayer(),
                        getModel().getCursorType(),
                        getModel().getAction(), getContext());
        setMap(map);

        return successOrAjaxSuccess();
    }

    /**
     * @return Model being used to drive this action.
     * @see com.opensymphony.xwork2.ModelDriven#getModel()
     */
    public ManipulateMapForm getModel() {
        if (manipulateMapForm == null) {
            manipulateMapForm = new ManipulateMapForm();
        }
        return manipulateMapForm;
    }
}

package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;

/**
 * Demonstrates how to manipulate (eg. pan or zoom) an existing Route map
 * using the {@link #getMapDelegate()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ManipulateRouteMapAction extends AbstractMapAction {

    private ManipulateMapForm manipulateMapForm;

    /**
     * Executes this action and returns a result name.
     *
     * @return result name.
     */
    public String execute() {
        final Map map =
                getMapDelegate().manipulateRouteMap(
                        getModel().getRouteHandle(),
                        new JourneyWaypoints(getModel().getRouteStart(),
                                getModel().getRouteEnd()),
                        getModel().getRoutingOption(), getModel().getMapUrl(),
                        getModel().getMapLayer(), getModel().getAction(),
                        getContext());
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

package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;

/**
 * Demonstrates how to manipulate (eg. pan or zoom) an existing Route leg step map
 * using the {@link #getMapDelegate()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ManipulateRouteLegStepMapAction extends AbstractMapAction {

    private ManipulateMapForm manipulateMapForm;

    /**
     * Executes this action and returns a result name.
     *
     * @return result name.
     */
    public String execute() {
        final Map map =
                getMapDelegate().manipulateRouteLegStepMap(
                        getModel().getRouteHandle(),
                        new JourneyWaypoints(getModel().getRouteStart(),
                                getModel().getRouteEnd()),
                        getModel().getRoutingOption(), getModel().getMapUrl(),
                        getModel().getMapLayer(), getModel().getAction(),
                        getContext());
        setMap(map);

        rememberCurrentLegStepMapZoom(map);

        return successOrAjaxSuccess();
    }

    /**
     * @param map
     */
    private void rememberCurrentLegStepMapZoom(final Map map) {
        getContext().getRawSessionData().put(
                GetRouteLegStepMapAction.ROUTE_LEG_STEP_MAP_ZOOM_SESSION_KEY,
                map.getMapUrl().getZoom());
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

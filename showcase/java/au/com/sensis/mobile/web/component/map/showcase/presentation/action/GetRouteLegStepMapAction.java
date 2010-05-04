package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;

/**
 * Demonstrates how to an initial Route leg step map
 * using the {@link #getMapDelegate()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GetRouteLegStepMapAction extends AbstractMapAction {

    /**
     * Session key for storing the current zoom level being use for leg step maps.
     */
    public static final String ROUTE_LEG_STEP_MAP_ZOOM_SESSION_KEY = "routeLegStepMapZoom";

    /**
     * Default zoom to use for leg step maps.
     */
    public static final Integer DEFAULT_ROUTE_LEG_STEP_ZOOM = new Integer(4);

    private ManipulateMapForm manipulateMapForm;

    /**
     * Executes this action and returns a result name.
     *
     * @return result name.
     */
    public String execute() {

        final Integer zoom = getPreviousLegStepMapZoom();

        final Map map =
                getMapDelegate().getInitialRouteLegStepMap(
                        getModel().getRouteHandle(),
                        new JourneyWaypoints(getModel().getRouteStart(),
                                getModel().getRouteEnd()),
                        getModel().getRoutingOption(),
                        getModel().getRouteLegStepCentre(),
                        zoom,
                        getModel().getMapLayer(),
                        getContext());
        setMap(map);

        return successOrAjaxSuccess();
    }

    /**
     * @return
     */
    private Integer getPreviousLegStepMapZoom() {
        final Integer zoom = (Integer) getContext().getRawSessionData().get(
                GetRouteLegStepMapAction.ROUTE_LEG_STEP_MAP_ZOOM_SESSION_KEY);
        return zoom;
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

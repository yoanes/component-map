package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Demonstrates how to an initial Route leg step map
 * using the {@link MapDelegate}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GetRouteLegStepMapAction extends BusinessAction implements
        ModelDriven<ManipulateMapForm> {

    public static final String ROUTE_LEG_STEP_MAP_ZOOM_SESSION_KEY = "routeLegStepMapZoom";
    public static final Integer DEFAULT_ROUTE_LEG_STEP_ZOOM = new Integer(4);

    private ManipulateMapForm manipulateMapForm;

    private LocationDelegate locationDelegate;
    private MapDelegate mapDelegate;

    private Map map;

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

        return ResultName.SUCCESS;
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

    /**
     * @return the locationDelegate
     */
    public LocationDelegate getLocationDelegate() {
        return locationDelegate;
    }

    /**
     * @param locationDelegate
     *            the locationDelegate to set
     */
    public void setLocationDelegate(final LocationDelegate locationDelegate) {
        this.locationDelegate = locationDelegate;
    }

    /**
     * @return the mapDelegate
     */
    public MapDelegate getMapDelegate() {
        return mapDelegate;
    }

    /**
     * @param mapDelegate
     *            the mapDelegate to set
     */
    public void setMapDelegate(final MapDelegate mapDelegate) {
        this.mapDelegate = mapDelegate;
    }

    /**
     * @return the map
     */
    public Map getMap() {
        return map;
    }

    /**
     * @param map
     *            the map to set
     */
    public void setMap(final Map map) {
        this.map = map;
    }

}

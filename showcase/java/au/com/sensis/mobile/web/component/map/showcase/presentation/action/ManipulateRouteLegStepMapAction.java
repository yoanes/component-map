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
 * Demonstrates how to manipulate (eg. pan or zoom) an existing Route leg step map
 * using the {@link MapDelegate}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ManipulateRouteLegStepMapAction extends BusinessAction implements
        ModelDriven<ManipulateMapForm> {

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

        return ResultName.SUCCESS;
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

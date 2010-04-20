package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import org.apache.log4j.Logger;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.mapping.MapLayer;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Demonstrates how to get an initial Route map using the {@link MapDelegate}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GetRouteAction extends BusinessAction implements
        ModelDriven<MapForm> {

    private static Logger logger = Logger.getLogger(GetRouteAction.class);

    private MapForm mapForm;

    private LocationDelegate locationDelegate;
    private MapDelegate mapDelegate;

    private Map map;

    /**
     * Executes this action and returns a result name.
     *
     * @return result name.
     */
    public String execute() {

        resetRouteLegStepMapZoom();

        final GeocodedAddress startAddress =
                getLocationDelegate().resolveToSingleLocation(
                        getModel().getRouteStartAddress());

        final GeocodedAddress endAddress =
                getLocationDelegate().resolveToSingleLocation(
                        getModel().getRouteEndAddress());

        final JourneyWaypoints journeyWaypoints =
                new JourneyWaypoints(startAddress.getCoordinates(), endAddress
                        .getCoordinates());

        final Map map =
                getMapDelegate().getInitialRouteMap(journeyWaypoints,
                        getModel().getRoutingOption(), MapLayer.Map,
                        getContext());
        // Update the model with the actual routing option used, since the MapDelegate
        // may change it if the original request was invalid. eg. attempting to walk
        // a distance > 10km.
        getModel().setRoutingOption(map.getRouteDetails().getRoutingOption());

        setMap(map);

        if (logger.isDebugEnabled()) {
            logger.debug("mapUrl found: " + getMap().getMapUrl());
        }

        return ResultName.SUCCESS;

    }

    /**
     *
     */
    private void resetRouteLegStepMapZoom() {
        getContext().getRawSessionData().put(
                GetRouteLegStepMapAction.ROUTE_LEG_STEP_MAP_ZOOM_SESSION_KEY,
                GetRouteLegStepMapAction.DEFAULT_ROUTE_LEG_STEP_ZOOM);
    }

    /**
     * @return Model being used to drive this action.
     * @see com.opensymphony.xwork2.ModelDriven#getModel()
     */
    public MapForm getModel() {
        if (mapForm == null) {
            mapForm = new MapForm();
        }
        return mapForm;
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
}

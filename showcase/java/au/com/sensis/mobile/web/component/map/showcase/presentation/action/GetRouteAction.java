package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import org.apache.log4j.Logger;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.mapping.MapLayer;

/**
 * Demonstrates how to get an initial Route map using the {@link #getMapDelegate()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GetRouteAction extends AbstractMapAction {

    private static Logger logger = Logger.getLogger(GetRouteAction.class);

    private MapForm mapForm;

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

        return successOrAjaxSuccess();
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
}

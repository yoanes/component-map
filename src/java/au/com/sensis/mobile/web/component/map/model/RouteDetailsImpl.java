package au.com.sensis.mobile.web.component.map.model;

import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.RouteHandle;
import au.com.sensis.wireless.manager.directions.RoutingOption;

/**
 * Default {@link RouteDetails} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class RouteDetailsImpl implements RouteDetails {

    private final RouteHandle routeHandle;
    private final RoutingOption routingOption;
    private final JourneyWaypoints journeyWaypoints;

    /**
     * Default constructor.
     *
     * @param routeHandle See {@link #getEmsRouteHandle()}.
     * @param routingOption See {@link #getRoutingOption()}.
     * @param journeyWaypoints See {@link #getWaypoints()}.
     */
    public RouteDetailsImpl(final RouteHandle routeHandle,
            final RoutingOption routingOption, final JourneyWaypoints journeyWaypoints) {
        this.routeHandle = routeHandle;
        this.routingOption = routingOption;
        this.journeyWaypoints = journeyWaypoints;
    }

    /**
     * {@inheritDoc}
     */
    public String getEmsJsTransportType() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public RouteHandle getEmsRouteHandle() {
        return routeHandle;
    }

    /**
     * {@inheritDoc}
     */
    public RoutingOption getRoutingOption() {
        return routingOption;
    }

    /**
     * {@inheritDoc}
     */
    public JourneyWaypoints getWaypoints() {
        return journeyWaypoints;
    }

}

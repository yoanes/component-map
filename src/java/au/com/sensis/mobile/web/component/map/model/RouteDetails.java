package au.com.sensis.mobile.web.component.map.model;

import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.RouteHandle;
import au.com.sensis.wireless.manager.directions.RoutingOption;

/**
 * Route related details of a {@link Map}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface RouteDetails {

    /**
     * @return Handle to the generated route.
     */
    RouteHandle getEmsRouteHandle();

    /**
     * Returns the {@link RoutingOption} that the (route) map was requested
     * with.
     *
     * @return the {@link RoutingOption} that the (route) map was requested
     *         with
     */
    RoutingOption getRoutingOption();

    /**
     * @return {@link JourneyWaypoints} used to generate the route.
     */
    JourneyWaypoints getWaypoints();

    /**
     * @return the "transportType" value required by clients that call the EMS
     *         JavaScript interface directly. This value corresponds to
     *         {@link RoutingOption#getTransportOption()}. @ Thrown if
     *         {@link #isRouteMap()} is false.
     */
    String getEmsJsTransportType();

}

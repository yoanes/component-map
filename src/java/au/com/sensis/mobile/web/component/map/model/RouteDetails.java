package au.com.sensis.mobile.web.component.map.model;

import java.util.List;

import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.Leg;
import au.com.sensis.wireless.manager.directions.LegStepDetail;
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

    /**
     * @return Legs contained by the route. May not be null.
     */
    List<Leg> getLegs();

    /**
     * @return Total distance of the route in metres.
     */
    double getTotalDistanceMetres();

    /**
     * @return Total duration of the route in seconds.
     */
    int getTotalDurationSeconds();

    /**
     * @return Total number of legsteps.
     */
    int getTotalLegSteps();

    /**
     * @return Details for all leg steps in all legs.
     */
    List<LegStepDetail> getAllLegStepDetails();

}

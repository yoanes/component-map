package au.com.sensis.mobile.web.component.map.model;

import java.util.HashMap;

import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.RouteHandle;
import au.com.sensis.wireless.manager.directions.RoutingOption;

/**
 * Default {@link RouteDetails} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class RouteDetailsImpl implements RouteDetails {

    private static final String EMS_JS_TRANSPORT_TYPE_ALL_VEHICLES = "ALL_VEHICLES";
    private static final String EMS_JS_TRANSPORT_TYPE_PEDESTRIAN = "PEDESTRIAN";
    private final RouteHandle routeHandle;
    private final RoutingOption routingOption;
    private final JourneyWaypoints journeyWaypoints;
    private static java.util.Map<RoutingOption, String>
        routingOptionToEmsJsTransportTypeMap;

    static {
        setRoutingOptionToEmsJsTransportTypeMap(new HashMap<RoutingOption, String>());
        getRoutingOptionToEmsJsTransportTypeMap().put(RoutingOption.BY_FOOT,
                EMS_JS_TRANSPORT_TYPE_PEDESTRIAN);
        getRoutingOptionToEmsJsTransportTypeMap().put(
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                EMS_JS_TRANSPORT_TYPE_ALL_VEHICLES);
        getRoutingOptionToEmsJsTransportTypeMap().put(
                RoutingOption.FASTEST_BY_ROAD_WITH_TOLLS,
                EMS_JS_TRANSPORT_TYPE_ALL_VEHICLES);

        for (final RoutingOption routingOption : RoutingOption.values()) {
            if (getRoutingOptionToEmsJsTransportTypeMap().get(routingOption) == null) {
                throw new IllegalStateException(
                        "The routingOptionToEmsJsTransportTypeMap contains no entry "
                                + "for RoutingOption: " + routingOption.name());
            }
        }
    }

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
        // The map is guaranteed to contain a value for the non-null RoutingOption, due to the
        // validation performed by the static initializer.
        return getRoutingOptionToEmsJsTransportTypeMap().get(getRoutingOption());
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

    /**
     * @param routingOptionToEmsJsTransportTypeMap the routingOptionToEmsJsTransportTypeMap to set
     */
    private static void setRoutingOptionToEmsJsTransportTypeMap(
            final java.util.Map<RoutingOption, String> routingOptionToEmsJsTransportTypeMap) {
        RouteDetailsImpl.routingOptionToEmsJsTransportTypeMap
            = routingOptionToEmsJsTransportTypeMap;
    }

    /**
     * @return the routingOptionToEmsJsTransportTypeMap
     */
    private static java.util.Map<RoutingOption, String> getRoutingOptionToEmsJsTransportTypeMap() {
        return routingOptionToEmsJsTransportTypeMap;
    }

}

package au.com.sensis.mobile.web.component.map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import au.com.sensis.wireless.manager.directions.JourneyDescriptor;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.Leg;
import au.com.sensis.wireless.manager.directions.LegStepDetail;
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
    private final JourneyWaypoints journeyWaypoints;
    private static java.util.Map<RoutingOption, String>
        routingOptionToEmsJsTransportTypeMap;
    private final JourneyDescriptor journeyDescriptor;

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
     * @param journeyWaypoints See {@link #getWaypoints()}.
     * @param journeyDescriptor {@link JourneyDescriptor} generated for the route.
     */
    public RouteDetailsImpl(final JourneyWaypoints journeyWaypoints,
            final JourneyDescriptor journeyDescriptor) {
        this.journeyWaypoints = journeyWaypoints;
        this.journeyDescriptor = journeyDescriptor;
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
        if (getJourneyDescriptor().getEmsRouteHandle() != null) {
            return new RouteHandle(getJourneyDescriptor().getEmsRouteHandle());
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public RoutingOption getRoutingOption() {
        return getJourneyDescriptor().getRoutingOption();
    }

    /**
     * {@inheritDoc}
     */
    public JourneyWaypoints getWaypoints() {
        return journeyWaypoints;
    }

    /**
     * {@inheritDoc}
     */
    public List<Leg> getLegs() {
        if (getJourneyDescriptor().getLegs() != null) {
            return getJourneyDescriptor().getLegs();
        } else {
            return new ArrayList<Leg>();
        }
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

    /**
     * @return the journeyDescriptor
     */
    private JourneyDescriptor getJourneyDescriptor() {
        return journeyDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public List<LegStepDetail> getAllLegStepDetails() {
        return getJourneyDescriptor().getAllLegStepDetails();
    }

    /**
     * {@inheritDoc}
     */
    public double getTotalDistanceMetres() {
        return getJourneyDescriptor().getTotalDistanceMetres();
    }

    /**
     * {@inheritDoc}
     */
    public int getTotalLegSteps() {
        return getJourneyDescriptor().getTotalLegSteps();
    }


}

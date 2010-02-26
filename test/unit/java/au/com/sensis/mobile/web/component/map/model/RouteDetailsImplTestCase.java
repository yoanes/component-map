package au.com.sensis.mobile.web.component.map.model;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.RouteHandle;
import au.com.sensis.wireless.manager.directions.RoutingOption;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link RouteDetailsImpl}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class RouteDetailsImplTestCase extends AbstractJUnit4TestCase {

    private RouteDetailsImpl objectUnderTest;

    private RouteHandle mockRouteHandle;
    private JourneyWaypoints mockJourneyWaypoints;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new RouteDetailsImpl(getMockRouteHandle(),
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                getMockJourneyWaypoints()));
    }

    @Test
    public void testGetEmsJsTransportType() throws Throwable {
        final java.util.Map<RoutingOption, String> expectedValuesMap =
                new HashMap<RoutingOption, String>();
        expectedValuesMap.put(RoutingOption.BY_FOOT, "PEDESTRIAN");
        expectedValuesMap.put(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                "ALL_VEHICLES");
        expectedValuesMap.put(RoutingOption.FASTEST_BY_ROAD_WITH_TOLLS,
                "ALL_VEHICLES");

        for (final RoutingOption routingOption : RoutingOption.values()) {
            setObjectUnderTest(new RouteDetailsImpl(getMockRouteHandle(),
                    routingOption, getMockJourneyWaypoints()));

            if (expectedValuesMap.get(routingOption) == null) {
                throw new IllegalStateException(
                        "The test data contains no entry for RoutingOption: "
                                + routingOption.name() + ". Fix your test data !");
            }

            Assert.assertEquals(
                    "emsJsTransportType is wrong for RoutingOption: "
                            + routingOption.name(), expectedValuesMap
                            .get(routingOption), getObjectUnderTest()
                            .getEmsJsTransportType());
        }

    }

    /**
     * @return the objectUnderTest
     */
    public RouteDetailsImpl getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(final RouteDetailsImpl objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockRouteHandle
     */
    public RouteHandle getMockRouteHandle() {
        return mockRouteHandle;
    }

    /**
     * @param mockRouteHandle the mockRouteHandle to set
     */
    public void setMockRouteHandle(final RouteHandle mockRouteHandle) {
        this.mockRouteHandle = mockRouteHandle;
    }

    /**
     * @return the mockJourneyWaypoints
     */
    public JourneyWaypoints getMockJourneyWaypoints() {
        return mockJourneyWaypoints;
    }

    /**
     * @param mockJourneyWaypoints the mockJourneyWaypoints to set
     */
    public void setMockJourneyWaypoints(final JourneyWaypoints mockJourneyWaypoints) {
        this.mockJourneyWaypoints = mockJourneyWaypoints;
    }



}

package au.com.sensis.mobile.web.component.map.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.manager.directions.JourneyDescriptor;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.Leg;
import au.com.sensis.wireless.manager.directions.LegStepDetail;
import au.com.sensis.wireless.manager.directions.RouteHandle;
import au.com.sensis.wireless.manager.directions.RoutingOption;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

import com.whereis.ems.SoapRouteHandle;

/**
 * Unit test {@link RouteDetailsImpl}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class RouteDetailsImplTestCase extends AbstractJUnit4TestCase {

    private static final double DOUBLE_COMPARISON_DELTA = 1e-10;

    private RouteDetailsImpl objectUnderTest;

    private RouteHandle mockRouteHandle;
    private JourneyWaypoints mockJourneyWaypoints;
    private JourneyDescriptor mockJourneyDescriptor;
    private List<Leg> legs;
    private Leg mockLeg;

    /**
     * Setup test data.
     *
     * @throws Exception
     *             Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setLegs(new ArrayList<Leg>());
        getLegs().add(getMockLeg());

        setObjectUnderTest(new RouteDetailsImpl(getMockJourneyWaypoints(),
                getMockJourneyDescriptor()));
    }

    @Test
    public void testGetRouteHandleWhenJourneyDescriptorContainsNullHandle()
            throws Throwable {
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
                .andReturn(null);

        replay();

        Assert.assertNull("getEmsRouteHandle() should be null",
                getObjectUnderTest().getEmsRouteHandle());

    }

    @Test
    public void testGetRouteHandleWhenJourneyDescriptorContainsNonNullHandle()
            throws Throwable {
        final SoapRouteHandle soapRouteHandle = new SoapRouteHandle();
        soapRouteHandle.setIdentifier("myHandle");

        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
                .andReturn(soapRouteHandle).atLeastOnce();

        replay();

        Assert.assertNotNull("getEmsRouteHandle() should not be null",
                getObjectUnderTest().getEmsRouteHandle());
        Assert
                .assertEquals("emsRouteHandle contains wrong identifier",
                        "myHandle", objectUnderTest.getEmsRouteHandle()
                                .getIdentifier());

    }

    @Test
    public void testGetLegsWhenJourneyDescriptorContainsNullLegs()
            throws Throwable {
        EasyMock.expect(getMockJourneyDescriptor().getLegs()).andReturn(null);

        replay();

        Assert.assertEquals("legs list is wrong", new ArrayList<Leg>(),
                getObjectUnderTest().getLegs());

    }

    @Test
    public void testGetLegsWhenJourneyDescriptorContainsNonNullLegs()
            throws Throwable {
        final ArrayList<Leg> expectedLegs = new ArrayList<Leg>();
        EasyMock.expect(getMockJourneyDescriptor().getLegs()).andReturn(
                expectedLegs).atLeastOnce();

        replay();

        Assert.assertSame("legs list is wrong", expectedLegs, objectUnderTest
                .getLegs());
    }

    @Test
    public void testGetTotalDistanceMetres() throws Throwable {
        final Double expectedDistance = new Double(100);
        EasyMock.expect(getMockJourneyDescriptor().getTotalDistanceMetres())
                .andReturn(expectedDistance);

        replay();

        Assert.assertEquals("distance is wrong",
                expectedDistance.doubleValue(), getObjectUnderTest()
                        .getTotalDistanceMetres(), DOUBLE_COMPARISON_DELTA);

    }

    @Test
    public void testGetTotalLegSteps() throws Throwable {
        final int expectedTotalLegSteps = 100;
        EasyMock.expect(getMockJourneyDescriptor().getTotalLegSteps())
            .andReturn(expectedTotalLegSteps);

        replay();

        Assert.assertEquals("totalLegSteps is wrong",
                expectedTotalLegSteps, getObjectUnderTest()
                .getTotalLegSteps());

    }

    @Test
    public void testGetAllLegStepDetails() throws Throwable {
        final List<LegStepDetail> expectedStepDetails =
                new ArrayList<LegStepDetail>();
        EasyMock.expect(getMockJourneyDescriptor().getAllLegStepDetails())
                .andReturn(expectedStepDetails);

        replay();

        Assert.assertSame("allLegStepDetails are wrong", expectedStepDetails,
                getObjectUnderTest().getAllLegStepDetails());

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
            setObjectUnderTest(new RouteDetailsImpl(
                    getMockJourneyWaypoints(), getMockJourneyDescriptor()));

            EasyMock.expect(getMockJourneyDescriptor().getRoutingOption())
                .andReturn(routingOption);

            if (expectedValuesMap.get(routingOption) == null) {
                throw new IllegalStateException(
                        "The test data contains no entry for RoutingOption: "
                                + routingOption.name() + ". Fix your test data !");
            }

            replay();

            Assert.assertEquals(
                    "emsJsTransportType is wrong for RoutingOption: "
                            + routingOption.name(), expectedValuesMap
                            .get(routingOption), getObjectUnderTest()
                            .getEmsJsTransportType());

            // Reset mocks prior to next iteration.
            reset();
            setReplayed(false);
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

    /**
     * @return the mockLeg
     */
    public Leg getMockLeg() {
        return mockLeg;
    }

    /**
     * @param mockLeg the mockLeg to set
     */
    public void setMockLeg(final Leg mockLeg) {
        this.mockLeg = mockLeg;
    }

    /**
     * @return the legs
     */
    public List<Leg> getLegs() {
        return legs;
    }

    /**
     * @param legs the legs to set
     */
    public void setLegs(final List<Leg> legs) {
        this.legs = legs;
    }

    /**
     * @return the mockJourneyDescriptor
     */
    public JourneyDescriptor getMockJourneyDescriptor() {
        return mockJourneyDescriptor;
    }

    /**
     * @param mockJourneyDescriptor the mockJourneyDescriptor to set
     */
    public void setMockJourneyDescriptor(final JourneyDescriptor mockJourneyDescriptor) {
        this.mockJourneyDescriptor = mockJourneyDescriptor;
    }



}

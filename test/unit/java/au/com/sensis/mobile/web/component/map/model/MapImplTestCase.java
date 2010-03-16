package au.com.sensis.mobile.web.component.map.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.WGS84PointTestDataFactory;
import au.com.sensis.wireless.manager.directions.JourneyDescriptor;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.RoutingOption;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;
import au.com.sensis.wireless.manager.mapping.ResolvedIcon;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

import com.whereis.ems.SoapRouteHandle;

/**
 * Unit test {@link MapImpl}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapImplTestCase extends AbstractJUnit4TestCase {

    private static final int EMS_ZOOM_LEVEL = 16;
    private static final int ZOOM_LEVEL = 5;
    private static final String ROUTE_IDENTIFIER = "myFunkyRoute";
    private WGS84PointTestDataFactory wgs84PointTestDataFactory;
    private WGS84Point wgs84Point1;
    private WGS84Point wgs84Point2;
    private MapUrl mockMapUrl;
    private JourneyDescriptor mockJourneyDescriptor;
    private SoapRouteHandle mockSoapRouteHandle;
    private MobilesBoundingBox mockMobilesBoundingBox;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setWgs84PointTestDataFactory(new WGS84PointTestDataFactory());
        setWgs84Point1(getWgs84PointTestDataFactory().createValidWGS84Point());
        setWgs84Point2(getWgs84PointTestDataFactory().createValidWGS84Point2());
    }

    @Test
    public void testCreateMapRetrievedInstance() throws Throwable {
        final List<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        final Map map =
                MapImpl.createMapRetrievedInstance(getWgs84Point1(),
                        MapLayer.Photo,
                        getMockMapUrl(), resolvedIcons,
                        EMS_ZOOM_LEVEL, true, false);

        Assert.assertTrue("isMapImageRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertFalse(
                "isMapImageRetrievalDeferredToClient() should be false",
                map.isMapImageRetrievalDeferredToClient());
        Assert.assertEquals("mapUrl is wrong", getMockMapUrl(), map
                .getMapUrl());
        Assert.assertEquals("originalMapCentre is wrong", getWgs84Point1(),
                map.getOriginalMapCentre());
        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong",
                map.isPhotoWithStreetsLayer());

        Assert.assertEquals("getResolvedIcons() is wrong", new ArrayList<ResolvedIcon>(),
                map.getResolvedIcons());

        Assert.assertTrue("isZoomDetailsDefined() should be true", map.isZoomDetailsDefined());
        Assert.assertNotNull("zoomDetails should not be null", map.getZoomDetails());
        Assert.assertEquals("EMS zoom is wrong", EMS_ZOOM_LEVEL,
                map.getZoomDetails().getEmsZoom());
        Assert.assertTrue("isAtMinimumZoom should be true",
                map.getZoomDetails().isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom should be false",
                map.getZoomDetails().isAtMaximumZoom());

        Assert.assertSame("resolvedIcons is wrong", resolvedIcons,
                map.getResolvedIcons());

    }

    @Test
    public void testCreateMapRetrievalDeferredInstance() throws Throwable {
        final List<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        final Map map =
                MapImpl.createMapRetrievalDeferrendInstance(
                        getWgs84Point1(), getWgs84Point2(), MapLayer.Map, resolvedIcons,
                        ZOOM_LEVEL, EMS_ZOOM_LEVEL, false, true);

        Assert.assertFalse("isMapImageRetrieved() should be false",
                map.isMapImageRetrieved());
        Assert.assertTrue(
                "isMapImageRetrievalDeferredToClient() should be true",
                map.isMapImageRetrievalDeferredToClient());

        Assert.assertNotNull("mapUrl should not be null", map
                .getMapUrl());
        Assert.assertEquals("mapCentre is wrong", getWgs84Point2(),
                map.getMapUrl().getMapCentre());
        Assert.assertEquals("zoom is wrong", ZOOM_LEVEL,
                map.getMapUrl().getZoom());
        Assert.assertEquals("imageUrl is wrong", StringUtils.EMPTY,
                map.getMapUrl().getImageUrl());
        Assert.assertNull("boundingBox is wrong",
                map.getMapUrl().getBoundingBox());

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertEquals("originalMapCentre is wrong", getWgs84Point1(),
                map.getOriginalMapCentre());
        Assert.assertTrue("isMapLayer is wrong", map.isMapLayer());
        Assert.assertFalse("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong",
                map.isPhotoWithStreetsLayer());

        Assert.assertTrue("isZoomDetailsDefined() should be true",
                map.isZoomDetailsDefined());
        Assert.assertNotNull("zoomDetails should not be null", map.getZoomDetails());
        Assert.assertEquals("EMS zoom is wrong", EMS_ZOOM_LEVEL,
                map.getZoomDetails().getEmsZoom());
        Assert.assertFalse("isAtMinimumZoom should be false",
                map.getZoomDetails().isAtMinimumZoom());
        Assert.assertTrue("isAtMaximumZoom should be true",
                map.getZoomDetails().isAtMaximumZoom());
    }

    @Test
    public void testCreateRouteMapRetrievedInstance() throws Throwable {

        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl());

        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
                .andReturn(getMockSoapRouteHandle()).atLeastOnce();
        EasyMock.expect(getMockSoapRouteHandle().getIdentifier()).andReturn(
                ROUTE_IDENTIFIER);
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption())
            .andReturn(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS).atLeastOnce();

        replay();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;

        final JourneyWaypoints journeyWaypoints =
            createJourneyWaypoints(startLat, startLong, endLat, endLong);

        final List<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();

        final Map map =
                MapImpl.createRouteMapRetrievedInstance(journeyWaypoints,
                        getMockJourneyDescriptor(),
                        MapLayer.Photo, resolvedIcons,
                        EMS_ZOOM_LEVEL, true, false);

        Assert.assertTrue("isMapImageRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertFalse(
                "isMapImageRetrievalDeferredToClient() should be false", map
                        .isMapImageRetrievalDeferredToClient());
        Assert.assertEquals("mapUrl is wrong", getMockMapUrl(), map
                        .getMapUrl());

        try {
            map.getOriginalMapCentre();
            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {
            Assert.assertEquals("IllegalStateException has wrong message",
                    "It is illegal to call getOriginalMapCentre() "
                            + "when isRouteMap() is true", e.getMessage());
        }

        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong", map
                .isPhotoWithStreetsLayer());

        Assert.assertSame("getResolvedIcons() is wrong",
                resolvedIcons, map.getResolvedIcons());

        Assert.assertTrue("isZoomDetailsDefined() should be true", map.isZoomDetailsDefined());
        Assert.assertNotNull("zoomDetails should not be null", map.getZoomDetails());
        Assert.assertEquals("EMS zoom is wrong", EMS_ZOOM_LEVEL,
                map.getZoomDetails().getEmsZoom());
        Assert.assertTrue("isAtMinimumZoom should be true",
                map.getZoomDetails().isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom should be false",
                map.getZoomDetails().isAtMaximumZoom());

        Assert.assertTrue("isRouteMap is wrong", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails should not be null", map
                .getRouteDetails());
        Assert.assertEquals("RouteDetails contains wrong waypoints",
                journeyWaypoints, map.getRouteDetails().getWaypoints());
        Assert.assertEquals("RouteDetails contains wrong RoutingOption",
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, map.getRouteDetails()
                        .getRoutingOption());
        Assert.assertEquals("RouteDetails contains wrong RoutingHandle",
                ROUTE_IDENTIFIER, map.getRouteDetails().getEmsRouteHandle()
                        .getIdentifier());

    }

    @Test
    public void testCreateRouteMapRetrievalDeferredInstanceWithNoZoomDetails() throws Throwable {

        EasyMock.expect(getMockJourneyDescriptor().getMap())
            .andReturn(getMockMapUrl());
        EasyMock.expect(getMockMapUrl().getBoundingBox())
            .andReturn(getMockMobilesBoundingBox()).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption())
            .andReturn(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS);
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
            .andReturn(null);

        replay();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;

        final JourneyWaypoints journeyWaypoints
            = createJourneyWaypoints(startLat, startLong, endLat, endLong);
        final List<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        final Map map =
            MapImpl.createRouteMapRetrievalDeferredInstance(journeyWaypoints,
                    getMockJourneyDescriptor(),
                    MapLayer.Photo, resolvedIcons);


        Assert.assertFalse("isMapImageRetrieved() should be false", map
                .isMapImageRetrieved());
        Assert.assertTrue(
                "isMapImageRetrievalDeferredToClient() should be true",
                map.isMapImageRetrievalDeferredToClient());

        Assert.assertSame("mapUrl is wrong", getMockMapUrl(), map
                .getMapUrl());

        try {
            map.getOriginalMapCentre();
            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {
            Assert.assertEquals("IllegalStateException has wrong message",
                    "It is illegal to call getOriginalMapCentre() "
                            + "when isRouteMap() is true", e.getMessage());
        }
        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong",
                map.isPhotoWithStreetsLayer());

        Assert.assertSame("getResolvedIcons() is wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertFalse("isZoomDetailsDefined() should be false", map.isZoomDetailsDefined());
        Assert.assertTrue("isBoundingBoxDefined() should be true", map.isBoundingBoxDefined());
        Assert.assertSame("geMobilesBoundingBox() is wrong", getMockMobilesBoundingBox(),
                map.getMapUrl().getBoundingBox());

        Assert.assertTrue("isRouteMap is wrong", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails should not be null", map.getRouteDetails());
        Assert.assertEquals("RouteDetails contains wrong waypoints",
                journeyWaypoints, map.getRouteDetails().getWaypoints());
        Assert.assertEquals("RouteDetails contains wrong RoutingOption",
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                map.getRouteDetails().getRoutingOption());
        Assert.assertNull("RouteDetails should have a null RoutingHandle",
                map.getRouteDetails().getEmsRouteHandle());

    }

    @Test
    public void testCreateRouteMapRetrievalDeferredInstanceWithZoomDetails()
            throws Throwable {

        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl());
        EasyMock.expect(getMockMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox()).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption())
                .andReturn(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS);
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
                .andReturn(null);

        replay();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;

        final JourneyWaypoints journeyWaypoints =
                createJourneyWaypoints(startLat, startLong, endLat, endLong);
        final List<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        final int emsZoom = 14;
        final boolean atMinimumZoom = true;
        final boolean atMaximumZoom = false;
        final Map map =
                MapImpl.createRouteMapRetrievalDeferredInstance(
                        journeyWaypoints, getMockJourneyDescriptor(),
                        MapLayer.Photo, resolvedIcons, emsZoom, atMinimumZoom,
                        atMaximumZoom);

        Assert.assertFalse("isMapImageRetrieved() should be false", map
                .isMapImageRetrieved());
        Assert.assertTrue(
                "isMapImageRetrievalDeferredToClient() should be true", map
                        .isMapImageRetrievalDeferredToClient());

        Assert.assertSame("mapUrl is wrong", getMockMapUrl(), map
                .getMapUrl());

        try {
            map.getOriginalMapCentre();
            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {
            Assert.assertEquals("IllegalStateException has wrong message",
                    "It is illegal to call getOriginalMapCentre() "
                            + "when isRouteMap() is true", e.getMessage());
        }
        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong", map
                .isPhotoWithStreetsLayer());

        Assert.assertSame("getResolvedIcons() is wrong", resolvedIcons, map
                .getResolvedIcons());

        Assert.assertTrue("isZoomDetailsDefined() should be true", map
                .isZoomDetailsDefined());
        Assert.assertEquals("emsZoom is wrong", emsZoom, map.getZoomDetails()
                .getEmsZoom());
        Assert.assertEquals("atMinimumZoom is wrong", atMinimumZoom, map
                .getZoomDetails().isAtMinimumZoom());
        Assert.assertEquals("atMaximumZoom is wrong", atMaximumZoom, map
                .getZoomDetails().isAtMaximumZoom());

        Assert.assertTrue("isBoundingBoxDefined() should be true", map
                .isBoundingBoxDefined());
        Assert.assertSame("geMobilesBoundingBox() is wrong",
                getMockMobilesBoundingBox(), map.getMapUrl().getBoundingBox());

        Assert.assertTrue("isRouteMap is wrong", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails should not be null", map
                .getRouteDetails());
        Assert.assertEquals("RouteDetails contains wrong waypoints",
                journeyWaypoints, map.getRouteDetails().getWaypoints());
        Assert.assertEquals("RouteDetails contains wrong RoutingOption",
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, map.getRouteDetails()
                        .getRoutingOption());
        Assert.assertNull("RouteDetails should have a null RoutingHandle", map
                .getRouteDetails().getEmsRouteHandle());

    }

    private JourneyWaypoints createJourneyWaypoints(final double startLat, final double startLong,
            final double endLat, final double endLong) {
        final WGS84Point startWGS84Pt = new WGS84Point(startLong, startLat);
        final WGS84Point endWGS84Pt = new WGS84Point(endLong, endLat);
        final JourneyWaypoints journeyWayPoints = new JourneyWaypoints(startWGS84Pt, endWGS84Pt);
        return journeyWayPoints;
    }

    @Test
    public void testGetMapLayerShortCode() throws Throwable {
        final MapLayer[] testValues =
                { MapLayer.Map, MapLayer.Photo, MapLayer.PhotoWithStreets };
        final String[] expectedValues = { "m", "p", "ps" };

        for (int i = 0; i < testValues.length; i++) {
            final Map map =
                    MapImpl.createMapRetrievedInstance(
                            getWgs84Point1(), testValues[i], getMockMapUrl(),
                            new ArrayList<ResolvedIcon>(), EMS_ZOOM_LEVEL,
                            true, false);

            Assert.assertEquals("MapLayer short code is wrong",
                    expectedValues[i], map.getMapLayerShortCode());
        }
    }

    @Test
    public void testGetJsMapLayer() throws Throwable {
        final MapLayer[] testValues =
        { MapLayer.Map, MapLayer.Photo, MapLayer.PhotoWithStreets };
        final String[] expectedValues = { "map", "photo", "map" };

        for (int i = 0; i < testValues.length; i++) {
            final Map map =
                MapImpl.createMapRetrievedInstance(
                        getWgs84Point1(), testValues[i], getMockMapUrl(),
                        new ArrayList<ResolvedIcon>(),
                        EMS_ZOOM_LEVEL, true, false);

            Assert.assertEquals("MapLayer short code is wrong",
                    expectedValues[i], map.getJsMapLayer());
        }
    }

    /**
     * @return the wgs84Point1
     */
    public WGS84Point getWgs84Point1() {
        return wgs84Point1;
    }

    /**
     * @param wgs84Point1 the wgs84Point1 to set
     */
    public void setWgs84Point1(final WGS84Point wgs84Point1) {
        this.wgs84Point1 = wgs84Point1;
    }

    /**
     * @return the mockMapUrl
     */
    public MapUrl getMockMapUrl() {
        return mockMapUrl;
    }

    /**
     * @param mockMapUrl the mockMapUrl to set
     */
    public void setMockMapUrl(final MapUrl mockMapUrl) {
        this.mockMapUrl = mockMapUrl;
    }

    /**
     * @return the wgs84PointTestDataFactory
     */
    public WGS84PointTestDataFactory getWgs84PointTestDataFactory() {
        return wgs84PointTestDataFactory;
    }

    /**
     * @param wgs84PointTestDataFactory the wgs84PointTestDataFactory to set
     */
    public void setWgs84PointTestDataFactory(
            final WGS84PointTestDataFactory wgs84PointTestDataFactory) {
        this.wgs84PointTestDataFactory = wgs84PointTestDataFactory;
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

    /**
     * @return the mockSoapRouteHandle
     */
    public SoapRouteHandle getMockSoapRouteHandle() {
        return mockSoapRouteHandle;
    }

    /**
     * @param mockSoapRouteHandle the mockSoapRouteHandle to set
     */
    public void setMockSoapRouteHandle(final SoapRouteHandle mockSoapRouteHandle) {
        this.mockSoapRouteHandle = mockSoapRouteHandle;
    }

    /**
     * @return the wgs84Point2
     */
    public WGS84Point getWgs84Point2() {
        return wgs84Point2;
    }

    /**
     * @param wgs84Point2 the wgs84Point2 to set
     */
    public void setWgs84Point2(final WGS84Point wgs84Point2) {
        this.wgs84Point2 = wgs84Point2;
    }

    /**
     * @return the mockMobilesBoundingBox
     */
    public MobilesBoundingBox getMockMobilesBoundingBox() {
        return mockMobilesBoundingBox;
    }

    /**
     * @param mockMobilesBoundingBox the mockMobilesBoundingBox to set
     */
    public void setMockMobilesBoundingBox(final MobilesBoundingBox mockMobilesBoundingBox) {
        this.mockMobilesBoundingBox = mockMobilesBoundingBox;
    }
}

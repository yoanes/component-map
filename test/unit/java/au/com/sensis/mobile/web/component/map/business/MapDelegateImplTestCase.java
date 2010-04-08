package au.com.sensis.mobile.web.component.map.business;

import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.WGS84PointTestDataFactory;
import au.com.sensis.mobile.web.component.core.device.DeviceConfigRegistry;
import au.com.sensis.mobile.web.component.map.business.MapDelegate.Action;
import au.com.sensis.mobile.web.component.map.business.MapDelegateImpl.ScreenDimensionsStrategy;
import au.com.sensis.mobile.web.component.map.device.generated.DeviceConfig;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.sal.common.UserContext;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.manager.directions.JourneyDescriptor;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.RouteHandle;
import au.com.sensis.wireless.manager.directions.RoutingOption;
import au.com.sensis.wireless.manager.ems.EMSManager;
import au.com.sensis.wireless.manager.mapping.IconDescriptor;
import au.com.sensis.wireless.manager.mapping.IconType;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;
import au.com.sensis.wireless.manager.mapping.MobilesIconType;
import au.com.sensis.wireless.manager.mapping.PanZoomDetail;
import au.com.sensis.wireless.manager.mapping.ResolvedIcon;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.manager.mapping.UserMapInteraction;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;
import au.com.sensis.wireless.web.common.validation.ValidatableTestUtils;
import au.com.sensis.wireless.web.mobile.MobileContext;

import com.whereis.ems.SoapRouteHandle;

/**
 * Unit test {@link MapDelegateImpl}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapDelegateImplTestCase extends AbstractJUnit4TestCase {

    private static final int MAX_ZOOM = 10;

    private static final int MIN_ZOOM = 1;

    private MapDelegateImpl objectUnderTest;

    private WGS84PointTestDataFactory wgs84PointTestDataFactory;

    private WGS84Point point1;
    private WGS84Point point2;
    private WGS84Point point3;
    private WGS84Point point4;
    private WGS84Point point5;

    private static final int ZOOM_LEVEL = 5;
    private static final int EMS_ZOOM_LEVEL = 16;
    private static final float POI_MAP_RADIUS_MULTIPLIER = 1.05f;

    private MobileContext mockMobileContext;
    private UserContext mockUserContext;
    private EMSManager mockEmsManager;
    private MapUrl mockMapUrl;
    private MapUrl mockExistingMapUrl;
    private ScreenDimensionsStrategy mockScreenDimensionsStrategy;
    private ScreenDimensions mockScreenDimensions;
    private MobilesBoundingBox mockMobilesBoundingBox;
    private MobilesBoundingBox mockUpdatedMobilesBoundingBox;
    private DeviceConfigRegistry mockDeviceConfigRegistry;
    private Device mockDevice;
    private DeviceConfig deviceConfig;
    private JourneyDescriptor mockJourneyDescriptor;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setWgs84PointTestDataFactory(new WGS84PointTestDataFactory());
        setPoint1(getWgs84PointTestDataFactory().createValidWGS84Point());
        setPoint2(getWgs84PointTestDataFactory().createValidWGS84Point2());
        setPoint3(getWgs84PointTestDataFactory().createValidWGS84Point3());
        setPoint4(getWgs84PointTestDataFactory().createValidWGS84Point4());
        setPoint5(getWgs84PointTestDataFactory().createValidWGS84Point5());

        setDeviceConfig(new DeviceConfig());

        setObjectUnderTest(new MapDelegateImpl());
        getObjectUnderTest().setScreenDimensionsStrategy(getMockScreenDimensionsStrategy());
        getObjectUnderTest().setEmsManager(getMockEmsManager());
        getObjectUnderTest().setDeviceConfigRegistry(getMockDeviceConfigRegistry());

        // TODO: There has to be a better way of validating the zoom. See
        // MapDelegate.getInitialMap comments.
        getObjectUnderTest().setMinZoom(MIN_ZOOM);
        getObjectUnderTest().setMaxZoom(MAX_ZOOM);

        getObjectUnderTest().setPoiMapRadiusMultiplier(POI_MAP_RADIUS_MULTIPLIER);
    }

    @Test
    public void testValidateStateWhenEmsManagerIsNull() throws Throwable {
        getObjectUnderTest().setEmsManager(null);
        ValidatableTestUtils.testValidateState(getObjectUnderTest(), "emsManager", "null");
    }

    @Test
    public void testValidateStateWhenScreenDimensionsStrategyIsNull()
            throws Throwable {
        getObjectUnderTest().setScreenDimensionsStrategy(null);
        ValidatableTestUtils.testValidateState(getObjectUnderTest(),
                "screenDimensionsStrategy", "null");
    }

    @Test
    public void testValidateStateWhenValid() throws Throwable {
        getObjectUnderTest().validateState();
    }

    @Test
    public void testGetInitialMapWhenServerSideMapShouldBeGenerated() throws Throwable {

        recordShouldGenerateServerSideMap(true);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions()).atLeastOnce();

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        final PanZoomDetail panZoomDetail =
                new PanZoomDetail(null, getPoint1(),
                        UserMapInteraction.NO_ACTION, ZOOM_LEVEL);

        EasyMock.expect(
                getMockEmsManager().getMap(getMockScreenDimensions(),
                        getPoint1(), MobilesIconType.CROSS_HAIR,
                        MapLayer.PhotoWithStreets, panZoomDetail,
                        getMockUserContext())).andReturn(
                getMockMapUrl());

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockMapUrl().getImageUrl())
            .andReturn("dummy url").anyTimes();

        EasyMock.expect(getMockEmsManager().getEmsZoomLevel(ZOOM_LEVEL))
            .andReturn(EMS_ZOOM_LEVEL);

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(ZOOM_LEVEL).atLeastOnce();

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolvePoiIcons(getPoint1(),
                MobilesIconType.CROSS_HAIR,
                new ArrayList<IconDescriptor>(), getMockScreenDimensions()))
                .andReturn(resolvedIcons);

        replay();

        final Map map =
                getObjectUnderTest().getInitialMap(getPoint1(),
                        ZOOM_LEVEL, MapLayer.PhotoWithStreets,
                        MobilesIconType.CROSS_HAIR, getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());
        Assert.assertSame("map has wrong originalMapCentre", getPoint1(),
                map.getOriginalMapCentre());

        assertMapLayers(map, false, false, true);

        assertZoomDetails(map, EMS_ZOOM_LEVEL, false, false);

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());
    }

    private void recordShouldGenerateServerSideMap(
            final boolean recordShouldGenerateServerSideMap) {
        getDeviceConfig().setGenerateServerSideMap(recordShouldGenerateServerSideMap);

        EasyMock.expect(getMockMobileContext().getDevice())
            .andReturn(getMockDevice()).atLeastOnce();
        EasyMock.expect(getMockDeviceConfigRegistry().getDeviceConfig(getMockDevice()))
            .andReturn(getDeviceConfig());
    }

    private void assertZoomDetails(final Map map, final int expectedEmsZoomLevel,
            final boolean expectedAtMinimumZoom, final boolean expectedAtMaximumZoom) {
        Assert.assertTrue("isZoomDetailsDefined() should be true", map
                .isZoomDetailsDefined());
        Assert.assertNotNull("zoomDetails should not be null", map
                .getZoomDetails());
        Assert.assertEquals("EMS zoom is wrong", expectedEmsZoomLevel, map
                .getZoomDetails().getEmsZoom());
        Assert.assertEquals("isAtMinimumZoom is wrong", expectedAtMinimumZoom,
                map.getZoomDetails().isAtMinimumZoom());
        Assert.assertEquals("isAtMaximumZoom is wrong", expectedAtMaximumZoom,
                map.getZoomDetails().isAtMaximumZoom());

    }

    @Test
    public void testGetInitialMapWhenServerSideMapShouldNotBeGenerated() throws Throwable {

        recordShouldGenerateServerSideMap(false);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        EasyMock.expect(getMockEmsManager().getEmsZoomLevel(ZOOM_LEVEL))
            .andReturn(EMS_ZOOM_LEVEL);

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions());

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolvePoiIcons(getPoint1(),
                MobilesIconType.CROSS_HAIR,
                new ArrayList<IconDescriptor>(), getMockScreenDimensions()))
                .andReturn(resolvedIcons);

        replay();

        final Map map =
                getObjectUnderTest().getInitialMap(getPoint1(),
                        ZOOM_LEVEL, MapLayer.PhotoWithStreets,
                        MobilesIconType.CROSS_HAIR, getMockMobileContext());

        Assert.assertFalse("isMapImageRetrieved() should be false", map
                .isMapImageRetrieved());

        Assert.assertNotNull("map should have non-null mapUrl",
                map.getMapUrl());
        Assert.assertEquals("mapUrl has wrong mapCentre", getPoint1(),
                map.getMapUrl().getMapCentre());
        Assert.assertEquals("mapUrl has wrong zoom", ZOOM_LEVEL,
                map.getMapUrl().getZoom());

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertSame("map has wrong originalMapCentre", getPoint1(),
                map.getOriginalMapCentre());

        assertMapLayers(map, false, false, true);

        assertZoomDetails(map, EMS_ZOOM_LEVEL, false, false);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedPanEast() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_EAST, UserMapInteraction.EAST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedPanWest() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_WEST, UserMapInteraction.WEST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedPanNorth() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_NORTH, UserMapInteraction.NORTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedPanSouth() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_SOUTH, UserMapInteraction.SOUTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedZoomIn() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedZoomInWhenAlreadyAtMinZoom()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedZoomOut() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedZoomInWhenAlreadyAtMaxZoom()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedNoOp()
            throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.NO_OP, UserMapInteraction.NO_ACTION,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedChangeToMapView()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Photo,
                MapLayer.Map, Action.MAP_VIEW, UserMapInteraction.NO_ACTION,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedChangeToPhotoView()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldBeGeneratedChangeToHybridView()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulateMapWhenServerSideMapShouldBeGenerated(
            final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction, final int oldZoomLevel,
            final int newZoomLevel) {

        recordShouldGenerateServerSideMap(true);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions()).atLeastOnce();

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(getMockExistingMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox());

        EasyMock.expect(getMockExistingMapUrl().getZoom())
                .andReturn(oldZoomLevel);
        EasyMock.expect(getMockExistingMapUrl().getMapCentre()).andReturn(
                getPoint1()).atLeastOnce();

        final PanZoomDetail panZoomDetail =
                new PanZoomDetail(getMockMobilesBoundingBox(), getPoint1(),
                        userMapInteraction, newZoomLevel);

        EasyMock.expect(
                getMockEmsManager().getMap(getMockScreenDimensions(),
                        getPoint2(), MobilesIconType.CROSS_HAIR,
                        getNewMapLayerAfterApplyingMapDelegateAction(existingMapLayer,
                                mapDelegateAction),
                        panZoomDetail, getMockUserContext())).andReturn(
                getMockMapUrl());

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(newZoomLevel)
            .atLeastOnce();

        EasyMock.expect(getMockEmsManager().getEmsZoomLevel(newZoomLevel))
            .andReturn(EMS_ZOOM_LEVEL);

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolvePoiIcons(getPoint2(),
                MobilesIconType.CROSS_HAIR,
                new ArrayList<IconDescriptor>(), getMockScreenDimensions()))
                .andReturn(resolvedIcons);

        replay();

        final Map map =
                getObjectUnderTest().manipulateMap(getPoint2(), getMockExistingMapUrl(),
                        existingMapLayer, MobilesIconType.CROSS_HAIR,
                        mapDelegateAction, getMockMobileContext());

        Assert.assertTrue("isMapImageRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());
        Assert.assertSame("map has wrong originalMapCentre", getPoint2(),
                map.getOriginalMapCentre());

        assertMapLayers(map, MapLayer.Map.equals(expectedMapLayerAfterAction),
                MapLayer.Photo.equals(expectedMapLayerAfterAction),
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction));

        assertZoomDetails(map, EMS_ZOOM_LEVEL, newZoomLevel == MIN_ZOOM,
                newZoomLevel == MAX_ZOOM);

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());


    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedPanEast() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_EAST, UserMapInteraction.EAST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedPanWest() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_WEST, UserMapInteraction.WEST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedPanNorth() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_NORTH, UserMapInteraction.NORTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedPanSouth() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_SOUTH, UserMapInteraction.SOUTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedZoomIn() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedZoomInWhenAlreadyAtMinZoom()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedZoomOut() throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedZoomInWhenAlreadyAtMaxZoom()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedNoOp()
            throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.NO_OP, UserMapInteraction.NO_ACTION,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedChangeToMapView()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Photo,
                MapLayer.Map, Action.MAP_VIEW, UserMapInteraction.NO_ACTION,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedChangeToPhotoView()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapWhenServerSideMapShouldNotBeGeneratedChangeToHybridView()
        throws Throwable {
        doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }


    private void doTestManipulateMapWhenServerSideMapShouldNotBeGenerated(
            final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction, final int oldZoomLevel,
            final int newZoomLevel) {

        recordShouldGenerateServerSideMap(false);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                                getMockScreenDimensions()).atLeastOnce();

        EasyMock.expect(getMockExistingMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox());

        EasyMock.expect(getMockExistingMapUrl().getZoom())
            .andReturn(oldZoomLevel);
        EasyMock.expect(getMockExistingMapUrl().getMapCentre()).andReturn(
                getPoint1()).atLeastOnce();

        final PanZoomDetail panZoomDetail =
            new PanZoomDetail(getMockMobilesBoundingBox(), getPoint1(),
                    userMapInteraction, newZoomLevel);

        EasyMock.expect(getMockEmsManager().getEmsZoomLevel(newZoomLevel))
            .andReturn(EMS_ZOOM_LEVEL);

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolvePoiIcons(getPoint2(),
                MobilesIconType.CROSS_HAIR,
                new ArrayList<IconDescriptor>(), getMockScreenDimensions()))
                .andReturn(resolvedIcons);

        // anyTimes expectations on MockMobilesBoundingBox methods to cater
        // to multiple possible paths through PanZoomDetail.calculateNewCentre.
        EasyMock.expect(getMockMobilesBoundingBox().getBottomRight())
            .andReturn(getPoint4()).anyTimes();
        EasyMock.expect(getMockMobilesBoundingBox().getTopLeft())
            .andReturn(getPoint5()).anyTimes();

        replay();

        final Map map =
            getObjectUnderTest().manipulateMap(getPoint2(), getMockExistingMapUrl(),
                    existingMapLayer, MobilesIconType.CROSS_HAIR,
                    mapDelegateAction, getMockMobileContext());

        Assert.assertFalse("isMapImageRetrieved() should be false", map
                .isMapImageRetrieved());

        Assert.assertNotNull("map should have non-null mapUrl",
                map.getMapUrl());
        Assert.assertEquals("mapUrl has wrong mapCentre",
                panZoomDetail.calculateNewCentre(),
                map.getMapUrl().getMapCentre());
        Assert.assertEquals("mapUrl has wrong zoom", newZoomLevel,
                map.getMapUrl().getZoom());

        Assert.assertSame("map has wrong originalMapCentre", getPoint2(),
                map.getOriginalMapCentre());

        assertMapLayers(map, MapLayer.Map.equals(expectedMapLayerAfterAction),
                MapLayer.Photo.equals(expectedMapLayerAfterAction),
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction));

        assertZoomDetails(map, EMS_ZOOM_LEVEL, newZoomLevel == MIN_ZOOM,
                newZoomLevel == MAX_ZOOM);

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());


    }

    private MapLayer getNewMapLayerAfterApplyingMapDelegateAction(
            final MapLayer existingMapLayer, final Action action) {

        if (Action.MAP_VIEW.equals(action)) {
            return MapLayer.Map;
        } else if (Action.PHOTO_VIEW.equals(action)) {
            return MapLayer.Photo;
        } else if (Action.HYBRID_VIEW.equals(action)) {
            return MapLayer.PhotoWithStreets;
        } else {
            return existingMapLayer;
        }
    }

    @Test
    public void testGetInitialPoiMapWhenServerSideMapShouldBeGenerated()
            throws Exception {

        recordShouldGenerateServerSideMap(true);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(ZOOM_LEVEL).atLeastOnce();

        final List<IconDescriptor> iconDescriptors = createIconDescriptors();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions());

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        final int mobilesZoomThreshold = 4;
        EasyMock.expect(mockEmsManager.getPoiMap(
                EasyMock.same(getMockScreenDimensions()), EasyMock.eq(getPoint1()),
                EasyMock.eq(MapLayer.Photo), EasyMock.eq(iconDescriptors),
                EasyMock.eq(POI_MAP_RADIUS_MULTIPLIER, 0.01),
                EasyMock.eq(mobilesZoomThreshold),
                EasyMock.isA(UserContext.class))).andReturn(getMockMapUrl());

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockMapUrl().getImageUrl())
            .andReturn("dummy url").anyTimes();

        EasyMock.expect(mockEmsManager.getEmsZoomLevel(ZOOM_LEVEL)).andReturn(EMS_ZOOM_LEVEL);

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolvePoiIcons(getPoint1(),
                MobilesIconType.CROSS_HAIR,
                iconDescriptors, getMockScreenDimensions()))
                .andReturn(resolvedIcons);

        replay();

        final Map map = getObjectUnderTest().getInitialPoiMap(getPoint1(),
                MapLayer.Photo, iconDescriptors, mobilesZoomThreshold,
                getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());
        Assert.assertSame("map has wrong originalMapCentre", getPoint1(),
                map.getOriginalMapCentre());

        assertMapLayers(map, false, true, false);

        assertZoomDetails(map, EMS_ZOOM_LEVEL, false, false);

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

    }

    @Test
    public void testGetInitialPoiMapWhenServerSideMapShouldNotBeGenerated()
        throws Exception {

        recordShouldGenerateServerSideMap(false);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final List<IconDescriptor> iconDescriptors = createIconDescriptors();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions());

        final int mobilesZoomThreshold = 4;
        EasyMock.expect(mockEmsManager.getPoiMapBoundingBox(
                EasyMock.eq(getPoint1()),
                EasyMock.eq(iconDescriptors),
                EasyMock.eq(POI_MAP_RADIUS_MULTIPLIER, 0.01)))
                    .andReturn(getMockMobilesBoundingBox());
        EasyMock.expect(mockEmsManager.getEmsZoomLevel(mobilesZoomThreshold)).andReturn(
                EMS_ZOOM_LEVEL);

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolvePoiIcons(getPoint1(),
                MobilesIconType.CROSS_HAIR,
                iconDescriptors, getMockScreenDimensions()))
                    .andReturn(resolvedIcons);

        replay();

        final Map map = getObjectUnderTest().getInitialPoiMap(getPoint1(),
                MapLayer.PhotoWithStreets, iconDescriptors, mobilesZoomThreshold,
                getMockMobileContext());

        Assert.assertFalse("isMapImageRetrieved() should be false", map
                .isMapImageRetrieved());

        Assert.assertNotNull("map should have non-null mapUrl",
                map.getMapUrl());
        Assert.assertEquals("mapUrl has wrong mapCentre", getPoint1(),
                map.getMapUrl().getMapCentre());
        Assert.assertEquals("mapUrl has wrong zoom", 0,
                map.getMapUrl().getZoom());

        Assert.assertFalse("isZoomDetailsDefined() should be false", map.isZoomDetailsDefined());
        Assert.assertTrue("isBoundingBoxDefined() should be true", map.isBoundingBoxDefined());
        Assert.assertEquals("geMobilesBoundingBox() is wrong", getMockMobilesBoundingBox(),
                map.getMapUrl().getBoundingBox());

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertSame("map has wrong originalMapCentre", getPoint1(),
                map.getOriginalMapCentre());

        Assert.assertEquals("getBoundingBoxEmsJavaScriptZoomInThreshold() is wrong",
                new Integer(EMS_ZOOM_LEVEL - 1),
                map.getBoundingBoxEmsJavaScriptZoomInThreshold());

        assertMapLayers(map, false, false, true);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedPanEast()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_EAST, UserMapInteraction.EAST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedPanWest()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_WEST, UserMapInteraction.WEST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedPanNorth()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_NORTH, UserMapInteraction.NORTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedPanSouth()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_SOUTH, UserMapInteraction.SOUTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedZoomIn()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedZoomInWhenAlreadyAtMinZoom()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedZoomOut()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedZoomInWhenAlreadyAtMaxZoom()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedNoOp()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.NO_OP, UserMapInteraction.NO_ACTION,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedChangeToMapView()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedChangeToPhotoView()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldBeGeneratedChangeToHybridView()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(MapLayer.Map,
                MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulatePoiMapWhenServerSideMapShouldBeGenerated(
            final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction, final int oldZoomLevel,
            final int newZoomLevel) {

        recordShouldGenerateServerSideMap(true);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final List<IconDescriptor> iconDescriptors = createIconDescriptors();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions());

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(getMockExistingMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox());

        EasyMock.expect(getMockExistingMapUrl().getZoom()).andReturn(
                oldZoomLevel);
        EasyMock.expect(getMockExistingMapUrl().getMapCentre()).andReturn(
                getPoint1()).atLeastOnce();

        final PanZoomDetail panZoomDetail =
                new PanZoomDetail(getMockMobilesBoundingBox(), getPoint1(),
                        userMapInteraction, newZoomLevel);

        EasyMock.expect(
                getMockEmsManager().manipulatePoiMap(getMockScreenDimensions(),
                        getPoint2(), iconDescriptors,
                        getNewMapLayerAfterApplyingMapDelegateAction(existingMapLayer,
                                mapDelegateAction),
                        panZoomDetail, getMockUserContext()))
                .andReturn(getMockMapUrl());

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(newZoomLevel)
                .atLeastOnce();

        EasyMock.expect(getMockEmsManager().getEmsZoomLevel(newZoomLevel))
                .andReturn(EMS_ZOOM_LEVEL);

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolvePoiIcons(getPoint2(),
                MobilesIconType.CROSS_HAIR,
                iconDescriptors, getMockScreenDimensions()))
                    .andReturn(resolvedIcons);

        replay();

        final int mobilesZoomThreshold = 4;
        final Map map =
                getObjectUnderTest().manipulatePoiMap(getPoint2(),
                        getMockExistingMapUrl(), existingMapLayer,
                        iconDescriptors, mobilesZoomThreshold,
                        mapDelegateAction,
                        getMockMobileContext());

        Assert.assertTrue("isMapImageRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());
        Assert.assertSame("map has wrong originalMapCentre",
                getPoint2(), map.getOriginalMapCentre());

        assertMapLayers(map, MapLayer.Map.equals(expectedMapLayerAfterAction),
                MapLayer.Photo.equals(expectedMapLayerAfterAction),
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction));

        assertZoomDetails(map, EMS_ZOOM_LEVEL, newZoomLevel == MIN_ZOOM,
                newZoomLevel == MAX_ZOOM);

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedPanEast()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_EAST, UserMapInteraction.EAST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedPanWest()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_WEST, UserMapInteraction.WEST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedPanNorth()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_NORTH, UserMapInteraction.NORTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedPanSouth()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.MOVE_SOUTH, UserMapInteraction.SOUTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedZoomIn()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedZoomInWhenAlreadyAtMinZoom()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedZoomOut()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedZoomInWhenAlreadyAtMaxZoom()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedNoOp()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Map, Action.NO_OP, UserMapInteraction.NO_ACTION,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedChangeToMapView()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedChangeToPhotoView()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapWhenServerSideMapShouldNotBeGeneratedChangeToHybridView()
            throws Throwable {
        doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(MapLayer.Map,
                MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulatePoiMapWhenServerSideMapShouldNotBeGenerated(
            final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction, final int oldZoomLevel,
            final int newZoomLevel) {

        recordShouldGenerateServerSideMap(false);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final List<IconDescriptor> iconDescriptors = createIconDescriptors();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                                getMockScreenDimensions());

        final int mobilesZoomThreshold = 4;
        EasyMock.expect(mockEmsManager.getPoiMapBoundingBox(
                EasyMock.eq(getPoint1()),
                EasyMock.eq(iconDescriptors),
                EasyMock.eq(POI_MAP_RADIUS_MULTIPLIER, 0.01)))
                    .andReturn(getMockMobilesBoundingBox());
        EasyMock.expect(mockEmsManager.getEmsZoomLevel(mobilesZoomThreshold)).andReturn(
                EMS_ZOOM_LEVEL);

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolvePoiIcons(getPoint1(),
                MobilesIconType.CROSS_HAIR,
                iconDescriptors, getMockScreenDimensions()))
                .andReturn(resolvedIcons);

        replay();

        final Map map =
            getObjectUnderTest().manipulatePoiMap(getPoint1(),
                    getMockExistingMapUrl(), existingMapLayer,
                    iconDescriptors, mobilesZoomThreshold,
                    mapDelegateAction,
                    getMockMobileContext());

        Assert.assertFalse("isMapImageRetrieved() should be false", map
                .isMapImageRetrieved());

        Assert.assertNotNull("map should have non-null mapUrl",
                map.getMapUrl());
        Assert.assertEquals("mapUrl has wrong mapCentre",
                getPoint1(),
                map.getMapUrl().getMapCentre());
        Assert.assertEquals("mapUrl has wrong zoom", 0,
                map.getMapUrl().getZoom());

        Assert.assertFalse("isZoomDetailsDefined() should be false", map.isZoomDetailsDefined());
        Assert.assertTrue("isBoundingBoxDefined() should be true", map.isBoundingBoxDefined());
        Assert.assertEquals("geMobilesBoundingBox() is wrong", getMockMobilesBoundingBox(),
                map.getMapUrl().getBoundingBox());

        Assert.assertSame("map has wrong originalMapCentre",
                getPoint1(), map.getOriginalMapCentre());

        assertMapLayers(map, MapLayer.Map.equals(expectedMapLayerAfterAction),
                MapLayer.Photo.equals(expectedMapLayerAfterAction),
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction));

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertEquals("getBoundingBoxEmsJavaScriptZoomInThreshold() is wrong",
                new Integer(EMS_ZOOM_LEVEL - 1),
                map.getBoundingBoxEmsJavaScriptZoomInThreshold());

    }

    @Test
    public void testGetInitialRouteMapWhenServerSideMapShouldBeGenerated()
            throws Exception {

        recordShouldGenerateServerSideMap(true);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions()).atLeastOnce();

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(getMockJourneyDescriptor().getMap())
            .andReturn(getMockMapUrl()).atLeastOnce();

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
            .andReturn(soapRouteHandle).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption())
            .andReturn(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS).atLeastOnce();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;

        final JourneyWaypoints journeyWaypoints
            = createJourneyWaypoints(startLat, startLong, endLat, endLong);
        EasyMock.expect(mockEmsManager.getJourneyDescriptor(
                EasyMock.same(getMockScreenDimensions()), EasyMock.same(journeyWaypoints),
                EasyMock.eq(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS), EasyMock.eq(MapLayer.Photo),
                EasyMock.same(getMockUserContext()))).andReturn(getMockJourneyDescriptor());

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockMapUrl().getImageUrl())
            .andReturn("dummy url").anyTimes();


        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(ZOOM_LEVEL).atLeastOnce();

        EasyMock.expect(mockEmsManager.getEmsZoomLevel(ZOOM_LEVEL)).andReturn(EMS_ZOOM_LEVEL);

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        recordResolveRouteWaypointIcons(resolvedIcons);

        replay();

        final Map map = getObjectUnderTest().getInitialRouteMap(journeyWaypoints,
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, MapLayer.Photo, getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());

        assertGetOriginalMapCentreIllegal(map);

        assertMapLayers(map, false, true, false);

        assertZoomDetails(map, EMS_ZOOM_LEVEL, false, false);

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map.getRouteDetails());

        assertRouteDetails(map, journeyWaypoints, RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                new RouteHandle(soapRouteHandle));
    }

    private void recordResolveRouteWaypointIcons(final ArrayList<ResolvedIcon> resolvedIcons) {
        EasyMock.expect(getMockEmsManager().resolveRouteWaypointIcons(
                getMockJourneyDescriptor(),
                getMockScreenDimensions()))
                    .andReturn(resolvedIcons);
    }

    private void assertRouteDetails(final Map map,
            final JourneyWaypoints expectedSameJourneyWayPoints,
            final RoutingOption expectedRoutingOption, final RouteHandle expectedRouteHandle) {
        Assert.assertSame("RouteDetails waypoints are wrong", expectedSameJourneyWayPoints,
                map.getRouteDetails().getWaypoints());
        Assert.assertEquals("RouteDetails routingOption is wrong",
                expectedRoutingOption, map.getRouteDetails().getRoutingOption());
        Assert.assertEquals("RouteDetails emsRouteHandle has wrong identifier",
                expectedRouteHandle,
                map.getRouteDetails().getEmsRouteHandle());

    }

    @Test
    public void testGetInitialRouteMapWhenServerSideMapShouldNotBeGenerated()
        throws Exception {

        recordShouldGenerateServerSideMap(false);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;

        final JourneyWaypoints journeyWaypoints
            = createJourneyWaypoints(startLat, startLong, endLat, endLong);
        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions()).atLeastOnce();
        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(getMockUserContext());
        EasyMock.expect(getMockEmsManager().getJourneyDescriptorWithoutMapImageUrl(
                getMockScreenDimensions(), journeyWaypoints,
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, MapLayer.Photo,
                getMockUserContext())).andReturn(getMockJourneyDescriptor());

        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption()).andReturn(
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS);
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle()).andReturn(
                null);
        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl());
        EasyMock.expect(getMockMapUrl().getBoundingBox())
            .andReturn(getMockMobilesBoundingBox()).atLeastOnce();

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        recordResolveRouteWaypointIcons(resolvedIcons);

        replay();

        final Map map = getObjectUnderTest().getInitialRouteMap(journeyWaypoints,
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, MapLayer.Photo, getMockMobileContext());

        Assert.assertFalse("isMapImageRetrieved() should be false", map
                .isMapImageRetrieved());
        Assert.assertSame("map is wrong", getMockMapUrl(), map.getMapUrl());
        Assert.assertTrue("isMapImageRetrievalDeferredToClient should be true", map
                .isMapImageRetrievalDeferredToClient());

        assertGetOriginalMapCentreIllegal(map);

        assertMapLayers(map, false, true, false);

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertFalse("isZoomDetailsDefined() should be false", map.isZoomDetailsDefined());
        Assert.assertTrue("isBoundingBoxDefined() should be true", map.isBoundingBoxDefined());
        Assert.assertSame("geMobilesBoundingBox() is wrong", getMockMobilesBoundingBox(),
                map.getMapUrl().getBoundingBox());

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map.getRouteDetails());

        assertRouteDetails(map, journeyWaypoints, RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, null);
    }

    /**
     * @param map
     */
    private void assertGetOriginalMapCentreIllegal(final Map map) {
        try {
            map.getOriginalMapCentre();
            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {
            Assert.assertEquals("IllegalStateException has wrong message",
                    "It is illegal to call getOriginalMapCentre() "
                    + "when isRouteMap() is true", e.getMessage());
        }
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedPanEast()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_EAST,
                UserMapInteraction.EAST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedPanWest()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_WEST,
                UserMapInteraction.WEST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedPanNorth()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_NORTH,
                UserMapInteraction.NORTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedPanSouth()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_SOUTH,
                UserMapInteraction.SOUTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedZoomIn()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedZoomInWhenAlreadyAtMinZoom()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedZoomOut()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedZoomInWhenAlreadyAtMaxZoom()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedNoOp()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.NO_OP,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedChangeToMapView()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedChangeToPhotoView()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldBeGeneratedChangeToHybridView()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulateRouteMapWhenServerSideMapShouldBeGenerated(
            final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction,
            final int oldZoomLevel, final int newZoomLevel) {

        recordShouldGenerateServerSideMap(true);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;
        final JourneyWaypoints journeyWaypoints =
                createJourneyWaypoints(startLat, startLong, endLat, endLong);

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                                getMockScreenDimensions()).atLeastOnce();

        EasyMock.expect(getMockExistingMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox());
        EasyMock.expect(getMockExistingMapUrl().getZoom()).andReturn(
                oldZoomLevel);
        EasyMock.expect(getMockExistingMapUrl().getMapCentre()).andReturn(
                getPoint1()).atLeastOnce();
        final PanZoomDetail panZoomDetail =
            new PanZoomDetail(getMockMobilesBoundingBox(), getPoint1(),
                    userMapInteraction, newZoomLevel);

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(
                mockEmsManager.updateJourneyDescriptorMapFromRouteHandle(
                        createRouteHandle(), journeyWaypoints,
                        RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                        getNewMapLayerAfterApplyingMapDelegateAction(existingMapLayer,
                                mapDelegateAction),
                        getMockScreenDimensions(), panZoomDetail,
                        getMockUserContext()))
                .andReturn(getMockJourneyDescriptor());


        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl()).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption()).andReturn(
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS).atLeastOnce();

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockMapUrl().getImageUrl()).andReturn("dummy url")
                .anyTimes();

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(newZoomLevel)
                .atLeastOnce();

        EasyMock.expect(mockEmsManager.getEmsZoomLevel(newZoomLevel)).andReturn(
                EMS_ZOOM_LEVEL);

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
            .andReturn(soapRouteHandle).atLeastOnce();

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        recordResolveRouteWaypointIcons(resolvedIcons);

        replay();

        final Map map =
                getObjectUnderTest().manipulateRouteMap(createRouteHandle(),
                        journeyWaypoints,
                        RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                        getMockExistingMapUrl(), existingMapLayer, mapDelegateAction,
                        getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(), map
                .getMapUrl());

        assertGetOriginalMapCentreIllegal(map);

        assertMapLayers(map, MapLayer.Map.equals(expectedMapLayerAfterAction),
                MapLayer.Photo.equals(expectedMapLayerAfterAction),
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction));

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        assertZoomDetails(map, EMS_ZOOM_LEVEL, newZoomLevel == MIN_ZOOM,
                newZoomLevel == MAX_ZOOM);

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map
                .getRouteDetails());

        assertRouteDetails(map, journeyWaypoints, RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                new RouteHandle(soapRouteHandle));
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedPanEast()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_EAST,
                UserMapInteraction.EAST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedPanWest()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_WEST,
                UserMapInteraction.WEST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedPanNorth()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_NORTH,
                UserMapInteraction.NORTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedPanSouth()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_SOUTH,
                UserMapInteraction.SOUTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedZoomIn()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedZoomInWhenAlreadyAtMinZoom()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedZoomOut()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedZoomInWhenAlreadyAtMaxZoom()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedNoOp()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.NO_OP,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedChangeToMapView()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedChangeToPhotoView()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapWhenServerSideMapShouldNotBeGeneratedChangeToHybridView()
            throws Throwable {
        doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulateRouteMapWhenServerSideMapShouldNotBeGenerated(
            final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction,
            final int oldZoomLevel, final int newZoomLevel) {

        recordShouldGenerateServerSideMap(false);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;
        final JourneyWaypoints journeyWaypoints =
            createJourneyWaypoints(startLat, startLong, endLat, endLong);

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                                getMockScreenDimensions()).atLeastOnce();

        EasyMock.expect(getMockExistingMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox());
        EasyMock.expect(getMockExistingMapUrl().getZoom()).andReturn(
                oldZoomLevel);
        EasyMock.expect(getMockExistingMapUrl().getMapCentre()).andReturn(
                getPoint1()).atLeastOnce();
        final PanZoomDetail panZoomDetail =
            new PanZoomDetail(getMockMobilesBoundingBox(), getPoint1(),
                    userMapInteraction, newZoomLevel);

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(
                mockEmsManager.updateJourneyDescriptorMapFromRouteHandleWithoutMapImageUrl(
                        createRouteHandle(), journeyWaypoints,
                        RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                        getNewMapLayerAfterApplyingMapDelegateAction(existingMapLayer,
                                mapDelegateAction),
                                getMockScreenDimensions(), panZoomDetail,
                                getMockUserContext()))
                                .andReturn(getMockJourneyDescriptor());


        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl()).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption()).andReturn(
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS).atLeastOnce();

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockMapUrl().getImageUrl()).andReturn("dummy url")
            .anyTimes();

        EasyMock.expect(getMockMapUrl().getBoundingBox()).andReturn(
                getMockUpdatedMobilesBoundingBox()).atLeastOnce();

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
        .andReturn(soapRouteHandle).atLeastOnce();

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        recordResolveRouteWaypointIcons(resolvedIcons);

        replay();

        final Map map =
            getObjectUnderTest().manipulateRouteMap(createRouteHandle(),
                    journeyWaypoints,
                    RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                    getMockExistingMapUrl(), existingMapLayer, mapDelegateAction,
                    getMockMobileContext());

        Assert.assertFalse("isMapRetrieved() should be false", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(), map
                .getMapUrl());

        assertGetOriginalMapCentreIllegal(map);

        assertMapLayers(map, MapLayer.Map.equals(expectedMapLayerAfterAction),
                MapLayer.Photo.equals(expectedMapLayerAfterAction),
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction));

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertFalse("isZoomDetailsDefined() should be false", map
                .isZoomDetailsDefined());
        Assert.assertTrue("isBoundingBoxDefined() should be true",
                map.isBoundingBoxDefined());
        Assert.assertSame("geMobilesBoundingBox() is wrong",
                getMockUpdatedMobilesBoundingBox(),
                map.getMapUrl().getBoundingBox());

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map
                .getRouteDetails());

        assertRouteDetails(map, journeyWaypoints, RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                new RouteHandle(soapRouteHandle));
    }

    @Test
    public void testGetInitialRouteLegStepMapWhenServerSideMapShouldBeGenerated()
            throws Exception {

        EasyMock.expect(mockEmsManager.getEmsZoomLevel(ZOOM_LEVEL)).andReturn(
                EMS_ZOOM_LEVEL);

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;

        final JourneyWaypoints journeyWaypoints =
                createJourneyWaypoints(startLat, startLong, endLat, endLong);

        recordShouldGenerateServerSideMap(true);

        final PanZoomDetail panZoomDetail =
                new PanZoomDetail(null, getPoint1(),
                        UserMapInteraction.NO_ACTION, ZOOM_LEVEL);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPhone")
                .anyTimes();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions()).atLeastOnce();

        final RouteHandle routeHandle = createRouteHandle();
        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());
        EasyMock.expect(
                mockEmsManager.updateJourneyDescriptorMapFromRouteHandle(
                        EasyMock.eq(routeHandle), EasyMock
                                .same(journeyWaypoints), EasyMock
                                .eq(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS),
                        EasyMock.eq(MapLayer.Map), EasyMock
                                .same(getMockScreenDimensions()), EasyMock
                                .eq(panZoomDetail), EasyMock
                                .same(getMockUserContext()))).andReturn(
                getMockJourneyDescriptor());

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        recordResolveRouteWaypointIcons(resolvedIcons);

        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl()).atLeastOnce();

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
                .andReturn(soapRouteHandle).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption())
                .andReturn(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS)
                .atLeastOnce();

        replay();

        final Map map =
                getObjectUnderTest().getInitialRouteLegStepMap(routeHandle, journeyWaypoints,
                        RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, getPoint1(), ZOOM_LEVEL,
                        MapLayer.Map, getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(), map
                .getMapUrl());
        assertGetOriginalMapCentreIllegal(map);

        assertMapLayers(map, true, false, false);

        assertZoomDetails(map, EMS_ZOOM_LEVEL, false, false);

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map
                .getRouteDetails());

        assertRouteDetails(map, journeyWaypoints,
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, new RouteHandle(
                        soapRouteHandle));
    }

    @Test
    public void testGetInitialRouteLegStepMapWhenServerSideMapShouldNotBeGenerated()
            throws Exception {

        EasyMock.expect(mockEmsManager.getEmsZoomLevel(ZOOM_LEVEL)).andReturn(
                EMS_ZOOM_LEVEL);

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;

        final JourneyWaypoints journeyWaypoints =
                createJourneyWaypoints(startLat, startLong, endLat, endLong);

        recordShouldGenerateServerSideMap(false);

        final PanZoomDetail panZoomDetail =
                new PanZoomDetail(null, getPoint1(),
                        UserMapInteraction.NO_ACTION, ZOOM_LEVEL);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName()).andReturn("Apple-iPhone")
                .anyTimes();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions()).atLeastOnce();

        final RouteHandle routeHandle = createRouteHandle();
        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());
        EasyMock
                .expect(
                        mockEmsManager
                                .updateJourneyDescriptorMapFromRouteHandleWithoutMapImageUrl(
                                        EasyMock.eq(routeHandle),
                                        EasyMock.same(journeyWaypoints),
                                        EasyMock
                                                .eq(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS),
                                        EasyMock.eq(MapLayer.Map),
                                        EasyMock
                                                .same(getMockScreenDimensions()),
                                        EasyMock.eq(panZoomDetail), EasyMock
                                                .same(getMockUserContext())))
                .andReturn(getMockJourneyDescriptor());

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        recordResolveRouteWaypointIcons(resolvedIcons);

        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl()).atLeastOnce();

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
                .andReturn(soapRouteHandle).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption())
                .andReturn(RoutingOption.FASTEST_BY_ROAD_NO_TOLLS)
                .atLeastOnce();

        replay();

        final Map map =
                getObjectUnderTest().getInitialRouteLegStepMap(routeHandle,
                        journeyWaypoints,
                        RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, getPoint1(),
                        ZOOM_LEVEL, MapLayer.Map, getMockMobileContext());

        Assert.assertFalse("isMapRetrieved() should be false", map
                .isMapImageRetrieved());
        Assert.assertTrue("isMapImageRetrievalDeferredToClient() should be true",
                map.isMapImageRetrievalDeferredToClient());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(), map
                .getMapUrl());
        assertGetOriginalMapCentreIllegal(map);

        assertMapLayers(map, true, false, false);

        assertZoomDetails(map, EMS_ZOOM_LEVEL, false, false);

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map
                .getRouteDetails());

        assertRouteDetails(map, journeyWaypoints,
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, new RouteHandle(
                        soapRouteHandle));
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedPanEast()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_EAST,
                UserMapInteraction.EAST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedPanWest()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_WEST,
                UserMapInteraction.WEST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedPanNorth()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_NORTH,
                UserMapInteraction.NORTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedPanSouth()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_SOUTH,
                UserMapInteraction.SOUTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedZoomIn()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedZoomInWhenAlreadyAtMinZoom()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedZoomOut()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedZoomInWhenAlreadyAtMaxZoom()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedNoOp()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.NO_OP,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedChangeToMapView()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedChangeToPhotoView()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldBeGeneratedChangeToHybridView()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
                MapLayer.Map, MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulateRouteLegStepMapWhenServerSideMapShouldBeGenerated(
            final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction,
            final int oldZoomLevel, final int newZoomLevel) {

        recordShouldGenerateServerSideMap(true);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;
        final JourneyWaypoints journeyWaypoints =
                createJourneyWaypoints(startLat, startLong, endLat, endLong);

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                                getMockScreenDimensions()).atLeastOnce();

        EasyMock.expect(getMockExistingMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox());
        EasyMock.expect(getMockExistingMapUrl().getZoom()).andReturn(
                oldZoomLevel);
        EasyMock.expect(getMockExistingMapUrl().getMapCentre()).andReturn(
                getPoint1()).atLeastOnce();
        final PanZoomDetail panZoomDetail =
            new PanZoomDetail(getMockMobilesBoundingBox(), getPoint1(),
                    userMapInteraction, newZoomLevel);

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(
                mockEmsManager.updateJourneyDescriptorMapFromRouteHandle(
                        createRouteHandle(), journeyWaypoints,
                        RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                        getNewMapLayerAfterApplyingMapDelegateAction(existingMapLayer,
                                mapDelegateAction),
                        getMockScreenDimensions(), panZoomDetail,
                        getMockUserContext()))
                .andReturn(getMockJourneyDescriptor());


        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl()).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption()).andReturn(
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS).atLeastOnce();

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockMapUrl().getImageUrl()).andReturn("dummy url")
                .anyTimes();

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(newZoomLevel)
                .atLeastOnce();

        EasyMock.expect(mockEmsManager.getEmsZoomLevel(newZoomLevel)).andReturn(
                EMS_ZOOM_LEVEL);

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
            .andReturn(soapRouteHandle).atLeastOnce();

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        recordResolveRouteWaypointIcons(resolvedIcons);

        replay();

        final Map map =
                getObjectUnderTest().manipulateRouteLegStepMap(createRouteHandle(),
                        journeyWaypoints,
                        RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                        getMockExistingMapUrl(), existingMapLayer, mapDelegateAction,
                        getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(), map
                .getMapUrl());

        assertGetOriginalMapCentreIllegal(map);

        assertMapLayers(map, MapLayer.Map.equals(expectedMapLayerAfterAction),
                MapLayer.Photo.equals(expectedMapLayerAfterAction),
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction));

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        assertZoomDetails(map, EMS_ZOOM_LEVEL, newZoomLevel == MIN_ZOOM,
                newZoomLevel == MAX_ZOOM);

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map
                .getRouteDetails());

        assertRouteDetails(map, journeyWaypoints, RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                new RouteHandle(soapRouteHandle));
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedPanEast()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_EAST,
                UserMapInteraction.EAST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedPanWest()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_WEST,
                UserMapInteraction.WEST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedPanNorth()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_NORTH,
                UserMapInteraction.NORTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedPanSouth()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.MOVE_SOUTH,
                UserMapInteraction.SOUTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedZoomIn()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedZoomInWhenAlreadyAtMinZoom()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM, MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedZoomOut()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedZoomInWhenAlreadyAtMaxZoom()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM, MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedNoOp()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Map, Action.NO_OP,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedChangeToMapView()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedChangeToPhotoView()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGeneratedChangeToHybridView()
            throws Throwable {
        doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
                MapLayer.Map, MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulateRouteLegStepMapWhenServerSideMapShouldNotBeGenerated(
            final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction,
            final int oldZoomLevel, final int newZoomLevel) {

        recordShouldGenerateServerSideMap(false);

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;
        final JourneyWaypoints journeyWaypoints =
            createJourneyWaypoints(startLat, startLong, endLat, endLong);

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                                getMockScreenDimensions()).atLeastOnce();

        EasyMock.expect(getMockExistingMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox());
        EasyMock.expect(getMockExistingMapUrl().getZoom()).andReturn(
                oldZoomLevel);
        EasyMock.expect(getMockExistingMapUrl().getMapCentre()).andReturn(
                getPoint1()).atLeastOnce();
        final PanZoomDetail panZoomDetail =
            new PanZoomDetail(getMockMobilesBoundingBox(), getPoint1(),
                    userMapInteraction, newZoomLevel);

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(
                mockEmsManager.updateJourneyDescriptorMapFromRouteHandleWithoutMapImageUrl(
                        createRouteHandle(), journeyWaypoints,
                        RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                        getNewMapLayerAfterApplyingMapDelegateAction(existingMapLayer,
                                mapDelegateAction),
                                getMockScreenDimensions(), panZoomDetail,
                                getMockUserContext()))
                                .andReturn(getMockJourneyDescriptor());


        EasyMock.expect(getMockJourneyDescriptor().getMap()).andReturn(
                getMockMapUrl()).atLeastOnce();
        EasyMock.expect(getMockJourneyDescriptor().getRoutingOption()).andReturn(
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS).atLeastOnce();

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockMapUrl().getImageUrl()).andReturn("dummy url")
            .anyTimes();

        EasyMock.expect(getMockMapUrl().getBoundingBox()).andReturn(
                getMockUpdatedMobilesBoundingBox()).atLeastOnce();

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
            .andReturn(soapRouteHandle).atLeastOnce();

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        recordResolveRouteWaypointIcons(resolvedIcons);

        replay();

        final Map map =
            getObjectUnderTest().manipulateRouteLegStepMap(createRouteHandle(),
                    journeyWaypoints,
                    RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                    getMockExistingMapUrl(), existingMapLayer, mapDelegateAction,
                    getMockMobileContext());

        Assert.assertFalse("isMapRetrieved() should be false", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(), map
                .getMapUrl());

        assertGetOriginalMapCentreIllegal(map);

        assertMapLayers(map, MapLayer.Map.equals(expectedMapLayerAfterAction),
                MapLayer.Photo.equals(expectedMapLayerAfterAction),
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction));

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertFalse("isZoomDetailsDefined() should be false", map
                .isZoomDetailsDefined());
        Assert.assertTrue("isBoundingBoxDefined() should be true",
                map.isBoundingBoxDefined());
        Assert.assertSame("geMobilesBoundingBox() is wrong",
                getMockUpdatedMobilesBoundingBox(),
                map.getMapUrl().getBoundingBox());

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map
                .getRouteDetails());

        assertRouteDetails(map, journeyWaypoints, RoutingOption.FASTEST_BY_ROAD_NO_TOLLS,
                new RouteHandle(soapRouteHandle));
    }


    private void assertMapLayers(final Map map, final boolean isMapLayer,
            final boolean isPhotoLayer, final boolean isPhotoWithStreetsLayer) {

        Assert.assertEquals("isMapLayer is wrong", isMapLayer, map
                .isMapLayer());
        Assert.assertEquals("isPhotoLayer is wrong", isPhotoLayer, map
                .isPhotoLayer());
        Assert.assertEquals("isPhotoWithStreetsLayer is wrong",
                isPhotoWithStreetsLayer, map.isPhotoWithStreetsLayer());

    }

    /**
     * @return
     */
    private SoapRouteHandle createSoapRouteHandle() {
        final SoapRouteHandle soapRouteHandle = new SoapRouteHandle();
        soapRouteHandle.setIdentifier("myRoute");
        return soapRouteHandle;
    }

    /**
     * @return
     */
    private RouteHandle createRouteHandle() {
        return new RouteHandle("myRoute");
    }

    private JourneyWaypoints createJourneyWaypoints(final double startLat, final double startLong,
            final double endLat, final double endLong) {
        final WGS84Point startWGS84Pt = new WGS84Point(startLong, startLat);
        final WGS84Point endWGS84Pt = new WGS84Point(endLong, endLat);
        final JourneyWaypoints journeyWayPoints = new JourneyWaypoints(startWGS84Pt, endWGS84Pt);
        return journeyWayPoints;
    }

    /**
     * @return
     */
    private List<IconDescriptor> createIconDescriptors() {
        final List<IconDescriptor> iconDescriptors = new ArrayList<IconDescriptor>();
        final WGS84Point firstPoiLocation = new WGS84Point(10, 10);
        final WGS84Point secondPoiLocation = new WGS84Point(20, 20);
        final IconDescriptor firstIconDescriptor = createIconDescriptorFor(firstPoiLocation);
        final IconDescriptor secondIconDescriptor = createIconDescriptorFor(secondPoiLocation);
        iconDescriptors.add(firstIconDescriptor);
        iconDescriptors.add(secondIconDescriptor);
        return iconDescriptors;
    }

    private IconDescriptor createIconDescriptorFor(final WGS84Point firstPoiLocation) {

        final IconDescriptor firstIconDescriptor = new IconDescriptor();

        firstIconDescriptor.setIconType(IconType.FREE);
        firstIconDescriptor.setPoint(firstPoiLocation);

        return firstIconDescriptor;
    }


    /**
     * @return the objectUnderTest
     */
    public MapDelegateImpl getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(final MapDelegateImpl objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }

    /**
     * @return the mockMobileContext
     */
    public MobileContext getMockMobileContext() {
        return mockMobileContext;
    }

    /**
     * @param mockMobileContext the mockMobileContext to set
     */
    public void setMockMobileContext(final MobileContext mockMobileContext) {
        this.mockMobileContext = mockMobileContext;
    }

    /**
     * @return the mockEmsManager
     */
    public EMSManager getMockEmsManager() {
        return mockEmsManager;
    }

    /**
     * @param mockEmsManager the mockEmsManager to set
     */
    public void setMockEmsManager(final EMSManager mockEmsManager) {
        this.mockEmsManager = mockEmsManager;
    }

    /**
     * @return the mockUserContext
     */
    public UserContext getMockUserContext() {
        return mockUserContext;
    }

    /**
     * @param mockUserContext the mockUserContext to set
     */
    public void setMockUserContext(final UserContext mockUserContext) {
        this.mockUserContext = mockUserContext;
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
     * @return the mockScreenDimensionsStrategy
     */
    public ScreenDimensionsStrategy getMockScreenDimensionsStrategy() {
        return mockScreenDimensionsStrategy;
    }

    /**
     * @param mockScreenDimensionsStrategy the mockScreenDimensionsStrategy to set
     */
    public void setMockScreenDimensionsStrategy(
            final ScreenDimensionsStrategy mockScreenDimensionsStrategy) {
        this.mockScreenDimensionsStrategy = mockScreenDimensionsStrategy;
    }

    /**
     * @return the mockScreenDimensions
     */
    public ScreenDimensions getMockScreenDimensions() {
        return mockScreenDimensions;
    }

    /**
     * @param mockScreenDimensions the mockScreenDimensions to set
     */
    public void setMockScreenDimensions(final ScreenDimensions mockScreenDimensions) {
        this.mockScreenDimensions = mockScreenDimensions;
    }

    /**
     * @param point1 the point1 to set
     */
    private void setPoint1(final WGS84Point point1) {
        this.point1 = point1;
    }

    /**
     * @return the point1
     */
    private WGS84Point getPoint1() {
        return point1;
    }

    /**
     * @param wgs84PointTestDataFactory
     *            the wgs84PointTestDataFactory to set
     */
    private void setWgs84PointTestDataFactory(
            final WGS84PointTestDataFactory wgs84PointTestDataFactory) {
        this.wgs84PointTestDataFactory = wgs84PointTestDataFactory;
    }

    /**
     * @return the wgs84PointTestDataFactory
     */
    private WGS84PointTestDataFactory getWgs84PointTestDataFactory() {
        return wgs84PointTestDataFactory;
    }

    /**
     * @return the mockExistingMapUrl
     */
    public MapUrl getMockExistingMapUrl() {
        return mockExistingMapUrl;
    }

    /**
     * @param mockExistingMapUrl the mockExistingMapUrl to set
     */
    public void setMockExistingMapUrl(final MapUrl mockExistingMapUrl) {
        this.mockExistingMapUrl = mockExistingMapUrl;
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

    /**
     * @return the point2
     */
    public WGS84Point getPoint2() {
        return point2;
    }

    /**
     * @param point2 the point2 to set
     */
    public void setPoint2(final WGS84Point point2) {
        this.point2 = point2;
    }

    /**
     * @return the point3
     */
    public WGS84Point getPoint3() {
        return point3;
    }

    /**
     * @param point3 the point3 to set
     */
    public void setPoint3(final WGS84Point point3) {
        this.point3 = point3;
    }

    /**
     * @return the mockDeviceConfigRegistry
     */
    public DeviceConfigRegistry getMockDeviceConfigRegistry() {
        return mockDeviceConfigRegistry;
    }

    /**
     * @param mockDeviceConfigRegistry the mockDeviceConfigRegistry to set
     */
    public void setMockDeviceConfigRegistry(
            final DeviceConfigRegistry mockDeviceConfigRegistry) {
        this.mockDeviceConfigRegistry = mockDeviceConfigRegistry;
    }

    /**
     * @return the mockDevice
     */
    public Device getMockDevice() {
        return mockDevice;
    }

    /**
     * @param mockDevice the mockDevice to set
     */
    public void setMockDevice(final Device mockDevice) {
        this.mockDevice = mockDevice;
    }

    /**
     * @return the deviceConfig
     */
    public DeviceConfig getDeviceConfig() {
        return deviceConfig;
    }

    /**
     * @param deviceConfig the deviceConfig to set
     */
    public void setDeviceConfig(final DeviceConfig deviceConfig) {
        this.deviceConfig = deviceConfig;
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
     * @return the mockUpdatedMobilesBoundingBox
     */
    public MobilesBoundingBox getMockUpdatedMobilesBoundingBox() {
        return mockUpdatedMobilesBoundingBox;
    }

    /**
     * @param mockUpdatedMobilesBoundingBox the mockUpdatedMobilesBoundingBox to set
     */
    public void setMockUpdatedMobilesBoundingBox(
            final MobilesBoundingBox mockUpdatedMobilesBoundingBox) {
        this.mockUpdatedMobilesBoundingBox = mockUpdatedMobilesBoundingBox;
    }

    /**
     * @return the point4
     */
    private WGS84Point getPoint4() {
        return point4;
    }

    /**
     * @param point4 the point4 to set
     */
    private void setPoint4(final WGS84Point point4) {
        this.point4 = point4;
    }

    /**
     * @return the point5
     */
    private WGS84Point getPoint5() {
        return point5;
    }

    /**
     * @param point5 the point5 to set
     */
    private void setPoint5(final WGS84Point point5) {
        this.point5 = point5;
    }
}

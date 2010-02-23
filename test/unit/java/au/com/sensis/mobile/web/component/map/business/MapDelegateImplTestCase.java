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
import au.com.sensis.mobile.web.component.map.device.generated.DeviceConfigType;
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
    private DeviceConfigRegistry<DeviceConfigType> mockDeviceConfigRegistry;
    private Device mockDevice;
    private DeviceConfigType deviceConfigType;
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

        setDeviceConfigType(new DeviceConfigType());

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

        getDeviceConfigType().setGenerateServerSideMap(true);

        EasyMock.expect(getMockMobileContext().getDevice())
            .andReturn(getMockDevice()).atLeastOnce();
        EasyMock.expect(getMockDeviceConfigRegistry().getDeviceConfig(getMockDevice()))
            .andReturn(getDeviceConfigType());

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
        EasyMock.expect(getMockEmsManager().resolveIcons(getPoint1(),
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
        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertFalse("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertTrue("isPhotoWithStreetsLayer is wrong",
                map.isPhotoWithStreetsLayer());
        Assert.assertFalse("isAtMinimumZoom() should be false",
                map.isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom() should be false",
                map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());
    }

    @Test
    public void testGetInitialMapWhenServerSideMapShouldNotBeGenerated() throws Throwable {

        getDeviceConfigType().setGenerateServerSideMap(false);

        EasyMock.expect(getMockMobileContext().getDevice())
            .andReturn(getMockDevice()).atLeastOnce();
        EasyMock.expect(getMockDeviceConfigRegistry().getDeviceConfig(getMockDevice()))
            .andReturn(getDeviceConfigType());

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
        EasyMock.expect(getMockEmsManager().resolveIcons(getPoint1(),
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
        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertFalse("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertTrue("isPhotoWithStreetsLayer is wrong",
                map.isPhotoWithStreetsLayer());
        Assert.assertFalse("isAtMinimumZoom() should be false",
                map.isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom() should be false",
                map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());

    }

    @Test
    public void testManipulateMapPanEast() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.MOVE_EAST, UserMapInteraction.EAST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapPanWest() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.MOVE_WEST, UserMapInteraction.WEST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapPanNorth() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.MOVE_NORTH, UserMapInteraction.NORTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapPanSouth() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.MOVE_SOUTH, UserMapInteraction.SOUTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapZoomIn() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateMapZoomInWhenAlreadyAtMinZoom() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateMapZoomOut() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateMapZoomInWhenAlreadyAtMaxZoom() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateMapNoOp() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Map, Action.NO_OP,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapChangeToMapView() throws Throwable {
        doTestManipulateMap(MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapChangeToPhotoView() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapChangeToHybridView() throws Throwable {
        doTestManipulateMap(MapLayer.Map, MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulateMap(final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction, final int oldZoomLevel,
            final int newZoomLevel) {
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
        EasyMock.expect(getMockEmsManager().resolveIcons(getPoint2(),
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

        Assert.assertEquals("isMapLayer is wrong",
                MapLayer.Map.equals(expectedMapLayerAfterAction), map.isMapLayer());
        Assert.assertEquals("isPhotoLayer is wrong",
                MapLayer.Photo.equals(expectedMapLayerAfterAction), map.isPhotoLayer());
        Assert.assertEquals("isPhotoWithStreetsLayer is wrong",
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction),
                map.isPhotoWithStreetsLayer());

        Assert.assertEquals("isAtMinimumZoom() is wrong",
                newZoomLevel == MIN_ZOOM, map.isAtMinimumZoom());
        Assert.assertEquals("isAtMaximumZoom() is wrong",
                newZoomLevel == MAX_ZOOM, map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());

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

        getDeviceConfigType().setGenerateServerSideMap(true);

        EasyMock.expect(getMockMobileContext().getDevice())
            .andReturn(getMockDevice()).atLeastOnce();
        EasyMock.expect(getMockDeviceConfigRegistry().getDeviceConfig(getMockDevice()))
            .andReturn(getDeviceConfigType());

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
        EasyMock.expect(getMockEmsManager().resolveIcons(getPoint1(),
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

        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong",
                map.isPhotoWithStreetsLayer());

        Assert.assertFalse("isAtMinimumZoom() is wrong", map.isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom() is wrong", map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

    }

    @Test
    public void testGetInitialPoiMapWhenServerSideMapShouldNotBeGenerated()
        throws Exception {

        getDeviceConfigType().setGenerateServerSideMap(false);

        EasyMock.expect(getMockMobileContext().getDevice())
            .andReturn(getMockDevice()).atLeastOnce();
        EasyMock.expect(getMockDeviceConfigRegistry().getDeviceConfig(getMockDevice()))
            .andReturn(getDeviceConfigType());

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        final List<IconDescriptor> iconDescriptors = createIconDescriptors();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                                getMockScreenDimensions()).atLeastOnce();

        final int mobilesZoomThreshold = 4;
        EasyMock.expect(mockEmsManager.getPoiMapZoom(
                EasyMock.same(getMockScreenDimensions()), EasyMock.eq(getPoint1()),
                EasyMock.eq(iconDescriptors),
                EasyMock.eq(POI_MAP_RADIUS_MULTIPLIER, 0.01),
                EasyMock.eq(mobilesZoomThreshold))).andReturn(ZOOM_LEVEL);
        EasyMock.expect(mockEmsManager.getEmsZoomLevel(ZOOM_LEVEL)).andReturn(EMS_ZOOM_LEVEL);

        final ArrayList<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        EasyMock.expect(getMockEmsManager().resolveIcons(getPoint1(),
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
        Assert.assertEquals("mapUrl has wrong zoom", ZOOM_LEVEL,
                map.getMapUrl().getZoom());

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());

        Assert.assertSame("map has wrong originalMapCentre", getPoint1(),
                map.getOriginalMapCentre());
        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertFalse("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertTrue("isPhotoWithStreetsLayer is wrong",
                map.isPhotoWithStreetsLayer());
        Assert.assertFalse("isAtMinimumZoom() should be false",
                map.isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom() should be false",
                map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());

    }

    @Test
    public void testManipulatePoiMapPanEast() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.MOVE_EAST,
                UserMapInteraction.EAST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapPanWest() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.MOVE_WEST,
                UserMapInteraction.WEST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapPanNorth() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.MOVE_NORTH,
                UserMapInteraction.NORTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapPanSouth() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.MOVE_SOUTH,
                UserMapInteraction.SOUTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapZoomIn() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulatePoiMapZoomInWhenAlreadyAtMinZoom() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_IN, UserMapInteraction.ZOOM,
                MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulatePoiMapZoomOut() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulatePoiMapZoomInWhenAlreadyAtMaxZoom() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulatePoiMapNoOp() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Map, Action.NO_OP,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapChangeToMapView() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapChangeToPhotoView() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapChangeToHybridView() throws Throwable {
        doTestManipulatePoiMap(MapLayer.Map, MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }



    private void doTestManipulatePoiMap(final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction, final int oldZoomLevel,
            final int newZoomLevel) {

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
        EasyMock.expect(getMockEmsManager().resolveIcons(getPoint2(),
                iconDescriptors, getMockScreenDimensions()))
                .andReturn(resolvedIcons);

        replay();

        final Map map =
                getObjectUnderTest().manipulatePoiMap(getPoint2(),
                        getMockExistingMapUrl(), existingMapLayer,
                        iconDescriptors,
                        mapDelegateAction,
                        getMockMobileContext());

        Assert.assertTrue("isMapImageRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());
        Assert.assertSame("map has wrong originalMapCentre",
                getPoint2(), map.getOriginalMapCentre());

        Assert.assertEquals("isMapLayer is wrong",
                MapLayer.Map.equals(expectedMapLayerAfterAction), map.isMapLayer());
        Assert.assertEquals("isPhotoLayer is wrong",
                MapLayer.Photo.equals(expectedMapLayerAfterAction), map.isPhotoLayer());
        Assert.assertEquals("isPhotoWithStreetsLayer is wrong",
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction),
                map.isPhotoWithStreetsLayer());

        Assert.assertEquals("isAtMinimumZoom() is wrong",
                newZoomLevel == MIN_ZOOM, map.isAtMinimumZoom());
        Assert.assertEquals("isAtMaximumZoom() is wrong",
                newZoomLevel == MAX_ZOOM, map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL, map
                .getEmsZoom());

        Assert.assertSame("resolvedIcons are wrong", resolvedIcons,
                map.getResolvedIcons());
    }

    @Test
    public void testGetInitialRouteMapWhenServerSideMapShouldBeGenerated()
            throws Exception {

        getDeviceConfigType().setGenerateServerSideMap(true);

        EasyMock.expect(getMockMobileContext().getDevice())
            .andReturn(getMockDevice()).atLeastOnce();
        EasyMock.expect(getMockDeviceConfigRegistry().getDeviceConfig(getMockDevice()))
            .andReturn(getDeviceConfigType());

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockDevice().getName())
            .andReturn("Apple-iPhone").anyTimes();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions());

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(getMockJourneyDescriptor().getMap())
            .andReturn(getMockMapUrl()).atLeastOnce();

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
            .andReturn(soapRouteHandle);

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

        replay();

        final Map map = getObjectUnderTest().getInitialRouteMap(journeyWaypoints,
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, MapLayer.Photo, getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());

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

        Assert.assertFalse("isAtMinimumZoom() is wrong", map.isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom() is wrong", map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map.getRouteDetails());

        Assert.assertSame("RouteDetails waypoints are wrong", journeyWaypoints,
                map.getRouteDetails().getWaypoints());
        Assert.assertEquals("RouteDetails routingOption is wrong",
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, map.getRouteDetails().getRoutingOption());
        Assert.assertNotNull("RouteDetails emsRouteHandle should not be null",
                map.getRouteDetails().getEmsRouteHandle());
        Assert.assertEquals("RouteDetails emsRouteHandle has wrong identifier",
                soapRouteHandle.getIdentifier(),
                map.getRouteDetails().getEmsRouteHandle().getIdentifier());

    }

// TODO
//    @Test
//    public void testGetInitialRouteMapWhenServerSideMapShouldNotBeGenerated()
//        throws Exception {
//
//        getDeviceConfigType().setGenerateServerSideMap(true);
//
//        EasyMock.expect(getMockMobileContext().getDevice())
//            .andReturn(getMockDevice()).atLeastOnce();
//        EasyMock.expect(getMockDeviceConfigRegistry().getDeviceConfig(getMockDevice()))
//            .andReturn(getDeviceConfigType());
//
//        // Expectation to cover off debug logging.
//        EasyMock.expect(getMockDevice().getName())
//            .andReturn("Apple-iPhone").anyTimes();
//
//
//        final double startLat = -45.45;
//        final double startLong = 145.145;
//        final double endLat = -45.46;
//        final double endLong = 146.146;
//
//        final JourneyWaypoints journeyWaypoints
//            = createJourneyWaypoints(startLat, startLong, endLat, endLong);
//
//        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(ZOOM_LEVEL).atLeastOnce();
//
//        EasyMock.expect(mockEmsManager.getEmsZoomLevel(ZOOM_LEVEL)).andReturn(EMS_ZOOM_LEVEL);
//
//        replay();
//
//        final Map map = getObjectUnderTest().getInitialRouteMap(journeyWaypoints,
//                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, MapLayer.Photo, getMockMobileContext());
//
//        Assert.assertFalse("isMapRetrieved() should be false", map
//                .isMapImageRetrieved());
//        Assert.assertNotNull("map should have non-null mapUrl",
//                map.getMapUrl());
//
//        try {
//            map.getOriginalMapCentre();
//            Assert.fail("IllegalStateException expected");
//        } catch (final IllegalStateException e) {
//            Assert.assertEquals("IllegalStateException has wrong message",
//                    "It is illegal to call getOriginalMapCentre() "
//                    + "when isRouteMap() is true", e.getMessage());
//        }
//
//        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
//        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
//        Assert.assertFalse("isPhotoWithStreetsLayer is wrong",
//                map.isPhotoWithStreetsLayer());
//
//        Assert.assertFalse("isAtMinimumZoom() is wrong", map.isAtMinimumZoom());
//        Assert.assertFalse("isAtMaximumZoom() is wrong", map.isAtMaximumZoom());
//
//        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL,
//                map.getEmsZoom());
//
//        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
//        Assert.assertNotNull("getRouteDetails() should not be null", map.getRouteDetails());
//
//        Assert.assertSame("RouteDetails waypoints are wrong", journeyWaypoints,
//                map.getRouteDetails().getWaypoints());
//        Assert.assertEquals("RouteDetails routingOption is wrong",
//                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, map.getRouteDetails().getRoutingOption());
//        Assert.assertNull("RouteDetails emsRouteHandle should be null",
//                map.getRouteDetails().getEmsRouteHandle());
//
//    }

    @Test
    public void testManipulateRouteMapPanEast() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.MOVE_EAST,
                UserMapInteraction.EAST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapPanWest() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.MOVE_WEST,
                UserMapInteraction.WEST, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapPanNorth() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.MOVE_NORTH,
                UserMapInteraction.NORTH, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapPanSouth() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.MOVE_SOUTH,
                UserMapInteraction.SOUTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapZoomIn() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateRouteMapZoomInWhenAlreadyAtMinZoom() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_IN,
                UserMapInteraction.ZOOM,
                MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateRouteMapZoomOut() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateRouteMapZoomInWhenAlreadyAtMaxZoom() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.ZOOM_OUT,
                UserMapInteraction.ZOOM,
                MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateRouteMapNoOp() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Map, Action.NO_OP,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapChangeToMapView() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Photo, MapLayer.Map, Action.MAP_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapChangeToPhotoView() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.Photo, Action.PHOTO_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateRouteMapChangeToHybridView() throws Throwable {
        doTestManipulateRouteMap(MapLayer.Map, MapLayer.PhotoWithStreets, Action.HYBRID_VIEW,
                UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulateRouteMap(final MapLayer existingMapLayer,
            final MapLayer expectedMapLayerAfterAction,
            final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction,
            final int oldZoomLevel, final int newZoomLevel) {

        final double startLat = -45.45;
        final double startLong = 145.145;
        final double endLat = -45.46;
        final double endLong = 146.146;
        final JourneyWaypoints journeyWaypoints =
                createJourneyWaypoints(startLat, startLong, endLat, endLong);

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                                getMockScreenDimensions());

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

        // Expectation to cover off debug logging.
        EasyMock.expect(getMockMapUrl().getImageUrl()).andReturn("dummy url")
                .anyTimes();

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(newZoomLevel)
                .atLeastOnce();

        EasyMock.expect(mockEmsManager.getEmsZoomLevel(newZoomLevel)).andReturn(
                EMS_ZOOM_LEVEL);

        final SoapRouteHandle soapRouteHandle = createSoapRouteHandle();
        EasyMock.expect(getMockJourneyDescriptor().getEmsRouteHandle())
            .andReturn(soapRouteHandle);

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

        try {
            map.getOriginalMapCentre();
            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {
            Assert.assertEquals("IllegalStateException has wrong message",
                    "It is illegal to call getOriginalMapCentre() "
                            + "when isRouteMap() is true", e.getMessage());
        }

        Assert.assertEquals("isMapLayer is wrong",
                MapLayer.Map.equals(expectedMapLayerAfterAction), map.isMapLayer());
        Assert.assertEquals("isPhotoLayer is wrong",
                MapLayer.Photo.equals(expectedMapLayerAfterAction), map.isPhotoLayer());
        Assert.assertEquals("isPhotoWithStreetsLayer is wrong",
                MapLayer.PhotoWithStreets.equals(expectedMapLayerAfterAction),
                map.isPhotoWithStreetsLayer());

        Assert.assertEquals("isAtMinimumZoom() is wrong",
                newZoomLevel == MIN_ZOOM, map.isAtMinimumZoom());
        Assert.assertEquals("isAtMaximumZoom() is wrong",
                newZoomLevel == MAX_ZOOM, map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL, map
                .getEmsZoom());

        Assert.assertTrue("isRouteMap() should be true", map.isRouteMap());
        Assert.assertNotNull("getRouteDetails() should not be null", map
                .getRouteDetails());

        Assert.assertSame("RouteDetails waypoints are wrong", journeyWaypoints,
                map.getRouteDetails().getWaypoints());
        Assert.assertEquals("RouteDetails routingOption is wrong",
                RoutingOption.FASTEST_BY_ROAD_NO_TOLLS, map.getRouteDetails()
                        .getRoutingOption());
        Assert.assertNotNull("RouteDetails emsRouteHandle should not be null",
                map.getRouteDetails().getEmsRouteHandle());
        Assert.assertEquals("RouteDetails emsRouteHandle has wrong identifier",
                soapRouteHandle.getIdentifier(), map.getRouteDetails()
                        .getEmsRouteHandle().getIdentifier());
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
    public DeviceConfigRegistry<DeviceConfigType> getMockDeviceConfigRegistry() {
        return mockDeviceConfigRegistry;
    }

    /**
     * @param mockDeviceConfigRegistry the mockDeviceConfigRegistry to set
     */
    public void setMockDeviceConfigRegistry(
            final DeviceConfigRegistry<DeviceConfigType> mockDeviceConfigRegistry) {
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
     * @return the deviceConfigType
     */
    public DeviceConfigType getDeviceConfigType() {
        return deviceConfigType;
    }

    /**
     * @param deviceConfigType the deviceConfigType to set
     */
    public void setDeviceConfigType(final DeviceConfigType deviceConfigType) {
        this.deviceConfigType = deviceConfigType;
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

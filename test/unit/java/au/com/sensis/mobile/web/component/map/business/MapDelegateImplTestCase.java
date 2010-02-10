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
                getMockScreenDimensions());

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
        doTestManipulateMap(Action.MOVE_EAST, UserMapInteraction.EAST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapPanWest() throws Throwable {
        doTestManipulateMap(Action.MOVE_WEST, UserMapInteraction.WEST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapPanNorth() throws Throwable {
        doTestManipulateMap(Action.MOVE_NORTH, UserMapInteraction.NORTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapPanSouth() throws Throwable {
        doTestManipulateMap(Action.MOVE_SOUTH, UserMapInteraction.SOUTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulateMapZoomIn() throws Throwable {
        doTestManipulateMap(Action.ZOOM_IN, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulateMapZoomInWhenAlreadyAtMinZoom() throws Throwable {
        doTestManipulateMap(Action.ZOOM_IN, UserMapInteraction.ZOOM,
                MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulateMapZoomOut() throws Throwable {
        doTestManipulateMap(Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulateMapZoomInWhenAlreadyAtMaxZoom() throws Throwable {
        doTestManipulateMap(Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulateMapNoOp() throws Throwable {
        doTestManipulateMap(Action.NO_OP, UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }

    private void doTestManipulateMap(final Action mapDelegateAction,
            final UserMapInteraction userMapInteraction, final int oldZoomLevel,
            final int newZoomLevel) {
        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions());

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
                        getPoint2(), MobilesIconType.CROSS_HAIR, MapLayer.Photo,
                        panZoomDetail, getMockUserContext())).andReturn(
                getMockMapUrl());

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(newZoomLevel)
            .atLeastOnce();

        EasyMock.expect(getMockEmsManager().getEmsZoomLevel(newZoomLevel))
            .andReturn(EMS_ZOOM_LEVEL);

        replay();

        final Map map =
                getObjectUnderTest().manipulateMap(getPoint2(), getMockExistingMapUrl(),
                        MapLayer.Photo, MobilesIconType.CROSS_HAIR,
                        mapDelegateAction, getMockMobileContext());

        Assert.assertTrue("isMapImageRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());
        Assert.assertSame("map has wrong originalMapCentre", getPoint2(),
                map.getOriginalMapCentre());

        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong",
                map.isPhotoWithStreetsLayer());

        Assert.assertEquals("isAtMinimumZoom() is wrong",
                newZoomLevel == MIN_ZOOM, map.isAtMinimumZoom());
        Assert.assertEquals("isAtMaximumZoom() is wrong",
                newZoomLevel == MAX_ZOOM, map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());
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
    public void testManipulatePoiMap() throws Exception {

        final List<IconDescriptor> iconDescriptors = createIconDescriptors();

        EasyMock.expect(
                getMockScreenDimensionsStrategy().createScreenDimensions(
                        getMockMobileContext())).andReturn(
                getMockScreenDimensions());

        EasyMock.expect(getMockMobileContext().asUserContext()).andReturn(
                getMockUserContext());

        EasyMock.expect(getMockExistingMapUrl().getBoundingBox()).andReturn(
                getMockMobilesBoundingBox());

        final Integer oldZoomLevel = ZOOM_LEVEL;
        final Integer newZoomLevel = ZOOM_LEVEL - 1;
        EasyMock.expect(getMockExistingMapUrl().getZoom()).andReturn(
                oldZoomLevel);
        EasyMock.expect(getMockExistingMapUrl().getMapCentre()).andReturn(
                getPoint1()).atLeastOnce();

        final PanZoomDetail panZoomDetail =
                new PanZoomDetail(getMockMobilesBoundingBox(), getPoint1(),
                        UserMapInteraction.ZOOM, newZoomLevel);

        EasyMock.expect(
                getMockEmsManager().manipulatePoiMap(getMockScreenDimensions(),
                        getPoint2(), iconDescriptors,
                        MapLayer.Photo, panZoomDetail, getMockUserContext()))
                .andReturn(getMockMapUrl());

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(newZoomLevel)
                .atLeastOnce();

        EasyMock.expect(getMockEmsManager().getEmsZoomLevel(newZoomLevel))
                .andReturn(EMS_ZOOM_LEVEL);

        replay();

        final Action mapDelegateAction = Action.ZOOM_IN;
        final Map map =
                getObjectUnderTest().manipulatePoiMap(getPoint2(),
                        getMockExistingMapUrl(), MapLayer.Photo,
                        iconDescriptors,
                        mapDelegateAction,
                        getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());
        Assert.assertSame("map has wrong originalMapCentre",
                getPoint2(), map.getOriginalMapCentre());

        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong", map
                .isPhotoWithStreetsLayer());

        Assert.assertEquals("isAtMinimumZoom() is wrong",
                newZoomLevel == MIN_ZOOM, map.isAtMinimumZoom());
        Assert.assertEquals("isAtMaximumZoom() is wrong",
                newZoomLevel == MAX_ZOOM, map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL, map
                .getEmsZoom());
    }

    @Test
    public void testManipulatePoiMapPanEast() throws Throwable {
        doTestManipulatePoiMap(Action.MOVE_EAST, UserMapInteraction.EAST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapPanWest() throws Throwable {
        doTestManipulatePoiMap(Action.MOVE_WEST, UserMapInteraction.WEST,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapPanNorth() throws Throwable {
        doTestManipulatePoiMap(Action.MOVE_NORTH, UserMapInteraction.NORTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapPanSouth() throws Throwable {
        doTestManipulatePoiMap(Action.MOVE_SOUTH, UserMapInteraction.SOUTH,
                ZOOM_LEVEL, ZOOM_LEVEL);
    }

    @Test
    public void testManipulatePoiMapZoomIn() throws Throwable {
        doTestManipulatePoiMap(Action.ZOOM_IN, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL - 1);
    }

    @Test
    public void testManipulatePoiMapZoomInWhenAlreadyAtMinZoom() throws Throwable {
        doTestManipulatePoiMap(Action.ZOOM_IN, UserMapInteraction.ZOOM,
                MIN_ZOOM, MIN_ZOOM);
    }

    @Test
    public void testManipulatePoiMapZoomOut() throws Throwable {
        doTestManipulatePoiMap(Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                ZOOM_LEVEL, ZOOM_LEVEL + 1);
    }

    @Test
    public void testManipulatePoiMapZoomInWhenAlreadyAtMaxZoom() throws Throwable {
        doTestManipulatePoiMap(Action.ZOOM_OUT, UserMapInteraction.ZOOM,
                MAX_ZOOM, MAX_ZOOM);
    }

    @Test
    public void testManipulatePoiMapNoOp() throws Throwable {
        doTestManipulatePoiMap(Action.NO_OP, UserMapInteraction.NO_ACTION, ZOOM_LEVEL, ZOOM_LEVEL);
    }


    private void doTestManipulatePoiMap(final Action mapDelegateAction,
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
                        MapLayer.Photo, panZoomDetail, getMockUserContext()))
                .andReturn(getMockMapUrl());

        EasyMock.expect(getMockMapUrl().getZoom()).andReturn(newZoomLevel)
                .atLeastOnce();

        EasyMock.expect(getMockEmsManager().getEmsZoomLevel(newZoomLevel))
                .andReturn(EMS_ZOOM_LEVEL);

        replay();

        final Map map =
                getObjectUnderTest().manipulatePoiMap(getPoint2(),
                        getMockExistingMapUrl(), MapLayer.Photo,
                        iconDescriptors,
                        mapDelegateAction,
                        getMockMobileContext());

        Assert.assertTrue("isMapImageRetrieved() should be true", map
                .isMapImageRetrieved());
        Assert.assertSame("map has wrong mapUrl", getMockMapUrl(),
                map.getMapUrl());
        Assert.assertSame("map has wrong originalMapCentre",
                getPoint2(), map.getOriginalMapCentre());

        Assert.assertFalse("isMapLayer is wrong", map.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", map.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong", map
                .isPhotoWithStreetsLayer());

        Assert.assertEquals("isAtMinimumZoom() is wrong",
                newZoomLevel == MIN_ZOOM, map.isAtMinimumZoom());
        Assert.assertEquals("isAtMaximumZoom() is wrong",
                newZoomLevel == MAX_ZOOM, map.isAtMaximumZoom());

        Assert.assertEquals("emsZoom is wrong", EMS_ZOOM_LEVEL, map
                .getEmsZoom());
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
}

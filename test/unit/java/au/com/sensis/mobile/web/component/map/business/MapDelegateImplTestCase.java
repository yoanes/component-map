package au.com.sensis.mobile.web.component.map.business;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.WGS84PointTestDataFactory;
import au.com.sensis.mobile.web.component.map.business.MapDelegate.Action;
import au.com.sensis.mobile.web.component.map.business.MapDelegateImpl.ScreenDimensionsStrategy;
import au.com.sensis.mobile.web.component.map.model.MapUrlHolder;
import au.com.sensis.sal.common.UserContext;
import au.com.sensis.wireless.manager.ems.EMSManager;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;
import au.com.sensis.wireless.manager.mapping.MobilesIconType;
import au.com.sensis.wireless.manager.mapping.PanZoomDetail;
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

    private MobileContext mockMobileContext;
    private UserContext mockUserContext;
    private EMSManager mockEmsManager;
    private MapUrl mockMapUrl;
    private MapUrl mockExistingMapUrl;
    private ScreenDimensionsStrategy mockScreenDimensionsStrategy;
    private ScreenDimensions mockScreenDimensions;
    private MobilesBoundingBox mockMobilesBoundingBox;

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

        setObjectUnderTest(new MapDelegateImpl());
        getObjectUnderTest().setScreenDimensionsStrategy(getMockScreenDimensionsStrategy());
        getObjectUnderTest().setEmsManager(getMockEmsManager());

        // TODO: There has to be a better way of validating the zoom. See
        // MapDelegate.retrieveInitialMap comments.
        getObjectUnderTest().setMinZoom(MIN_ZOOM);
        getObjectUnderTest().setMaxZoom(MAX_ZOOM);
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
    public void testRetrieveInitialMapWhenClientWillNotRetrieveMapItself() throws Throwable {

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
                        MapLayer.Map, panZoomDetail,
                        getMockUserContext())).andReturn(
                getMockMapUrl());

        replay();

        final MapUrlHolder mapUrlHolder =
                getObjectUnderTest().retrieveInitialMap(getPoint1(),
                        ZOOM_LEVEL, getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", mapUrlHolder
                .isMapImageRetrieved());
        Assert.assertSame("mapUrlHolder has wrong mapUrl", getMockMapUrl(),
                mapUrlHolder.getMapUrl());
        Assert.assertSame("mapUrlHolder has wrong originalMapCentre", getPoint1(),
                mapUrlHolder.getOriginalMapCentre());

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
                        getPoint2(), MobilesIconType.CROSS_HAIR, MapLayer.Map,
                        panZoomDetail, getMockUserContext())).andReturn(
                getMockMapUrl());

        replay();

        final MapUrlHolder mapUrlHolder =
                getObjectUnderTest().manipulateMap(getPoint2(), getMockExistingMapUrl(),
                        mapDelegateAction, getMockMobileContext());

        Assert.assertTrue("isMapRetrieved() should be true", mapUrlHolder
                .isMapImageRetrieved());
        Assert.assertSame("mapUrlHolder has wrong mapUrl", getMockMapUrl(),
                mapUrlHolder.getMapUrl());
        Assert.assertSame("mapUrlHolder has wrong originalMapCentre", getPoint2(),
                mapUrlHolder.getOriginalMapCentre());

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
}

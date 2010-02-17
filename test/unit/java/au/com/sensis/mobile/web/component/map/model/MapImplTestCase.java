package au.com.sensis.mobile.web.component.map.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.WGS84PointTestDataFactory;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.ResolvedIcon;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link MapImpl}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapImplTestCase extends AbstractJUnit4TestCase {

    private static final int EMS_ZOOM_LEVEL = 16;
    private static final int ZOOM_LEVEL = 5;
    private WGS84PointTestDataFactory wgs84PointTestDataFactory;
    private WGS84Point wgs84Point1;
    private MapUrl mockMapUrl;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setWgs84PointTestDataFactory(new WGS84PointTestDataFactory());
        setWgs84Point1(getWgs84PointTestDataFactory().createValidWGS84Point());
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

        Assert.assertEquals("EMS zoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());

        Assert.assertTrue("isAtMinimumZoom should be true",
                map.isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom should be false",
                map.isAtMaximumZoom());

        Assert.assertSame("resolvedIcons is wrong", resolvedIcons,
                map.getResolvedIcons());

    }

    @Test
    public void testCreateMapRetrievalDeferrendInstance() throws Throwable {
        final List<ResolvedIcon> resolvedIcons = new ArrayList<ResolvedIcon>();
        final Map map =
                MapImpl.createMapRetrievalDeferrendInstance(
                        getWgs84Point1(), MapLayer.Map, resolvedIcons,
                        ZOOM_LEVEL, EMS_ZOOM_LEVEL, false, true);

        Assert.assertFalse("isMapImageRetrieved() should be false",
                map.isMapImageRetrieved());
        Assert.assertTrue(
                "isMapImageRetrievalDeferredToClient() should be true",
                map.isMapImageRetrievalDeferredToClient());

        Assert.assertNotNull("mapUrl should not be null", map
                .getMapUrl());
        Assert.assertEquals("mapCentre is wrong", getWgs84Point1(),
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

        Assert.assertEquals("EMS zoom is wrong", EMS_ZOOM_LEVEL,
                map.getEmsZoom());

        Assert.assertFalse("isAtMinimumZoom should be false",
                map.isAtMinimumZoom());
        Assert.assertTrue("isAtMaximumZoom should be true",
                map.isAtMaximumZoom());
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

}

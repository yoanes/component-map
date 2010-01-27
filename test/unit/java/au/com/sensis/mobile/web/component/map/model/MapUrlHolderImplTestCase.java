package au.com.sensis.mobile.web.component.map.model;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.WGS84PointTestDataFactory;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link MapUrlHolderImpl}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapUrlHolderImplTestCase extends AbstractJUnit4TestCase {

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
        final MapUrlHolder mapUrlHolder =
                MapUrlHolderImpl.createMapRetrievedInstance(getWgs84Point1(),
                        MapLayer.Photo,
                        getMockMapUrl(), true, false);

        Assert.assertTrue("isMapImageRetrieved() should be true", mapUrlHolder
                .isMapImageRetrieved());
        Assert.assertFalse(
                "isMapImageRetrievalDeferredToClient() should be false",
                mapUrlHolder.isMapImageRetrievalDeferredToClient());
        Assert.assertEquals("mapUrl is wrong", getMockMapUrl(), mapUrlHolder
                .getMapUrl());
        Assert.assertEquals("originalMapCentre is wrong", getWgs84Point1(),
                mapUrlHolder.getOriginalMapCentre());
        Assert.assertFalse("isMapLayer is wrong", mapUrlHolder.isMapLayer());
        Assert.assertTrue("isPhotoLayer is wrong", mapUrlHolder.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong",
                mapUrlHolder.isPhotoWithStreetsLayer());

        Assert.assertTrue("isAtMinimumZoom should be true",
                mapUrlHolder.isAtMinimumZoom());
        Assert.assertFalse("isAtMaximumZoom should be false",
                mapUrlHolder.isAtMaximumZoom());

    }

    @Test
    public void testCreateMapRetrievalDeferrendInstance() throws Throwable {
        final MapUrlHolder mapUrlHolder =
                MapUrlHolderImpl.createMapRetrievalDeferrendInstance(
                        getWgs84Point1(), MapLayer.Map, ZOOM_LEVEL, false, true);

        Assert.assertFalse("isMapImageRetrieved() should be false",
                mapUrlHolder.isMapImageRetrieved());
        Assert.assertTrue(
                "isMapImageRetrievalDeferredToClient() should be true",
                mapUrlHolder.isMapImageRetrievalDeferredToClient());

        Assert.assertNotNull("mapUrl should not be null", mapUrlHolder
                .getMapUrl());
        Assert.assertEquals("mapCentre is wrong", getWgs84Point1(),
                mapUrlHolder.getMapUrl().getMapCentre());
        Assert.assertEquals("zoom is wrong", ZOOM_LEVEL,
                mapUrlHolder.getMapUrl().getZoom());
        Assert.assertEquals("imageUrl is wrong", StringUtils.EMPTY,
                mapUrlHolder.getMapUrl().getImageUrl());
        Assert.assertNull("boundingBox is wrong",
                mapUrlHolder.getMapUrl().getBoundingBox());

        Assert.assertEquals("originalMapCentre is wrong", getWgs84Point1(),
                mapUrlHolder.getOriginalMapCentre());
        Assert.assertTrue("isMapLayer is wrong", mapUrlHolder.isMapLayer());
        Assert.assertFalse("isPhotoLayer is wrong", mapUrlHolder.isPhotoLayer());
        Assert.assertFalse("isPhotoWithStreetsLayer is wrong",
                mapUrlHolder.isPhotoWithStreetsLayer());

        Assert.assertFalse("isAtMinimumZoom should be false",
                mapUrlHolder.isAtMinimumZoom());
        Assert.assertTrue("isAtMaximumZoom should be true",
                mapUrlHolder.isAtMaximumZoom());
    }

    @Test
    public void testGetMapLayerShortCode() throws Throwable {
        final MapLayer[] testValues =
                { MapLayer.Map, MapLayer.Photo, MapLayer.PhotoWithStreets };
        final String[] expectedValues = { "m", "p", "ps" };

        for (int i = 0; i < testValues.length; i++) {
            final MapUrlHolder mapUrlHolder =
                    MapUrlHolderImpl.createMapRetrievedInstance(
                            getWgs84Point1(), testValues[i], getMockMapUrl(),
                            true, false);

            Assert.assertEquals("MapLayer short code is wrong",
                    expectedValues[i], mapUrlHolder.getMapLayerShortCode());
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

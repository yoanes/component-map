package au.com.sensis.mobile.web.component.map.model;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.manager.mapping.MapLayer;

/**
 * Unit test {@link MapLayerTransformer}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapLayerTransformerTestCase {

    private MapLayerTransformer objectUnderTest;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new MapLayerTransformer());
    }

    @Test
    public void testTransformToShortCode() throws Throwable {
        final MapLayer[] testValues =
                { MapLayer.Map, MapLayer.Photo, MapLayer.PhotoWithStreets };
        final String[] expectedValues = { "m", "p", "ps" };

        for (int i = 0; i < testValues.length; i++) {
            Assert.assertEquals("MapLayer short code is wrong",
                    expectedValues[i], getObjectUnderTest()
                            .transformToShortCode(testValues[i]));
        }

    }

    @Test
    public void testTransformFromShortCode() throws Throwable {
        final String[] testValues = { "m", "p", "ps" };
        final MapLayer[] expectedValues =
                { MapLayer.Map, MapLayer.Photo, MapLayer.PhotoWithStreets };
        for (int i = 0; i < testValues.length; i++) {
            Assert.assertEquals("MapLayer is wrong", expectedValues[i],
                    getObjectUnderTest().transformFromShortCode(testValues[i]));
        }

    }

    @Test
    public void testTransformToJsCode() throws Throwable {
        final MapLayer[] testValues =
                { MapLayer.Map, MapLayer.Photo, MapLayer.PhotoWithStreets };
        final String[] expectedValues = { "map", "photo", "map" };

        for (int i = 0; i < testValues.length; i++) {
            Assert.assertEquals("MapLayer JS code is wrong",
                    expectedValues[i], getObjectUnderTest()
                            .transformToJsCode(testValues[i]));
        }

    }

    /**
     * @return the objectUnderTest
     */
    public MapLayerTransformer getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(final MapLayerTransformer objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }
}

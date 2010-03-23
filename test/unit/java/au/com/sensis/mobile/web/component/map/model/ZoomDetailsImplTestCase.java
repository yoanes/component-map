package au.com.sensis.mobile.web.component.map.model;

import org.junit.Assert;
import org.junit.Test;

import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Test {@link ZoomDetailsImpl}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ZoomDetailsImplTestCase extends AbstractJUnit4TestCase {

    private static final int DEFAULT_EMS_ZOOM = 5;
    private static final boolean DEFAULT_IS_AT_MAX_ZOOM = false;
    private static final boolean DEFAULT_IS_AT_MIN_ZOOM = false;
    private ZoomDetailsImpl objectUnderTest;

    @Test
    public void testGetEmsZoom() throws Throwable {
        setObjectUnderTest(new ZoomDetailsImpl(DEFAULT_EMS_ZOOM,
                DEFAULT_IS_AT_MAX_ZOOM, DEFAULT_IS_AT_MIN_ZOOM));

        Assert.assertEquals("emsZoom is wrong", DEFAULT_EMS_ZOOM,
                getObjectUnderTest().getEmsZoom());
    }

    @Test
    public void testGetEmsJavaScriptZoom() throws Throwable {
        setObjectUnderTest(new ZoomDetailsImpl(DEFAULT_EMS_ZOOM,
                DEFAULT_IS_AT_MAX_ZOOM, DEFAULT_IS_AT_MIN_ZOOM));

        Assert.assertEquals("emsJavaScriptZoom is wrong", DEFAULT_EMS_ZOOM - 1,
                getObjectUnderTest().getEmsJavaScriptZoom());
    }

    @Test
    public void testGetIsAtMaximumZoomWhenTrue() throws Throwable {
        setObjectUnderTest(new ZoomDetailsImpl(DEFAULT_EMS_ZOOM,
                DEFAULT_IS_AT_MIN_ZOOM, true));

        Assert.assertTrue("isAtMaximumZoom is wrong",
                getObjectUnderTest().isAtMaximumZoom());
    }

    @Test
    public void testGetIsAtMaximumZoomWhenFalse() throws Throwable {
        setObjectUnderTest(new ZoomDetailsImpl(DEFAULT_EMS_ZOOM,
                DEFAULT_IS_AT_MIN_ZOOM, false));

        Assert.assertFalse("isAtMaximumZoom is wrong",
                getObjectUnderTest().isAtMaximumZoom());
    }

    @Test
    public void testGetIsAtMinimumZoomWhenTrue() throws Throwable {
        setObjectUnderTest(new ZoomDetailsImpl(DEFAULT_EMS_ZOOM,
                true, DEFAULT_IS_AT_MAX_ZOOM));

        Assert.assertTrue("isAtMinimumZoom is wrong",
                getObjectUnderTest().isAtMinimumZoom());
    }

    @Test
    public void testGetIsAtMinimumZoomWhenFalse() throws Throwable {
        setObjectUnderTest(new ZoomDetailsImpl(DEFAULT_EMS_ZOOM,
                false, DEFAULT_IS_AT_MAX_ZOOM));

        Assert.assertFalse("isAtMinimumZoom is wrong",
                getObjectUnderTest().isAtMinimumZoom());
    }

    /**
     * @return the objectUnderTest
     */
    private ZoomDetailsImpl getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(final ZoomDetailsImpl objectUnderTest) {
        this.objectUnderTest = objectUnderTest;
    }


}

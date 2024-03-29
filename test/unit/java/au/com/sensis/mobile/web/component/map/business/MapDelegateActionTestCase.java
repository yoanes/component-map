package au.com.sensis.mobile.web.component.map.business;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.mobile.web.component.map.business.MapDelegate.Action;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;

/**
 * Unit test {@link Action}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapDelegateActionTestCase extends AbstractJUnit4TestCase {


    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testFromShortCodeForZoomIn() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.ZOOM_IN, Action
                .fromShortCode(Action.ZOOM_IN.getShortCode()));
    }

    @Test
    public void testFromShortCodeForZoomOut() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.ZOOM_OUT, Action
                .fromShortCode(Action.ZOOM_OUT.getShortCode()));
    }

    @Test
    public void testFromShortCodeForPanNorth() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.MOVE_NORTH, Action
                .fromShortCode(Action.MOVE_NORTH.getShortCode()));
    }

    @Test
    public void testFromShortCodeForPanSouth() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.MOVE_SOUTH, Action
                .fromShortCode(Action.MOVE_SOUTH.getShortCode()));
    }

    @Test
    public void testFromShortCodeForPanEast() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.MOVE_EAST, Action
                .fromShortCode(Action.MOVE_EAST.getShortCode()));
    }

    @Test
    public void testFromShortCodeForPanWest() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.MOVE_WEST, Action
                .fromShortCode(Action.MOVE_WEST.getShortCode()));
    }

    @Test
    public void testFromShortCodeForMapView() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.MAP_VIEW, Action
                .fromShortCode(Action.MAP_VIEW.getShortCode()));
    }

    @Test
    public void testFromShortCodeForPhotoView() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.PHOTO_VIEW, Action
                .fromShortCode(Action.PHOTO_VIEW.getShortCode()));
    }

    @Test
    public void testFromShortCodeForHybridView() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.HYBRID_VIEW, Action
                .fromShortCode(Action.HYBRID_VIEW.getShortCode()));
    }

    @Test
    public void testFromShortCodeForNoOp() throws Throwable {
        Assert.assertEquals("fromShortCode is wrong", Action.NO_OP, Action
                .fromShortCode(Action.NO_OP.getShortCode()));
    }

    @Test
    public void testFromShortCodeForUnrecognisedString() throws Throwable {
        final String[] testValues = { null, StringUtils.EMPTY, " ", "dummy" };
        for (final String testValue : testValues) {
            Assert.assertEquals("fromShortCode is wrong", Action.NO_OP, Action
                    .fromShortCode(testValue));
        }
    }

    @Test
    public void testIsPanAction() throws Throwable {
        Assert.assertTrue("isPanAction() should return true for Pan East",
                Action.MOVE_EAST.isPanAction());
        Assert.assertTrue("isPanAction() should return true for Pan West",
                Action.MOVE_WEST.isPanAction());
        Assert.assertTrue("isPanAction() should return true for Pan North",
                Action.MOVE_NORTH.isPanAction());
        Assert.assertTrue("isPanAction() should return true for Pan South",
                Action.MOVE_SOUTH.isPanAction());

        Assert.assertFalse("isPanAction() should return false for Zoom In",
                Action.ZOOM_IN.isPanAction());
        Assert.assertFalse("isPanAction() should return false for Zoom Out",
                Action.ZOOM_OUT.isPanAction());

        Assert.assertFalse("isPanAction() should return false for Map View",
                Action.MAP_VIEW.isPanAction());
        Assert.assertFalse("isPanAction() should return false for Photo View",
                Action.PHOTO_VIEW.isPanAction());
        Assert.assertFalse("isPanAction() should return false for Hybrid View",
                Action.HYBRID_VIEW.isPanAction());

    }

    @Test
    public void testIsZoomAction() throws Throwable {
        Assert.assertFalse("isZoomAction() should return false for Pan East",
                Action.MOVE_EAST.isZoomAction());
        Assert.assertFalse("isZoomAction() should return false for Pan West",
                Action.MOVE_WEST.isZoomAction());
        Assert.assertFalse("isZoomAction() should return false for Pan North",
                Action.MOVE_NORTH.isZoomAction());
        Assert.assertFalse("isZoomAction() should return false for Pan South",
                Action.MOVE_SOUTH.isZoomAction());

        Assert.assertTrue("isZoomAction() should return true for Zoom In",
                Action.ZOOM_IN.isZoomAction());
        Assert.assertTrue("isZoomAction() should return true for Zoom Out",
                Action.ZOOM_OUT.isZoomAction());

        Assert.assertFalse("isZoomAction() should return false for Map View",
                Action.MAP_VIEW.isZoomAction());
        Assert.assertFalse("isZoomAction() should return false for Photo View",
                Action.PHOTO_VIEW.isZoomAction());
        Assert.assertFalse("isZoomAction() should return false for Hybrid View",
                Action.HYBRID_VIEW.isZoomAction());


    }

    @Test
    public void testIsViewAction() throws Throwable {
        Assert.assertFalse("isViewAction() should return false for Pan East",
                Action.MOVE_EAST.isViewAction());
        Assert.assertFalse("isViewAction() should return false for Pan West",
                Action.MOVE_WEST.isViewAction());
        Assert.assertFalse("isViewAction() should return false for Pan North",
                Action.MOVE_NORTH.isViewAction());
        Assert.assertFalse("isViewAction() should return false for Pan South",
                Action.MOVE_SOUTH.isViewAction());

        Assert.assertFalse("isViewAction() should return false for Zoom In",
                Action.ZOOM_IN.isViewAction());
        Assert.assertFalse("isViewAction() should return false for Zoom Out",
                Action.ZOOM_OUT.isViewAction());

        Assert.assertTrue("isViewAction() should return true for Map View",
                Action.MAP_VIEW.isViewAction());
        Assert.assertTrue("isViewAction() should return true for Photo View",
                Action.PHOTO_VIEW.isViewAction());
        Assert.assertTrue("isViewAction() should return true for Hybrid View",
                Action.HYBRID_VIEW.isViewAction());
    }
}

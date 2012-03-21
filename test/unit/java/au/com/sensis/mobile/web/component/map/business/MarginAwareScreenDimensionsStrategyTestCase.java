package au.com.sensis.mobile.web.component.map.business;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.devicerepository.Device;
import au.com.sensis.devicerepository.ImageCategory;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Unit test {@link MarginAwareScreenDimensionsStrategy}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MarginAwareScreenDimensionsStrategyTestCase extends
AbstractJUnit4TestCase {

    private static final Integer DEFAULT_MARGIN = new Integer(2);

    private static final Integer TEST_MARGIN = new Integer(16);

    private static final Integer SMALL_MARGIN = new Integer(4);

    private MarginAwareScreenDimensionsStrategy objectUnderTest;

    private MobileContext mockMobileContext;
    private Device mockDevice;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    /**
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new MarginAwareScreenDimensionsStrategy());

        getObjectUnderTest().setDefaultMargin(DEFAULT_MARGIN);

        getObjectUnderTest().setSmallMargin(SMALL_MARGIN);
    }

    @Test
    public void testCreateScreenDimensionsWhenDeviceIsNull() throws Throwable {

        EasyMock.expect(getMockMobileContext().getDevice()).andReturn(null);

        replay();

        try {
            getObjectUnderTest().createScreenDimensions(getMockMobileContext());
            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {
            Assert
            .assertEquals(
                    "IllegalStateException has wrong message",
                    "mobileContext.getDevice() is null. This should never happen !!!",
                    e.getMessage());
        }
        verify();

    }

    @Test
    public void testCreateScreenDimensionsWhenImageCategoryFound()
            throws Throwable {

        EasyMock.expect(getMockMobileContext().getDevice()).andReturn(
                getMockDevice()).atLeastOnce();

        EasyMock.expect(getMockDevice().getImageCategory()).andReturn(
                ImageCategory.S).atLeastOnce();

        final int devicePixelsX = 50;
        EasyMock.expect(getMockDevice().getPixelsX()).andReturn(devicePixelsX)
        .atLeastOnce();

        final int devicePixelsY = 150;
        EasyMock.expect(getMockDevice().getPixelsY()).andReturn(devicePixelsY)
        .atLeastOnce();

        replay();

        final ScreenDimensions actualScreenDimensions =
                getObjectUnderTest().createScreenDimensions(
                        getMockMobileContext());

        Assert
        .assertEquals("actualScreenDimensions width is wrong",
                devicePixelsX - SMALL_MARGIN, actualScreenDimensions
                .getWidth());
        Assert.assertEquals("actualScreenDimensions height is wrong",
                devicePixelsY, actualScreenDimensions.getHeight());

        verify();

    }

    @Test
    public void testCreateScreenDimensionsWhenImageCategoryNotFound()
            throws Throwable {

        EasyMock.expect(getMockMobileContext().getDevice()).andReturn(
                getMockDevice()).atLeastOnce();

        EasyMock.expect(getMockDevice().getImageCategory()).andReturn(
                ImageCategory.L).atLeastOnce();

        final int devicePixelsX = 50;
        EasyMock.expect(getMockDevice().getPixelsX()).andReturn(devicePixelsX)
        .atLeastOnce();

        final int devicePixelsY = 150;
        EasyMock.expect(getMockDevice().getPixelsY()).andReturn(devicePixelsY)
        .atLeastOnce();

        replay();

        final ScreenDimensions actualScreenDimensions =
                getObjectUnderTest().createScreenDimensions(
                        getMockMobileContext());

        Assert.assertEquals("actualScreenDimensions width is wrong",
                devicePixelsX - DEFAULT_MARGIN, actualScreenDimensions
                .getWidth());
        Assert.assertEquals("actualScreenDimensions height is wrong",
                devicePixelsY, actualScreenDimensions.getHeight());

        verify();
    }

    @Test
    public void testSetGetXtraSmallMargin() throws Throwable {
        getObjectUnderTest().setXtraSmallMargin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN,
                getObjectUnderTest().getXtraSmallMargin());
    }

    @Test
    public void testSetGetSmallMargin() throws Throwable {
        getObjectUnderTest().setSmallMargin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN,
                getObjectUnderTest().getSmallMargin());
    }

    @Test
    public void testSetGetMediumMargin() throws Throwable {
        getObjectUnderTest().setMediumMargin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN,
                getObjectUnderTest().getMediumMargin());
    }

    @Test
    public void testSetGetLargeMargin() throws Throwable {
        getObjectUnderTest().setLargeMargin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN,
                getObjectUnderTest().getLargeMargin());
    }

    @Test
    public void testSetGetHD480Margin() throws Throwable {
        getObjectUnderTest().setHd480Margin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN,
                getObjectUnderTest().getHd480Margin());
    }

    @Test
    public void testSetGetHD600Margin() throws Throwable {
        getObjectUnderTest().setHd600Margin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN,
                getObjectUnderTest().getHd600Margin());
    }

    @Test
    public void testSetGetHD800Margin() throws Throwable {
        getObjectUnderTest().setHd800Margin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN,
                getObjectUnderTest().getHd800Margin());
    }

    @Test
    public void testSetGetHD1024Margin() throws Throwable {
        getObjectUnderTest().setHd1024Margin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN,
                getObjectUnderTest().getHd1024Margin());
    }

    @Test
    public void testSetGetDefaultMargin() throws Throwable {
        getObjectUnderTest().setDefaultMargin(TEST_MARGIN);

        Assert.assertEquals("margin is wrong", TEST_MARGIN.intValue(),
                getObjectUnderTest().getDefaultMargin());
    }

    /**
     * @return the objectUnderTest
     */
    private MarginAwareScreenDimensionsStrategy getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    private void setObjectUnderTest(
            final MarginAwareScreenDimensionsStrategy objectUnderTest) {
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
}

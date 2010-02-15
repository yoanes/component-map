package au.com.sensis.mobile.web.component.map.business;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.test.AbstractJUnit4TestCase;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Unit test {@link DefaultScreenDimensionsStrategy}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DefaultScreenDimensionsStrategyTestCase extends
        AbstractJUnit4TestCase {

    private static final Integer PIXELS_X = new Integer(100);
    private static final Integer PIXELS_Y = new Integer(120);

    private DefaultScreenDimensionsStrategy objectUnderTest;
    private MobileContext mockMobileContext;
    private Device mockDevice;

    /**
     * Setup test data.
     *
     * @throws Exception Thrown if any error occurs.
     */
    @Before
    public void setUp() throws Exception {
        setObjectUnderTest(new DefaultScreenDimensionsStrategy());
    }

    @Test
    public void testCreateScreenDimensions() throws Throwable {
        EasyMock.expect(getMockMobileContext().getDevice()).andReturn(
                getMockDevice()).atLeastOnce();

        EasyMock.expect(getMockDevice().getPixelsX()).andReturn(PIXELS_X)
                .atLeastOnce();
        EasyMock.expect(getMockDevice().getPixelsY()).andReturn(PIXELS_Y)
                .atLeastOnce();

        replay();

        final ScreenDimensions screenDimensions =
                getObjectUnderTest().createScreenDimensions(
                        getMockMobileContext());

        Assert.assertEquals("width is wrong", PIXELS_X.intValue(),
                screenDimensions.getWidth());
        Assert.assertEquals("heigth is wrong", PIXELS_Y.intValue(),
                screenDimensions.getHeight());

    }

    @Test
    public void testCreateScreenDimensionsWhenDeviceIsNull() throws Throwable {
        EasyMock.expect(getMockMobileContext().getDevice()).andReturn(null)
                .atLeastOnce();

        replay();

        try {
            getObjectUnderTest().createScreenDimensions(getMockMobileContext());
            Assert.fail("IllegalStateException expected");
        } catch (final IllegalStateException e) {
            Assert.assertEquals("IllegalStateException has wrong message",
                    "mobileContext.getDevice() is null. "
                            + "This should never happen !!!", e.getMessage());
        }
    }

    /**
     * @return the objectUnderTest
     */
    public DefaultScreenDimensionsStrategy getObjectUnderTest() {
        return objectUnderTest;
    }

    /**
     * @param objectUnderTest the objectUnderTest to set
     */
    public void setObjectUnderTest(final DefaultScreenDimensionsStrategy objectUnderTest) {
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

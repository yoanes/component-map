package au.com.sensis.mobile.web.component.map.business;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.sensis.mobile.web.component.map.business.MapDelegateImpl.ScreenDimensionsStrategy;
import au.com.sensis.wireless.common.volantis.devicerepository.api.Device;
import au.com.sensis.wireless.common.volantis.devicerepository.api.ImageCategory;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * A Strategy that can ask for a smaller map based on the margins.
 *
 * @author Boyd Sharrock
 */
public class MarginAwareScreenDimensionsStrategy implements ScreenDimensionsStrategy {

    private static final Logger LOGGER =
            Logger.getLogger(MarginAwareScreenDimensionsStrategy.class);

    private final Map<ImageCategory, Integer> marginsByImageCategory =
        new HashMap<ImageCategory, Integer>();

    private int defaultMargin;

    /**
     * {@inheritDoc}
     */
    public ScreenDimensions createScreenDimensions(
            final MobileContext mobileContext) {

        final Device device = mobileContext.getDevice();

        if (device == null) {

            throw new IllegalStateException(
                    "mobileContext.getDevice() is null. This should never happen !!!");
        } else {

            return new ScreenDimensions(
                    mobileContext.getDevice().getPixelsX() - marginSizeFor(device),
                    mobileContext.getDevice().getPixelsY());
        }
    }

    /**
     * @param device
     *            .
     * @return The appropriate margin size...
     */
    protected int marginSizeFor(final Device device) {

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ImageCategory is " + device.getImageCategory());
        }

        if (device.getImageCategory() != null) {

            final Integer candidateMargin =
                    marginsByImageCategory.get(device.getImageCategory());

            if (candidateMargin != null) {

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Margin to use: " + candidateMargin);
                }

                return candidateMargin;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No margin found for ImageCategory: "
                    + device.getImageCategory() + ". Using defaultMargin: "
                    + getDefaultMargin());
        }

        return getDefaultMargin();
    }

    // Accessors //

    /**
     * @return  the total margin.
     */
    public Integer getXtraSmallMargin() {

        return marginsByImageCategory.get(ImageCategory.XS);
    }

    /**
     * @param totalMargin the total margin to set.
     */
    public void setXtraSmallMargin(final Integer totalMargin) {

        marginsByImageCategory.put(ImageCategory.XS, totalMargin);
    }

    /**
     * @return  the smallMargin.
     */
    public Integer getSmallMargin() {

        return marginsByImageCategory.get(ImageCategory.S);
    }

    /**
     * @param totalMargin the totalMargin to set.
     */
    public void setSmallMargin(final Integer totalMargin) {

        marginsByImageCategory.put(ImageCategory.S, totalMargin);
    }

    /**
     * @return  the total margin.
     */
    public Integer getMediumMargin() {

        return marginsByImageCategory.get(ImageCategory.M);
    }

    /**
     * @param totalMargin the total margin to set.
     */
    public void setMediumMargin(final Integer totalMargin) {

        marginsByImageCategory.put(ImageCategory.M, totalMargin);
    }

    /**
     * @return  the total margin.
     */
    public Integer getLargeMargin() {

        return marginsByImageCategory.get(ImageCategory.L);
    }

    /**
     * @param totalMargin the total margin to set.
     */
    public void setLargeMargin(final Integer totalMargin) {

        marginsByImageCategory.put(ImageCategory.L, totalMargin);
    }

    /**
     * @return  the total margin.
     */
    public Integer getHd480Margin() {

        return marginsByImageCategory.get(ImageCategory.HD480);
    }

    /**
     * @param totalMargin the total margin to set.
     */
    public void setHd480Margin(final Integer totalMargin) {

        marginsByImageCategory.put(ImageCategory.HD480, totalMargin);
    }

    /**
     * @return  the total margin.
     */
    public Integer getHd600Margin() {

        return marginsByImageCategory.get(ImageCategory.HD600);
    }

    /**
     * @param totalMargin the total margin to set.
     */
    public void setHd600Margin(final Integer totalMargin) {

        marginsByImageCategory.put(ImageCategory.HD600, totalMargin);
    }

    /**
     * @return  the total margin.
     */
    public Integer getHd800Margin() {

        return marginsByImageCategory.get(ImageCategory.HD800);
    }

    /**
     * @param totalMargin the total margin to set.
     */
    public void setHd800Margin(final Integer totalMargin) {

        marginsByImageCategory.put(ImageCategory.HD800, totalMargin);
    }

    /**
     * @return  the total margin.
     */
    public Integer getHd1024Margin() {

        return marginsByImageCategory.get(ImageCategory.HD1024);
    }

    /**
     * @param totalMargin the total margin to set.
     */
    public void setHd1024Margin(final Integer totalMargin) {

        marginsByImageCategory.put(ImageCategory.HD1024, totalMargin);
    }

    // Accessors //

    /**
     * @return  the defaultMargin.
     */
    public int getDefaultMargin() {

        return defaultMargin;
    }

    /**
     * @param defaultMargin the defaultMargin to set.
     */
    public void setDefaultMargin(final int defaultMargin) {

        this.defaultMargin = defaultMargin;
    }
}

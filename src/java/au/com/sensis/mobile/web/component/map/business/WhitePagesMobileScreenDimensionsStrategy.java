package au.com.sensis.mobile.web.component.map.business;

import org.apache.log4j.Logger;

import au.com.sensis.mobile.web.component.map.business.MapDelegateImpl.ScreenDimensionsStrategy;
import au.com.sensis.wireless.common.volantis.devicerepository.api.ImageCategory;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * This implementation was shamelessly pinched from
 * au.com.sensis.mobile.whitepages.service.location.LocationManagerImpl.
 *
 * TODO: determine if this strategy implemntation is still valid. If so, write unit tests for it.
 *
 * @author Adrian.Koh2@sensis.com.au
 *
 */
public class WhitePagesMobileScreenDimensionsStrategy implements
        ScreenDimensionsStrategy {

    private static final Logger LOGGER =
            Logger.getLogger(WhitePagesMobileScreenDimensionsStrategy.class);

    private int mapWidthMarginLarge;
    private int mapHeightMarginLarge;
    private int mapWidthMarginMedium;
    private int mapHeightMarginMedium;

    /**
     * {@inheritDoc}
     */
    public ScreenDimensions createScreenDimensions(
            final MobileContext mobileContext) {
        int widthMargin = getMapWidthMarginMedium();
        int heightMargin = getMapHeightMarginMedium();

        // Should maintain a MAP of entries - Spring wire map into a single
        // variable in this
        // class, and map each entry from app.prop file into the Spring
        // config...

        // if
        // (StringUtils.startsWithIgnoreCase(mobileContext.asAgentDetails().getDeviceName(),
        // "Apple-iPhone")) {
        // RIM-Blackberry-9000 -- 23 pix

        // Nokia-6120
        // Nokia-Series60

        if (mobileContext.asAgentDetails().getImageCategory() == ImageCategory.L) {

            widthMargin = getMapWidthMarginLarge();
            heightMargin = getMapHeightMarginLarge();
        }

        final int mapWidth =
                mobileContext.getDevice().getPixelsX() - widthMargin;
        final int mapHeight =
                mobileContext.getDevice().getPixelsY() - heightMargin;

        LOGGER.debug("Screen dimensions of this device: "
                + mobileContext.asAgentDetails().getScreenWidth() + "px X "
                + mobileContext.asAgentDetails().getScreenHeight()
                + "px. - The image category was: "
                + mobileContext.asAgentDetails().getImageCategory().name());

        LOGGER.debug("Specifying the map size as: " + mapWidth + "px X "
                + mapHeight + "px.");

        return new ScreenDimensions(mapWidth, mapHeight);
    }

    /**
     * @return the mapWidthMarginLarge
     */
    public int getMapWidthMarginLarge() {
        return mapWidthMarginLarge;
    }

    /**
     * @param mapWidthMarginLarge
     *            the mapWidthMarginLarge to set
     */
    public void setMapWidthMarginLarge(final int mapWidthMarginLarge) {
        this.mapWidthMarginLarge = mapWidthMarginLarge;
    }

    /**
     * @return the mapHeightMarginLarge
     */
    public int getMapHeightMarginLarge() {
        return mapHeightMarginLarge;
    }

    /**
     * @param mapHeightMarginLarge
     *            the mapHeightMarginLarge to set
     */
    public void setMapHeightMarginLarge(final int mapHeightMarginLarge) {
        this.mapHeightMarginLarge = mapHeightMarginLarge;
    }

    /**
     * @return the mapWidthMarginMedium
     */
    public int getMapWidthMarginMedium() {
        return mapWidthMarginMedium;
    }

    /**
     * @param mapWidthMarginMedium
     *            the mapWidthMarginMedium to set
     */
    public void setMapWidthMarginMedium(final int mapWidthMarginMedium) {
        this.mapWidthMarginMedium = mapWidthMarginMedium;
    }

    /**
     * @return the mapHeightMarginMedium
     */
    public int getMapHeightMarginMedium() {
        return mapHeightMarginMedium;
    }

    /**
     * @param mapHeightMarginMedium
     *            the mapHeightMarginMedium to set
     */
    public void setMapHeightMarginMedium(final int mapHeightMarginMedium) {
        this.mapHeightMarginMedium = mapHeightMarginMedium;
    }

}

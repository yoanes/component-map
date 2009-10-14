package au.com.sensis.mobile.web.component.map.service;

import java.util.List;

import org.apache.log4j.Logger;

import au.com.sensis.address.Address;
import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.simple.GeocodedAddressImpl;
import au.com.sensis.mobile.web.component.map.model.MapCriteria;
import au.com.sensis.sal.common.UserContext;
import au.com.sensis.wireless.manager.ems.EMSManager;
import au.com.sensis.wireless.manager.ems.MobilesEMSManager;
import au.com.sensis.wireless.manager.ems.MobilesEMSManagerException;
import au.com.sensis.wireless.manager.mapping.LocationMapUrl;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;
import au.com.sensis.wireless.manager.mapping.MobilesIconType;
import au.com.sensis.wireless.manager.mapping.PanZoomDetail;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.web.common.exception.ApplicationRuntimeException;
import au.com.sensis.wireless.web.common.validation.ValidatableUtils;

/**
 * @author Elvis Svonja (Cloned and modified from WPM)
 */
public class LocationManagerImpl
        implements LocationManager {

    private static final Logger LOGGER = Logger.getLogger(LocationManagerImpl.class);

    private EMSManager emsManager;

    private int mapWidthMarginLarge;
    private int mapHeightMarginLarge;
    private int mapWidthMarginMedium;
    private int mapHeightMarginMedium;

//    private EmsLocationUtilityManager emsLocationUtilityManager;

    /**
     * {@inheritDoc}
     */
    public GeocodedAddress resolveGeocodedLocation(final WGS84Point position) {

        try {

            return getEmsManager().reverseGeocode(position);

        } catch (final MobilesEMSManagerException e) {

            LOGGER.error("Problems reverse geocoding: " + position, e);

            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<GeocodedAddress> resolveLocation(final String location) {

        final GeocodedAddressImpl address = new GeocodedAddressImpl();
        address.setSuburb(location);

        final List<GeocodedAddress> resolvedList = getEmsManager().resolveAddress(address,
                address.getState());

        return resolvedList;
    }

//    /**
//     * {@inheritDoc}
//     */
//    public GeocodedAddress resolveSingleAddress(final Address address) {
//
//        return getEmsLocationUtilityManager().findGeocodedAddress(address);
//    }
//
//    /**
//     * {@inheritDoc}
//     */
//    public List<Address> getSurroundingSuburbs(final String suburb, final String postcode) {
//
//        return getEmsLocationUtilityManager().getSurroundingSuburbs(suburb, postcode);
//    }

    /**
     * {@inheritDoc}
     */
    public MapUrl retrieveMapUrlFromEms(final MapCriteria mapCriteria,
            final UserContext userContext) {

        final ScreenDimensions screenDimensions = createScreenDimensions(mapCriteria);

        // This is null initially, but gets set the first time we get the map. It must be kept in
        // the session so that navigation can work (i.e. recalculation of coordinates).
        final MobilesBoundingBox mapBoundingBox = mapCriteria.getMapBoundingBox();
        WGS84Point mapCentreCoordinates = mapCriteria.getCoordinates();

        // This is used to determine the requested action - i.e. zooming, or movement
        final PanZoomDetail panZoomDetail = new PanZoomDetail(mapBoundingBox, mapCentreCoordinates,
                mapCriteria.getDirection(), mapCriteria.getZoom());

        mapCentreCoordinates = panZoomDetail.calculateNewCentre();

        // TODO - You’re going to need this too,
        // <property name="mapUrlTransformerImplChooserMap" ref="mapUrlTransformerImplChooserMap"/>
        // Which is defined in applicationContext.xml. It’s a convoluted way to add the text 2g into
        // a url for charging purposes, see testShouldCreate2GMobilesMap.

        MapUrl mapUrl = null;
        try {

            mapUrl = getEmsManager().getMap(screenDimensions, mapCentreCoordinates,
                    MobilesIconType.CROSS_HAIR, MapLayer.Map, panZoomDetail, userContext);

        } catch (final MobilesEMSManagerException e) {

            mapUrl = new LocationMapUrl();

            LOGGER.error(e);
        }

        return mapUrl;
    }

    /**
     * Constructs the screen dimensions object based on device screen width and height values.
     * Additionally we must subtract a margin constant from both width and height, based on
     * predefined margin values for large or medium device type.
     *
     * @param mapCriteria   contains the search parameters.
     *
     * @return  a new ScreenDimensions object.
     */
    protected ScreenDimensions createScreenDimensions(final MapCriteria mapCriteria) {

        int widthMargin = getMapWidthMarginMedium();
        int heightMargin = getMapHeightMarginMedium();

        // Should maintain a MAP of entries - Spring wire map into a single variable in this
        // class, and map each entry from app.prop file into the Spring config...

        //if (StringUtils.startsWithIgnoreCase(mapCriteria.getDeviceName(), "Apple-iPhone")) {
        //RIM-Blackberry-9000 -- 23 pix

        // Nokia-6120
        // Nokia-Series60


        if (mapCriteria.isLargeDevice()) {

            widthMargin = getMapWidthMarginLarge();
            heightMargin = getMapHeightMarginLarge();
        }

        final int mapWidth = mapCriteria.getScreenWidth() - widthMargin;
        final int mapHeight = mapCriteria.getScreenHeight() - heightMargin;

        LOGGER.debug("Screen dimensions of this device: " + mapCriteria.getScreenWidth() + "px X "
                + mapCriteria.getScreenHeight() + "px. - The large device status was: "
                + mapCriteria.isLargeDevice());

        LOGGER.debug("Specifying the map size as: " + mapWidth + "px X " + mapHeight + "px.");

        return new ScreenDimensions(mapWidth, mapHeight);
    }

    /**
     * {@inheritDoc}
     */
    public void validateState()
            throws ApplicationRuntimeException {

        ValidatableUtils.validateObjectIsNotNull(getEmsManager(), "emsManager");
        ValidatableUtils.validateObjectIsNotNull(getMapWidthMarginLarge(), "mapWidthMarginLarge");
        ValidatableUtils.validateObjectIsNotNull(getMapHeightMarginLarge(), "mapHeightMarginLarge");
        ValidatableUtils.validateObjectIsNotNull(getMapWidthMarginMedium(), "mapWidthMarginMedium");
        ValidatableUtils.validateObjectIsNotNull(getMapHeightMarginMedium(),
                "mapHeightMarginMedium");
//        ValidatableUtils.validateObjectIsNotNull(getEmsLocationUtilityManager(),
//                "emsLocationUtilityManager");
    }

    /**
     * @return  the emsManager.
     */
    protected EMSManager getEmsManager() {

        return emsManager;
    }

    /**
     * @param emsManager    the emsManager to set.
     */
    public void setEmsManager(final EMSManager emsManager) {

        this.emsManager = emsManager;
    }

    /**
     * @return  the mapWidthMarginLarge.
     */
    public int getMapWidthMarginLarge() {

        return mapWidthMarginLarge;
    }

    /**
     * @param mapWidthMarginLarge   the mapWidthMarginLarge to set.
     */
    public void setMapWidthMarginLarge(final int mapWidthMarginLarge) {

        this.mapWidthMarginLarge = mapWidthMarginLarge;
    }

    /**
     * @return  the mapHeightMarginLarge.
     */
    public int getMapHeightMarginLarge() {

        return mapHeightMarginLarge;
    }

    /**
     * @param mapHeightMarginLarge  the mapHeightMarginLarge to set.
     */
    public void setMapHeightMarginLarge(final int mapHeightMarginLarge) {

        this.mapHeightMarginLarge = mapHeightMarginLarge;
    }

    /**
     * @return  the mapWidthMarginMedium.
     */
    public int getMapWidthMarginMedium() {

        return mapWidthMarginMedium;
    }

    /**
     * @param mapWidthMarginMedium  the mapWidthMarginMedium to set.
     */
    public void setMapWidthMarginMedium(final int mapWidthMarginMedium) {

        this.mapWidthMarginMedium = mapWidthMarginMedium;
    }

    /**
     * @return  the mapHeightMarginMedium.
     */
    public int getMapHeightMarginMedium() {

        return mapHeightMarginMedium;
    }

    /**
     * @param mapHeightMarginMedium the mapHeightMarginMedium to set.
     */
    public void setMapHeightMarginMedium(final int mapHeightMarginMedium) {

        this.mapHeightMarginMedium = mapHeightMarginMedium;
    }

//    /**
//     * @return  the emsLocationUtilityManager.
//     */
//    protected EmsLocationUtilityManager getEmsLocationUtilityManager() {
//
//        return emsLocationUtilityManager;
//    }
//
//    /**
//     * @param emsLocationUtilityManager the emsLocationUtilityManager to set.
//     */
//    public void setEmsLocationUtilityManager(
//            final EmsLocationUtilityManager emsLocationUtilityManager) {
//
//        this.emsLocationUtilityManager = emsLocationUtilityManager;
//    }
}

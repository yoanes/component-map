package au.com.sensis.mobile.web.component.map.business;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.mobile.web.component.core.device.DeviceConfigRegistry;
import au.com.sensis.mobile.web.component.map.device.generated.DeviceConfigType;
import au.com.sensis.mobile.web.component.map.model.MapUrlHolder;
import au.com.sensis.mobile.web.component.map.model.MapUrlHolderImpl;
import au.com.sensis.sal.common.UserContext;
import au.com.sensis.wireless.manager.ems.EMSManager;
import au.com.sensis.wireless.manager.mapping.IconDescriptor;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesIconType;
import au.com.sensis.wireless.manager.mapping.PanZoomDetail;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.manager.mapping.UserMapInteraction;
import au.com.sensis.wireless.web.common.exception.ApplicationRuntimeException;
import au.com.sensis.wireless.web.common.validation.Validatable;
import au.com.sensis.wireless.web.common.validation.ValidatableUtils;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapDelegateImpl implements Validatable, MapDelegate {

    /** Not final so that we can inject a mock for unit testing. */
    private static Logger logger = Logger.getLogger(MapDelegateImpl.class);

    private EMSManager emsManager;

    private ScreenDimensionsStrategy screenDimensionsStrategy;

    private DeviceConfigRegistry<DeviceConfigType> deviceConfigRegistry;

    private int minZoom;
    private int maxZoom;

    private float poiMapRadiusMultiplier;

    /**
     * Strategy interface for determining the {@link ScreenDimensions} for the
     * current user's device. This interface arose due to WPM, WM and YM having
     * different existing rules for calculating the screen dimensions. In
     * future, the rules may be unified. For now, though, we shield the
     * difference behind an interface.
     */
    public interface ScreenDimensionsStrategy {
        /**
         * @param mobileContext
         *            Context of the user that the map is being retrieved for.
         *
         * @return {@link ScreenDimensions} for the current user's device. Map
         *         not be null.
         */
        ScreenDimensions createScreenDimensions(MobileContext mobileContext);
    }

    /**
     * {@inheritDoc}
     */
    public void validateState() throws ApplicationRuntimeException {
        ValidatableUtils.validateObjectIsNotNull(getEmsManager(), "emsManager");
        ValidatableUtils.validateObjectIsNotNull(getScreenDimensionsStrategy(),
                "screenDimensionsStrategy");
        // TODO: validate new deviceConfigRegistry
    }

    /**
     * {@inheritDoc}
     */
    public MapUrlHolder retrieveInitialMap(final WGS84Point mapCentre,
            final int zoomLevel, final MapLayer mapLayer, final MobilesIconType centreIconType,
            final MobileContext mobileContext) {

        final int emsZoomLevel = getEmsManager().getEmsZoomLevel(zoomLevel);
        if (deviceNeedsServerSideMapGenerated(mobileContext)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Will retrieve map for device: "
                        + mobileContext.getDevice().getName());
            }

            // Construct PanZoomDetail with a null bounding box since this is
            // the initial map retrieval.
            final PanZoomDetail panZoomDetail =
                    new PanZoomDetail(null, mapCentre,
                            UserMapInteraction.NO_ACTION, zoomLevel);
            final MapUrl mapUrl =
                    getEmsManager().getMap(
                            getScreenDimensionsStrategy()
                                    .createScreenDimensions(mobileContext),
                            mapCentre, centreIconType,
                            mapLayer, panZoomDetail,
                            mobileContext.asUserContext());

            if (logger.isDebugEnabled()) {
                logger.debug("map url is " + mapUrl.getImageUrl());
            }

            return MapUrlHolderImpl.createMapRetrievedInstance(mapCentre,
                    mapLayer, mapUrl, emsZoomLevel,
                    isZoomLevelMin(mapUrl.getZoom()),
                    isZoomLevelMax(mapUrl.getZoom()));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve map for device: "
                        + mobileContext.getDevice().getName());
            }

            return MapUrlHolderImpl.createMapRetrievalDeferrendInstance(mapCentre,
                    mapLayer, zoomLevel, emsZoomLevel,
                    isZoomLevelMin(zoomLevel), isZoomLevelMax(zoomLevel));
        }

    }

    /**
     * @return True if the device needs the server to generate the map. Some
     *         clients are able to retrieve the map themselves by talking to EMS
     *         directly and then displaying a really funky AJAX enabled map.
     */
    private boolean deviceNeedsServerSideMapGenerated(
            final MobileContext mobileContext) {
        final DeviceConfigType deviceConfigType =
                getDeviceConfigRegistry().getDeviceConfig(
                        mobileContext.getDevice());
        return deviceConfigType.isGenerateServerSideMap();
    }

    private boolean isZoomLevelMax(final int zoomLevel) {
        return getMaxZoom() == zoomLevel;
    }

    private boolean isZoomLevelMin(final int zoomLevel) {
        return getMinZoom() == zoomLevel;
    }

    /**
     * {@inheritDoc}
     */
    public MapUrlHolder manipulateMap(final WGS84Point originalMapCentrePoint,
            final MapUrl existingMapUrl, final MapLayer mapLayer,
            final MobilesIconType originalCentreIconType,
            final Action mapManipulationAction, final MobileContext mobileContext) {

        final PanZoomDetail panZoomDetail =
                createMapManipulationPanZoomDetail(existingMapUrl,
                        mapManipulationAction);

        final MapUrl mapUrl = getEmsManager().getMap(
                getScreenDimensionsStrategy()
                        .createScreenDimensions(mobileContext),
                        originalMapCentrePoint, originalCentreIconType,
                mapLayer, panZoomDetail,
                mobileContext.asUserContext());
        final int emsZoomLevel = getEmsManager().getEmsZoomLevel(
                panZoomDetail.getZoom());
        return MapUrlHolderImpl.createMapRetrievedInstance(
                originalMapCentrePoint, mapLayer, mapUrl,
                emsZoomLevel, isZoomLevelMin(mapUrl.getZoom()),
                isZoomLevelMax(mapUrl.getZoom()));
    }

    private UserMapInteraction transformPanActionToUserMapInteraction(
            final Action mapManipulationAction) {
        if (Action.MOVE_EAST.equals(mapManipulationAction)) {
            return UserMapInteraction.EAST;
        } else if (Action.MOVE_NORTH.equals(mapManipulationAction)) {
            return UserMapInteraction.NORTH;
        } else if (Action.MOVE_SOUTH.equals(mapManipulationAction)) {
            return UserMapInteraction.SOUTH;
        } else if (Action.MOVE_WEST.equals(mapManipulationAction)) {
            return UserMapInteraction.WEST;
        } else {
            throw new IllegalStateException(
                    "mapManipulationAction argument should have corresponded to "
                            + "a pan action but it was not: '"
                            + mapManipulationAction + "'");
        }
    }

    private int calculateNewZoomLevel(
            final int zoomLevel,
            final Action mapManipulationAction) {

        int newZoomValue;
        if (Action.ZOOM_IN.equals(mapManipulationAction)) {
            newZoomValue = zoomLevel - 1;
        } else {
            newZoomValue = zoomLevel + 1;
        }

        if ((newZoomValue >= getMinZoom()) && (newZoomValue <= getMaxZoom())) {

            return newZoomValue;

        } else {

            if (logger.isEnabledFor(Level.WARN)) {
                logger.warn("Attempted to apply zoom " + mapManipulationAction
                        + " to current level of " + zoomLevel
                        + ". Valid zoom range is " + getMinZoom() + " to "
                        + getMaxZoom() + " only.");
            }

            return zoomLevel;
        }

    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation originally based on the PoiSearchResultsMapMaker class
     * from WhereisMobile.
     * </p>
     */
    public MapUrlHolder getInitialPoiMap(final WGS84Point mapCentre,
            final MapLayer mapLayer, final List<IconDescriptor> poiIcons,
            final int mobilesZoomThreshold, final MobileContext mobileContext) {

        final ScreenDimensions screenDimensions =
            getScreenDimensionsStrategy().createScreenDimensions(
                    mobileContext);
        if (deviceNeedsServerSideMapGenerated(mobileContext)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Will retrieve map for device: "
                        + mobileContext.getDevice().getName());
            }
            MapUrl mapUrl = null;

            final UserContext userContext = mobileContext.asUserContext();
            mapUrl =
                    emsManager.getPoiMap(screenDimensions, mapCentre, mapLayer,
                            poiIcons, getPoiMapRadiusMultiplier(),
                            mobilesZoomThreshold, userContext);

            if (logger.isDebugEnabled()) {
                logger.debug("map url is " + mapUrl.getImageUrl());
            }

            final int emsZoomLevel =
                    getEmsManager().getEmsZoomLevel(mapUrl.getZoom());
            return MapUrlHolderImpl.createMapRetrievedInstance(mapCentre, mapLayer,
                    mapUrl, emsZoomLevel, isZoomLevelMin(mapUrl.getZoom()),
                    isZoomLevelMax(mapUrl.getZoom()));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve map for device: "
                        + mobileContext.getDevice().getName());
            }

            final int zoomLevel = getEmsManager().getPoiMapZoom(screenDimensions, mapCentre,
                    poiIcons, getPoiMapRadiusMultiplier(), mobilesZoomThreshold);
            final int emsZoomLevel = getEmsManager().getEmsZoomLevel(zoomLevel);
            return MapUrlHolderImpl.createMapRetrievalDeferrendInstance(mapCentre,
                    mapLayer, zoomLevel, emsZoomLevel,
                    isZoomLevelMin(zoomLevel), isZoomLevelMax(zoomLevel));
        }

    }

    /**
     * {@inheritDoc}
     */
    public MapUrlHolder manipulatePoiMap(final WGS84Point originalMapCentrePoint,
            final MapUrl existingMapUrl, final MapLayer mapLayer,
            final List<IconDescriptor> poiIcons,
            final Action mapManipulationAction,
            final MobileContext mobileContext) {

        final PanZoomDetail panZoomDetail =
                createMapManipulationPanZoomDetail(existingMapUrl,
                        mapManipulationAction);

        final MapUrl mapUrl = getEmsManager().manipulatePoiMap(
                getScreenDimensionsStrategy()
                        .createScreenDimensions(mobileContext),
                        originalMapCentrePoint, poiIcons,
                mapLayer, panZoomDetail,
                mobileContext.asUserContext());
        final int emsZoomLevel = getEmsManager().getEmsZoomLevel(
                panZoomDetail.getZoom());
        return MapUrlHolderImpl.createMapRetrievedInstance(
                originalMapCentrePoint, mapLayer, mapUrl,
                emsZoomLevel, isZoomLevelMin(mapUrl.getZoom()),
                isZoomLevelMax(mapUrl.getZoom()));

    }

    /**
     * @param existingMapUrl
     * @param mapManipulationAction
     * @return
     */
    private PanZoomDetail createMapManipulationPanZoomDetail(
            final MapUrl existingMapUrl, final Action mapManipulationAction) {
        PanZoomDetail panZoomDetail;
        if (mapManipulationAction.isPanAction()) {
            panZoomDetail = new PanZoomDetail(existingMapUrl.getBoundingBox(),
                    existingMapUrl.getMapCentre(),
                    transformPanActionToUserMapInteraction(mapManipulationAction),
                    existingMapUrl.getZoom());

        } else if (mapManipulationAction.isZoomAction()) {
            panZoomDetail = new PanZoomDetail(existingMapUrl.getBoundingBox(),
                    existingMapUrl.getMapCentre(), UserMapInteraction.ZOOM,
                    calculateNewZoomLevel(existingMapUrl.getZoom(),
                            mapManipulationAction));
        } else {
            panZoomDetail = new PanZoomDetail(existingMapUrl.getBoundingBox(),
                    existingMapUrl.getMapCentre(),
                    UserMapInteraction.NO_ACTION, existingMapUrl.getZoom());
        }
        return panZoomDetail;
    }


    /**
     * @return the minZoom
     */
    public int getMinZoom() {
        return minZoom;
    }

    /**
     * @param minZoom
     *            the minZoom to set
     */
    public void setMinZoom(final int minZoom) {
        this.minZoom = minZoom;
    }

    /**
     * @return the maxZoom
     */
    public int getMaxZoom() {
        return maxZoom;
    }

    /**
     * @param maxZoom
     *            the maxZoom to set
     */
    public void setMaxZoom(final int maxZoom) {
        this.maxZoom = maxZoom;
    }

    /**
     * @return the emsManager
     */
    public EMSManager getEmsManager() {
        return emsManager;
    }

    /**
     * @param emsManager the emsManager to set
     */
    public void setEmsManager(final EMSManager emsManager) {
        this.emsManager = emsManager;
    }

    /**
     * @return the screenDimensionsStrategy
     */
    public ScreenDimensionsStrategy getScreenDimensionsStrategy() {
        return screenDimensionsStrategy;
    }

    /**
     * @param screenDimensionsStrategy the screenDimensionsStrategy to set
     */
    public void setScreenDimensionsStrategy(
            final ScreenDimensionsStrategy screenDimensionsStrategy) {
        this.screenDimensionsStrategy = screenDimensionsStrategy;
    }

    /**
     * @return the deviceConfigRegistry
     */
    public DeviceConfigRegistry<DeviceConfigType> getDeviceConfigRegistry() {
        return deviceConfigRegistry;
    }

    /**
     * @param deviceConfigRegistry the deviceConfigRegistry to set
     */
    public void setDeviceConfigRegistry(
            final DeviceConfigRegistry<DeviceConfigType> deviceConfigRegistry) {
        this.deviceConfigRegistry = deviceConfigRegistry;
    }

    /**
     * @return the poiMapRadiusMultiplier
     */
    public float getPoiMapRadiusMultiplier() {
        return poiMapRadiusMultiplier;
    }

    /**
     * @param poiMapRadiusMultiplier the poiMapRadiusMultiplier to set
     */
    public void setPoiMapRadiusMultiplier(final float poiMapRadiusMultiplier) {
        this.poiMapRadiusMultiplier = poiMapRadiusMultiplier;
    }


}

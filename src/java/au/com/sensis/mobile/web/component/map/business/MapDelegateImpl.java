package au.com.sensis.mobile.web.component.map.business;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.mobile.web.component.core.sdpcommon.web.framework.MobileContext;
import au.com.sensis.mobile.web.component.map.model.MapCriteria;
import au.com.sensis.mobile.web.component.map.model.MapResult;
import au.com.sensis.mobile.web.component.map.model.MapState;
import au.com.sensis.mobile.web.component.map.service.LocationManager;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.UserMapInteraction;
import au.com.sensis.wireless.web.common.exception.ApplicationRuntimeException;
import au.com.sensis.wireless.web.common.validation.Validatable;

/**
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapDelegateImpl implements Validatable, MapDelegate {

    /** Not final so that we can inject a mock for unit testing. */
    private static Logger logger = Logger.getLogger(MapDelegateImpl.class);

    private LocationManager locationManager;

    private int minZoom;
    private int maxZoom;

    public void validateState() throws ApplicationRuntimeException {
        // TODO Auto-generated method stub

    }

    // TODO: should just return a raw MapUrl from the MapDelegateImpl like Heather
    // does.
    // This is an sdpcommon interface that theoretically contains everything
    // that we need.
    // However, can't be stuffed trying to do this upgrade now because MapUrl
    // from 1.0-050 doesn't contain getZoom but upgrading to 1.0-057 requires
    // more work than I can muster right now.
    /* (non-Javadoc)
     * @see au.com.sensis.mobile.web.component.map.business.MapDelegate#retrieveInitialMap(au.com.sensis.address.GeocodedAddress, int, au.com.sensis.mobile.web.component.core.sdpcommon.web.framework.MobileContext)
     */
    public MapResult retrieveInitialMap(final GeocodedAddress geocodedAddress,
            final int zoomLevel, final MobileContext mobileContext) {
        if (mobileContext.getDevice().isWeb2Supported()) {
            final MapState mapState = new MapState(geocodedAddress.getCoordinates(),
                    null, zoomLevel);
            return MapResult.createMapRetrievalClientResponsible(mapState);
        } else {
            final MapCriteria mapCriteria = new MapCriteria();
            mapCriteria.setCoordinates(geocodedAddress.getCoordinates());
            mapCriteria.setDirection(UserMapInteraction.NO_ACTION);

            // TODO: need to range check the zoom level.
            mapCriteria.setZoom(zoomLevel);

            packMobileContextIntoMapCriteria(mobileContext, mapCriteria);

            // bounding box not known yet.
            mapCriteria.setMapBoundingBox(null);
            return retrieveMapFromLocationManager(mapCriteria, mobileContext);
        }

    }

    // TODO: should just return a raw MapUrl from the MapDelegateImpl like Heather does.
    // This is an sdpcommon interface that theoretically contains everything that we need.
    // However, can't be stuffed trying to do this upgrade now because MapUrl
    // from 1.0-050 doesn't contain getZoom but upgrading to 1.0-057 requires
    // more work than I can muster right now. I do prefer my class names and the
    // separation of the MapState from the imageUrl but oh well.
    /* (non-Javadoc)
     * @see au.com.sensis.mobile.web.component.map.business.MapDelegate#manipulateMap(au.com.sensis.mobile.web.component.map.model.MapState, au.com.sensis.mobile.web.component.map.business.MapDelegateImpl.Action, au.com.sensis.mobile.web.component.core.sdpcommon.web.framework.MobileContext)
     */
    public MapResult manipulateMap(final MapState currentMapState,
            final Action mapManipulationAction, final MobileContext mobileContext) {
        final MapCriteria mapCriteria = new MapCriteria();
        mapCriteria.setCoordinates(currentMapState.getCoordinates());

        if (mapManipulationAction.isPanAction()) {
            mapCriteria
                    .setDirection(transformActionToMapCriteriaDirection(mapManipulationAction));
            mapCriteria.setMapBoundingBox(currentMapState.getMapBoundingBox());
            mapCriteria.setZoom(currentMapState.getZoomLevel());
        } else if (mapManipulationAction.isZoomAction()) {
            mapCriteria.setDirection(UserMapInteraction.ZOOM);
            mapCriteria.setZoom(calculateNewZoomLevel(currentMapState
                    .getZoomLevel(), mapManipulationAction));
        } else {
            // Just retrieve the original map again.
            mapCriteria.setDirection(UserMapInteraction.NO_ACTION);
            mapCriteria.setZoom(currentMapState.getZoomLevel());
        }

        packMobileContextIntoMapCriteria(mobileContext, mapCriteria);

        return retrieveMapFromLocationManager(mapCriteria, mobileContext);
    }

    /**
     * @param mobileContext
     * @param mapCriteria
     * @return
     */
    // TODO: I suspect Heather has a better implementation of this in WhereisMobile
    // that doesn't use the WPM MapCriteria.
    private MapResult retrieveMapFromLocationManager(final MapCriteria mapCriteria,
            final MobileContext mobileContext) {
        final MapUrl mapUrl =
                getLocationManager().retrieveMapUrlFromEms(mapCriteria,
                        mobileContext.asUserContext());

        final MapState mapState =
                new MapState(mapUrl.getMapCentre(), mapUrl.getBoundingBox(),
                        mapCriteria.getZoom());
        return MapResult.createMapRetrievedResult(mapState, mapUrl.getImageUrl());
    }

    /**
     * @param mobileContext
     * @param mapCriteria
     */
    private void packMobileContextIntoMapCriteria(final MobileContext mobileContext,
            final MapCriteria mapCriteria) {
        mapCriteria.setScreenWidth(mobileContext.getDevice().getPixelsX());
        mapCriteria.setScreenHeight(mobileContext.getDevice().getPixelsY());

        // TODO: mobileContext.getDevice().isLargeDevice() is not yet available
        // in sdpcommon.
        // mapCriteria.setLargeDevice(mobileContext.getDevice().isLargeDevice());
    }

    private UserMapInteraction transformActionToMapCriteriaDirection(
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
                    "mapManipulationAction argument should have corresponded to a pan action but it was not: '"
                            + mapManipulationAction + "'");
        }
    }

    private int calculateNewZoomLevel(
            final int zoomLevel,
            final au.com.sensis.mobile.web.component.map.business.MapDelegateImpl.Action mapManipulationAction) {

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
     * @return the locationManager
     */
    public LocationManager getLocationManager() {
        return locationManager;
    }

    /**
     * @param locationManager
     *            the locationManager to set
     */
    public void setLocationManager(final LocationManager locationManager) {
        this.locationManager = locationManager;
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

}
package au.com.sensis.mobile.web.component.map.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.mobile.web.component.core.device.DeviceConfigRegistry;
import au.com.sensis.mobile.web.component.map.device.generated.DeviceConfig;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.model.MapImpl;
import au.com.sensis.sal.common.UserContext;
import au.com.sensis.wireless.manager.directions.JourneyDescriptor;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.RouteHandle;
import au.com.sensis.wireless.manager.directions.RoutingOption;
import au.com.sensis.wireless.manager.ems.EMSManager;
import au.com.sensis.wireless.manager.mapping.IconDescriptor;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;
import au.com.sensis.wireless.manager.mapping.MobilesIconType;
import au.com.sensis.wireless.manager.mapping.PanZoomDetail;
import au.com.sensis.wireless.manager.mapping.ResolvedIcon;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.manager.mapping.UserMapInteraction;
import au.com.sensis.wireless.web.common.exception.ApplicationRuntimeException;
import au.com.sensis.wireless.web.common.validation.Validatable;
import au.com.sensis.wireless.web.common.validation.ValidatableUtils;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Default {@link MapDelegate} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapDelegateImpl implements Validatable, MapDelegate {

    /** Not final so that we can inject a mock for unit testing. */
    private static Logger logger = Logger.getLogger(MapDelegateImpl.class);

    private EMSManager emsManager;

    private ScreenDimensionsStrategy screenDimensionsStrategy;

    private DeviceConfigRegistry deviceConfigRegistry;

    private float poiMapRadiusMultiplier;

    /**
     * Strategy interface for determining the {@link ScreenDimensions} for the
     * current user's device. This interface arose due to WPM, WM and YM having
     * different existing rules for calculating the screen dimensions.
     * <p>
     * {@link DefaultScreenDimensionsStrategy} is provided as a
     * default implementation. Applications can substitute their own implementation
     * by providing a Spring context file that overrides the default Spring bean
     * but it is expected that they should not need to.
     * </p>
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
    public Map getInitialMap(final WGS84Point mapCentre,
            final int zoomLevel, final MapLayer mapLayer, final MobilesIconType centreIconType,
            final MobileContext mobileContext) {

        final int emsZoomLevel = getEmsManager().getEmsZoomLevel(zoomLevel);
        final List<ResolvedIcon> resolvedIcons = getEmsManager().resolvePoiIcons(mapCentre,
                centreIconType,
                new ArrayList<IconDescriptor>(),
                getScreenDimensionsStrategy().createScreenDimensions(mobileContext));
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

            return MapImpl.createMapRetrievedInstance(mapCentre,
                    mapLayer, mapUrl, resolvedIcons, emsZoomLevel,
                    isZoomLevelMin(mapUrl.getZoom()),
                    isZoomLevelMax(mapUrl.getZoom()));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve map for device: "
                        + mobileContext.getDevice().getName());
            }

            return MapImpl.createMapRetrievalDeferrendInstance(mapCentre,
                    mapCentre, mapLayer, resolvedIcons, zoomLevel, emsZoomLevel,
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
        final DeviceConfig deviceConfig = (DeviceConfig)
                getDeviceConfigRegistry().getDeviceConfig(
                        mobileContext.getDevice());
        return deviceConfig.isGenerateServerSideMap();
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
    public Map manipulateMap(final WGS84Point originalMapCentrePoint,
            final MapUrl existingMapUrl, final MapLayer existingMapLayer,
            final MobilesIconType originalCentreIconType,
            final Action mapManipulationAction, final MobileContext mobileContext) {

        final List<ResolvedIcon> resolvedIcons = getEmsManager().resolvePoiIcons(
                originalMapCentrePoint, originalCentreIconType,
                new ArrayList<IconDescriptor>(),
                getScreenDimensionsStrategy().createScreenDimensions(mobileContext));

        final PanZoomDetail panZoomDetail =
            createMapManipulationPanZoomDetail(existingMapUrl,
                    mapManipulationAction);
        final MapLayer newMapLayer = createMapLayerAfterAction(
                existingMapLayer, mapManipulationAction);
        final int emsZoomLevel = getEmsManager().getEmsZoomLevel(
                panZoomDetail.getZoom());

        if (deviceNeedsServerSideMapGenerated(mobileContext)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Will retrieve manipulated map for device: "
                        + mobileContext.getDevice().getName());
            }

            final MapUrl mapUrl = getEmsManager().getMap(
                    getScreenDimensionsStrategy()
                            .createScreenDimensions(mobileContext),
                            originalMapCentrePoint, originalCentreIconType,
                    newMapLayer, panZoomDetail,
                    mobileContext.asUserContext());

            return MapImpl.createMapRetrievedInstance(
                    originalMapCentrePoint, newMapLayer, mapUrl, resolvedIcons,
                    emsZoomLevel, isZoomLevelMin(mapUrl.getZoom()),
                    isZoomLevelMax(mapUrl.getZoom()));
        } else {
            // This method can be safely called for non-server side
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve manipulated map for device: "
                        + mobileContext.getDevice().getName());
            }

            return MapImpl.createMapRetrievalDeferrendInstance(originalMapCentrePoint,
                    panZoomDetail.calculateNewCentre(), newMapLayer, resolvedIcons,
                    panZoomDetail.getZoom(), emsZoomLevel,
                    isZoomLevelMin(panZoomDetail.getZoom()),
                    isZoomLevelMax(panZoomDetail.getZoom()));

        }
    }

    private MapLayer createMapLayerAfterAction(final MapLayer existingMapLayer,
            final Action mapManipulationAction) {
        if (mapManipulationAction.isViewAction()) {
            if (Action.MAP_VIEW.equals(mapManipulationAction)) {
                return MapLayer.Map;
            } else if (Action.PHOTO_VIEW.equals(mapManipulationAction)) {
                return MapLayer.Photo;
            } else if (Action.HYBRID_VIEW.equals(mapManipulationAction)) {
                return MapLayer.PhotoWithStreets;
            } else {
                if (logger.isEnabledFor(Level.ERROR)) {
                    logger.error("Action unsupported: '" + mapManipulationAction
                            + ". Looks like your code has a bug !!! Will assume MapLayer.Map.");
                }
                return MapLayer.Map;
            }
        } else {
            return existingMapLayer;
        }
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
    // WMT PA modified
    public Map getInitialPoiMap(final WGS84Point mapCentre,
            final MapLayer mapLayer, final List<IconDescriptor> poiIcons,final IconDescriptor paidAdIcon,
            final int mobilesZoomThreshold, final MobileContext mobileContext) {

        final ScreenDimensions screenDimensions =
            getScreenDimensionsStrategy().createScreenDimensions(
                    mobileContext);
        final List<ResolvedIcon> resolvedIcons = getEmsManager().resolvePoiIcons(mapCentre,
                MobilesIconType.CROSS_HAIR, poiIcons, screenDimensions);
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
            return MapImpl.createMapRetrievedInstance(mapCentre, mapLayer,
                    mapUrl, resolvedIcons, emsZoomLevel, isZoomLevelMin(mapUrl.getZoom()),
                    isZoomLevelMax(mapUrl.getZoom()));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve map for device: "
                        + mobileContext.getDevice().getName());
            }

            final MobilesBoundingBox mobilesBoundingBox
                = getEmsManager().getPoiMapBoundingBox(mapCentre, poiIcons,
                        getPoiMapRadiusMultiplier());
            return MapImpl.createBoundingBoxMapRetrievalDeferredInstance(mapCentre, mapCentre,
                    mapLayer, resolvedIcons, mobilesBoundingBox,
                    getEmsManager().getEmsZoomLevel(mobilesZoomThreshold));
        }

    }

    /**
     * {@inheritDoc}
     */
    // WMT PA modified
    public Map manipulatePoiMap(final WGS84Point originalMapCentrePoint,
            final MapUrl existingMapUrl, final MapLayer existingMapLayer,
            final List<IconDescriptor> poiIcons,final IconDescriptor paidAdIcon, final int mobilesZoomThreshold,
            final Action mapManipulationAction,
            final MobileContext mobileContext) {

        final ScreenDimensions screenDimensions =
            getScreenDimensionsStrategy().createScreenDimensions(
                    mobileContext);
        final List<ResolvedIcon> resolvedIcons =
            getEmsManager().resolvePoiIcons(originalMapCentrePoint,
                    MobilesIconType.CROSS_HAIR, poiIcons, screenDimensions);

        final MapLayer newMapLayer =
                createMapLayerAfterAction(existingMapLayer,
                        mapManipulationAction);

        if (deviceNeedsServerSideMapGenerated(mobileContext)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Will retrieve manipulated POI map for device: "
                        + mobileContext.getDevice().getName());
            }
            final PanZoomDetail panZoomDetail =
                createMapManipulationPanZoomDetail(existingMapUrl,
                        mapManipulationAction);
            final int emsZoomLevel =
                getEmsManager().getEmsZoomLevel(panZoomDetail.getZoom());

            final MapUrl mapUrl =
                    getEmsManager().manipulatePoiMap(screenDimensions,
                            originalMapCentrePoint, poiIcons, newMapLayer,
                            panZoomDetail, mobileContext.asUserContext());

            return MapImpl.createMapRetrievedInstance(originalMapCentrePoint,
                    newMapLayer, mapUrl, resolvedIcons, emsZoomLevel,
                    isZoomLevelMin(mapUrl.getZoom()), isZoomLevelMax(mapUrl
                            .getZoom()));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve manipulated POI map for device: "
                        + mobileContext.getDevice().getName());
            }

            // TODO: The implementation of getInitialPoiMap now only calculates a bounding box,
            // not a zoom level. The client side JavaScript is responsible for calculating the zoom
            // level based on the mobilesZoomThreshold. There is no way for the server side to
            // calculate the zoom because it doesn't know the dimensions of the hi end map.
            // Therefore, this manipulatePoiMap has no choice but to generate the map in the same
            // way as getInitialPoiMap. Remember to change this in the event that we allow the
            // webapp clients to be made aware of pan and zoom changes for hi end maps.
            final MobilesBoundingBox mobilesBoundingBox
                = getEmsManager().getPoiMapBoundingBox(originalMapCentrePoint, poiIcons,
                    getPoiMapRadiusMultiplier());
            return MapImpl.createBoundingBoxMapRetrievalDeferredInstance(originalMapCentrePoint,
                    originalMapCentrePoint, newMapLayer, resolvedIcons, mobilesBoundingBox,
                    getEmsManager().getEmsZoomLevel(mobilesZoomThreshold));


        }
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
        return getEmsManager().getMinZoom().intValue();
    }

    /**
     * @return the maxZoom
     */
    public int getMaxZoom() {
        return getEmsManager().getMaxZoom().intValue();
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
    public DeviceConfigRegistry getDeviceConfigRegistry() {
        return deviceConfigRegistry;
    }

    /**
     * @param deviceConfigRegistry the deviceConfigRegistry to set
     */
    public void setDeviceConfigRegistry(
            final DeviceConfigRegistry deviceConfigRegistry) {
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

    /**
     * {@inheritDoc}
     */
    public Map getInitialRouteMap(final JourneyWaypoints waypoints,
            final RoutingOption routingOption, final MapLayer mapLayer,
            final MobileContext mobileContext) {

        if (deviceNeedsServerSideMapGenerated(mobileContext)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Will retrieve route map for device: "
                        + mobileContext.getDevice().getName());
            }

            final JourneyDescriptor journeyDescriptor =
                    getEmsManager().getJourneyDescriptor(
                            getScreenDimensionsStrategy()
                                    .createScreenDimensions(mobileContext),
                            waypoints, routingOption, mapLayer,
                            mobileContext.asUserContext());

            if (logger.isDebugEnabled()) {
                logger.debug("route map url is "
                        + journeyDescriptor.getMap().getImageUrl());
            }

            final int zoom = journeyDescriptor.getMap().getZoom();

            final List<ResolvedIcon> resolvedIcons =
                    resolveRouteWaypointIcons(mobileContext, journeyDescriptor);

            return MapImpl.createRouteMapRetrievedInstance(waypoints,
                    journeyDescriptor,
                    mapLayer, resolvedIcons, getEmsManager().getEmsZoomLevel(zoom),
                    isZoomLevelMin(zoom), isZoomLevelMax(zoom));

        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve route map for device: "
                        + mobileContext.getDevice().getName());
            }

            final JourneyDescriptor journeyDescriptor
                = getEmsManager().getJourneyDescriptorWithoutMapImageUrl(
                    getScreenDimensionsStrategy().createScreenDimensions(mobileContext), waypoints,
                    routingOption, mapLayer, mobileContext.asUserContext());

            final List<ResolvedIcon> resolvedIcons =
                    resolveRouteWaypointIcons(mobileContext, journeyDescriptor);

            return MapImpl.createRouteMapRetrievalDeferredInstance(waypoints,
                    journeyDescriptor, mapLayer, resolvedIcons);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Map manipulateRouteMap(final RouteHandle routeHandle,
            final JourneyWaypoints waypoints,
            final RoutingOption routingOption, final MapUrl existingMapUrl,
            final MapLayer existingMapLayer,
            final Action mapManipulationAction,
            final MobileContext mobileContext) {

        final PanZoomDetail panZoomDetail =
                createMapManipulationPanZoomDetail(existingMapUrl,
                        mapManipulationAction);

        final MapLayer newMapLayer =
                createMapLayerAfterAction(existingMapLayer,
                        mapManipulationAction);

        if (deviceNeedsServerSideMapGenerated(mobileContext)) {
            if (logger.isDebugEnabled()) {
                logger.debug("Will retrieve manipulated route map for device: "
                        + mobileContext.getDevice().getName());
            }

            final JourneyDescriptor journeyDescriptor =
                    getEmsManager().updateJourneyDescriptorMapFromRouteHandle(
                            routeHandle,
                            waypoints,
                            routingOption,
                            createMapLayerAfterAction(existingMapLayer,
                                    mapManipulationAction),
                            getScreenDimensionsStrategy()
                                    .createScreenDimensions(mobileContext),
                            panZoomDetail, mobileContext.asUserContext());
            final int emsZoomLevel =
                    getEmsManager().getEmsZoomLevel(panZoomDetail.getZoom());

            final List<ResolvedIcon> resolvedIcons =
                    resolveRouteWaypointIcons(mobileContext, journeyDescriptor);

            return MapImpl.createRouteMapRetrievedInstance(waypoints,
                    journeyDescriptor, newMapLayer, resolvedIcons,
                    emsZoomLevel, isZoomLevelMin(journeyDescriptor.getMap()
                            .getZoom()), isZoomLevelMax(journeyDescriptor
                            .getMap().getZoom()));

        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve manipulated route map for device: "
                        + mobileContext.getDevice().getName());
            }

            final JourneyDescriptor journeyDescriptor =
                    getEmsManager()
                            .updateJourneyDescriptorMapFromRouteHandleWithoutMapImageUrl(
                                    routeHandle,
                                    waypoints,
                                    routingOption,
                                    newMapLayer,
                                    getScreenDimensionsStrategy()
                                            .createScreenDimensions(
                                                    mobileContext),
                                    panZoomDetail,
                                    mobileContext.asUserContext());

            final List<ResolvedIcon> resolvedIcons =
                    resolveRouteWaypointIcons(mobileContext, journeyDescriptor);

            return MapImpl.createRouteMapRetrievalDeferredInstance(waypoints,
                    journeyDescriptor, newMapLayer, resolvedIcons);
        }

    }

    /**
     * {@inheritDoc}
     */
    public Map getInitialRouteLegStepMap(final RouteHandle routeHandle,
            final JourneyWaypoints waypoints, final RoutingOption routingOption,
            final WGS84Point legCentre, final int zoomLevel,
            final MapLayer mapLayer, final MobileContext mobileContext) {

        final int emsZoomLevel = getEmsManager().getEmsZoomLevel(zoomLevel);

        // Construct PanZoomDetail with a null bounding box since this is
        // the initial map retrieval.
        final PanZoomDetail panZoomDetail =
            new PanZoomDetail(null, legCentre,
                    UserMapInteraction.NO_ACTION, zoomLevel);

        if (deviceNeedsServerSideMapGenerated(mobileContext)) {

            if (logger.isDebugEnabled()) {
                logger.debug("Will retrieve route leg step map for device: "
                        + mobileContext.getDevice().getName());
            }


            final JourneyDescriptor journeyDescriptor = getEmsManager()
                .updateJourneyDescriptorMapFromRouteHandle(
                    routeHandle, waypoints, routingOption, mapLayer,
                    getScreenDimensionsStrategy().createScreenDimensions(mobileContext),
                    panZoomDetail, mobileContext.asUserContext());

            final List<ResolvedIcon> resolvedIcons =
                    resolveRouteWaypointIcons(mobileContext, journeyDescriptor);

            return MapImpl.createRouteMapRetrievedInstance(waypoints,
                    journeyDescriptor,
                    mapLayer, resolvedIcons, emsZoomLevel,
                    isZoomLevelMin(zoomLevel), isZoomLevelMax(zoomLevel));
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Will NOT retrieve route leg step map for device: "
                        + mobileContext.getDevice().getName());
            }

            final JourneyDescriptor journeyDescriptor
                = getEmsManager().updateJourneyDescriptorMapFromRouteHandleWithoutMapImageUrl(
                        routeHandle, waypoints, routingOption, mapLayer,
                        getScreenDimensionsStrategy().createScreenDimensions(mobileContext),
                        panZoomDetail, mobileContext.asUserContext());

            final List<ResolvedIcon> resolvedIcons =
                    resolveRouteWaypointIcons(mobileContext, journeyDescriptor);

            return MapImpl.createRouteMapRetrievalDeferredInstance(waypoints,
                    journeyDescriptor, mapLayer, resolvedIcons,
                    emsZoomLevel,
                    isZoomLevelMin(zoomLevel),
                    isZoomLevelMax(zoomLevel));

        }
    }

    private List<ResolvedIcon> resolveRouteWaypointIcons(
            final MobileContext mobileContext,
            final JourneyDescriptor journeyDescriptor) {
        final List<ResolvedIcon> resolvedIcons = getEmsManager()
            .resolveRouteWaypointIcons(journeyDescriptor, getScreenDimensionsStrategy()
                .createScreenDimensions(mobileContext));
        return resolvedIcons;
    }

    /**
     * {@inheritDoc}
     */
    public Map manipulateRouteLegStepMap(final RouteHandle routeHandle,
            final JourneyWaypoints waypoints,
            final RoutingOption routingOption,
            final MapUrl existingMapUrl,
            final MapLayer existingMapLayer, final Action mapManipulationAction,
            final MobileContext mobileContext) {

        // Manipulating a route leg step map is just a special case of manipulating
        // a route map, since a leg step map is just a specific PanZoomDetail view
        // on a route map.
        return manipulateRouteMap(routeHandle, waypoints, routingOption, existingMapUrl,
                existingMapLayer, mapManipulationAction, mobileContext);
    }

}

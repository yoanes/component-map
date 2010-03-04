package au.com.sensis.mobile.web.component.map.model;

import java.util.ArrayList;
import java.util.List;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.directions.JourneyDescriptor;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.mapping.LocationMapUrl;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.ResolvedIcon;

/**
 * Default {@link Map} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public final class MapImpl implements Map {

    private static enum MapImageStatus {

        /**
         * Map image was retrieved. ie. {@link MapUrl#getImageUrl()} is non-blank in
         * the {@link MapUrl} returned by  {@link MapImpl#getMapUrl()}.
         */
        MAP_IMAGE_RETRIEVED,

        /**
         * Retrieval of the map image has been deferred to the client.
         * The client is responsible for the actual map image retrieval
         * (eg. by accessing EMS directly via JavaScript).
         * ie. {@link MapUrl#getImageUrl()} is blank in
         * the {@link MapUrl} returned by  {@link MapImpl#getMapUrl()}.
         */
        MAP_IMAGE_RETRIEVAL_DEFERRED_TO_CLIENT

    }

    private static final MapLayerTransformer MAP_LAYER_TRANSFORMER
        = new MapLayerTransformer();

    private MapUrl mapUrl;
    private WGS84Point originalMapCentre;
    private MapLayer mapLayer;

    private MapImageStatus mapImageStatus;

    private List<ResolvedIcon> resolvedIcons;

    private RouteDetails routeDetails;
    private ZoomDetails zoomDetails;

    /**
     * Private constructor. Use one of the create* factory methods instead (see
     * Martin Fowler's "Refactoring" book). They have much more descriptive
     * names that indicate when they should be called.
     *
     * @param originalMapCentre
     *            Centre of the map that was originally/initially requested.
     * @param mapLayer
     *            {@link MapLayer} that the map was/is to be rendered with.
     * @param mapUrl
     *            {@link MapUrl} encapsulating the state of the map.
     * @param mapImageStatus
     *            {@link MapImageStatus} indicating if the map image has been
     *            retrieved or whether this has been deferred to the client to
     *            do.
     * @param resolvedIcons
     *            List of {@link ResolvedIcon}s to be used for
     *            client generated maps.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     */
    private MapImpl(final WGS84Point originalMapCentre,
            final MapLayer mapLayer, final MapUrl mapUrl,
            final MapImageStatus mapImageStatus,
            final List<ResolvedIcon> resolvedIcons, final int emsZoom,
            final boolean atMinimumZoom, final boolean atMaximumZoom) {
        setOriginalMapCentre(originalMapCentre);
        setMapLayer(mapLayer);
        setStatus(mapImageStatus);
        setMapUrl(mapUrl);
        setResolvedIcons(resolvedIcons);

        setZoomDetails(
                new ZoomDetailsImpl(emsZoom, atMinimumZoom, atMaximumZoom));
    }

    /**
     * Private constructor. Use one of the create* factory methods instead (see
     * Martin Fowler's "Refactoring" book). They have much more descriptive
     * names that indicate when they should be called.
     *
     * @param journeyWaypoints
     *            {@link JourneyWaypoints} use to generate the route.
     * @param journeyDescriptor
     *            {@link JourneyDescriptor} describing the generated route.
     * @param mapLayer
     *            {@link MapLayer} that the map was/is to be rendered with.
     * @param resolvedIcons
     *            List of {@link ResolvedIcon}s to be used for
     *            client generated maps.
     * @param mapImageStatus
     *            {@link MapImageStatus} indicating if the map image has been
     *            retrieved or whether this has been deferred to the client to
     *            do.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     */
    private MapImpl(final JourneyWaypoints journeyWaypoints,
            final JourneyDescriptor journeyDescriptor,
            final MapLayer mapLayer,
            final List<ResolvedIcon> resolvedIcons,
            final MapImageStatus mapImageStatus, final int emsZoom,
            final boolean atMinimumZoom, final boolean atMaximumZoom) {

        this(journeyWaypoints, journeyDescriptor, mapLayer, resolvedIcons, mapImageStatus);

        setZoomDetails(
                new ZoomDetailsImpl(emsZoom, atMinimumZoom, atMaximumZoom));

    }

    private MapImpl(final JourneyWaypoints journeyWaypoints,
            final JourneyDescriptor journeyDescriptor,
            final MapLayer mapLayer,
            final List<ResolvedIcon> resolvedIcons,
            final MapImageStatus mapImageStatus) {

        setMapLayer(mapLayer);
        setStatus(mapImageStatus);
        setMapUrl(journeyDescriptor.getMap());
        setResolvedIcons(resolvedIcons);

        final RouteDetailsImpl routeDetailsImpl = new RouteDetailsImpl(
                journeyWaypoints, journeyDescriptor);
        setRouteDetails(routeDetailsImpl);
    }

    /**
     * Creates an instance of this {@link MapImpl} for which
     * {@link #isMapImageRetrieved()} is true. Call this when the map image
     * has been retrieved and is thus contained in the passed in mapUrl.
     *
     * @param originalMapCentre
     *            Original centre of the map.
     * @param mapLayer {@link MapLayer} requested.
     * @param mapUrl
     *            {@link MapUrl} containing the retrieved map image.
     * @param resolvedIcons
     *            List of {@link ResolvedIcon}s to be used for
     *            client generated maps. Note that we require
     *            this even though this method explicitly covers
     *            the case when the map <b>was</b> retrieved. This is a safeguard
     *            in case we actually generated a map
     *            that will be replaced with a client generated map.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     * @return an instance of this {@link MapImpl} for which
     *         {@link #isMapImageRetrieved()} is true.
     */
    public static MapImpl createMapRetrievedInstance(
            final WGS84Point originalMapCentre, final MapLayer mapLayer,
            final MapUrl mapUrl, final List<ResolvedIcon> resolvedIcons,
            final int emsZoom, final boolean atMinimumZoom,
            final boolean atMaximumZoom) {
        return new MapImpl(originalMapCentre, mapLayer, mapUrl,
                MapImageStatus.MAP_IMAGE_RETRIEVED, resolvedIcons,
                emsZoom, atMinimumZoom, atMaximumZoom);
    }

    /**
     * Creates an instance of this {@link MapImpl} for which
     * {@link #isMapImageRetrieved()} is true and {@link #isRouteMap()} is true.
     * Call this when the map image has been retrieved and is thus contained in
     * the passed in mapUrl.
     *
     * @param journeyWaypoints
     *            {@link JourneyWaypoints} use to generate the route.
     * @param journeyDescriptor
     *            {@link JourneyDescriptor} describing the generated route.
     * @param mapLayer
     *            {@link MapLayer} requested.
     * @param resolvedIcons
     *            List of {@link ResolvedIcon}s to be used for
     *            client generated maps. Note that we require
     *            this even though this method explicitly covers
     *            the case when the map <b>was</b> retrieved. This is a safeguard
     *            in case we actually generated a map
     *            that will be replaced with a client generated map.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     * @return an instance of this {@link MapImpl} for which
     *         {@link #isMapImageRetrieved()} is true and {@link #isRouteMap()}
     *         is true.
     */
    public static MapImpl createRouteMapRetrievedInstance(
            final JourneyWaypoints journeyWaypoints,
            final JourneyDescriptor journeyDescriptor,
            final MapLayer mapLayer,  final List<ResolvedIcon> resolvedIcons,
            final int emsZoom, final boolean atMinimumZoom,
            final boolean atMaximumZoom) {
        return new MapImpl(journeyWaypoints, journeyDescriptor,
                mapLayer, resolvedIcons,
                MapImageStatus.MAP_IMAGE_RETRIEVED, emsZoom, atMinimumZoom,
                atMaximumZoom);
    }


    /**
     * Creates an instance of this {@link MapImpl} for which
     * {@link #isMapImageRetrievalDeferredToClient()} is true. Call this when
     * the map image has <b>not</b> been retrieved.
     *
     * @param originalMapCentre
     *            Original centre of the map.
     * @param mapLayer
     *            {@link MapLayer} requested.
     * @param resolvedIcons
     *            List of {@link ResolvedIcon}s to be used for
     *            client generated maps.
     * @param zoomLevel
     *            Zoom level requested.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     * @return an instance of this {@link MapImpl} for which
     *         {@link #isMapImageRetrievalDeferredToClient()} is true.
     */
    public static MapImpl createMapRetrievalDeferrendInstance(
            final WGS84Point originalMapCentre, final MapLayer mapLayer,
            final List<ResolvedIcon> resolvedIcons, final int zoomLevel,
            final int emsZoom, final boolean atMinimumZoom,
            final boolean atMaximumZoom) {
        final LocationMapUrl locationMapUrl = new LocationMapUrl();
        locationMapUrl.setMapCentre(originalMapCentre);
        locationMapUrl.setZoom(zoomLevel);
        return new MapImpl(originalMapCentre, mapLayer,
                locationMapUrl,
                MapImageStatus.MAP_IMAGE_RETRIEVAL_DEFERRED_TO_CLIENT,
                resolvedIcons, emsZoom, atMinimumZoom, atMaximumZoom);
    }

    /**
     * Creates an instance of this {@link MapImpl} for which
     * {@link #isMapImageRetrieved()} is true and {@link #isRouteMap()} is true.
     * Call this when the map image has been retrieved and is thus contained in
     * the passed in mapUrl.
     *
     * @param journeyWaypoints
     *            {@link JourneyWaypoints} use to generate the route.
     * @param journeyDescriptor
     *            {@link JourneyDescriptor} generated for the route.
     * @param mapLayer
     *            {@link MapLayer} requested.
     * @param resolvedIcons
     *            List of {@link ResolvedIcon}s to be used for
     *            client generated maps. Note that we require
     *            this even though this method explicitly covers
     *            the case when the map <b>was</b> retrieved. This is a safeguard
     *            in case we actually generated a map
     *            that will be replaced with a client generated map.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     * @return an instance of this {@link MapImpl} for which
     *         {@link #isMapImageRetrieved()} is true and {@link #isRouteMap()}
     *         is true.
     */
    public static MapImpl createRouteMapRetrievalDeferredInstance(
            final JourneyWaypoints journeyWaypoints,
            final JourneyDescriptor journeyDescriptor,
            final MapLayer mapLayer, final List<ResolvedIcon> resolvedIcons,
            final int emsZoom, final boolean atMinimumZoom, final boolean atMaximumZoom) {

        return new MapImpl(journeyWaypoints, journeyDescriptor,
                mapLayer, resolvedIcons,
                MapImageStatus.MAP_IMAGE_RETRIEVAL_DEFERRED_TO_CLIENT,
                emsZoom, atMinimumZoom, atMaximumZoom);
    }

    /**
     * Creates an instance of this {@link MapImpl} for which
     * {@link #isMapImageRetrieved()} is true and {@link #isRouteMap()} is true.
     * Call this when the map image has been retrieved and is thus contained in
     * the passed in mapUrl.
     *
     * @param journeyWaypoints
     *            {@link JourneyWaypoints} use to generate the route.
     * @param journeyDescriptor
     *            {@link JourneyDescriptor} describing the generated route.
     * @param mapLayer
     *            {@link MapLayer} requested.
     * @param resolvedIcons
     *            List of {@link ResolvedIcon}s to be used for
     *            client generated maps. Note that we require
     *            this even though this method explicitly covers
     *            the case when the map <b>was</b> retrieved. This is a safeguard
     *            in case we actually generated a map
     *            that will be replaced with a client generated map.
     * @return an instance of this {@link MapImpl} for which
     *         {@link #isMapImageRetrieved()} is true and {@link #isRouteMap()}
     *         is true.
     */
    public static MapImpl createRouteMapRetrievalDeferredInstance(
            final JourneyWaypoints journeyWaypoints,
            final JourneyDescriptor journeyDescriptor,
            final MapLayer mapLayer, final List<ResolvedIcon> resolvedIcons) {

        return new MapImpl(journeyWaypoints, journeyDescriptor,
                mapLayer, resolvedIcons,
                MapImageStatus.MAP_IMAGE_RETRIEVAL_DEFERRED_TO_CLIENT);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMapImageRetrieved() {
        return MapImageStatus.MAP_IMAGE_RETRIEVED.equals(getStatus());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMapImageRetrievalDeferredToClient() {
        return MapImageStatus.MAP_IMAGE_RETRIEVAL_DEFERRED_TO_CLIENT.equals(getStatus());
    }


    /**
     * {@inheritDoc}
     */
    public MapUrl getMapUrl() {
        return mapUrl;
    }

    /**
     * @param mapUrl the mapUrl to set
     */
    private void setMapUrl(final MapUrl mapUrl) {
        this.mapUrl = mapUrl;
    }

    private MapImageStatus getStatus() {
        return mapImageStatus;
    }

    /**
     * @param mapImageStatus the mapImageStatus to set
     */
    private void setStatus(final MapImageStatus mapImageStatus) {
        this.mapImageStatus = mapImageStatus;
    }

    /**
     * {@inheritDoc}
     */
    public WGS84Point getOriginalMapCentre() {
        if (isRouteMap()) {
            throw new IllegalStateException(
                    "It is illegal to call getOriginalMapCentre() "
                            + "when isRouteMap() is true");
        } else {
            return originalMapCentre;
        }
    }

    /**
     * @param originalMapCentre
     *            the originalMapCentre to set
     */
    private void setOriginalMapCentre(final WGS84Point originalMapCentre) {
        this.originalMapCentre = originalMapCentre;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isMapLayer() {
        return MapLayer.Map.equals(getMapLayer());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPhotoLayer() {
        return MapLayer.Photo.equals(getMapLayer());
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPhotoWithStreetsLayer() {
        return MapLayer.PhotoWithStreets.equals(getMapLayer());
    }

    /**
     * @param mapLayer the mapLayer to set
     */
    private void setMapLayer(final MapLayer mapLayer) {
        this.mapLayer = mapLayer;
    }

    /**
     * {@inheritDoc}
     */
    public MapLayer getMapLayer() {
        return mapLayer;
    }

    /**
     * {@inheritDoc}
     */
    public String getMapLayerShortCode() {
        return getMapLayerTransformer().transformToShortCode(getMapLayer());
    }

    /**
     * {@inheritDoc}
     */
    public String getJsMapLayer() {
        return getMapLayerTransformer().transformToJsCode(getMapLayer());
    }

    private static MapLayerTransformer getMapLayerTransformer() {
        return MAP_LAYER_TRANSFORMER;
    }

    /**
     * {@inheritDoc}
     */
    public List<ResolvedIcon> getResolvedIcons() {
        if (resolvedIcons != null) {
            return resolvedIcons;
        } else {
            return new ArrayList<ResolvedIcon>();
        }
    }

    /**
     * @param resolvedIcons the resolvedIcons to set
     */
    private void setResolvedIcons(final List<ResolvedIcon> resolvedIcons) {
        this.resolvedIcons = resolvedIcons;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRouteMap() {
        return routeDetails != null;
    }

    /**
     * {@inheritDoc}
     */
    public RouteDetails getRouteDetails() throws IllegalStateException {
        if (isRouteMap()) {
            return routeDetails;
        } else {
            throw new IllegalStateException(
                    "Illegal to call getRouteDetails() when isRouteMap() is false.");
        }
    }

    /**
     * @param routeDetails the routeDetails to set
     */
    private void setRouteDetails(final RouteDetails routeDetails) {
        this.routeDetails = routeDetails;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isBoundingBoxDefined() {
        return getMapUrl().getBoundingBox() != null;
    }

    /**
     * {@inheritDoc}
     */
    public ZoomDetails getZoomDetails() {
        if (isZoomDetailsDefined()) {
            return zoomDetails;
        } else {
            throw new IllegalStateException(
                    "Illegal to call getZoomDetails() when isZoomDetailsDefined() is false.");

        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isZoomDetailsDefined() {
        return zoomDetails != null;
    }

    /**
     * @param zoomDetails the zoomDetails to set
     */
    private void setZoomDetails(final ZoomDetails zoomDetails) {
        this.zoomDetails = zoomDetails;
    }

}

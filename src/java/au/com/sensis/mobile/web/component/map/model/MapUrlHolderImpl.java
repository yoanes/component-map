package au.com.sensis.mobile.web.component.map.model;

import java.util.ArrayList;
import java.util.List;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.LocationMapUrl;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.ResolvedIcon;

/**
 * Default {@link MapUrlHolder} implementation.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public final class MapUrlHolderImpl implements MapUrlHolder {

    private static enum MapImageStatus {

        /**
         * Map image was retrieved. ie. {@link MapUrl#getImageUrl()} is non-blank in
         * the {@link MapUrl} returned by  {@link MapUrlHolderImpl#getMapUrl()}.
         */
        MAP_IMAGE_RETRIEVED,

        /**
         * Retrieval of the map image has been deferred to the client.
         * The client is responsible for the actual map image retrieval
         * (eg. by accessing EMS directly via JavaScript).
         * ie. {@link MapUrl#getImageUrl()} is blank in
         * the {@link MapUrl} returned by  {@link MapUrlHolderImpl#getMapUrl()}.
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

    /**
     * Zoom level that {@link MapUrl#getZoom()} corresponds to.
     * Required by AJAX maps that talk to EMS directly.
     */
    private int emsZoom;

    private boolean atMinimumZoom;
    private boolean atMaximumZoom;

    /**
     * Private constructor. Use one of the create* factory methods instead (see
     * Martin Fowler's "Refactoring" book). They have much more descriptive
     * names that indicate when they should be called.
     *
     * @param originalMapCentre
     *            Centre of the map that was originally/initially requested.
     * @param mapUrl
     *            {@link MapUrl} encapsulating the state of the map.
     * @param mapImageStatus
     *            {@link MapImageStatus} indicating if the map image has been
     *            retrieved or whether this has been deferred to the client to
     *            do.
     * @param resolvedIcons
     *            List of {@link ResolvedIcon}s to be used for
     *            {@link MapImageStatus#MAP_IMAGE_RETRIEVAL_DEFERRED_TO_CLIENT}.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     */
    private MapUrlHolderImpl(final WGS84Point originalMapCentre,
            final MapLayer mapLayer, final MapUrl mapUrl,
            final MapImageStatus mapImageStatus,
            final List<ResolvedIcon> resolvedIcons, final int emsZoom,
            final boolean atMinimumZoom, final boolean atMaximumZoom) {
        setOriginalMapCentre(originalMapCentre);
        setMapLayer(mapLayer);
        setStatus(mapImageStatus);
        setMapUrl(mapUrl);
        setEmsZoom(emsZoom);
        setAtMinimumZoom(atMinimumZoom);
        setAtMaximumZoom(atMaximumZoom);
        setResolvedIcons(resolvedIcons);
    }

    /**
     * Creates an instance of this {@link MapUrlHolderImpl} for which
     * {@link #isMapImageRetrieved()} is true. Call this when the map image
     * has been retrieved and is thus contained in the passed in mapUrl.
     *
     * @param originalMapCentre
     *            Original centre of the map.
     * @param mapLayer {@link MapLayer} requested.
     * @param mapUrl
     *            {@link MapUrl} containing the retrieved map image.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     * @return an instance of this {@link MapUrlHolderImpl} for which
     *         {@link #isMapImageRetrieved()} is true.
     */
    public static MapUrlHolderImpl createMapRetrievedInstance(
            final WGS84Point originalMapCentre, final MapLayer mapLayer,
            final MapUrl mapUrl, final int emsZoom, final boolean atMinimumZoom,
            final boolean atMaximumZoom) {
        return new MapUrlHolderImpl(originalMapCentre, mapLayer, mapUrl,
                MapImageStatus.MAP_IMAGE_RETRIEVED, null,
                emsZoom, atMinimumZoom, atMaximumZoom);
    }


    /**
     * Creates an instance of this {@link MapUrlHolderImpl} for which
     * {@link #isMapImageRetrievalDeferredToClient()} is true. Call this when
     * the map image has <b>not</b> been retrieved.
     *
     * @param originalMapCentre
     *            Original centre of the map.
     * @param mapLayer
     *            {@link MapLayer} requested.
     * @param zoomLevel
     *            Zoom level requested.
     * @param emsZoom
     *            the EMS zoom that the map was/is to be rendered using.
     *            Required by AJAX maps that talk to EMS directly.
     * @param atMinimumZoom
     *            True if the {@link MapUrl#getZoom()} is the minimum allowed.
     * @param atMaximumZoom
     *            True if the {@link MapUrl#getZoom()} is the maximum allowed.
     * @return an instance of this {@link MapUrlHolderImpl} for which
     *         {@link #isMapImageRetrievalDeferredToClient()} is true.
     */
    public static MapUrlHolderImpl createMapRetrievalDeferrendInstance(
            final WGS84Point originalMapCentre, final MapLayer mapLayer,
            final List<ResolvedIcon> resolvedIcons, final int zoomLevel,
            final int emsZoom, final boolean atMinimumZoom,
            final boolean atMaximumZoom) {
        final LocationMapUrl locationMapUrl = new LocationMapUrl();
        locationMapUrl.setMapCentre(originalMapCentre);
        locationMapUrl.setZoom(zoomLevel);
        return new MapUrlHolderImpl(originalMapCentre, mapLayer,
                locationMapUrl,
                MapImageStatus.MAP_IMAGE_RETRIEVAL_DEFERRED_TO_CLIENT,
                resolvedIcons, emsZoom, atMinimumZoom, atMaximumZoom);
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
        return originalMapCentre;
    }

    /**
     * @param originalMapCentre the originalMapCentre to set
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
    public boolean isAtMaximumZoom() {
        return atMaximumZoom;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAtMinimumZoom() {
        return atMinimumZoom;
    }

    /**
     * @param atMinimumZoom the atMinimumZoom to set
     */
    public void setAtMinimumZoom(final boolean atMinimumZoom) {
        this.atMinimumZoom = atMinimumZoom;
    }

    /**
     * @param atMaximumZoom the atMaximumZoom to set
     */
    public void setAtMaximumZoom(final boolean atMaximumZoom) {
        this.atMaximumZoom = atMaximumZoom;
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

    private static MapLayerTransformer getMapLayerTransformer() {
        return MAP_LAYER_TRANSFORMER;
    }

    /**
     * {@inheritDoc}
     */
    public int getEmsZoom() {
        return emsZoom;
    }

    /**
     * @param emsZoom the emsZoom to set
     */
    private void setEmsZoom(final int emsZoom) {
        this.emsZoom = emsZoom;
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
}

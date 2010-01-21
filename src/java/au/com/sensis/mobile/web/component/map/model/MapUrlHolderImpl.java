package au.com.sensis.mobile.web.component.map.model;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.LocationMapUrl;
import au.com.sensis.wireless.manager.mapping.MapUrl;

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

    private MapUrl mapUrl;
    private WGS84Point originalMapCentre;

    private MapImageStatus mapImageStatus;

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
     */
    private MapUrlHolderImpl(final WGS84Point originalMapCentre,
            final MapUrl mapUrl, final MapImageStatus mapImageStatus) {
        setOriginalMapCentre(originalMapCentre);
        setStatus(mapImageStatus);
        setMapUrl(mapUrl);
    }

    /**
     * Creates an instance of this {@link MapUrlHolderImpl} for which
     * {@link #isMapImageRetrieved()} is true. Call this when the map image
     * has been retrieved and is thus contained in the passed in mapUrl.
     *
     * @param originalMapCentre
     *            Original centre of the map.
     * @param mapUrl
     *            {@link MapUrl} containing the retrieved map image.
     * @return an instance of this {@link MapUrlHolderImpl} for which
     *         {@link #isMapImageRetrieved()} is true.
     */
    public static MapUrlHolderImpl createMapRetrievedInstance(
            final WGS84Point originalMapCentre, final MapUrl mapUrl) {
        return new MapUrlHolderImpl(originalMapCentre, mapUrl,
                MapImageStatus.MAP_IMAGE_RETRIEVED);
    }


    /**
     * Creates an instance of this {@link MapUrlHolderImpl} for which
     * {@link #isMapImageRetrievalDeferredToClient()} is true. Call this when
     * the map image has <b>not</b> been retrieved.
     *
     * @param originalMapCentre
     *            Original centre of the map.
     * @param zoomLevel
     *            Zoom level requested.
     * @return an instance of this {@link MapUrlHolderImpl} for which
     *         {@link #isMapImageRetrievalDeferredToClient()} is true.
     */
    public static MapUrlHolderImpl createMapRetrievalDeferrendInstance(
            final WGS84Point originalMapCentre, final int zoomLevel) {
        final LocationMapUrl locationMapUrl = new LocationMapUrl();
        locationMapUrl.setMapCentre(originalMapCentre);
        locationMapUrl.setZoom(zoomLevel);
        return new MapUrlHolderImpl(originalMapCentre, locationMapUrl,
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
    public MapUrl getMapUrl() throws IllegalStateException {
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


}

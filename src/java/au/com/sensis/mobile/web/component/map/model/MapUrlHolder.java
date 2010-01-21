package au.com.sensis.mobile.web.component.map.model;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.MapUrl;

/**
 * A holder for a {@link MapUrl} instance, where the
 * {@link MapUrl#getImageUrl()} is only non-null if
 * {@link #isMapImageRetrieved()} is true. Otherwise,
 * {@link #isMapImageRetrievalDeferredToClient()} is true and it is up to the
 * client to retrieve the map image itself (eg. by accessing EMS directly for
 * JavaScript enhanced maps).
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface MapUrlHolder {

    /**
     * @return true if {@link #getMapUrl()} is non-null and
     *         {@link #isMapImageRetrievalDeferredToClient()} is false.
     */
    boolean isMapImageRetrieved();

    /**
     * @return true if {@link #getMapUrl()} is null and
     *         {@link #isMapImageRetrieved()} is false.
     */
    boolean isMapImageRetrievalDeferredToClient();

    /**
     * @return the held {@link MapUrl}.
     * @throws IllegalStateException
     *             Thrown if {@link #isMapImageRetrieved()} is false.
     */
    MapUrl getMapUrl() throws IllegalStateException;

    /**
     * The centre of the map when it was originally/initially retrieved. It is
     * essential that clients preserve this state when panning or zooming the
     * map via the
     * {@link au.com.sensis.mobile.web.component.map.business.MapDelegate},
     * otherwise the original reference point will be lost.
     *
     * @return the centre of the map when it was originally/initially retrieved.
     *         It is essential that clients preserve this state when panning or
     *         zooming the map via the
     *         {@link au.com.sensis.mobile.web.component.map.business.MapDelegate}
     *         , otherwise the original reference point will be lost.
     */
    WGS84Point getOriginalMapCentre();

}

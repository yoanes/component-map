package au.com.sensis.mobile.web.component.map.model;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.MapLayer;
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

    /**
     * Returns true if the map was/is to be rendered using a
     * {@link au.com.sensis.wireless.manager.mapping.MapLayer#Map} layer.
     *
     * @return true if the map was/is to be rendered using a
     *         {@link au.com.sensis.wireless.manager.mapping.MapLayer#Map} layer.
     */
    boolean isMapLayer();

    /**
     * Returns true if the map was/is to be rendered using a
     * {@link au.com.sensis.wireless.manager.mapping.MapLayer#Photo} layer.
     *
     * @return true if the map was/is to be rendered using a
     *         {@link au.com.sensis.wireless.manager.mapping.MapLayer#Photo} layer.
     */
    boolean isPhotoLayer();

    /**
     * Returns true if the map was/is to be rendered using a
     * {@link au.com.sensis.wireless.manager.mapping.MapLayer#PhotoWithStreets} layer.
     *
     * @return true if the map was/is to be rendered using a
     *         {@link au.com.sensis.wireless.manager.mapping.MapLayer#PhotoWithStreets} layer.
     */
    boolean isPhotoWithStreetsLayer();

    /**
     * @return true if the requested zoom level is the minimum allowed.
     */
    boolean isAtMinimumZoom();

    /**
     * @return true if the requested zoom level is the maximum allowed.
     */
    boolean isAtMaximumZoom();

    /**
     * Returns the {@link MapLayer} that the map was/is to be rendered using.
     *
     * @return Returns the {@link MapLayer} that the map was/is to be rendered
     *         using.
     */
    MapLayer getMapLayer();

    /**
     * String short code representation of {@link #getMapLayer()}.
     *
     * @return String short code representation of {@link #getMapLayer()}.
     */
    String getMapLayerShortCode();

    /**
     * Returns the EMS zoom that the map was/is to be rendered using.
     * Required by AJAX maps that talk to EMS directly.
     *
     * @return the EMS zoom that the map was/is to be rendered using.
     * Required by AJAX maps that talk to EMS directly.
     *
     * TODO: This method should possibly be moved into the {@link MapUrl}.
     */
    int getEmsZoom();
}

package au.com.sensis.mobile.web.component.map.model;

import java.util.List;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.ResolvedIcon;

/**
 * A holder for a {@link MapUrl} instance and supporting methods, where either
 * {@link #isMapImageRetrieved()} or
 * {@link #isMapImageRetrievalDeferredToClient()} is true. The latter indicates
 * that it is up to the client to retrieve the map image itself (eg. by
 * accessing EMS directly for JavaScript enhanced maps).
 * <p>
 * Also provides {@link #getOriginalMapCentre()} which needs to be retained when
 * calling any of the map manipulation methods in
 * {@link au.com.sensis.mobile.web.component.map.business.MapDelegate}.
 * </p>
 * <p>
 * Also provides key utility methods useful for a presentation layer to display
 * the map (in the event that {@link #isMapImageRetrieved()}) is true, such as
 * {@link #isAtMaximumZoom()} and {@link #isMapLayer()}.
 * </p>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface MapUrlHolder {

    /**
     * @return true if {@link MapUrl#getImageUrl()} is a valid image url and
     *         {@link #isMapImageRetrievalDeferredToClient()} is false.
     */
    boolean isMapImageRetrieved();

    /**
     * @return true if {@link MapUrl#getImageUrl()} is not a valid image url and
     *         {@link #isMapImageRetrieved()} is false.
     */
    boolean isMapImageRetrievalDeferredToClient();

    /**
     * @return the held {@link MapUrl}.
     */
    MapUrl getMapUrl();

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
     *         {@link au.com.sensis.wireless.manager.mapping.MapLayer#Map}
     *         layer.
     */
    boolean isMapLayer();

    /**
     * Returns true if the map was/is to be rendered using a
     * {@link au.com.sensis.wireless.manager.mapping.MapLayer#Photo} layer.
     *
     * @return true if the map was/is to be rendered using a
     *         {@link au.com.sensis.wireless.manager.mapping.MapLayer#Photo}
     *         layer.
     */
    boolean isPhotoLayer();

    /**
     * Returns true if the map was/is to be rendered using a
     * {@link au.com.sensis.wireless.manager.mapping.MapLayer#PhotoWithStreets}
     * layer.
     *
     * @return true if the map was/is to be rendered using a
     *         {@link au.com.sensis.wireless.manager.mapping.MapLayer#PhotoWithStreets}
     *         layer.
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
     * Returns the EMS zoom that the map was/is to be rendered using. Required
     * by AJAX maps that talk to EMS directly.
     *
     * @return the EMS zoom that the map was/is to be rendered using. Required
     *         by AJAX maps that talk to EMS directly.
     *
     *         TODO: This method should possibly be moved into the
     *         {@link MapUrl}.
     */
    int getEmsZoom();

    /**
     * List of {@link ResolvedIcon}s that the client should render on the map.
     * Only relevant if {@link #isMapImageRetrievalDeferredToClient()} is true.
     * However, the return value is guaranteed to never be null.
     *
     * @return List of {@link ResolvedIcon}s that the client should render on
     *         the map. Only relevant if
     *         {@link #isMapImageRetrievalDeferredToClient()} is true. May not
     *         be null.
     */
    List<ResolvedIcon> getResolvedIcons();
}

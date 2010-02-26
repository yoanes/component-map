package au.com.sensis.mobile.web.component.map.model;

import java.util.List;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.ResolvedIcon;

/**
 * Contains all details of a map to be rendered.
 * <p>
 * Either {@link #isMapImageRetrieved()} or
 * {@link #isMapImageRetrievalDeferredToClient()} will be true. The latter indicates
 * that it is up to the client to retrieve the map image itself (eg. by
 * accessing EMS directly for JavaScript enhanced maps). The former indicaes
 * that the map image is contained by {@link #getMapUrl()}.
 * </p>
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
public interface Map {

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
     * otherwise the original reference point will be lost. Note that the
     * reference point isn't strictly required for the panning and zooming,
     * though. It is only used to render a marker at the original reference
     * point.
     *
     * @return the centre of the map when it was originally/initially retrieved.
     *         It is essential that clients preserve this state when panning or
     *         zooming the map via the
     *         {@link au.com.sensis.mobile.web.component.map.business.MapDelegate}
     *         , otherwise the original reference point will be lost. Note that
     *         the reference point isn't strictly required for the panning and
     *         zooming, though. It is only used to render a marker at the
     *         original reference point.
     * @throws IllegalStateException
     *             thrown if {@link #isRouteMap()} is true, since route maps
     *             don't have a caller specific original map centre point. The
     *             centre point is generated based on the route.
     */
    WGS84Point getOriginalMapCentre() throws IllegalStateException;

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
     * Returns the {@link MapLayer} converted to a String that can be passed to
     * the (Mobiles) JavaScript maps interface.
     *
     * @return Returns the {@link MapLayer} converted to a String that can be
     *         passed to the (Mobiles) JavaScript maps interface.
     */
    String getJsMapLayer();

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

    /**
     * Return details of the route. Must only be called if {@link #isRouteMap()} is true.
     *
     * @return details of the route.
     * @throws IllegalStateException thrown if {@link #isRouteMap()} is false.
     */
    RouteDetails getRouteDetails() throws IllegalStateException;

    /**
     * Returns true if the map is of a route. Note that the route may be fully
     * visible, only partially visible or not visible at all, depending on the
     * {@link #getMapUrl()}.
     *
     * @return true if the map is of a route.
     */
    boolean isRouteMap();

    /**
     * @return true if {@link #getMapUrl()} has a defined bounding box.
     */
    boolean isBoundingBoxDefined();

    /**
     * @return The {@link ZoomDetails} of the map.
     * @throws IllegalStateException thrown if {@link #isZoomDetailsDefined()} is false.
     */
    ZoomDetails getZoomDetails() throws IllegalStateException;

    /**
     * @return true if this Map has zoom details ({@link #getZoomDetails()}).
     */
    boolean isZoomDetailsDefined();

}

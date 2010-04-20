package au.com.sensis.mobile.web.component.map.showcase.presentation.form;

import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.mobile.web.component.map.business.MapDelegate.Action;
import au.com.sensis.mobile.web.component.map.model.MapLayerTransformer;
import au.com.sensis.wireless.manager.directions.RouteHandle;
import au.com.sensis.wireless.manager.mapping.LocationMapUrl;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;

/**
 * Form for submitting map manipulation parameters.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ManipulateMapForm extends MapForm {

    // Current state of the map.
    private Double originalCentreLatitude;
    private Double originalCentreLongitude;
    private Double centreLatitude;
    private Double centreLongitude;
    private Double boundingBoxTopLeftLatitude;
    private Double boundingBoxTopLeftLongitude;
    private Double boundingBoxBottomRightLatitude;
    private Double boundingBoxBottomRightLongitude;
    private Integer zoomLevel;

    private String mapLayer;

    private String routeHandle;
    private Double routeStartLatitude;
    private Double routeStartLongitude;
    private Double routeEndLatitude;
    private Double routeEndLongitude;

    private Double routeLegStepCentreLatitude;
    private Double routeLegStepCentreLongitude;

    /**
     * Parameter that indicates the action being performed, where the valid values correspond to
     * {@link Action}. Convert to a real {@link Action} via {@link Action#fromValue(String)}. May be
     * null.
     */
    private String action;

    private static final MapLayerTransformer MAP_LAYER_TRANSFORMER
        = new MapLayerTransformer();

    /**
     * @return  the centreLatitude.
     */
    public Double getCentreLatitude() {

        return centreLatitude;
    }


    /**
     * @param centreLatitude the centreLatitude to set.
     */
    public void setCentreLatitude(final Double centreLatitude) {

        this.centreLatitude = centreLatitude;
    }

    /**
     * Shorthand for {@link #setCentreLatitude(Double)} so that it can be more easily
     * set from request params.
     *
     * @param centreLatitude the centreLatitude to set.
     */
    public void setMclat(final Double centreLatitude) {

        setCentreLatitude(centreLatitude);
    }


    /**
     * @return  the centreLongitude.
     */
    public Double getCentreLongitude() {

        return centreLongitude;
    }


    /**
     * @param centreLongitude the centreLongitude to set.
     */
    public void setCentreLongitude(final Double centreLongitude) {

        this.centreLongitude = centreLongitude;
    }

    /**
     * Shorthand for {@link #setCentreLongitude(Double)} so that it can be more easily
     * set from request params.
     *
     * @param centreLongitude the centreLongitude to set.
     */
    public void setMclon(final Double centreLongitude) {

        setCentreLongitude(centreLongitude);
    }


    /**
     * @return  the boundingBoxTopLeftLatitude.
     */
    public Double getBoundingBoxTopLeftLatitude() {

        return boundingBoxTopLeftLatitude;
    }


    /**
     * @param boundingBoxTopLeftLatitude the boundingBoxTopLeftLatitude to set.
     */
    public void setBoundingBoxTopLeftLatitude(
            final Double boundingBoxTopLeftLatitude) {

        this.boundingBoxTopLeftLatitude = boundingBoxTopLeftLatitude;
    }

    /**
     * Shorthand for {@link #setBoundingBoxTopLeftLatitude(Double)} so that it can be more
     * easily set from request params.
     *
     * @param boundingBoxTopLeftLatitude the boundingBoxTopLeftLatitude to set.
     */
    public void setTllat(final Double boundingBoxTopLeftLatitude) {

        setBoundingBoxTopLeftLatitude(boundingBoxTopLeftLatitude);
    }


    /**
     * @return  the boundingBoxTopLeftLongitude.
     */
    public Double getBoundingBoxTopLeftLongitude() {

        return boundingBoxTopLeftLongitude;
    }


    /**
     * @param boundingBoxTopLeftLongitude the boundingBoxTopLeftLongitude to set.
     */
    public void setBoundingBoxTopLeftLongitude(
            final Double boundingBoxTopLeftLongitude) {

        this.boundingBoxTopLeftLongitude = boundingBoxTopLeftLongitude;
    }

    /**
     * Shorthand for {@link #setBoundingBoxTopLeftLongitude(Double)} so that it can be
     * more easily set from request params.
     *
     * @param boundingBoxTopLeftLongitude the boundingBoxTopLeftLongitude to set.
     */
    public void setTllon(
            final Double boundingBoxTopLeftLongitude) {

        setBoundingBoxTopLeftLongitude(boundingBoxTopLeftLongitude);

    }


    /**
     * @return  the boundingBoxBottomRightLatitude.
     */
    public Double getBoundingBoxBottomRightLatitude() {

        return boundingBoxBottomRightLatitude;
    }


    /**
     * @param boundingBoxBottomRightLatitude the boundingBoxBottomRightLatitude to
     *            set.
     */
    public void setBoundingBoxBottomRightLatitude(
            final Double boundingBoxBottomRightLatitude) {

        this.boundingBoxBottomRightLatitude = boundingBoxBottomRightLatitude;
    }

    /**
     * Shorthand for {@link #setBoundingBoxBottomRightLatitude(Double)} so that it can be
     * more easily set from request params.
     *
     * @param boundingBoxBottomRightLatitude the boundingBoxBottomRightLatitude to
     *            set.
     */
    public void setBrlat(final Double boundingBoxBottomRightLatitude) {

        setBoundingBoxBottomRightLatitude(boundingBoxBottomRightLatitude);
    }


    /**
     * @return  the boundingBoxBottomRightLongitude.
     */
    public Double getBoundingBoxBottomRightLongitude() {

        return boundingBoxBottomRightLongitude;
    }


    /**
     * @param boundingBoxBottomRightLongitude the boundingBoxBottomRightLongitude to
     *            set.
     */
    public void setBoundingBoxBottomRightLongitude(
            final Double boundingBoxBottomRightLongitude) {

        this.boundingBoxBottomRightLongitude = boundingBoxBottomRightLongitude;
    }

    /**
     * Shorthand for {@link #setBoundingBoxBottomRightLongitude(Double)} so that it can be
     * more easily set from request params.
     *
     * @param boundingBoxBottomRightLongitude the boundingBoxBottomRightLongitude to
     *            set.
     */
    public void setBrlon(final Double boundingBoxBottomRightLongitude) {

        setBoundingBoxBottomRightLongitude(boundingBoxBottomRightLongitude);
    }

    /**
     * @return  the zoomLevel.
     */
    public Integer getZoomLevel() {

        return zoomLevel;
    }


    /**
     * @param zoomLevel the zoomLevel to set.
     */
    public void setZoomLevel(final Integer zoomLevel) {

        this.zoomLevel = zoomLevel;
    }

    /**
     * Shorthand for {@link #setZoomLevel(int)} so that it can be
     * more easily set from request params.
     *
     * @param zoomLevel the zoomLevel to set.
     */
    public void setMz(final Integer zoomLevel) {

        setZoomLevel(zoomLevel);
    }

    /**
     * @return  the action.
     */
    public Action getAction() {

        return Action.fromShortCode(action);
    }

    /**
     * @param action    the action to set.
     */
    public void setAction(final String action) {

        this.action = action;
    }

    /**
     * Short request parameter setter for {@link #setAction(String)}.
     *
     * @param action the action to set.
     */
    public void setAct(final String action) {

        setAction(action);
    }

    /**
     * Packs this {@link ManipulateMapForm} into a new {@link MapUrl},
     * discarding data that cannot be converted, such as {@link #getAction()}.
     *
     * @return this {@link ManipulateMapForm} packed into a new {@link MapUrl},
     * discarding data that cannot be converted, such as {@link #getAction()}.
     */
    public MapUrl getMapUrl() {
        final LocationMapUrl locationMapUrl = new LocationMapUrl();

        final WGS84Point boundingBoxTopLeft =
                new WGS84Point(getBoundingBoxTopLeftLongitude(),
                        getBoundingBoxTopLeftLatitude());
        final WGS84Point boundingBoxBottomRight =
                new WGS84Point(getBoundingBoxBottomRightLongitude(),
                        getBoundingBoxBottomRightLatitude());
        final MobilesBoundingBox mobilesBoundingBox =
                new MobilesBoundingBox(boundingBoxTopLeft,
                        boundingBoxBottomRight);
        locationMapUrl.setBoundingBox(mobilesBoundingBox);

        final WGS84Point mapCentre =
                new WGS84Point(getCentreLongitude(), getCentreLatitude());
        locationMapUrl.setMapCentre(mapCentre);

        locationMapUrl.setZoom(zoomLevel);
        return locationMapUrl;
    }

    /**
     * @return original map centre.
     */
    public WGS84Point getOrignalMapCentre() {
        return new WGS84Point(getOriginalCentreLongitude(),
                    getOriginalCentreLatitude());
    }


    /**
     * @return the originalCentreLatitude
     */
    public Double getOriginalCentreLatitude() {
        return originalCentreLatitude;
    }


    /**
     * @param originalCentreLatitude the originalCentreLatitude to set
     */
    public void setOriginalCentreLatitude(final Double originalCentreLatitude) {
        this.originalCentreLatitude = originalCentreLatitude;
    }

    /**
     * Shorthand for {@link #setOriginalCentreLatitude(Double)} so that it can be more easily
     * set from request params.
     *
     * @param originalCentreLatitude the originalCentreLatitude to set.
     */
    public void setMoclat(final Double originalCentreLatitude) {

        setOriginalCentreLatitude(originalCentreLatitude);
    }


    /**
     * @return the originalCentreLongitude
     */
    public Double getOriginalCentreLongitude() {
        return originalCentreLongitude;
    }


    /**
     * @param originalCentreLongitude the originalCentreLongitude to set
     */
    public void setOriginalCentreLongitude(final Double originalCentreLongitude) {
        this.originalCentreLongitude = originalCentreLongitude;
    }

    /**
     * Shorthand for {@link #setOriginalCentreLongitude(Double)} so that it can be more easily
     * set from request params.
     *
     * @param originalCentreLongitude the originalCentreLongitude to set.
     */
    public void setMoclon(final Double originalCentreLongitude) {

        setOriginalCentreLongitude(originalCentreLongitude);
    }


    /**
     * @return the mapLayer
     */
    public MapLayer getMapLayer() {
        return getMapLayerTransformer().transformFromShortCode(
                getMapLayerAsString());
    }


    /**
     * @param mapLayer the mapLayer to set
     */
    public void setMapLayerAsString(final String mapLayer) {
        this.mapLayer = mapLayer;
    }

    /**
     * @return the mapLayer
     */
    public String getMapLayerAsString() {
        return mapLayer;
    }

    /**
     * Shorthand for {@link #setMapLayer(String)}.
     * @param mapLayer the mapLayer to set
     */
    public void setLyr(final String mapLayer) {
        setMapLayerAsString(mapLayer);
    }


    /**
     * @return the mapLayerTransformer
     */
    private static MapLayerTransformer getMapLayerTransformer() {
        return MAP_LAYER_TRANSFORMER;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("centreLatitude", centreLatitude)
            .append("centreLongitude", centreLongitude)
            .append("boundingBoxTopLeftLatitude", boundingBoxTopLeftLatitude)
            .append("boundingBoxTopLeftLongitude", boundingBoxTopLeftLongitude)
            .append("boundingBoxBottomRightLatitude", boundingBoxBottomRightLatitude)
            .append("boundingBoxBottomRightLongitude", boundingBoxBottomRightLongitude)
            .append("zoomLevel", zoomLevel)
            .append("mapLayer", mapLayer)
            .toString();
    }


    /**
     * @return the routeHandle
     */
    public RouteHandle getRouteHandle() {
        return new RouteHandle(routeHandle);
    }


    /**
     * @param routeHandle the routeHandle to set
     */
    public void setRouteHandleAsString(final String routeHandle) {
        this.routeHandle = routeHandle;
    }

    /**
     * Shorthand for {@link #setRouteHandleAsString(String)} so that it can be more easily
     * set from request params.
     *
     * @param routeHandle the routeHandle to set
     */
    public void setRh(final String routeHandle) {
        setRouteHandleAsString(routeHandle);
    }

    /**
     * @return start coordinates for a route.
     */
    public WGS84Point getRouteStart() {
        return new WGS84Point(getRouteStartLongitude(),
                    getRouteStartLatitude());
    }

    /**
     * @return the routeStartLatitude
     */
    public Double getRouteStartLatitude() {
        return routeStartLatitude;
    }


    /**
     * @param routeStartLatitude the routeStartLatitude to set
     */
    public void setRouteStartLatitude(final Double routeStartLatitude) {
        this.routeStartLatitude = routeStartLatitude;
    }

    /**
     * Shorthand for {@link #setRouteStartLatitude(Double)} so that it can be more easily
     * set from request params.
     *
     * @param routeStartLatitude the routeStartLatitude to set
     */
    public void setRslat(final Double routeStartLatitude) {
        setRouteStartLatitude(routeStartLatitude);
    }


    /**
     * @return the routeStartLongitude
     */
    public Double getRouteStartLongitude() {
        return routeStartLongitude;
    }


    /**
     * @param routeStartLongitude the routeStartLongitude to set
     */
    public void setRouteStartLongitude(final Double routeStartLongitude) {
        this.routeStartLongitude = routeStartLongitude;
    }

    /**
     * Shorthand for {@link #setRouteStartLongitude(Double)} so that it can be
     * more easily set from request params.
     *
     * @param routeStartLongitude
     *            the routeStartLongitude to set
     */
    public void setRslon(final Double routeStartLongitude) {
        setRouteStartLongitude(routeStartLongitude);
    }

    /**
     * @return end coordinates for a route.
     */
    public WGS84Point getRouteEnd() {
        return new WGS84Point(getRouteEndLongitude(), getRouteEndLatitude());
    }


    /**
     * @return the routeEndLatitude
     */
    public Double getRouteEndLatitude() {
        return routeEndLatitude;
    }


    /**
     * @param routeEndLatitude the routeEndLatitude to set
     */
    public void setRouteEndLatitude(final Double routeEndLatitude) {
        this.routeEndLatitude = routeEndLatitude;
    }

    /**
     * Shorthand for {@link #setRouteEndLatitude(Double)} so that it can be more
     * easily set from request params.
     *
     * @param routeEndLatitude
     *            the routeEndLatitude to set
     */
    public void setRelat(final Double routeEndLatitude) {
        setRouteEndLatitude(routeEndLatitude);
    }


    /**
     * @return the routeEndLongitude
     */
    public Double getRouteEndLongitude() {
        return routeEndLongitude;
    }


    /**
     * @param routeEndLongitude the routeEndLongitude to set
     */
    public void setRouteEndLongitude(final Double routeEndLongitude) {
        this.routeEndLongitude = routeEndLongitude;
    }

    /**
     * Shorthand for {@link #setRouteEndLongitude(Double)} so that it can be more easily
     * set from request params.
     *
     * @param routeEndLongitude the routeEndLongitude to set
     */
    public void setRelon(final Double routeEndLongitude) {
        setRouteEndLongitude(routeEndLongitude);
    }


    /**
     * @return the routeLegStepCentreLatitude
     */
    public Double getRouteLegStepCentreLatitude() {
        return routeLegStepCentreLatitude;
    }


    /**
     * @param routeLegStepCentreLatitude the routeLegStepCentreLatitude to set
     */
    public void setRouteLegStepCentreLatitude(final Double routeLegStepCentreLatitude) {
        this.routeLegStepCentreLatitude = routeLegStepCentreLatitude;
    }

    /**
     * @param routeLegStepCentreLatitude the routeLegStepCentreLatitude to set
     */
    public void setRlsclat(final Double routeLegStepCentreLatitude) {
        setRouteLegStepCentreLatitude(routeLegStepCentreLatitude);
    }

    /**
     * @return the routeLegStepCentreLongitude
     */
    public Double getRouteLegStepCentreLongitude() {
        return routeLegStepCentreLongitude;
    }


    /**
     * @param routeLegStepCentreLongitude the routeLegStepCentreLongitude to set
     */
    public void setRouteLegStepCentreLongitude(final Double routeLegStepCentreLongitude) {
        this.routeLegStepCentreLongitude = routeLegStepCentreLongitude;
    }

    /**
     * @param routeLegStepCentreLongitude the routeLegStepCentreLongitude to set
     */
    public void setRlsclon(final Double routeLegStepCentreLongitude) {
        setRouteLegStepCentreLongitude(routeLegStepCentreLongitude);
    }

    /**
     * @return Center of the route leg step map to be manipulated.
     */
    public WGS84Point getRouteLegStepCentre() {
        return new WGS84Point(getRouteLegStepCentreLongitude(),
                getRouteLegStepCentreLatitude());

    }
}

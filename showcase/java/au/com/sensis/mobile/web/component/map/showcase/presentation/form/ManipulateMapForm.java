package au.com.sensis.mobile.web.component.map.showcase.presentation.form;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.mobile.web.component.map.business.MapDelegate.Action;
import au.com.sensis.mobile.web.component.map.model.MapLayerTransformer;
import au.com.sensis.wireless.manager.mapping.LocationMapUrl;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;

/**
 * Form for submitting map manipulation parameters.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ManipulateMapForm {

    // Current state of the map.
    private Double originalCentreLatitude;
    private Double originalCentreLongitude;
    private Double centreLatitude;
    private Double centreLongitude;
    private Double boundingBoxTopLeftLatitude;
    private Double boundingBoxTopLeftLongitude;
    private Double boundingBoxBottomRightLatitude;
    private Double boundingBoxBottomRightLongitude;
    private int zoomLevel;

    private String mapLayer;

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
    public int getZoomLevel() {

        return zoomLevel;
    }


    /**
     * @param zoomLevel the zoomLevel to set.
     */
    public void setZoomLevel(final int zoomLevel) {

        this.zoomLevel = zoomLevel;
    }

    /**
     * Shorthand for {@link #setZoomLevel(int)} so that it can be
     * more easily set from request params.
     *
     * @param zoomLevel the zoomLevel to set.
     */
    public void setMz(final int zoomLevel) {

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
     * Packs this {@link ManipulateMapForm} into a new {@link MapUrl},
     * discarding data that cannot be converted, such as {@link #getAction()}.
     *
     * @return this {@link ManipulateMapForm} packed into a new {@link MapUrl},
     * discarding data that cannot be converted, such as {@link #getAction()}.
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



}

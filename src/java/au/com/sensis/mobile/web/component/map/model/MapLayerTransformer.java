package au.com.sensis.mobile.web.component.map.model;

import java.util.HashMap;
import java.util.Map;

import au.com.sensis.wireless.manager.mapping.MapLayer;

/**
 * Transforms {@link MapLayer} objects to and from "short code" String
 * representations.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapLayerTransformer {

    /**
     * Value to be passed to {@link #setLyr(String)} corresponding to
     * {@link MapLayer#Map}.
     */
    public static final String MAP_LAYER_MAP = "m";

    /**
     * Value to be passed to {@link #setLyr(String)} corresponding to
     * {@link MapLayer#Photo}.
     */
    public static final String MAP_LAYER_PHOTO = "p";

    /**
     * Value to be passed to {@link #setLyr(String)} corresponding to
     * {@link MapLayer#PhotoWithStreets}.
     */
    public static final String MAP_LAYER_PHOTO_WITH_STREETS = "ps";

    // Use two maps instead of the Apache common's BidiMap so that we can be
    // type safe.
    private final Map<MapLayer, String> mapLayerToShortCodeMap;
    private final Map<String, MapLayer> shortCodeToMapLayerMap;

    /**
     * Default constructor.
     */
    public MapLayerTransformer() {
        mapLayerToShortCodeMap = new HashMap<MapLayer, String>();
        mapLayerToShortCodeMap.put(MapLayer.Map, MAP_LAYER_MAP);
        mapLayerToShortCodeMap.put(MapLayer.Photo, MAP_LAYER_PHOTO);
        mapLayerToShortCodeMap.put(MapLayer.PhotoWithStreets,
                MAP_LAYER_PHOTO_WITH_STREETS);

        shortCodeToMapLayerMap = new HashMap<String, MapLayer>();
        shortCodeToMapLayerMap.put(MAP_LAYER_MAP, MapLayer.Map);
        shortCodeToMapLayerMap.put(MAP_LAYER_PHOTO, MapLayer.Photo);
        shortCodeToMapLayerMap.put(MAP_LAYER_PHOTO_WITH_STREETS,
                MapLayer.PhotoWithStreets);
    }

    /**
     * Transform the given {@link MapLayer} to a short code String
     * representation.
     *
     * @param mapLayer
     *            {@link MapLayer} to transform.
     * @return short code String representation.
     */
    public String transformToShortCode(final MapLayer mapLayer) {
        final String transformedValue = mapLayerToShortCodeMap.get(mapLayer);
        if (transformedValue != null) {
            return transformedValue;
        } else {
            throw new IllegalStateException(
                    "Unrecognised mapLayer parameter: '" + mapLayer + "'");
        }
    }

    /**
     * Transform the given short code String
     * representation to a {@link MapLayer}.
     *
     * @param mapLayerShortCode
     *            Short code to to transform.
     * @return {@link MapLayer} corresponding to the short code.
     */
    public MapLayer transformFromShortCode(final String mapLayerShortCode) {
        final MapLayer transformedValue =
                shortCodeToMapLayerMap.get(mapLayerShortCode);
        if (transformedValue != null) {
            return transformedValue;
        } else {
            throw new IllegalStateException(
                    "Unrecognised mapLayerShortCode parameter: '"
                            + mapLayerShortCode + "'");
        }
    }
}

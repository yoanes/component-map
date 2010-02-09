package au.com.sensis.mobile.web.component.map.business;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.mobile.web.component.map.model.MapUrlHolder;
import au.com.sensis.wireless.manager.mapping.IconDescriptor;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesIconType;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Delegate for all map operations, such as:
 * <ol>
 * <li>getting a map centred at a particular location.</li>
 * <li>getting a map with a route rendered on it.</li>
 * <li>getting a map with a set of icons (POIs) rendered on it at specified locations.</li>
 * </ol>
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface MapDelegate {

    /**
     * Indicates the action being performed on a {@link MapUrl}. Note that this
     * enum is related to but not identical to the lower level
     * {@link au.com.sensis.wireless.manager.mapping.UserMapInteraction}. The
     * latter has a single ZOOM specification which must be used in conjunction
     * with a specific zoom level. The former is relative to the state of an existing
     * {@link MapUrl}. Furthermore, this {@link Action} provides a mapping between
     * short {@link String} codes that a web tier may use for request parameters.
     */
    public static enum Action {
        /** Zoom map in action. */
        ZOOM_IN("mzi"),

        /** Zoom map out action. */
        ZOOM_OUT("mzo"),

        /** Pan map north action. */
        MOVE_NORTH("mpn"),

        /** Pan map south action. */
        MOVE_SOUTH("mps"),

        /** Pan map east action. */
        MOVE_EAST("mpe"),

        /** Pan map west action. */
        MOVE_WEST("mpw"),

        /** No operation (ie. default if String conversion fails). */
        NO_OP("nop");

        private final String shortCode;

        private Action(final String value) {

            shortCode = value;
        }

        /**
         * Return the short shortCode of this Action.
         *
         * @return the short shortCode of this Action.
         */
        public String getShortCode() {
            return shortCode;
        }

        /**
         * Returns true if the given string shortCode matches this
         * {@link Action}'s shortCode.
         *
         * @param shortCode
         *            the shortCode to compare.
         *
         * @return whether the given string shortCode matches this
         *         {@link Action}'s shortCode.
         */
        private boolean equalsIgnoreCaseShortCode(final String shortCode) {

            return this.shortCode.equalsIgnoreCase(shortCode);
        }

        /**
         * Convert given shortCode to the enum type.
         *
         * @param shortCode
         *            String shortCode of the enum.
         *
         * @return a MapAction.
         */
        public static Action fromShortCode(final String shortCode) {

            if (StringUtils.isBlank(shortCode)) {

                return NO_OP;
            }

            for (final Action action : values()) {
                if (action.equalsIgnoreCaseShortCode(shortCode)) {
                    return action;
                }
            }

            return NO_OP;
        }

        /**
         * Returns true if this action corresponds to a pan action.
         *
         * @return true if this action corresponds to a pan action.
         */
        public boolean isPanAction() {

            return MOVE_NORTH.equals(this) || MOVE_SOUTH.equals(this)
                    || MOVE_EAST.equals(this) || MOVE_WEST.equals(this);
        }

        /**
         * Returns true if this action corresponds to a zoom action.
         *
         * @return true if this action corresponds to a zoom action.
         */
        public boolean isZoomAction() {

            return ZOOM_IN.equals(this) || ZOOM_OUT.equals(this);
        }
    }

    /**
     * Retrieve an initial map centred at a particular coordinate (as opposed to
     * manipulating an existing map - see
     * {@link #manipulateMap(WGS84Point, MapUrl, Action, MobileContext)}).
     *
     * @param mapCentre
     *            Coordinates of the centre of the map
     * @param zoomLevel
     *            Zoom level of the map. TODO: at the moment the range of
     *            allowed values is actually governed by the injection of the
     *            mobileToEmsZoomConversionMap into the underlying
     *            MobilesEMSManager. This is bad from the MapDelegate caller's
     *            perspective as it isn't clear what values can be passed in.
     * @param mapLayer
     *            The {@link MapLayer} that should be rendered to produce the
     *            map image.
     * @param centreIconType Type of icon to display at the centre of the map.
     * @param mobileContext
     *            Context of the user that the map is being retrieved for.
     * @return {@link MapUrlHolder}. May not be null.
     */
    MapUrlHolder getInitialMap(final WGS84Point mapCentre,
            final int zoomLevel, MapLayer mapLayer, MobilesIconType centreIconType,
            final MobileContext mobileContext);

    /**
     * Manipulate an existing map, such as panning or zooming it, or changing
     * the {@link MapLayer}. Note that to only change the {@link MapLayer}, you
     * should set the mapManipulationAction parameter to {@link Action#NO_OP}.
     *
     * @param originalMapCentrePoint
     *            The original map centre that was passed to
     *            {@link #getInitialMap(WGS84Point, int, MobileContext)}.
     *            Also contained by the {@link MapUrlHolder} returned by that
     *            method.
     * @param existingMapUrl
     *            The existing {@link MapUrl} to be manipulated. Contained in
     *            the {@link MapUrlHolder} returned by a previous call to
     *            {@link #getInitialMap(WGS84Point, int, MobileContext)} or
     *            this
     *            {@link #manipulateMap(WGS84Point, MapUrl, Action, MobileContext)}
     *            .
     * @param mapLayer
     *            The {@link MapLayer} that should be rendered to produce the
     *            map image.
     * @param originalCentreIconType
     *            Type of icon to display at the original centre of the map.
     * @param mapManipulationAction
     *            {@link Action} describing the type of manipulation to be
     *            performed.
     * @param mobileContext
     *            Context of the user that the map is being retrieved for.
     * @return {@link MapUrlHolder}. May not be null.
     */
    MapUrlHolder manipulateMap(final WGS84Point originalMapCentrePoint,
            final MapUrl existingMapUrl, MapLayer mapLayer,
            MobilesIconType originalCentreIconType,
            final Action mapManipulationAction,
            final MobileContext mobileContext);


    /**
     * Retrieve an initial POI map centred at a particular coordinate (as opposed to
     * manipulating an existing POI map - see
     * {@link #manipulatePoiMap(WGS84Point, MapUrl, MapLayer, List, Action, MobileContext).

     * @param mapCentre
     *            Coordinates of the centre of the map
     * @param mapLayer
     *            The {@link MapLayer} that should be rendered to produce the
     *            map image.
     * @param poiIcons
     *            List of icons to render on the map.
     * @param mobilesZoomThreshold
     *            Threshold that the zoom level of the map will not go below.
     * @param mobileContext
     *            Context of the user that the map is being retrieved for.
     * @return {@link MapUrlHolder}. May not be null.
     */
    MapUrlHolder getInitialPoiMap(final WGS84Point mapCentre,
            MapLayer mapLayer, final List<IconDescriptor> poiIcons,
            int mobilesZoomThreshold, final MobileContext mobileContext);

    /**
     * Manipulate an existing POI map, such as panning or zooming it, or changing
     * the {@link MapLayer}. Note that to only change the {@link MapLayer}, you
     * should set the mapManipulationAction parameter to {@link Action#NO_OP}.
     *
     * @param originalMapCentrePoint
     *            The original map centre that was passed to
     *            {@link #getInitialPoiMap(WGS84Point, MapLayer, List, int, MobileContext)}.
     *            Also contained by the {@link MapUrlHolder} returned by that
     *            method.
     * @param existingMapUrl
     *            The existing {@link MapUrl} to be manipulated. Contained in
     *            the {@link MapUrlHolder} returned by a previous call to
     *            {@link #getInitialPoiMap(WGS84Point, MapLayer, List, int, MobileContext) or
     *            this
     *            {@link #manipulatePoiMap(WGS84Point, MapUrl, MapLayer, List, Action,
     *            MobileContext)}.
     * @param mapLayer
     *            The {@link MapLayer} that should be rendered to produce the
     *            map image.
     * @param poiIcons
     *            List of icons to render on the map.
     * @param mapManipulationAction
     *            {@link Action} describing the type of manipulation to be
     *            performed.
     * @param mobileContext
     *            Context of the user that the map is being retrieved for.
     * @return {@link MapUrlHolder}. May not be null.
     */
    MapUrlHolder manipulatePoiMap(final WGS84Point originalMapCentrePoint,
            final MapUrl existingMapUrl, MapLayer mapLayer,
            final List<IconDescriptor> poiIcons,
            final Action mapManipulationAction,
            final MobileContext mobileContext);

}

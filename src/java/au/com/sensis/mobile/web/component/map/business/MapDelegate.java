package au.com.sensis.mobile.web.component.map.business;

import org.apache.commons.lang.StringUtils;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.mobile.web.component.map.model.MapResult;
import au.com.sensis.mobile.web.component.map.model.MapState;
import au.com.sensis.wireless.web.mobile.MobileContext;

public interface MapDelegate {

    /**
     * Indicates the action being performed on a listing.
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

    // TODO: should just return a raw MapUrl from the MapDelegateImpl like Heather
    // does.
    MapResult retrieveInitialMap(final GeocodedAddress geocodedAddress,
            final int zoomLevel, final MobileContext mobileContext);

    // TODO: should just return a raw MapUrl from the MapDelegateImpl like Heather does.
    MapResult manipulateMap(final MapState currentMapState,
            final Action mapManipulationAction,
            final MobileContext mobileContext);

}
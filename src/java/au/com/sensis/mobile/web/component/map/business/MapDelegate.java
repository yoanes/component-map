package au.com.sensis.mobile.web.component.map.business;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.mobile.web.component.core.sdpcommon.web.framework.MobileContext;
import au.com.sensis.mobile.web.component.map.model.MapCriteria;
import au.com.sensis.mobile.web.component.map.model.MapResult;
import au.com.sensis.mobile.web.component.map.model.MapState;
import au.com.sensis.mobile.web.component.map.service.LocationManager;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.UserMapInteraction;
import au.com.sensis.wireless.web.common.exception.ApplicationRuntimeException;
import au.com.sensis.wireless.web.common.validation.Validatable;

/**
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapDelegate
        implements Validatable {

    /** Not final so that we can inject a mock for unit testing. */
    private static Logger logger = Logger.getLogger(MapDelegate.class);
    
    private LocationManager locationManager;
    
	private int minZoom;
	private int maxZoom;
    
	public void validateState() throws ApplicationRuntimeException {
		// TODO Auto-generated method stub
		
	}
	
	public MapResult retrieveInitialMap(GeocodedAddress geocodedAddress, int zoomLevel, MobileContext mobileContext) {
		MapCriteria mapCriteria = new MapCriteria();
		mapCriteria.setCoordinates(geocodedAddress.getCoordinates());
		mapCriteria.setDirection(UserMapInteraction.NO_ACTION);
		
		// TODO: need to range check the zoom level.
		mapCriteria.setZoom(zoomLevel);
		
        packMobileContextIntoMapCriteria(mobileContext, mapCriteria);
		
		// bounding box not known yet.
		mapCriteria.setMapBoundingBox(null);
		return retrieveMapFromLocationManager(mapCriteria, mobileContext);

	}
	
	public MapResult manipulateMap(MapState currentMapState, Action mapManipulationAction, MobileContext mobileContext) {
		MapCriteria mapCriteria = new MapCriteria();
		mapCriteria.setCoordinates(currentMapState.getCoordinates());
		
		if (mapManipulationAction.isPanAction()) {
			mapCriteria.setDirection(transformActionToMapCriteriaDirection(mapManipulationAction));			
			mapCriteria.setMapBoundingBox(currentMapState.getMapBoundingBox());
			mapCriteria.setZoom(currentMapState.getZoomLevel());
		} else if (mapManipulationAction.isZoomAction()) {
			mapCriteria.setDirection(UserMapInteraction.ZOOM);
			mapCriteria.setZoom(calculateNewZoomLevel(currentMapState.getZoomLevel(), mapManipulationAction));
		} else {
			// Just retrieve the original map again.
			mapCriteria.setDirection(UserMapInteraction.NO_ACTION);
			mapCriteria.setZoom(currentMapState.getZoomLevel());
		}
		
		packMobileContextIntoMapCriteria(mobileContext, mapCriteria);

		return retrieveMapFromLocationManager(mapCriteria, mobileContext);
	}

	/**
	 * @param mobileContext
	 * @param mapCriteria
	 * @return
	 */
	private MapResult retrieveMapFromLocationManager(
			MapCriteria mapCriteria, MobileContext mobileContext) {
		MapUrl mapUrl = getLocationManager().retrieveMapUrlFromEms(mapCriteria, mobileContext.asUserContext());
		
		MapState mapState 
			= new MapState(mapUrl.getMapCentre(), mapUrl.getBoundingBox(), mapCriteria.getZoom());
		return new MapResult(mapUrl.getImageUrl(), mapState);
	}

	/**
	 * @param mobileContext
	 * @param mapCriteria
	 */
	private void packMobileContextIntoMapCriteria(MobileContext mobileContext,
			MapCriteria mapCriteria) {
		mapCriteria.setScreenWidth(mobileContext.getDevice().getPixelsX());
        mapCriteria.setScreenHeight(mobileContext.getDevice().getPixelsY());
        
        // TODO: mobileContext.getDevice().isLargeDevice() is not yet available in sdpcommon.
        //mapCriteria.setLargeDevice(mobileContext.getDevice().isLargeDevice());
	}
	
	private UserMapInteraction transformActionToMapCriteriaDirection(
			Action mapManipulationAction) {
		if (Action.MOVE_EAST.equals(mapManipulationAction)) {
			return UserMapInteraction.EAST;
		} else if (Action.MOVE_NORTH.equals(mapManipulationAction)) {
			return UserMapInteraction.NORTH;
		} else if (Action.MOVE_SOUTH.equals(mapManipulationAction)) {
			return UserMapInteraction.SOUTH;
		} else if (Action.MOVE_WEST.equals(mapManipulationAction)) {
			return UserMapInteraction.WEST;
		} else {
			throw new IllegalStateException(
					"mapManipulationAction argument should have corresponded to a pan action but it was not: '"
							+ mapManipulationAction + "'");
		}
	}

	private int calculateNewZoomLevel(
			int zoomLevel,
			au.com.sensis.mobile.web.component.map.business.MapDelegate.Action mapManipulationAction) {
		
		int newZoomValue;
		if (Action.ZOOM_IN.equals(mapManipulationAction)) {
			newZoomValue = zoomLevel - 1;
		} else {
			newZoomValue = zoomLevel + 1;
		}

        if ((newZoomValue >= getMinZoom()) && (newZoomValue <= getMaxZoom())) {

            return newZoomValue;

        } else {

        	if (logger.isEnabledFor(Level.WARN)) {
        		logger.warn("Attempted to apply zoom " + mapManipulationAction
						+ " to current level of " + zoomLevel
						+ ". Valid zoom range is " + getMinZoom() + " to "
						+ getMaxZoom() + " only.");
        	}
            
            return zoomLevel;
        }
		
		
	}

	/**
	 * @return the locationManager
	 */
	public LocationManager getLocationManager() {
		return locationManager;
	}

	/**
	 * @param locationManager the locationManager to set
	 */
	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}
	
	/**
	 * @return the minZoom
	 */
	public int getMinZoom() {
		return minZoom;
	}

	/**
	 * @param minZoom the minZoom to set
	 */
	public void setMinZoom(int minZoom) {
		this.minZoom = minZoom;
	}

	/**
	 * @return the maxZoom
	 */
	public int getMaxZoom() {
		return maxZoom;
	}

	/**
	 * @param maxZoom the maxZoom to set
	 */
	public void setMaxZoom(int maxZoom) {
		this.maxZoom = maxZoom;
	}


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

        private String shortCode;
        private Action(final String value) {

            this.shortCode = value;
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
         * Returns true if the given string shortCode matches this {@link Action}'s shortCode.
         *
         * @param shortCode the shortCode to compare.
         *
         * @return  whether the given string shortCode matches this {@link Action}'s shortCode.
         */
        private boolean equalsIgnoreCaseShortCode(final String shortCode) {

            return this.shortCode.equalsIgnoreCase(shortCode);
        }

        /**
         * Convert given shortCode to the enum type.
         *
         * @param shortCode String shortCode of the enum.
         *
         * @return  a MapAction.
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

            return MOVE_NORTH.equals(this)
                    || MOVE_SOUTH.equals(this) || MOVE_EAST.equals(this) || MOVE_WEST.equals(this);
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

}

package au.com.sensis.mobile.web.component.map.showcase.business.logic;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.sensis.address.AustralianState;
import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.simple.GeocodedAddressImpl;
import au.com.sensis.mobile.web.component.map.showcase.business.exception.LocationDelegateException;
import au.com.sensis.wireless.manager.ems.MobilesEMSManagerException;
import au.com.sensis.wireless.web.common.exception.ApplicationRuntimeException;
import au.com.sensis.wireless.web.common.validation.Validatable;

/**
 * @author Adrian.Koh2@sensis.com.au
 */
public class LocationDelegate
        implements Validatable {

    /** Not final so that we can inject a mock for unit testing. */
    private static Logger logger = Logger.getLogger(LocationDelegate.class);

    private static final WGS84Point MELBOURCE_VIC_COORDINATES
        = new WGS84Point(144.9628322, -37.8133895);

    private LocationManager locationManager;

    /**
     * Resolves the given location string to a single address. If multiple
     * matches are found, the first is returned. If no matches are found, a
     * default address of Melbourne is returned.
     *
     * @param location
     *            Location to be resolved.
     * @return Resolves the given location string to a single address.
     */
    public GeocodedAddress resolveToSingleLocation(final String location) {
        final List<GeocodedAddress> resolvedLocations =
                resolveLocation(location);
        GeocodedAddress addressToMap;
        if (resolvedLocations.isEmpty()) {
            addressToMap = new GeocodedAddressImpl();
            ((GeocodedAddressImpl) addressToMap).setSuburb("Melbourne");
            ((GeocodedAddressImpl) addressToMap).setState(AustralianState.VIC);
            ((GeocodedAddressImpl) addressToMap)
                    .setCoordinates(MELBOURCE_VIC_COORDINATES);
        } else if (resolvedLocations.size() > 1) {
            addressToMap = resolvedLocations.get(0);
        } else {
            addressToMap = resolvedLocations.get(0);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("addressToMap: " + addressToMap);
        }
        return addressToMap;

    }

    /**
     * Attempts to resolve entered location via EMS. It sets the return action page depending on the
     * number of results found. If EMS is down, it will simply set location object based on what has
     * been entered.
     *
     * @param location                      the location to be validated (may be null or empty).
     * @param provider                      the user's provider.
     * @param allowedToAutoLocateUsersState is the user potentially positionable.
     * @param positioningSessionId          the sessionId to use for positioning.
     * @param positioningType               the type of positioning to use (if necessary).
     * @param deviceProvidedWGS84PointHolder Holder for the device provided geocode.
     *
     * @return  ResultName.FAILURE if location failed, RESULT_LOCATION_SELECT if multiple locations
     *          where returned, otherwise null.
     */
    public List<GeocodedAddress> resolveLocation(final String location) {

        try {

            // Try to resolve the entered location via EMS. If EMS down, stop further validations.
            final List<GeocodedAddress> resolvedLocations
                    = getLocationManager().resolveLocation(location);

            if (resolvedLocations == null) {
                return new ArrayList<GeocodedAddress>();
            } else {
            	return resolvedLocations;
            }

        } catch (final MobilesEMSManagerException e) {

        	throw new LocationDelegateException("Error when resolving location", e);
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
	public void setLocationManager(final LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public void validateState() throws ApplicationRuntimeException {
		// TODO
	}

}

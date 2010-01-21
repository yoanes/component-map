package au.com.sensis.mobile.web.component.map.service;

import java.util.List;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.wireless.web.common.validation.Validatable;

/**
 * WPM interface to location functions.
 * This is an interface so we can substitute the real implementation with a stubbed broken
 * implementation to enable simulation of EMS being down for testing purposes.
 *
 * @author Elvis Svonja (Cloned and modified from WPM)
 */
public interface LocationManager
        extends Validatable {

    /**
     * Resolves the given input string against EMS. The result can return a single, multiple or no
     * matches.
     *
     * @param location  the location string to be resolved.
     *
     * @return  a {@link List} of {@link GeocodedAddress}es matching the passed in location string.
     */
    List<GeocodedAddress> resolveLocation(final String location);
}

package au.com.sensis.mobile.web.component.map.service;

import java.util.List;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.mobile.web.component.map.model.MapCriteria;
import au.com.sensis.sal.common.UserContext;
import au.com.sensis.wireless.manager.mapping.MapUrl;
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

//    /**
//     * Uses EMSManager to resolve the address based on the given geo-coordinates position.
//     *
//     * @param position  the point to be reverse geocoded.
//     *
//     * @return  the {@link GeocodedAddress} matching the passed in point.
//     */
//    GeocodedAddress resolveGeocodedLocation(final WGS84Point position);
//
    /**
     * Resolves the given input string against EMS. The result can return a single, multiple or no
     * matches.
     *
     * @param location  the location string to be resolved.
     *
     * @return  a {@link List} of {@link GeocodedAddress}es matching the passed in location string.
     */
    List<GeocodedAddress> resolveLocation(final String location);
//
//    /**
//     * Uses the location properties from the given address object to resolve to an actual EMS
//     * geocoded address.
//     *
//     * @param address   an {@link Address} with properties set for searching.
//     *
//     * @return  a GeocodedAddress matching the passed in location.
//     */
//    GeocodedAddress resolveSingleAddress(final Address address);
//
//    /**
//     * Returns the list of suburbs surrounding the passed in suburb.
//     *
//     * @param suburb    the suburb to get the surrounding suburbs for.
//     * @param postcode  the postcode to get the surrounding suburbs for.
//     *
//     * @return  the {@link List} of surrounding suburbs.
//     */
//    List<Address> getSurroundingSuburbs(final String suburb, final String postcode);

    /**
     * Obtains the {@link MapUrl} from EMS based on the map criteria.
     *
     * @param mapCriteria   contains the search parameters.
     * @param userContext   used to determine whether to charge the user for downloading the map.
     *
     * @return  {@link MapUrl}
     */
    MapUrl retrieveMapUrlFromEms(final MapCriteria mapCriteria, final UserContext userContext);
}

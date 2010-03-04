package au.com.sensis.mobile.web.component.map.showcase.business.logic;

import java.util.List;

import org.apache.log4j.Logger;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.simple.GeocodedAddressImpl;
import au.com.sensis.wireless.manager.ems.EMSManager;
import au.com.sensis.wireless.manager.ems.MobilesEMSManagerException;
import au.com.sensis.wireless.web.common.exception.ApplicationRuntimeException;
import au.com.sensis.wireless.web.common.validation.ValidatableUtils;

/**
 * @author Elvis Svonja (Cloned and modified from WPM)
 */
public class LocationManagerImpl
        implements LocationManager {

    private static final Logger LOGGER = Logger.getLogger(LocationManagerImpl.class);

    private EMSManager emsManager;

    /**
     * {@inheritDoc}
     */
    public GeocodedAddress resolveGeocodedLocation(final WGS84Point position) {

        try {

            return getEmsManager().reverseGeocode(position);

        } catch (final MobilesEMSManagerException e) {

            LOGGER.error("Problems reverse geocoding: " + position, e);

            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public List<GeocodedAddress> resolveLocation(final String location) {

        final GeocodedAddressImpl address = new GeocodedAddressImpl();
        address.setSuburb(location);

        final List<GeocodedAddress> resolvedList = getEmsManager().resolveAddress(address,
                address.getState());

        return resolvedList;
    }

    /**
     * {@inheritDoc}
     */
    public void validateState()
            throws ApplicationRuntimeException {

        ValidatableUtils.validateObjectIsNotNull(getEmsManager(), "emsManager");
//        ValidatableUtils.validateObjectIsNotNull(getEmsLocationUtilityManager(),
//                "emsLocationUtilityManager");
    }

    /**
     * @return  the emsManager.
     */
    protected EMSManager getEmsManager() {

        return emsManager;
    }

    /**
     * @param emsManager    the emsManager to set.
     */
    public void setEmsManager(final EMSManager emsManager) {

        this.emsManager = emsManager;
    }
}

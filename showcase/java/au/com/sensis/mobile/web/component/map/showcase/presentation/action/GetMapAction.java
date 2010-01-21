package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import java.util.List;

import org.apache.log4j.Logger;

import au.com.sensis.address.AustralianState;
import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.simple.GeocodedAddressImpl;
import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.MapUrlHolder;
import au.com.sensis.mobile.web.component.map.service.LocationManager;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Demonstrates how to get an initial map using the {@link MapDelegate}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GetMapAction extends BusinessAction implements
        ModelDriven<MapForm> {

    private static Logger logger = Logger.getLogger(GetMapAction.class);

    private MapForm mapForm;

    private LocationDelegate locationDelegate;
    private LocationManager locationManager;
    private MapDelegate mapDelegate;

    private MapUrlHolder mapUrlHolder;

    private int defaultZoom;

    /**
     * Executes this action and returns a result name.
     * @return result name.
     */
    public String execute() {
        final GeocodedAddress addressToMap =
                determineSingleLocationToMap(getModel().getLocation());

        // Example of how to use the MapDelegate to get an initial map.
        final MapUrlHolder mapUrlHolder =
                getMapDelegate().retrieveInitialMap(addressToMap.getCoordinates(),
                        getDefaultZoom(), getContext());
        setMapUrlHolder(mapUrlHolder);

        if (logger.isDebugEnabled()) {
            logger.debug("mapUrl found: "
                    + getMapUrlHolder().getMapUrl());
        }

        return ResultName.SUCCESS;
    }

    /**
     * TODO: Hacky code just for this showcase prototype to quickly get a
     * {@link GeocodedAddress} to map based on the entered location.
     *
     * @param location
     * @return
     */
    private GeocodedAddress determineSingleLocationToMap(final String location) {
        final List<GeocodedAddress> resolvedLocations =
                getLocationDelegate().resolveLocation(getModel().getLocation());
        GeocodedAddress addressToMap;
        if (resolvedLocations.isEmpty()) {
            addressToMap = new GeocodedAddressImpl();
            ((GeocodedAddressImpl) addressToMap).setSuburb("Melbourne");
            ((GeocodedAddressImpl) addressToMap).setState(AustralianState.VIC);
            final double camberwellVicLongitude = 145.0730816;
            final double camberwellVicLatitude = -37.8388769;
            ((GeocodedAddressImpl) addressToMap).setCoordinates(
                    new WGS84Point(camberwellVicLongitude, camberwellVicLatitude));
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
     * @return Model being used to drive this action.
     * @see com.opensymphony.xwork2.ModelDriven#getModel()
     */
    public MapForm getModel() {
        if (mapForm == null) {
            mapForm = new MapForm();
        }
        return mapForm;
    }

    /**
     * @return the locationDelegate
     */
    public LocationDelegate getLocationDelegate() {
        return locationDelegate;
    }

    /**
     * @param locationDelegate
     *            the locationDelegate to set
     */
    public void setLocationDelegate(final LocationDelegate locationDelegate) {
        this.locationDelegate = locationDelegate;
    }

    /**
     * @return the locationManager
     */
    public LocationManager getLocationManager() {
        return locationManager;
    }

    /**
     * @param locationManager
     *            the locationManager to set
     */
    public void setLocationManager(final LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    /**
     * @return the defaultZoom.
     */
    public int getDefaultZoom() {
        return defaultZoom;
    }

    /**
     * @return the mapUrlHolder
     */
    public MapUrlHolder getMapUrlHolder() {
        return mapUrlHolder;
    }

    /**
     * @param mapUrlHolder
     *            the mapUrlHolder to set
     */
    public void setMapUrlHolder(final MapUrlHolder mapUrlHolder) {
        this.mapUrlHolder = mapUrlHolder;
    }

    /**
     * @param defaultZoom
     *            the defaultZoom to set
     */
    public void setDefaultZoom(final int defaultZoom) {
        this.defaultZoom = defaultZoom;
    }

    /**
     * @return the mapDelegate
     */
    public MapDelegate getMapDelegate() {
        return mapDelegate;
    }

    /**
     * @param mapDelegate
     *            the mapDelegate to set
     */
    public void setMapDelegate(final MapDelegate mapDelegate) {
        this.mapDelegate = mapDelegate;
    }

}

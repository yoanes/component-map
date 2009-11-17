package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import java.util.List;

import org.apache.log4j.Logger;

import au.com.sensis.address.AustralianState;
import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.address.WGS84Point;
import au.com.sensis.address.simple.GeocodedAddressImpl;
import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.MapResult;
import au.com.sensis.mobile.web.component.map.service.LocationManager;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

import com.opensymphony.xwork2.ModelDriven;

public class GetMapAction extends BusinessAction implements
        ModelDriven<MapForm> {

    private static Logger logger = Logger.getLogger(GetMapAction.class);

    private MapForm mapForm;

    private LocationDelegate locationDelegate;
    private LocationManager locationManager;
    private MapDelegate mapDelegateImpl;

    private MapResult mapResult;

    private int defaultZoom;

    public String execute() {
        final GeocodedAddress addressToMap =
                determineSingleLocationToMap(getModel().getLocation());

        // Example of how to use the MapDelegateImpl to get an initial map.
        // TODO: should just return a raw MapUrl from the MapDelegateImpl like Heather does.
        // This is an sdpcommon interface that theoretically contains everything that we need.
        // However, can't be stuffed trying to do this upgrade now because MapUrl
        // from 1.0-050 doesn't contain getZoom but upgrading to 1.0-057 requires
        // more work than I can muster right now. I do prefer my class names and the
        // separation of the MapState from the imageUrl but oh well.
        final MapResult mapResult =
                getMapDelegate().retrieveInitialMap(addressToMap,
                        getDefaultZoom(), getContext());
        setMapResult(mapResult);

        if (logger.isDebugEnabled()) {
            logger.debug("mapUrl found (yep !!!!!): "
                    + getMapResult().getMapUrl());
        }

        return ResultName.SUCCESS;
    }

    /**
     * Hacky code just for this showcase prototype to quickly get a
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
     * @return the mapResult
     */
    public MapResult getMapResult() {
        return mapResult;
    }

    /**
     * @param mapResult
     *            the mapResult to set
     */
    public void setMapResult(final MapResult mapResult) {
        this.mapResult = mapResult;
    }

    /**
     * @param defaultZoom
     *            the defaultZoom to set
     */
    public void setDefaultZoom(final int defaultZoom) {
        this.defaultZoom = defaultZoom;
    }

    /**
     * @return the mapDelegateImpl
     */
    public MapDelegate getMapDelegate() {
        return mapDelegateImpl;
    }

    /**
     * @param mapDelegateImpl
     *            the mapDelegateImpl to set
     */
    public void setMapDelegate(final MapDelegate mapDelegateImpl) {
        this.mapDelegateImpl = mapDelegateImpl;
    }

}

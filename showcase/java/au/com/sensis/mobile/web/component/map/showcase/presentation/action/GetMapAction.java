package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import java.util.List;

import org.apache.log4j.Logger;

import au.com.sensis.address.AustralianState;
import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.address.simple.GeocodedAddressImpl;
import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.MapResult;
import au.com.sensis.mobile.web.component.map.service.LocationManager;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

import com.opensymphony.xwork2.ModelDriven;

public class GetMapAction extends BusinessAction implements ModelDriven<MapForm>{
	
	private static Logger logger = Logger.getLogger(GetMapAction.class);
	
	private MapForm mapForm;
	
	private LocationDelegate locationDelegate;
	private LocationManager locationManager;
	private MapDelegate mapDelegate;
	
	private MapResult mapResult;
	
	private int defaultZoom;
	
	public String execute() {
		GeocodedAddress addressToMap = determineSingleLocationToMap(getModel().getLocation());
		
		// Example of how to use the MapDelegate to get an initial map.
		MapResult mapResult = getMapDelegate().retrieveInitialMap(addressToMap, getDefaultZoom(), getContext());
		setMapResult(mapResult);
		
		if (logger.isDebugEnabled()) {
			logger.debug("mapUrl found (yep !!!!!): " + getMapResult().getMapUrl());
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
	private GeocodedAddress determineSingleLocationToMap(String location) {
		List<GeocodedAddress> resolvedLocations =
				getLocationDelegate().resolveLocation(getModel().getLocation());
		GeocodedAddress addressToMap;
		if (resolvedLocations.isEmpty()) {
			addressToMap = new GeocodedAddressImpl();
			addressToMap.setSuburb("Melbourne");
			addressToMap.setState(AustralianState.VIC);
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
	 * @param locationDelegate the locationDelegate to set
	 */
	public void setLocationDelegate(LocationDelegate locationDelegate) {
		this.locationDelegate = locationDelegate;
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
     * @return  the defaultZoom.
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
	 * @param mapResult the mapResult to set
	 */
	public void setMapResult(MapResult mapResult) {
		this.mapResult = mapResult;
	}


	/**
	 * @param defaultZoom the defaultZoom to set
	 */
	public void setDefaultZoom(int defaultZoom) {
		this.defaultZoom = defaultZoom;
	}

	/**
	 * @return the mapDelegate
	 */
	public MapDelegate getMapDelegate() {
		return mapDelegate;
	}

	/**
	 * @param mapDelegate the mapDelegate to set
	 */
	public void setMapDelegate(MapDelegate mapDelegate) {
		this.mapDelegate = mapDelegate;
	}

}

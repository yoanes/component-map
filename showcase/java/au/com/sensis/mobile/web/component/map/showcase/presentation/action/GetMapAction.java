package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import org.apache.log4j.Logger;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationManager;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.InteractivePoiInfo;

/**
 * Demonstrates how to get an initial map using the {@link #getMapDelegate()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GetMapAction extends AbstractMapAction {

    private static Logger logger = Logger.getLogger(GetMapAction.class);

    private MapForm mapForm;

    private LocationManager locationManager;
    private int defaultZoom;

    /* internal flag to set whether to use multiple or 1 popup content 
     * this is for showcase purpose only. 
     * */
    private boolean onePopup;
    
    private void setOnePopup(final boolean onePopup) {
    	this.onePopup = true;
    }
    
    public boolean isOnePopup() {
    	return onePopup;
    }
    
    /**
     * Executes this action and returns a result name.
     * @return result name.
     */
    public String execute() {
        final GeocodedAddress addressToMap =
                getLocationDelegate().resolveToSingleLocation(getModel().getLocation());

        InteractivePoiInfo centerPoiInfo;
        
        if(getModel().getLocation().equals("3000")) {
        	centerPoiInfo = new InteractivePoiInfo("Melbourne", "VIC - 3000", "poi-".concat(Integer.toString(0)), InteractivePoiInfo.NAMED);
        	setDocked(true);
        	setOnePopup(true);
        }
        
        else if(getModel().getLocation().equals("3002")) {
        	centerPoiInfo = new InteractivePoiInfo("East Melbourne", "", "poi-".concat(Integer.toString(0)), InteractivePoiInfo.NONGEO);
        }
        
        else if(getModel().getLocation().equals("3004")) {
        	centerPoiInfo = new InteractivePoiInfo("Melbourne Port Philip", "", "poi-".concat(Integer.toString(0)), InteractivePoiInfo.SLIMNONGEO);
        }
        
        else  { centerPoiInfo = new InteractivePoiInfo("", "", "poi-".concat(Integer.toString(0)), ""); }
        
        // Example of how to use the MapDelegate to get an initial map.
        final Map map =
                getMapDelegate().getInitialMap(addressToMap.getCoordinates(),
                        getDefaultZoom(), MapLayer.Map,
                        getModel().getCursorType(), getContext(), centerPoiInfo);
        setMap(map);

        if (logger.isDebugEnabled()) {
            logger.debug("mapUrl found: "
                    + getMap().getMapUrl());
        }

        return successOrAjaxSuccess();
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
     * @param defaultZoom
     *            the defaultZoom to set
     */
    public void setDefaultZoom(final int defaultZoom) {
        this.defaultZoom = defaultZoom;
    }
}

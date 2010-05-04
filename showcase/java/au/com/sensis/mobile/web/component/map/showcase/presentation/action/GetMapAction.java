package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import org.apache.log4j.Logger;

import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationManager;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.wireless.manager.mapping.MapLayer;

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

    /**
     * Executes this action and returns a result name.
     * @return result name.
     */
    public String execute() {
        final GeocodedAddress addressToMap =
                getLocationDelegate().resolveToSingleLocation(getModel().getLocation());

        // Example of how to use the MapDelegate to get an initial map.
        final Map map =
                getMapDelegate().getInitialMap(addressToMap.getCoordinates(),
                        getDefaultZoom(), MapLayer.Map,
                        getModel().getCursorType(), getContext());
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

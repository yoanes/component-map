package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import org.apache.log4j.Logger;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.MapUrlHolder;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;
import au.com.sensis.wireless.manager.mapping.MapLayer;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Demonstrates how to get an initial POI map using the {@link MapDelegate}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class GetPoisAction extends BusinessAction implements
        ModelDriven<MapForm> {

    private static final WGS84Point MELBOURCE_VIC_COORDINATES
        = new WGS84Point(144.9628322, -37.8133895);

    private static Logger logger = Logger.getLogger(GetPoisAction.class);

    private MapForm mapForm;

    private LocationDelegate locationDelegate;
    private MapDelegate mapDelegate;

    private MapUrlHolder mapUrlHolder;

    private int defaultZoom;

    /**
     * Executes this action and returns a result name.
     *
     * @return result name.
     */
    public String execute() {
        final MapUrlHolder mapUrlHolder =
                getMapDelegate().getInitialPoiMap(
                        MELBOURCE_VIC_COORDINATES, MapLayer.Map,
                        PoiResult.createWhereisMobileCarsNearbyMelbourneIconDescriptors(),
                        defaultZoom, getContext());

        setMapUrlHolder(mapUrlHolder);

        if (logger.isDebugEnabled()) {
            logger.debug("mapUrl found: " + getMapUrlHolder().getMapUrl());
        }

        return ResultName.SUCCESS;

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

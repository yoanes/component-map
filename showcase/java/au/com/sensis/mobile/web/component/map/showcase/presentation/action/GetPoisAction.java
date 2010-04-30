package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import org.apache.log4j.Logger;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.Map;
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
    private static final WGS84Point TOORAK_VIC_COORDINATES
        = new WGS84Point(145.0155578, -37.841882);
    private static final WGS84Point LOC_140_CHURCH_ST_BRIGHTON_VIC_COORDINATES
        = new WGS84Point(144.996491, -37.916238);
    private static final WGS84Point TULLAMARINE_VIC_COORDINATES
        = new WGS84Point(144.8747924, -37.701235);
    private static final WGS84Point LOC_142_CHURCH_ST_BRIGHTON_VIC_COORDINATES
        = new WGS84Point(144.99653, -37.91627);
    private static final WGS84Point POSTCODE_3006_COORDINATES
    = new WGS84Point(144.9612317, -37.826947);

    private static final String CARS_NEAR_MELBOURNE_VIC_SEARCH_KEY = "carsNearMelbourneVic";
    private static final String BARS_NEAR_TOORAK_VIC_SEARCH_KEY = "barsNearToorakVic";
    private static final String BASSETT_SMITH_VALUERS_NEAR_140_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
        = "bassettSmithValuersNear140ChurchStBrightonVic";
    private static final String CAFE_NEAR_TULLAMARINE_VIC_SEARCH_KEY = "cafeNearTullamarineVic";
    private static final String BASSETT_SMITH_VALUERS_NEAR_142_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
        = "bassettSmithValuersNear142ChurchStBrightonVic";
    private static final String RESTAURANTS_NEAR_3006_SEARCH_KEY = "restaurantsNear3006";


    private static Logger logger = Logger.getLogger(GetPoisAction.class);

    private MapForm mapForm;

    private LocationDelegate locationDelegate;
    private MapDelegate mapDelegate;

    private Map map;

    private int defaultZoom;

    /**
     * Executes this action and returns a result name.
     *
     * @return result name.
     */
    public String execute() {
        Map map = null;

        if (CARS_NEAR_MELBOURNE_VIC_SEARCH_KEY.equals(getModel().getSearch())) {
            map = getMapDelegate().getInitialPoiMap(
                    MELBOURCE_VIC_COORDINATES, MapLayer.Map,
                    PoiResult.createWhereisMobileCarsNearbyMelbourneIconDescriptors(),
                    defaultZoom, getContext());
        } else if (BARS_NEAR_TOORAK_VIC_SEARCH_KEY.equals(getModel().getSearch())) {
            map = getMapDelegate().getInitialPoiMap(
                    TOORAK_VIC_COORDINATES, MapLayer.Map,
                    PoiResult.createWhereisMobileBarsNearbyToorakVicIconDescriptors(),
                    defaultZoom, getContext());
        } else if (BASSETT_SMITH_VALUERS_NEAR_140_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
                .equals(getModel().getSearch())) {
            map = getMapDelegate().getInitialPoiMap(
                LOC_140_CHURCH_ST_BRIGHTON_VIC_COORDINATES, MapLayer.Map,
                PoiResult.createWhereisMobileBassettSmithValuersNearbyBrightonVicIconDescriptors(),
                defaultZoom, getContext());
        } else if (CAFE_NEAR_TULLAMARINE_VIC_SEARCH_KEY
                .equals(getModel().getSearch())) {
            map = getMapDelegate().getInitialPoiMap(
                    TULLAMARINE_VIC_COORDINATES, MapLayer.Map,
                    PoiResult.createWhereisMobileCafeNearbyTullamarineVicIconDescriptors(),
                    defaultZoom, getContext());
        } else if (BASSETT_SMITH_VALUERS_NEAR_142_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
                .equals(getModel().getSearch())) {
            // This test case tests that the poi maps don't fall over if there is a single
            // result at the same (lat, lon) as the search centre.
            map = getMapDelegate().getInitialPoiMap(
                    LOC_142_CHURCH_ST_BRIGHTON_VIC_COORDINATES, MapLayer.Map,
                    PoiResult.createSingleResultAt142ChurchStBrightonVicIconDescriptors(),
                    defaultZoom, getContext());
        } else if (RESTAURANTS_NEAR_3006_SEARCH_KEY
                .equals(getModel().getSearch())) {
            // This test case tests that the poi maps don't fall over if there is a single
            // result at the same (lat, lon) as the search centre.
            map = getMapDelegate().getInitialPoiMap(
                    POSTCODE_3006_COORDINATES, MapLayer.Map,
                    PoiResult.createWhereisMobileRestaurantsNearby3006WithPageSize10IconDescriptors(),
                    defaultZoom, getContext());
        } else {
            throw new UnsupportedOperationException("Unsupported search key: '"
                    + getModel().getSearch() + "'");
        }

        setMap(map);

        if (logger.isDebugEnabled()) {
            logger.debug("mapUrl found: " + getMap().getMapUrl());
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
     * @return the map
     */
    public Map getMap() {
        return map;
    }

    /**
     * @param map
     *            the map to set
     */
    public void setMap(final Map map) {
        this.map = map;
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

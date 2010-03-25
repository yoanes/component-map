package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Demonstrates how to manipulate (eg. pan or zoom) an existing POI map using
 * the {@link MapDelegate}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ManipulatePoiMapAction extends BusinessAction implements
        ModelDriven<ManipulateMapForm> {

    private static final String CARS_NEAR_MELBOURNE_VIC_SEARCH_KEY =
            "carsNearMelbourneVic";
    private static final String BARS_NEAR_TOORAK_VIC_SEARCH_KEY =
            "barsNearToorakVic";
    private static final String BASSETT_SMITH_VALUERS_NEAR_140_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
        = "bassettSmithValuersNear140ChurchStBrightonVic";
    private static final String CAFE_NEAR_TULLAMARINE_VIC_SEARCH_KEY = "cafeNearTullamarineVic";

    private ManipulateMapForm manipulateMapForm;

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
            map = getMapDelegate()
                    .manipulatePoiMap(
                            getModel().getOrignalMapCentre(),
                            getModel().getMapUrl(),
                            getModel().getMapLayer(),
                            PoiResult
                                    .createWhereisMobileCarsNearbyMelbourneIconDescriptors(),
                            getDefaultZoom(),
                            getModel().getAction(), getContext());
        } else if (BARS_NEAR_TOORAK_VIC_SEARCH_KEY.equals(getModel()
                .getSearch())) {
            map = getMapDelegate()
                    .manipulatePoiMap(
                            getModel().getOrignalMapCentre(),
                            getModel().getMapUrl(),
                            getModel().getMapLayer(),
                            PoiResult
                                    .createWhereisMobileBarsNearbyToorakVicIconDescriptors(),
                            getDefaultZoom(),
                            getModel().getAction(), getContext());
        } else if (BASSETT_SMITH_VALUERS_NEAR_140_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
                .equals(getModel().getSearch())) {
            map = getMapDelegate().manipulatePoiMap(
                getModel().getOrignalMapCentre(),
                getModel().getMapUrl(),
                getModel().getMapLayer(),
                PoiResult
                        .createWhereisMobileBassettSmithValuersNearbyBrightonVicIconDescriptors(),
                getDefaultZoom(),
                getModel().getAction(), getContext());
        } else if (CAFE_NEAR_TULLAMARINE_VIC_SEARCH_KEY
                .equals(getModel().getSearch())) {
            map = getMapDelegate().manipulatePoiMap(
                    getModel().getOrignalMapCentre(),
                    getModel().getMapUrl(),
                    getModel().getMapLayer(),
                    PoiResult.createWhereisMobileCafeNearbyTullamarineVicIconDescriptors(),
                    getDefaultZoom(),
                    getModel().getAction(), getContext());
        } else {
            throw new UnsupportedOperationException("Unsupported search key: '"
                    + getModel().getSearch() + "'");
        }

        setMap(map);

        return ResultName.SUCCESS;
    }

    /**
     * @return Model being used to drive this action.
     * @see com.opensymphony.xwork2.ModelDriven#getModel()
     */
    public ManipulateMapForm getModel() {
        if (manipulateMapForm == null) {
            manipulateMapForm = new ManipulateMapForm();
        }
        return manipulateMapForm;
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
     * @return the defaultZoom
     */
    public int getDefaultZoom() {
        return defaultZoom;
    }

    /**
     * @param defaultZoom the defaultZoom to set
     */
    public void setDefaultZoom(final int defaultZoom) {
        this.defaultZoom = defaultZoom;
    }

}

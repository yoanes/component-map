package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;

/**
 * Demonstrates how to manipulate (eg. pan or zoom) an existing POI map using
 * the {@link #getMapDelegate()}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ManipulatePoiMapAction extends AbstractMapAction {

    private static final String CARS_NEAR_MELBOURNE_VIC_SEARCH_KEY =
            "carsNearMelbourneVic";
    private static final String BARS_NEAR_TOORAK_VIC_SEARCH_KEY =
            "barsNearToorakVic";
    private static final String BASSETT_SMITH_VALUERS_NEAR_140_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
        = "bassettSmithValuersNear140ChurchStBrightonVic";
    private static final String CAFE_NEAR_TULLAMARINE_VIC_SEARCH_KEY = "cafeNearTullamarineVic";
    private static final String BASSETT_SMITH_VALUERS_NEAR_142_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
        = "bassettSmithValuersNear142ChurchStBrightonVic";
    private static final String RESTAURANTS_NEAR_3006_SEARCH_KEY = "restaurantsNear3006";

    private ManipulateMapForm manipulateMapForm;

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
        } else if (BASSETT_SMITH_VALUERS_NEAR_142_CHURCH_ST_BRIGHTON_VIC_SEARCH_KEY
                .equals(getModel().getSearch())) {
            // This test case tests that the poi maps don't fall over if there is a single
            // result at the same (lat, lon) as the search centre.
            map = getMapDelegate().manipulatePoiMap(
                    getModel().getOrignalMapCentre(),
                    getModel().getMapUrl(),
                    getModel().getMapLayer(),
                    PoiResult.createSingleResultAt142ChurchStBrightonVicIconDescriptors(),
                    getDefaultZoom(),
                    getModel().getAction(), getContext());
        } else if (RESTAURANTS_NEAR_3006_SEARCH_KEY
                .equals(getModel().getSearch())) {
            map = getMapDelegate().manipulatePoiMap(
                    getModel().getOrignalMapCentre(),
                    getModel().getMapUrl(),
                    getModel().getMapLayer(),
                    PoiResult
                        .createWhereisMobileRestaurantsNearby3006WithPageSize10IconDescriptors(),
                    getDefaultZoom(),
                    getModel().getAction(), getContext());
        } else {
            throw new UnsupportedOperationException("Unsupported search key: '"
                    + getModel().getSearch() + "'");
        }

        setMap(map);

        return successOrAjaxSuccess();
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

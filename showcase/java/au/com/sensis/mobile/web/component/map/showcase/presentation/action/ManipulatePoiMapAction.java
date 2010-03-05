package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.address.WGS84Point;
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

    private static final WGS84Point MELBOURCE_VIC_COORDINATES =
            new WGS84Point(144.9628322, -37.8133895);
    private static final WGS84Point TOORAK_VIC_COORDINATES =
            new WGS84Point(145.0155578, -37.841882);

    private static final String CARS_NEAR_MELBOURNE_VIC_SEARCH_KEY =
            "carsNearMelbourneVic";
    private static final String BARS_NEAR_TOORAK_VIC_SEARCH_KEY =
            "barsNearToorakVic";

    private ManipulateMapForm manipulateMapForm;

    private LocationDelegate locationDelegate;
    private MapDelegate mapDelegate;

    private Map map;

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

}

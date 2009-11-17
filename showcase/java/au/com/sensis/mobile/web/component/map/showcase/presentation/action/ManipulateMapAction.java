package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.MapResult;
import au.com.sensis.mobile.web.component.map.model.MapState;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

import com.opensymphony.xwork2.ModelDriven;

public class ManipulateMapAction extends BusinessAction implements
        ModelDriven<ManipulateMapForm> {

    private ManipulateMapForm manipulateMapForm;

    private LocationDelegate locationDelegate;
    private MapDelegate mapDelegateImpl;

    private MapResult mapResult;

    public String execute() {
        final MapState mapState = getModel().asMapState();

        // TODO: should just return a raw MapUrl from the MapDelegateImpl like Heather does.
        // This is an sdpcommon interface that theoretically contains everything that we need.
        // However, can't be stuffed trying to do this upgrade now because MapUrl
        // from 1.0-050 doesn't contain getZoom but upgrading to 1.0-057 requires
        // more work than I can muster right now. I do prefer my class names and the
        // separation of the MapState from the imageUrl but oh well.
        final MapResult mapResult =
                getMapDelegate().manipulateMap(mapState,
                        getModel().getAction(), getContext());
        setMapResult(mapResult);

        return ResultName.SUCCESS;
    }

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

}

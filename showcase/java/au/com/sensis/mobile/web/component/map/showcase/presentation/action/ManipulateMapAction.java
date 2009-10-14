package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.MapResult;
import au.com.sensis.mobile.web.component.map.model.MapState;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

import com.opensymphony.xwork2.ModelDriven;

public class ManipulateMapAction extends BusinessAction implements ModelDriven<ManipulateMapForm>{
	
	private ManipulateMapForm manipulateMapForm;
	
	private LocationDelegate locationDelegate;
	private MapDelegate mapDelegate;
	
	private MapResult mapResult;
	
	public String execute() {
		MapState mapState = getModel().asMapState();
	
		MapResult mapResult = getMapDelegate().manipulateMap(mapState, getModel().getAction(), getContext());
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
	 * @param locationDelegate the locationDelegate to set
	 */
	public void setLocationDelegate(LocationDelegate locationDelegate) {
		this.locationDelegate = locationDelegate;
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

}

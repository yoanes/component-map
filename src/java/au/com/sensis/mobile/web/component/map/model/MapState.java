package au.com.sensis.mobile.web.component.map.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;

/**
 * Encapsulates the current state of the map so that we can subsequently perform operations on it, such as panning and zooming.
 * This state does not include the map image URL, which can be derived from the state. 
 * 
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapState {

	/**
	 * Centre of the map.
	 */
	private WGS84Point coordinates;
	
	/**
	 * Bounding box state is required if you subsequently wish to pan the map.
	 */
	private MobilesBoundingBox mapBoundingBox;
	
	/**
	 * Zoom level state is required if you subsequently wish to zoom the map.
	 */
	private int zoomLevel;

	public MapState(WGS84Point coordinates,
			MobilesBoundingBox mapBoundingBox, int zoomLevel) {
		this.coordinates = coordinates;
		this.mapBoundingBox = mapBoundingBox;
		this.zoomLevel = zoomLevel;
	}

	/**
	 * @return the coordinates
	 */
	public WGS84Point getCoordinates() {
		return coordinates;
	}

	/**
	 * @return the mapBoundingBox
	 */
	public MobilesBoundingBox getMapBoundingBox() {
		return mapBoundingBox;
	}

	/**
	 * @return the zoomLevel
	 */
	public int getZoomLevel() {
		return zoomLevel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		return ToStringBuilder.reflectionToString(this);
	}
}

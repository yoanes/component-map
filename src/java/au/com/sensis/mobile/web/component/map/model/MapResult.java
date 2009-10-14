package au.com.sensis.mobile.web.component.map.model;

public class MapResult {
	
	private final MapState mapState;

	private final String mapUrl;

	public MapResult(String mapUrl, MapState mapState) {
		this.mapUrl = mapUrl;
		this.mapState = mapState;
	}

	/**
	 * @return the mapUrl
	 */
	public String getMapUrl() {
		return mapUrl;
	}

	/**
	 * @return the mapState
	 */
	public MapState getMapState() {
		return mapState;
	}
	
	
}

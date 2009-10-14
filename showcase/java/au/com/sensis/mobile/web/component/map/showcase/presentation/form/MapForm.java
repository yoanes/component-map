package au.com.sensis.mobile.web.component.map.showcase.presentation.form;

import org.apache.commons.lang.StringUtils;

public class MapForm {

	private String location;

	/**
	 * @return the location
	 */
	public String getLocation() {
		if (location == null) {
			return StringUtils.EMPTY;
		}
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String location) {
		this.location = location;
	}
	
	/**
	 * Shorthand for {@link #setLocation(String)}.
	 * 
	 * @param location the location to set
	 */
	public void setMl(String location) {
		setLocation(location);
	}
	
}

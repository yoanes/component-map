package au.com.sensis.mobile.web.component.map.showcase.presentation.form;

import org.apache.commons.lang.StringUtils;

/**
 * Struts2 form object for maps.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
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
     * @param location
     *            the location to set
     */
    public void setLocation(final String location) {
        this.location = location;
    }

    /**
     * Shorthand for {@link #setLocation(String)}.
     *
     * @param location
     *            the location to set
     */
    public void setMl(final String location) {
        setLocation(location);
    }

}

package au.com.sensis.mobile.web.component.map.showcase.presentation.form;

import org.apache.commons.lang.StringUtils;

/**
 * Struts2 form object for maps.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapForm {

    private String location;

    private String routeStartAddress;
    private String routeEndAddress;

    private String search;

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

    /**
     * @return the routeStartAddress
     */
    public String getRouteStartAddress() {
        return routeStartAddress;
    }

    /**
     * @param routeStartAddress the routeStartAddress to set
     */
    public void setRouteStartAddress(final String routeStartAddress) {
        this.routeStartAddress = routeStartAddress;
    }

    /**
     * Shorthand for {@link #setRouteStartAddress(String)}.
     *
     * @param routeStartAddress the routeStartAddress to set
     */
    public void setRsa(final String routeStartAddress) {
        setRouteStartAddress(routeStartAddress);
    }

    /**
     * @return the routeEndAddress
     */
    public String getRouteEndAddress() {
        return routeEndAddress;
    }

    /**
     * @param routeEndAddress the routeEndAddress to set
     */
    public void setRouteEndAddress(final String routeEndAddress) {
        this.routeEndAddress = routeEndAddress;
    }

    /**
     * Shorthand for {@link #setRouteEndAddress(String)}.
     *
     * @param routeEndAddress the routeEndAddress to set
     */
    public void setRea(final String routeEndAddress) {
        setRouteEndAddress(routeEndAddress);
    }

    /**
     * @return the search
     */
    public String getSearch() {
        return search;
    }

    /**
     * @param search the search to set
     */
    public void setSearch(final String search) {
        this.search = search;
    }
}

package au.com.sensis.mobile.web.component.map.showcase.presentation.form;

import org.apache.commons.lang.StringUtils;

import au.com.sensis.wireless.manager.directions.RoutingOption;
import au.com.sensis.wireless.manager.mapping.MobilesIconType;

/**
 * Struts2 form object for maps.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapForm {

    private String location;

    private String routeStartAddress;
    private String routeEndAddress;
    private String routingOptionAsString;

    /**
     * POI search key.
     */
    private String search;

    /**
     * Map of a location cursor type.
     */
    private String cursorType;

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

    /**
     * @return the cursorType
     */
    public String getCursorTypeAsString() {
        return cursorType;
    }

    /**
     * @param cursorType the cursorType to set
     */
    public void setCursorTypeAsString(final String cursorType) {
        this.cursorType = cursorType;
    }

    /**
     * Shorthand for {@link #setCursorTypeAsString(String)}.
     *
     * @param cursorType the cursorType to set
     */
    public void setCt(final String cursorType) {
        setCursorTypeAsString(cursorType);
    }

    /**
     * @return Map of a location cursor type.
     */
    public MobilesIconType getCursorType() {
        for (final MobilesIconType mobilesIconType : MobilesIconType.values()) {
            if (mobilesIconType.name().equals(getCursorTypeAsString())) {
                return mobilesIconType;
            }
        }

        throw new IllegalStateException("Cannot match cursorType '"
                + getCursorTypeAsString() + "' to any MobilesIconType");
    }

    /**
     * @param routingOptionAsString the routingOptionAsString to set
     */
    public void setRoutingOptionAsString(final String routingOptionAsString) {
        this.routingOptionAsString = routingOptionAsString;
    }

    /**
     * @return the routingOptionAsString
     */
    public String getRoutingOptionAsString() {
        return routingOptionAsString;
    }

    /**
     * Shorthand for {@link #setRoutingOptionAsString(String)}.
     *
     * @param routingOptionAsString the routingOptionAsString to set
     */
    public void setRop(final String routingOptionAsString) {
        setRoutingOptionAsString(routingOptionAsString);
    }

    /**
     * @return the routingOption
     */
    public RoutingOption getRoutingOption() {
        return RoutingOption.fromShortNameString(getRoutingOptionAsString());
    }

    /**
     * @param routingOption the routingOption to set
     */
    public void setRoutingOption(final RoutingOption routingOption) {
        setRoutingOptionAsString(routingOption.getShortName());
    }
}

package au.com.sensis.mobile.web.component.map.model;

/**
 * Zoom related details of a {@link Map}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public interface ZoomDetails {

    /**
     * @return true if the requested zoom level is the minimum allowed.
     */
    boolean isAtMinimumZoom();

    /**
     * @return true if the requested zoom level is the maximum allowed.
     */
    boolean isAtMaximumZoom();

    /**
     * Returns the EMS zoom that the map was/is to be rendered using. Required
     * by AJAX maps that talk to EMS directly.
     *
     * @return the EMS zoom that the map was/is to be rendered using. Required
     *         by AJAX maps that talk to EMS directly.
     *
     *         TODO: This method should possibly be moved into the
     *         {@link au.com.sensis.wireless.manager.mapping.MapUrl}.
     */
    int getEmsZoom();
}

package au.com.sensis.mobile.web.component.map.model;

import org.apache.commons.lang.builder.ToStringBuilder;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;
import au.com.sensis.wireless.manager.mapping.UserMapInteraction;

/**
 * A view bean containing information used to display and browse a map.
 *
 * @author Elvis Svonja
 */
public class MapCriteria {

    private int zoom;
    private UserMapInteraction direction;
    private WGS84Point coordinates;
    private MobilesBoundingBox mapBoundingBox;

    private int screenWidth;
    private int screenHeight;
    private boolean largeDevice;

    /**
     * @return  the zoom.
     */
    public int getZoom() {

        return zoom;
    }

    /**
     * @param zoom  the zoom to set.
     */
    public void setZoom(final int zoom) {

        this.zoom = zoom;
    }

    /**
     * @return  the direction.
     */
    public UserMapInteraction getDirection() {

        return direction;
    }

    /**
     * @param direction the direction to set.
     */
    public void setDirection(final UserMapInteraction direction) {

        this.direction = direction;
    }

    /**
     * @return  the coordinates.
     */
    public WGS84Point getCoordinates() {

        return coordinates;
    }

    /**
     * @param coordinates   the coordinates to set.
     */
    public void setCoordinates(final WGS84Point coordinates) {

        this.coordinates = coordinates;
    }

    /**
     * @return  the mapBoundingBox.
     */
    public MobilesBoundingBox getMapBoundingBox() {

        return mapBoundingBox;
    }

    /**
     * @param mapBoundingBox    the mapBoundingBox to set.
     */
    public void setMapBoundingBox(final MobilesBoundingBox mapBoundingBox) {

        this.mapBoundingBox = mapBoundingBox;
    }

    /**
     * @return  the screenWidth.
     */
    public int getScreenWidth() {

        return screenWidth;
    }

    /**
     * @param screenWidth   the screenWidth to set.
     */
    public void setScreenWidth(final int screenWidth) {

        this.screenWidth = screenWidth;
    }

    /**
     * @return  the screenHeight.
     */
    public int getScreenHeight() {

        return screenHeight;
    }

    /**
     * @param screenHeight  the screenHeight to set.
     */
    public void setScreenHeight(final int screenHeight) {

        this.screenHeight = screenHeight;
    }

    /**
     * @return  the largeDevice.
     */
    public boolean isLargeDevice() {

        return largeDevice;
    }

    /**
     * @param largeDevice   the largeDevice to set.
     */
    public void setLargeDevice(final boolean largeDevice) {

        this.largeDevice = largeDevice;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {

        return ToStringBuilder.reflectionToString(this);
    }
}

package au.com.sensis.address;


/**
 * Test data factory for {@link WGS84Point}s.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class WGS84PointTestDataFactory {

    /** Longitude of the QV building. */
    public static final double QV_LONGITUDE = 144.9636111111111;

    /** Latitude of the QV building. */
    public static final double QV_LATITUDE = -37.812777777777775;

    /**
     * @return a new, valid {@link WGS84Point}.
     */
    public WGS84Point createValidWGS84Point() {
        return new WGS84Point(QV_LONGITUDE, QV_LATITUDE);
    }

    /**
     * @return a new, valid {@link WGS84Point}.
     */
    public WGS84Point createValidWGS84Point2() {
        return new WGS84Point(QV_LONGITUDE + 1, QV_LATITUDE + 1);
    }

    /**
     * @return a new, valid {@link WGS84Point}.
     */
    public WGS84Point createValidWGS84Point3() {
        return new WGS84Point(QV_LONGITUDE - 1, QV_LATITUDE - 1);
    }
}

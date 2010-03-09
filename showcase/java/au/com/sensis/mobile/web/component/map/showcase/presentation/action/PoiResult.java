package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import java.util.ArrayList;
import java.util.List;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.IconDescriptor;
import au.com.sensis.wireless.manager.mapping.IconType;

/**
 * Class for creating hard coded POI search results.
 */
public final class PoiResult {
    private final double lat;
    private final double lon;
    private final String name;
    private final String source;
    private final String address;

    /**
     * @return array of PoiResults matching a "cars" near "melbourne vic"
     *         search in the WhereisMobile SSO test environment (as of 05
     *         Feb 2010).
     */
    public static PoiResult[] createWhereisMobileCarsNearbyMelbourneResults() {
        final List<PoiResult> results = new ArrayList<PoiResult>();

        final double franceTravelLat = -37.8136;
        final double franceTravelLon = 144.962;
        results.add(new PoiResult("France &amp; Travel",
                "yellowCoreCombinedDataSource",
                "1/ 361 Little Bourke St, Melbourne, VIC", franceTravelLat,
                franceTravelLon));

        final double leaseExpressLat = -37.813555;
        final double leaseExpressLon = 144.96172;
        results.add(new PoiResult("Lease Express",
                "yellowCoreCombinedDataSource",
                "Level 1, 370 Little Bourke St, Melbourne, VIC",
                leaseExpressLat, leaseExpressLon));

        results.add(new PoiResult("Leasexpress",
                "yellowCoreCombinedDataSource",
                "Level 1, 370 Little Bourke St, Melbourne, VIC",
                leaseExpressLat, leaseExpressLon));

        final double carlingfordLat = -37.81358;
        final double carlingfordLon = 144.9612;
        results.add(new PoiResult("Carlingford",
                "yellowCoreCombinedDataSource",
                "Level 4/ 43 Hardware La, Melbourne, VIC", carlingfordLat,
                carlingfordLon));

        final double metroParkingLat = -37.81171;
        final double metroParkingLon = 144.96295;
        results.add(new PoiResult("Metro Parking Management Pty Ltd",
                "yellowCoreCombinedDataSource",
                "St Francis, 312 Lonsdale St,, Melbourne, VIC",
                metroParkingLat, metroParkingLon));

        return results.toArray(new PoiResult[] {});
    }

    /**
     * @return array of PoiResults matching a "bars" near "toora vic" search
     *         in the WhereisMobile SSO dev environment (as of 05 Mar 2010).
     */
    public static PoiResult[] createWhereisMobileBarsNearbyToorakVicResults() {
        final List<PoiResult> results = new ArrayList<PoiResult>();

        final double oxfordLoungeLat = -37.84059;
        final double oxfordLoungeLon = 144.99512;
        results.add(new PoiResult("Oxford Lounge",
                "yellowCoreCombinedDataSource",
                "10 Oxford St, South Yarra, VIC", oxfordLoungeLat,
                oxfordLoungeLon));

        final double trystLoungeBarLat = -37.84362;
        final double trystLoungeBarLon = 144.99563;
        results.add(new PoiResult("Tryst Lounge Bar",
                "yellowCoreCombinedDataSource",
                "3 Wilson St, South Yarra, VIC",
                trystLoungeBarLat, trystLoungeBarLon));

        final double cristalLat = -37.84522;
        final double cristalLon = 144.99484;
        results.add(new PoiResult("Cristal",
                "yellowCoreCombinedDataSource",
                "Lvl 1/Ste 8 402 Chapel St, South Yarra, VIC",
                cristalLat, cristalLon));

        final double oneSixOneLat = -37.85175;
        final double oneSixOneLon = 144.99363;
        results.add(new PoiResult("One Six One",
                "yellowCoreCombinedDataSource",
                "161 High St, Prahran, VIC", oneSixOneLat,
                oneSixOneLon));

        final double donMateosLat = -37.81955;
        final double donMateosLon = 145.00495;
        results.add(new PoiResult("Don Mateos",
                "yellowCoreCombinedDataSource",
                "406 Bridge Rd, Richmond, VIC",
                donMateosLat, donMateosLon));

        return results.toArray(new PoiResult[] {});
    }

    /**
     * @return array of PoiResults matching a "bars" near "toora vic" search
     *         in the WhereisMobile SSO dev environment (as of 05 Mar 2010).
     */
    public static PoiResult[]
        createWhereisMobileBassettSmithValuersNearbyBrightonVicResults() {

        final List<PoiResult> results = new ArrayList<PoiResult>();

        final double oxfordLoungeLat = -37.91627;
        final double oxfordLoungeLon = 144.99653;
        results.add(new PoiResult("Bassett-Smith Valuers",
                "yellowCoreCombinedDataSource",
                "142 Church St, Brighton, VIC", oxfordLoungeLat,
                oxfordLoungeLon));

        return results.toArray(new PoiResult[] {});
    }

    /**
     * @return array of IconDescriptor corresponding to
     *         {@link #createWhereisMobileCarsNearbyMelbourneResults()}.
     */
    public static List<IconDescriptor>
        createWhereisMobileCarsNearbyMelbourneIconDescriptors() {
        final List<IconDescriptor> iconDescriptors =
                new ArrayList<IconDescriptor>();

        for (final PoiResult poiResult : PoiResult
                .createWhereisMobileCarsNearbyMelbourneResults()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon()));
        }
        return iconDescriptors;
    }

    /**
     * @return array of IconDescriptor corresponding to
     *         {@link #createWhereisMobileCarsNearbyMelbourneResults()}.
     */
    public static List<IconDescriptor> createWhereisMobileBarsNearbyToorakVicIconDescriptors() {
        final List<IconDescriptor> iconDescriptors =
                new ArrayList<IconDescriptor>();

        for (final PoiResult poiResult : PoiResult
                .createWhereisMobileBarsNearbyToorakVicResults()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon()));
        }
        return iconDescriptors;
    }

    /**
     * @return array of IconDescriptor corresponding to
     *         {@link #createWhereisMobileCarsNearbyMelbourneResults()}.
     */
    public static List<IconDescriptor>
        createWhereisMobileBassettSmithValuersNearbyBrightonVicIconDescriptors() {

        final List<IconDescriptor> iconDescriptors =
            new ArrayList<IconDescriptor>();

        for (final PoiResult poiResult : PoiResult
                .createWhereisMobileBassettSmithValuersNearbyBrightonVicResults()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon()));
        }
        return iconDescriptors;
    }

    private static IconDescriptor createIconDescriptor(final double lat, final double lon) {
        return new IconDescriptor(IconType.FREE, new WGS84Point(lon, lat));
    }

    private PoiResult(final String name, final String source,
            final String address, final double lat, final double lon) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.source = source;
        this.address = address;
    }

    /**
     * @return the lat
     */
    public double getLat() {
        return lat;
    }

    /**
     * @return the lon
     */
    public double getLon() {
        return lon;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }
}

package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import java.util.ArrayList;
import java.util.List;

import au.com.sensis.address.WGS84Point;
import au.com.sensis.wireless.manager.mapping.IconDescriptor;
import au.com.sensis.wireless.manager.mapping.IconType;
import au.com.sensis.wireless.manager.mapping.InteractivePoiInfo;

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

        final double bassetSmithValuersLat = -37.91627;
        final double bassettSmithValuersLon = 144.99653;
        results.add(new PoiResult("Bassett-Smith Valuers",
                "yellowCoreCombinedDataSource",
                "142 Church St, Brighton, VIC", bassetSmithValuersLat,
                bassettSmithValuersLon));

        return results.toArray(new PoiResult[] {});
    }

    /**
     * @return a single result located at 142 Church St, Brighton Vic
     */
    public static PoiResult[]
                            createSingleResultAt142ChurchStBrightonVic() {

        final List<PoiResult> results = new ArrayList<PoiResult>();

        final double bassetSmithValuersLat = -37.91627;
        final double bassettSmithValuersLon = 144.99653;
        results.add(new PoiResult("Bassett-Smith Valuers",
                "yellowCoreCombinedDataSource",
                "142 Church St, Brighton, VIC", bassetSmithValuersLat,
                bassettSmithValuersLon));

        return results.toArray(new PoiResult[] {});
    }

    /**
     * @return array of PoiResults matching a "bars" near "toora vic" search
     *         in the WhereisMobile SSO dev environment (as of 05 Mar 2010).
     */
    public static PoiResult[]
                            createWhereisMobileCafeNearbyTullamarineVicResults() {

        final List<PoiResult> results = new ArrayList<PoiResult>();

        final double freightAwayDeliLat = -37.695815;
        final double freightAwayDeliLon = 144.870019;
        results.add(new PoiResult("Freight Away Deli",
                "yellowCoreCombinedDataSource",
                "International Drv, Tullamarine, VIC", freightAwayDeliLat,
                freightAwayDeliLon));

        final double jeannettesGourmetKitchenLat = -37.69518;
        final double jeannettesGourmetKitchenLon = 144.87038;
        results.add(new PoiResult("Jeneatte's Gourmet Kitchen",
                "yellowCoreCombinedDataSource",
                "Nippon Express, Tullamarine, VIC", jeannettesGourmetKitchenLat,
                jeannettesGourmetKitchenLon));

        final double costLessCurtainsPtyLtdLat = -37.70822;
        final double costLessCurtainsPtyLtdLon = 144.87842;
        results.add(new PoiResult("Cost Less Curtains Pty Ltd",
                "yellowCoreCombinedDataSource",
                "51 Sharps Rd, Tullamarine, VIC", costLessCurtainsPtyLtdLat,
                costLessCurtainsPtyLtdLon));

        final double melroseLoungeLat = -37.7047;
        final double melroseLoungeLon = 144.88424;
        results.add(new PoiResult("Melrose Lounge",
                "yellowCoreCombinedDataSource",
                "1 Melrose Crt, Tullamarine, VIC", melroseLoungeLat,
                melroseLoungeLon));

        final double cafeLilacLat = -37.70142;
        final double cafeLilacLon = 144.88608;
        results.add(new PoiResult("Cafe Lilac",
                "yellowCoreCombinedDataSource",
                "Fcty 2a/ 24 Carrick Drv, Tullamarine, VIC", cafeLilacLat,
                cafeLilacLon));

        return results.toArray(new PoiResult[] {});
    }

    /**
     * @return array of PoiResults matching a "restaurants" near "3006"
     *         search in the WhereisMobile SSO dev environment (as of 28
     *         Apr 2010), where WhereisMobile has been modified to display 10 results per page.
     */
    public static PoiResult[] createWhereisMobileRestaurantsNearby3006WithPageSize10Results() {
        final List<PoiResult> results = new ArrayList<PoiResult>();

        final double wineHouseBrasserieLat = -37.82548;
        final double wineHouseBrasserieLon = 144.96024;
        results.add(new PoiResult("Wine House Brasserie",
                "yellowCoreCombinedDataSource",
                "133 Queensbridge St, Southbank, VIC", wineHouseBrasserieLat,
                wineHouseBrasserieLon));

        final double enlightenedCuisineLat = -37.824734;
        final double enlightenedCuisineLon = 144.960305;
        results.add(new PoiResult("Enlightened Cuisine",
                "yellowCoreCombinedDataSource",
                "113 Queensbridge St, Southbank, VIC",
                enlightenedCuisineLat, enlightenedCuisineLon));

        final double luckyChanSeafood1RestaurantLat = -37.827248;
        final double luckyChanSeafood1RestaurantLon = 144.95817;
        results.add(new PoiResult("Lucky Chan Seafood Restaurant",
                "yellowCoreCombinedDataSource",
                "Casino Prm, Southbank, VIC",
                luckyChanSeafood1RestaurantLat, luckyChanSeafood1RestaurantLon));

        final double luckyChanSeafood2RestaurantLat = -37.827248;
        final double luckyChanSeafood2RestaurantLon = 144.95817;
        results.add(new PoiResult("Lucky Chan Seafood Restaurant",
                "yellowCoreCombinedDataSource",
                "Casino Promenade, Southbank, VIC",
                luckyChanSeafood2RestaurantLat, luckyChanSeafood2RestaurantLon));

        final double melbourneShortStayApartmentsLat = -37.82406;
        final double melbourneShortStayApartmentsLon = 144.96159;
        results.add(new PoiResult("Melbourne Short Stay Apartment",
                "yellowCoreCombinedDataSource",
                "186 City Rd, Southbank, VIC", melbourneShortStayApartmentsLat,
                melbourneShortStayApartmentsLon));

        final double daimonjiJapaneseRestaurantLat = -37.82987;
        final double daimonjiJapaneseRestaurantLon = 144.95945;
        results.add(new PoiResult("Daimonji Japanese Restaurant",
                "yellowCoreCombinedDataSource",
                "179 Clarendon St, South Melbourne, VIC",
                daimonjiJapaneseRestaurantLat, daimonjiJapaneseRestaurantLon));

        final double theMasalaHouseLat = -37.82361;
        final double theMasalaHouseLon = 144.9614;
        results.add(new PoiResult("The Masala House",
                "yellowCoreCombinedDataSource",
                "18 Power St, Southbank, VIC",
                theMasalaHouseLat, theMasalaHouseLon));

        final double waterfrontSeafoodRestaurantLat = -37.824611;
        final double waterfrontSeafoodRestaurantLon = 144.957586;
        results.add(new PoiResult("The Masala House",
                "yellowCoreCombinedDataSource",
                "Southbank, VIC",
                waterfrontSeafoodRestaurantLat, waterfrontSeafoodRestaurantLon));

        final double colonialTramcarRestaurantTheLat = -37.825138;
        final double colonialTramcarRestaurantTheLon = 144.957062;
        results.add(new PoiResult("Colonial Tramcar Restaurant The",
                "yellowCoreCombinedDataSource",
                "Corner of Clarendon & Whiteman Sts,, South Melbourne, VIC",
                colonialTramcarRestaurantTheLat, colonialTramcarRestaurantTheLon));

        final double brubakersBagelBarLat = -37.8232665;
        final double brubakersBagelBarLon = 144.9602375;
        results.add(new PoiResult("Brubakers Bagel Bar",
                "yellowCoreCombinedDataSource",
                "At Crown, Southbank, VIC",
                brubakersBagelBarLat, brubakersBagelBarLon));

        return results.toArray(new PoiResult[] {});
    }

    public static PoiResult[] createWhereisMobileVictorianCoachesMultiPoi() {
    	final List<PoiResult> results = new ArrayList<PoiResult>();
    	
    	final double victorianCoachesLat = -37.79801;
        final double victorianCoachesLon = 144.94025;
        results.add(new PoiResult("Victorian Coaches",
                "yellowCoreCombinedDataSource",
                "10 Fogarty St North Melbourne",
                victorianCoachesLat, victorianCoachesLon));
    	
        final double victorianCoaches2Lat = -37.79801;
        final double victorianCoaches2Lon = 144.94025;
        results.add(new PoiResult("Victorian Coaches 2",
                "yellowCoreCombinedDataSource",
                "10 Fogarty St North Melbourne",
                victorianCoaches2Lat, victorianCoaches2Lon));
        
        final double cafeLilacLat = -37.70142;
        final double cafeLilacLon = 144.88608;
        results.add(new PoiResult("Cafe Lilac",
                "yellowCoreCombinedDataSource",
                "Fcty 2a/ 24 Carrick Drv, Tullamarine, VIC", cafeLilacLat,
                cafeLilacLon));
        
        final double cafeLilacLat2 = -37.70142;
        final double cafeLilacLon2 = 144.88608;
        results.add(new PoiResult("Cafe Lilac 2",
                "yellowCoreCombinedDataSource",
                "Fcty 2a/ 24 Carrick Drv, Tullamarine, VIC", cafeLilacLat2,
                cafeLilacLon2));
        
        final double victorianCoaches3Lat = -37.79801;
        final double victorianCoaches3Lon = 144.94025;
        results.add(new PoiResult("Victorian Coaches 3",
                "yellowCoreCombinedDataSource",
                "10 Fogarty St North Melbourne",
                victorianCoaches3Lat, victorianCoaches3Lon));
        
        final double bassetSmithValuersLat = -37.91627;
        final double bassettSmithValuersLon = 144.99653;
        results.add(new PoiResult("Bassett-Smith Valuers",
                "yellowCoreCombinedDataSource",
                "142 Church St, Brighton, VIC", bassetSmithValuersLat,
                bassettSmithValuersLon));
        
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

        int c = 1;
        for (final PoiResult poiResult : PoiResult
                .createWhereisMobileCarsNearbyMelbourneResults()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon(), new InteractivePoiInfo(poiResult.getName(), poiResult.getAddress(), "poi-".concat(Integer.toString(c++)), InteractivePoiInfo.NAMED)));
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

        int c = 1;
        for (final PoiResult poiResult : PoiResult
                .createWhereisMobileBarsNearbyToorakVicResults()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon(), new InteractivePoiInfo(poiResult.getName(), poiResult.getAddress(), "poi-".concat(Integer.toString(c++)), InteractivePoiInfo.THICK)));
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

        int c = 1;
        for (final PoiResult poiResult : PoiResult
                .createWhereisMobileBassettSmithValuersNearbyBrightonVicResults()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon(), new InteractivePoiInfo(poiResult.getName(), poiResult.getAddress(), "poi-".concat(Integer.toString(c++)), InteractivePoiInfo.SLIM)));
        }
        return iconDescriptors;
    }

    /**
     * @return array of IconDescriptor corresponding to
     *         {@link #createSingleResultAt142ChurchStBrightonVic()}.
     */
    public static List<IconDescriptor>
        createSingleResultAt142ChurchStBrightonVicIconDescriptors() {

        final List<IconDescriptor> iconDescriptors =
            new ArrayList<IconDescriptor>();

        int c = 1;
        for (final PoiResult poiResult : PoiResult
                .createSingleResultAt142ChurchStBrightonVic()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon(),new InteractivePoiInfo(poiResult.getName(), ".", "poi-".concat(Integer.toString(c++)), InteractivePoiInfo.SLIM)));
        }
        return iconDescriptors;
    }

    /**
     * @return array of IconDescriptor corresponding to
     *         {@link #createWhereisMobileCarsNearbyMelbourneResults()}.
     */
    public static List<IconDescriptor>
        createWhereisMobileCafeNearbyTullamarineVicIconDescriptors(String ipoiType) {

        final List<IconDescriptor> iconDescriptors =
            new ArrayList<IconDescriptor>();

        int c = 1;
        String interactivePoiType = new String();
        if(ipoiType.equals("slim")) {
       	 interactivePoiType = InteractivePoiInfo.SLIM;
        }
        else { interactivePoiType = InteractivePoiInfo.THICK; }
        for (final PoiResult poiResult : PoiResult
                .createWhereisMobileCafeNearbyTullamarineVicResults()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon(), new InteractivePoiInfo(poiResult.getName(), ".", "poi-".concat(Integer.toString(c++)),  interactivePoiType)));
        }
        return iconDescriptors;
    }

    /**
     * @return array of IconDescriptor corresponding to
     *         {@link #createWhereisMobileRestaurantsNearby3006WithPageSize10Results()}.
     */
    public static List<IconDescriptor>
        createWhereisMobileRestaurantsNearby3006WithPageSize10IconDescriptors() {

        final List<IconDescriptor> iconDescriptors =
            new ArrayList<IconDescriptor>();

            int c = 1; 
        for (final PoiResult poiResult : PoiResult
                .createWhereisMobileRestaurantsNearby3006WithPageSize10Results()) {
            iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                    poiResult.getLon(), new InteractivePoiInfo(poiResult.getName(), poiResult.getAddress(), "poi-".concat(Integer.toString(c++)), InteractivePoiInfo.SLIM)));
        }
        return iconDescriptors;
    }
        
    public static List<IconDescriptor> createWhereisMobileVictorianCoachesMultiPoiIconDescriptors(String ipoiType) {
    	 final List<IconDescriptor> iconDescriptors =
             new ArrayList<IconDescriptor>();

             int c = 1; 
             String interactivePoiType = new String();
             if(ipoiType.equals("txt")) {
            	 interactivePoiType = InteractivePoiInfo.NAMED;
             }
             else { interactivePoiType = InteractivePoiInfo.THICK; }
         for (final PoiResult poiResult : PoiResult.createWhereisMobileVictorianCoachesMultiPoi()) {
             iconDescriptors.add(createIconDescriptor(poiResult.getLat(),
                     poiResult.getLon(), new InteractivePoiInfo(poiResult.getName(), poiResult.getAddress(), "poi-".concat(Integer.toString(c++)), interactivePoiType)));
         }
         return iconDescriptors;
    }
    
    private static IconDescriptor createIconDescriptor(final double lat, final double lon, final InteractivePoiInfo interactivePoiInfo) {
        return new IconDescriptor(IconType.FREE, new WGS84Point(lon, lat), interactivePoiInfo);
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

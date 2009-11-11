package au.com.sensis.mobile.web.component.map.showcase.stub;

import java.util.Collection;
import java.util.List;

import au.com.sensis.address.AustralianState;
import au.com.sensis.address.GeocodedAddress;
import au.com.sensis.address.WGS84Point;
import au.com.sensis.sal.common.UserContext;
import au.com.sensis.wireless.manager.directions.JourneyDescriptor;
import au.com.sensis.wireless.manager.directions.JourneyTransportOptionDescriptor;
import au.com.sensis.wireless.manager.directions.JourneyWaypoints;
import au.com.sensis.wireless.manager.directions.RoutingOption;
import au.com.sensis.wireless.manager.ems.EMSManager;
import au.com.sensis.wireless.manager.mapping.LocationMapUrl;
import au.com.sensis.wireless.manager.mapping.MapLayer;
import au.com.sensis.wireless.manager.mapping.MapUrl;
import au.com.sensis.wireless.manager.mapping.MobilesBoundingBox;
import au.com.sensis.wireless.manager.mapping.MobilesIconType;
import au.com.sensis.wireless.manager.mapping.PanZoomDetail;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;

public class EMSManagerStubImpl implements EMSManager {

    public JourneyDescriptor getJourneyDescriptor(final ScreenDimensions arg0,
            final JourneyWaypoints arg1, final RoutingOption arg2, final UserContext arg3) {
        // TODO Auto-generated method stub
        return null;
    }

    public JourneyTransportOptionDescriptor getJourneyTransportOptions(
            final JourneyWaypoints arg0, final Collection<RoutingOption> arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public MapUrl getLegStepMap(final JourneyDescriptor arg0, final ScreenDimensions arg1,
            final WGS84Point arg2, final int arg3, final MapLayer arg4, final UserContext arg5) {
        // TODO Auto-generated method stub
        return null;
    }

    public MapUrl getMap(final ScreenDimensions arg0, final WGS84Point requestedCentre,
            final MobilesIconType arg2, final MapLayer arg3, final PanZoomDetail panZoomDetail,
            final UserContext arg5) {
        final LocationMapUrl locationMapUrl = new LocationMapUrl();

        final double camberwellVicLongitude = 145.0730816;
        final double camberwellVicLatitude = -37.8388769;
        final WGS84Point dummyCentre =
                new WGS84Point(camberwellVicLongitude, camberwellVicLatitude);
        final WGS84Point boundingBoxTopLeft =
                new WGS84Point(145.065708553125, -37.832174505905165);
        final WGS84Point boundingBoxBottomRight =
                new WGS84Point(145.08045464687498, -37.84557929409483);
        locationMapUrl.setBoundingBox(new MobilesBoundingBox(
                boundingBoxTopLeft, boundingBoxBottomRight));
        locationMapUrl.setMapCentre(dummyCentre);
        locationMapUrl
                .setImageUrl("http://dsb.sensis.com.au/mcstestapp/imageserver/map-images/camberwellVic.png");
        return locationMapUrl;
    }

    public MapUrl getPoiMap(final ScreenDimensions arg0, final WGS84Point arg1,
            final MapLayer arg2, final List<WGS84Point> arg3, final double arg4, final UserContext arg5) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<GeocodedAddress> resolveAddress(final GeocodedAddress arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<GeocodedAddress> resolveAddress(final GeocodedAddress arg0,
            final AustralianState arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    public GeocodedAddress reverseGeocode(final WGS84Point arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}

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

	
	public JourneyDescriptor getJourneyDescriptor(ScreenDimensions arg0,
			JourneyWaypoints arg1, RoutingOption arg2, UserContext arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public JourneyTransportOptionDescriptor getJourneyTransportOptions(
			JourneyWaypoints arg0, Collection<RoutingOption> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MapUrl getLegStepMap(JourneyDescriptor arg0, ScreenDimensions arg1,
			WGS84Point arg2, int arg3, MapLayer arg4, UserContext arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public MapUrl getMap(ScreenDimensions arg0, WGS84Point requestedCentre,
			MobilesIconType arg2, MapLayer arg3, PanZoomDetail panZoomDetail,
			UserContext arg5) {
		LocationMapUrl locationMapUrl = new LocationMapUrl();
		
		final double camberwellVicLongitude = 145.0730816;
		final double camberwellVicLatitude = -37.8388769;
		WGS84Point dummyCentre = new WGS84Point(camberwellVicLongitude, camberwellVicLatitude);
		WGS84Point boundingBoxTopLeft = new WGS84Point(145.065708553125, -37.832174505905165);
		WGS84Point boundingBoxBottomRight = new WGS84Point(145.08045464687498, -37.84557929409483);
		locationMapUrl.setBoundingBox(new MobilesBoundingBox(boundingBoxTopLeft, boundingBoxBottomRight));
		locationMapUrl.setMapCentre(dummyCentre);
		locationMapUrl.setImageUrl("http://dsb.sensis.com.au/mcstestapp/imageserver/map-images/camberwellVic.png");
		return locationMapUrl;
	}

	
	public MapUrl getPoiMap(ScreenDimensions arg0, WGS84Point arg1,
			MapLayer arg2, List<WGS84Point> arg3, double arg4, UserContext arg5) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<GeocodedAddress> resolveAddress(GeocodedAddress arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<GeocodedAddress> resolveAddress(GeocodedAddress arg0,
			AustralianState arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public GeocodedAddress reverseGeocode(WGS84Point arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}

/* Device location instance on map component */
MAP.DEVICELOCATION = {};
MAP.DEVICELOCATION.errorMessage = "Sorry, we couldn't locate you.";

/* follow me */
MAP.DEVICELOCATION.AutoLocate = {};
MAP.DEVICELOCATION.AutoLocate.onlocate = function(position) {
	if(position == null) return;
	
	/* get distance in px */
	var panBy = MAP.instances[0].getLocMapCtrPxDiff(position);
	/* do the pan */
	MAP.instances[0].Map.pan(panBy.x, panBy.y, {animate:true});
	/* zoom in if the map is zoom out too much */
	if(MAP.instances[0].Map.getZoom() < 9)
		setTimeout(function() {EMS.Util.smoothZoom(MAP.instances[0].Map, MAP.instances[0].Map.getCenter(), userLatLon, 11 );} , 500);
	
	/* keep adding location poi */
	MAP.instances[0].addLocationPoi(position, position.accuracy, true);
};

MAP.DEVICELOCATION.AutoLocate.prelocate = function() {
	MAP.instances[0].clearLocationPoi();
};

MAP.DEVICELOCATION.AutoLocate.postlocate = function(lastRecordedPosition) {
	if(!$defined(lastRecordedPosition))
		alert(_DEVICELOCATION_WM_.errorMessage);
	
	else MAP.instances[0].addLocationPoi(lastRecordedPosition, lastRecordedPosition.accuracy, false);
};

MAP.DEVICELOCATION.AutoLocate.onerror = function(error) { 
	alert(MAP.DEVICELOCATION.errorMessage);
};

/* limits */
MAP.DEVICELOCATION.AutoLocate.limits = {};
MAP.DEVICELOCATION.AutoLocate.limits.tries = 10;
MAP.DEVICELOCATION.AutoLocate.limits.proximity = 50;
MAP.DEVICELOCATION.AutoLocate.limits.timeout = 30000;
MAP.DEVICELOCATION.AutoLocate.limits.acceptableProximity = 1000;

/* extends the MobEMS base class to use addLocationPoi() */
MobEMS.implement({
	LocationMarker: null,
	LocationPoi: null,
	
	addLocationPoi: function(coordinates, accuracy, stillLocating) {
		/* if there's no location poi on the map */
		if(this.LocationMarker == null) {
			/* this method expects the family level of device instantiate the LocationPoi correctly */
			this.LocationMarker = new OpenLayers.Marker(this.formatLatLon(coordinates), this.LocationPoi);
		
			this.Map.markersLayer.addMarker(this.LocationMarker);
			this.LocationPoi.setLocationAccuracy(accuracy);
			this.LocationPoi.startLocatingAnimation();
		} 
		else {
			/* don't use moveTo() because when the x,y = 0 
			 * the marker actually moved to the old center of the map
			 * !@#$!!
			 * 
			 * so we brutally set the lonlat manually
			 */
			this.LocationMarker.lonlat = this.formatLatLon(coordinates);
			this.LocationPoi.setLocationAccuracy(accuracy);
			
			if(!stillLocating) this.LocationPoi.startLocatedAnimation();
		}
	},
	
	clearLocationPoi: function() {
		if(this.LocationMarker != null) {
			this.Map.markersLayer.removeMarker(this.LocationMarker);
			this.LocationMarker = null;
		}
	},
	
	/* get the difference between the coords and the current map center in px */
	getLocMapCtrPxDiff: function(coords) {
		if(coords == null) return;

		/* grab the location in px */
		var userPos = this.Map.getPixelFromLonLat(this.formatLatLon(coords));
		/* grab the center of the map in px */
		var centerPos = this.Map.getPixelFromLonLat(this.Map.getCenter());
		/* calculate the distance in px */
		return new OpenLayers.Pixel(userPos.x - centerPos.x, userPos.y - centerPos.y);
	},
	
	createDeviceLocationInstance: function(map) {
		if(typeof(DeviceLocation) != 'undefined') {
			this.LocationPoi = this.createLocationPoi(map);
			MAP.DEVICELOCATION.instance = new DeviceLocation(null, MAP.DEVICELOCATION.AutoLocate);
		}
	}
});
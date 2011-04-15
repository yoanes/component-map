/* extends the MobEMS base class to use addLocationPoi() */
MobEMS.implement({
	LocationMarker: null,
	LocationPoi: null,
	
	LocateUserRelativeToPois: false,

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
			this.LocationMarker.moveTo(this.Map.getViewPortPxFromLonLat(this.formatLatLon(coordinates)));
			this.LocationPoi.setLocationAccuracy(accuracy);
		}
		
		if(!stillLocating) this.LocationPoi.startLocatedAnimation();
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
	
	/* this method will inject the DeviceLocationConfig attribute into this map instance 
	 * this way the DeviceLocation instance is tied in with an instance of map component object
	 * instead of the map component globally
	 */
	initDeviceLocationConfig: function() {
		/* Device location instance on map component */
		this.DeviceLocationConfig = {};
		this.DeviceLocationConfig.errorMessage = "Sorry, we couldn't locate you.";

		/* follow me */
		this.DeviceLocationConfig.AutoLocate = {};
		this.DeviceLocationConfig.AutoLocate.onlocate = function(position) {
			if(position == null) return;

			/* keep adding location poi */
			var doNotLockDownPosition = position.accuracy > 500 ? true : false;
			this.addLocationPoi(position, position.accuracy, doNotLockDownPosition);
			
			/* if the map has only 1 markers, that is this location marker 
			 * then we want to pan and center the map on the user's location
			 * 
			 * also pan the map only if the flag "LocateUserRelativeToPois" is set to
			 * false
			 */
			if(this.Map.markersLayer.markers.length == 1 || !this.LocateUserRelativeToPois) {
				/* if the user location is already within the boundaries 
				 * the simply display it
				 */
				if(!this.Map.calculateBounds().containsLonLat(this.formatLatLon(position))) {
					/* get distance in px */
					var panBy = this.getLocMapCtrPxDiff(position);
					/* do the pan */
					this.Map.pan(panBy.x, panBy.y, {animate:true});
					/* zoom in if the map is zoom out too much */
					if(this.Map.getZoom() < 9)
						setTimeout(function() {
							EMS.Util.smoothZoom(this.Map, this.Map.getCenter(), this.Map.getCenter(), 11);
						}.bind(this), 500);
				}
			}
			/* else we want to zoom out the map to show user's location relatives
			 * to the other pois already displayed
			 */
			else {
				/* but only does that if the user's location is off the map
				 * otherwise simply plot them there
				 */
				if(!this.Map.calculateBounds().containsLonLat(this.formatLatLon(position))) {
					var newBoundaries = this.getNewMapBoundsWithAllMarkers();
					EMS.Util.smoothZoom(this.Map, this.Map.getCenter(), newBoundaries.getCenterLonLat(), this.Map.getZoomForExtent(newBoundaries));
				}
				
				this.LocateUserRelativeToPois = false;
			}
		}.bind(this);

		this.DeviceLocationConfig.AutoLocate.prelocate = function () {
			 this.LocateUserRelativeToPois = true;
			/* reset the device location component manually */
			navigator.geolocation.clearWatch(this.DeviceLocation._autoLocate_locationId);
            this.DeviceLocation.reset();
		}.bind(this);

		this.DeviceLocationConfig.AutoLocate.postlocate = function(lastRecordedPosition) {
			if(!$defined(lastRecordedPosition))
				alert(this.DeviceLocationConfig.errorMessage);
			
			else this.addLocationPoi(lastRecordedPosition, lastRecordedPosition.accuracy, false);
		}.bind(this);

		this.DeviceLocationConfig.AutoLocate.onerror = function(error) { 
			alert(this.DeviceLocationConfig.errorMessage);
		}.bind(this);

		/* limits: we don't need any since we want to follow user always */
		this.DeviceLocationConfig.AutoLocate.limits = {};

		/* options: set the timeout between search in 5 secs. Note that setting
		 * timeout to infinity jitters the map slightly */
		this.DeviceLocationConfig.AutoLocate.options = {};
		this.DeviceLocationConfig.AutoLocate.options.timeout = 30000;
	},
	
	createDeviceLocationInstance: function(map) {
		if(typeof(DeviceLocation) != 'undefined') {
			this.LocationPoi = this.createLocationPoi(map);
			this.initDeviceLocationConfig();
			this.DeviceLocation = new DeviceLocation(null, this.DeviceLocationConfig.AutoLocate);
		}
	},
	
	getNewMapBoundsWithAllMarkers: function() {
		var currentBoundaries = this.Map.calculateBounds();
		var existingMarkers = this.Map.markersLayer.markers;
		
		var newBoundaries = currentBoundaries.clone();
		for(var i = 0; i < existingMarkers.length; i++) {
			newBoundaries.left = Math.min(existingMarkers[i].lonlat.lon, newBoundaries.left);
			newBoundaries.right = Math.max(existingMarkers[i].lonlat.lon, newBoundaries.right);
			newBoundaries.top = Math.max(existingMarkers[i].lonlat.lat, newBoundaries.top);
			newBoundaries.bottom = Math.min(existingMarkers[i].lonlat.lat, newBoundaries.bottom);
		}
	
		/* extend the boundaries by 30 px */
		newBoundaries.extend(new OpenLayers.Pixel(30, 30));
		
		return newBoundaries; 
	}
});
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
		} 
		else {
			this.LocationMarker.moveTo(this.Map.getLayerPxFromLonLat(this.formatLatLon(coordinates)));
			this.LocationPoi.setLocationAccuracy(accuracy);
		}
		
		if(!stillLocating) this.LocationPoi.startLocatedAnimation();
		else this.LocationPoi.startLocatingAnimation();
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
		
		this.DeviceLocationConfig.stopUpdatingMapOnLocate = false;
		
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
			 */
			if(!this.DeviceLocationConfig.stopUpdatingMapOnLocate) {
				if(this.Map.markersLayer.markers.length == 1) {
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
				        var zoomLevel = this.Map.getZoomForExtent(newBoundaries);

		                var appropriateZoomLevel = this.Map.isValidZoomLevel(zoomLevel - 1) ? zoomLevel - 1 : zoomLevel;
		                this.Map.setCenter(newBoundaries.getCenterLonLat(), appropriateZoomLevel);
					}
				}
				
				/* stop updating map after initial hit */
				this.DeviceLocationConfig.stopUpdatingMapOnLocate = true;
			}
			
			this.triggerLocationFoundEvent();
		}.bind(this);

		this.DeviceLocationConfig.AutoLocate.prelocate = function () {
			this.DeviceLocationConfig.stopUpdatingMapOnLocate = false;
			this.DeviceLocation.softReset();
		}.bind(this);

		this.DeviceLocationConfig.AutoLocate.postlocate = function(lastRecordedPosition) {
			if(!$defined(lastRecordedPosition))
				alert(this.DeviceLocationConfig.errorMessage);
			
			else this.addLocationPoi(lastRecordedPosition, lastRecordedPosition.accuracy, false);
		}.bind(this);

		this.DeviceLocationConfig.AutoLocate.onerror = null;

		/* options: set the timeout between search in 5 secs. Note that setting
		 * timeout to infinity jitters the map slightly */
		this.DeviceLocationConfig.AutoLocate.options = {};
		this.DeviceLocationConfig.AutoLocate.options.timeout = 30000;
		this.DeviceLocationConfig.AutoLocate.options.maximumAge = 0;
		this.DeviceLocationConfig.AutoLocate.options.enableHighAccuracy = true;
		this.DeviceLocationConfig.AutoLocate.options.exitOnError = false;
	},
	
	createDeviceLocationInstance: function(map) {
		if(typeof(DeviceLocation) != 'undefined') {
			this.LocationPoi = this.createLocationPoi(map);
			this.initDeviceLocationConfig();
			this.DeviceLocation = new DeviceLocation(null, this.DeviceLocationConfig.AutoLocate);
		}
	},
	
	getNewMapBoundsWithAllMarkers: function() {
		/* retrieve all markers */
		var existingMarkers = this.Map.markersLayer.markers;
		
		/* create empty bounds and use the first marker lat lon to populate the initial
		 * values
		 */
		var newBoundaries = new OpenLayers.Bounds();
		newBoundaries.bottom = newBoundaries.top = existingMarkers[0].lonlat.lat;
        newBoundaries.left = newBoundaries.right = existingMarkers[0].lonlat.lon;

        /* start from 1 because the 0th marker has been used */
		for(var i = 1; i < existingMarkers.length; i++) {
			newBoundaries.left = Math.min(existingMarkers[i].lonlat.lon, newBoundaries.left);
			newBoundaries.right = Math.max(existingMarkers[i].lonlat.lon, newBoundaries.right);
			newBoundaries.top = Math.max(existingMarkers[i].lonlat.lat, newBoundaries.top);
			newBoundaries.bottom = Math.min(existingMarkers[i].lonlat.lat, newBoundaries.bottom);
		}
		
		return newBoundaries; 
	},
	
	/* trigger our own custom event */
	triggerLocationFoundEvent: function() {
		var event = document.createEvent("HTMLEvents");
		event.initEvent('locationFound', true, true ); 
		this.Map.div.dispatchEvent(event);
	}
});
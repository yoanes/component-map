MobEMS.implement({ 
	createLocationPoi: function(map) {
		return new EMS.LocationMarkerIcon(map, {supportsScale3d: true}) 
	}
});
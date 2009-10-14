// map-controls.js
mapControls = true;

mapComponent.mapButtonEventCreator = function(deviceName) {
	
	return function() {
		function addAlert(mapButtonId, msg) {
			//var mapButtonId = 'mapZoomInButton';
			if ($(mapButtonId)) {
				$(mapButtonId).addEvent('click', function() {
					window.alert(deviceName + " - " + msg);
				});
			}
		}
		
		addAlert("mapZoomInButton", "Zoom In");
		addAlert("mapZoomOutButton", "Zoom Out");
		addAlert("mapPanNorthButton", "Pan North");
		addAlert("mapPanSouthButton", "Pan South");
		addAlert("mapPanEastButton", "Pan East");
		addAlert("mapPanWestButton", "Pan West");
	};
}




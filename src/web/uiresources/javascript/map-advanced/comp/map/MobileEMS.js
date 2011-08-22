MAP = {};
MAP.instances = new Array();

var MobEMS = new Class({
	
	Map: null,
	Geocoder: new EMS.Services.Geocoder(),
	Itin: null,
	RouteManager: null,
	
	/* 
	 * The map Control. Don't think we need it as an attribute. But this ease the debugging a lot 
	 * on devices.
	 */
	Control: null, 
	
	/**
	 * Array to hold points for routing purposes
	 */
	routeHistory: new Array(),
	
	nth: null,
	/**
	 * mapCenterOptions should be in JSON format
	 * i.e. {'latitude': 'latValue', 'longitude': 'lonValue', 'zoom': 'zoomLevel' } 
	 * all of the above attributes will be used for setCenter() on load.
	 */
	initialize: function(mapWrapper, viewOptions, mapOptions, poiOptions, directionOptions) {
		 _MapControlsCompulsory_ = _MapControlsCompulsory_.concat(this.filterOptionalMapControls());

		if(this.injectMapDiv(mapWrapper)) {
			this.Map = new EMS.Services.Map('map-div', {controls: _MapControlsCompulsory_, showMaxExtent: false, 
				onInit: function(map) {
					var mcl = map.controls.length;
					/* loop the controls */
					for(var i = 0; i<mcl; i++) {
						/* to switch the view */
						if(map.controls[i].CLASS_NAME == "EMS.Control.ViewMode"){
							if($defined(viewOptions) && $defined(viewOptions.layer))
								map.controls[i].switchTo(viewOptions.layer);
						}
						/* set this map instance to the LocateMe so the locate me controller
						 * is bind to this particular map instance
						 */
						if(map.controls[i].CLASS_NAME == "EMS.Control.LocateMe") {
							map.controls[i].setMapNth(this.nth);
						}
					}
					
					this.RouteManager = new EMS.Services.RouteManager(this.Map);
					
					/* parse the mapOption for initial display */
					if($defined(mapOptions)) {
						/* consider the boundingBox first. */
						if($defined(mapOptions.boundingBox)) {
							var bounds = new OpenLayers.Bounds();
							bounds.extend(this.formatLatLon(mapOptions.boundingBox.topLeft));
							bounds.extend(this.formatLatLon(mapOptions.boundingBox.bottomRight));
							
							/**
		                     * Special case for POI maps. The bounding box is considered the minimum area
		                     * to be viewed. Whereas the zoom might be more zoomed out to give the user more context
		                     * for where the POIs are. 
		                     */
							var zoomForExtent = this.Map.getZoomForExtent(bounds);
							if ($defined(mapOptions.zoomInThreshold)) {
								var newZoom = zoomForExtent >= mapOptions.zoomInThreshold ? 
										mapOptions.zoomInThreshold : zoomForExtent;
							} else {
								var newZoom = zoomForExtent;
							}
							this.Map.setCenter(bounds.getCenterLonLat(), newZoom);
						}
						/* if no boundingBox set the try the lat,lon and zoom */
						else if($defined(mapOptions.latitude) && $defined(mapOptions.longitude)) {
							var EMSLatLon = new EMS.LonLat(mapOptions.longitude, mapOptions.latitude);
							this.Map.setCenter(EMSLatLon, mapOptions.zoom);
						}
						
						/* add dock for popup if specified */
						if($defined(mapOptions.dock))
							this.addDock(map, mapOptions.dock);
					}
					
					/* parse the poiOption for initial display */
					if($defined(poiOptions)) {
						this.addPois(map, poiOptions);
					}
					
					/* parse the directionOptions for initial display */ 
					if($defined(directionOptions)) {
						var wayPointsLength = directionOptions.wayPoints.length;	
						for(var i = 0; i < wayPointsLength; i++)
							this.routeHistory.push(this.routeAddress(directionOptions.wayPoints[i]));
						this.route(directionOptions.fastest, directionOptions.tolls, directionOptions.transportType);
					}
					
					this.createDeviceLocationInstance(map);
				}.bind(this)
			});		
		}
		
		this.nth = MAP.instances.push(this) - 1;
	},
	
	/**
	 * Called by init() to inject an absolute-positioned div to contain a map into
	 * a relative-positioned mapWrapper. This is so that we can put the map anywhere 
	 * we want it in the page.
	 */
	injectMapDiv: function(mapWrapper) {
		if($defined(mapWrapper)) {
			$(mapWrapper).empty();
			
			var mapDiv = new Element('div');
			
			mapDiv.id = "map-div";
		
			$(mapWrapper).appendChild(mapDiv);
			return true;
		}
		
		else {
			try {
				sensis.warn('mapWrapper element is not defined');
			}
			catch(e) {}
				
			return false;
		}
	},

	/* filter down the valid controllers by white listing them and ignore the rest */
	filterOptionalMapControls: function() {
		var filteredOptionalMapControls = new Array();
		for(var i = 0; i < _MapControlsOptional_.length; i++) {
			var mapControlToBeCreated = _MapControlsOptional_[i];
			
			if($defined(EMS.Control[mapControlToBeCreated])) {
				var tempOptionalMapControl = new EMS.Control[mapControlToBeCreated];
				if(this.crossCheckOptionalIsNotDuplicateOfCompulsory(tempOptionalMapControl))
					filteredOptionalMapControls.push(tempOptionalMapControl);
			}
		}
		return filteredOptionalMapControls;
	},
	
	/* double check that the passed in controllers is not a duplicate of the compulsory controllers
	 * NOTE: although the white listing of the optional controllers kind of guarantee the uniqueness
	 * of the optional controllers registered, there is an edge case where Zoom is an optional from app 
	 * perspective but not quite so in the Android devices.
	 */
	crossCheckOptionalIsNotDuplicateOfCompulsory: function(optionalMapControlsInstance) {
		for(var i = 0; i < _MapControlsCompulsory_.length; i++) {
			if(optionalMapControlsInstance.CLASS_NAME == _MapControlsCompulsory_[i].CLASS_NAME) {
				return false;
				break;
			}
		}
		
		return true;
	},
	
	/** Lat Lon Utils **/
	isEMSLatLon: function(latlonObj) {
		if($defined(latlonObj.CLASS_NAME) && latlonObj.CLASS_NAME == "OpenLayers.LonLat")
			return true;
		return false;
	},
	
	formatLatLon: function(latlonObj) {
		if(this.isEMSLatLon(latlonObj)) return latlonObj;
		else return new EMS.LonLat(latlonObj.longitude, latlonObj.latitude);
	},
	
	isLatLonEqual: function(latlonObj1, latlonObj2) {
		if(latlonObj1.longitude == latlonObj2.longitude && latlonObj1.latitude == latlonObj2.latitude)
			return true;
		return false;
	},
	
	/** END **/
	
	/** POI Utils **/
	
	/* 
	 * Generates icon based on the option set
	 * 
	 * iconOption object needs the following attributes:
	 * -url
	 * -width
	 * -height
	 * -offsetX
	 * -offsetY
	 * 
	 * if none described, return false
	 **/
	createIcon: function(iconOption) {
		if(!$defined(iconOption)) return false;
		return new OpenLayers.Icon(
				iconOption.url,
				new OpenLayers.Size(iconOption.width, iconOption.height),
				new OpenLayers.Pixel(iconOption.offsetX, iconOption.offsetY)
		);
	},
	
	__addPoi: function(poiMarker, poiIcon) {
		var marker = new OpenLayers.Marker(this.formatLatLon(poiMarker.coordinates), this.createIcon(poiIcon));
		this.Map.markersLayer.addMarker(marker);

		marker.display(true);
		
		return marker;
	},
	
	addPoi: function(poiMarker, poiIcon) {
		return this.__addPoi(poiMarker, poiIcon);
	},
	
	/**
	 * iconsList needs the following attributes:
	 * - coordinates containing latitude and longitude attributes 
	 * - title for the pois *optional
	 * - description for the pois *optional
	 * - url for the icon
	 * - width for the icon
	 * - height for the icon
	 * - offsetX for the icon
	 * - offsetY for the icon
	 * 
	 * upon rendering the following method will take care the icons and
	 * markers generation based on the above info.
	 * 
	 * if no title and description given, the poi will not be clickable
	 */
	addPois: function(map, iconsList) {
		var resultLength = iconsList.length;
		if(resultLength > 0){
			for(var i = 0; i < resultLength; i++) {
				/* construct the marker and icon */
				var newMarker = {coordinates: iconsList[i].coordinates};
				var newIcon = {url: iconsList[i].url, 
						width: iconsList[i].width,
						height: iconsList[i].height,
						offsetX: iconsList[i].offsetX,
						offsetY: iconsList[i].offsetY
					};
				this.addPoi(newMarker, newIcon);
			}
		}
	},
	
	clearPois: function() {
		this.Map.markersLayer.clearMarkers();
	},
	/** END **/
	
	/** Route Utils **/
	
	/* build address to conform to RouteManager 
	 * 
	 * use this one to chunk the json object returned from the webapp
	 */
	routeAddress: function(wayPointObj) {
		var newWayPoint = {};
		newWayPoint.coordinates = wayPointObj.coordinates;
		// The EMS interface requires the street name to be set but it's
		// okay to set it to an empty string. Passing in a real street
		// would affect the "snap-to" street behaviour. 
		newWayPoint.street = {name: ''};
		return newWayPoint;
	},
	
	/* do the routing based on the points on routeHistory[].
	 * 
	 * all args (fastest, tolls, and transportType) should come from the 
	 * server.
	 * 
	 * Do nothing on display route, as the map height and zoom level has been taken 
	 * care of on init()
	 */
	route: function(fastest, tolls, transportType) {
		if(this.routeHistory.length >= 2)
			this.RouteManager.route(this.routeHistory, fastest, tolls, transportType, this.Map.vlayer, {});
	},
	
	clearRoutes: function() {
		this.RouteManager.clearRoute();
		this.routeHistory.empty();
	},
	/** END **/
	
	/**
	 * Function to clear all.
	 */
	clearMap: function() {
		this.clearRoutes();
		this.clearPois();
	},
	
	/* placeholder for additional stuff to be initiated along with the map */
	createDeviceLocationInstance: function() {},
	
	/* interface for creating dock for popup */
	addDock: function() {}
});
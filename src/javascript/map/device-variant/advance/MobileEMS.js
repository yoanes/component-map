MAP = {};
MAP.instances = new Array();

var MobEMS = new Class({
	
	Map: null,
	Geocoder: null,
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
		window.addEvent('load', function() {
			if(this.injectMapDiv(mapWrapper)) {
				this.Map = new EMS.Services.Map('map-div', {controls: _MapControls_});			
				this.Geocoder = new EMS.Services.Geocoder();
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
				}
				
				/* consider the view options before anything else */
				if($defined(viewOptions)) {
					if($defined(viewOptions.layer))
						this.switchView(viewOptions.layer);
					
					else viewOptions.layer = "map";
					
				}
				
				/* parse the poiOption for initial display */
				if($defined(poiOptions)) {
					this.addPois(poiOptions);
				}
				
				/* parse the directionOptions for initial display */ 
				if($defined(directionOptions)) {
					var wayPointsLength = directionOptions.wayPoints.length;
					
					for(var i = 0; i < wayPointsLength; i++)
						this.routeHistory.push(this.routeAddress(directionOptions.wayPoints[i]));
						
					this.route(directionOptions.fastest, directionOptions.tolls, directionOptions.transportType);
				}
			}
			return true;
		}.bind(this));
		
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
			mapDiv.style.position = 'absolute';
			mapDiv.style.top = '0px';
			mapDiv.style.left = '0px';
			mapDiv.style.height = '100%';
			mapDiv.style.width = '100%';
			mapDiv.style.overflow = 'hidden';
			
			mapDiv.style['-webkit-user-select'] = 'none';
			mapDiv.style['-webkit-touch-callout'] = 'none';
			
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
	
	formatPopupText: function(aPoiMarker) {
		var infoString = "<b>" + aPoiMarker.name + "</b><br/><i>" + aPoiMarker.description + "</i>";
		return infoString;
	},
	
	addPopup: function(marker, text) {
		/* 
		 * clear previous popup. Seems to quicken the switching between popup.
		 * leaving it to EMS to clear up slows the popup down considerably.
		 */
		if($defined(this.Popup)) { 
			this.Map.removePopup(this.Popup);
			this.Popup.destroy();
		}
		
		var popup = new EMS.Popup(marker.id, marker.lonlat, new OpenLayers.Size(100,50), text, marker.icon, true);
		this.Map.addPopup(popup, true);
		
		this.Popup = popup;
	},
	
	addPoi: function(poiMarker, poiIcon) {
		var marker = new OpenLayers.Marker(this.formatLatLon(poiMarker.coordinates), this.createIcon(poiIcon));
		this.Map.markersLayer.addMarker(marker);
		
		/* add popup when possible */
		if($defined(poiMarker.title) || $defined(poiMarker.description)) {
			marker.events.register("click", marker, function(e) { 
				this.addPopup(marker, this.formatPopupText(poiMarker));
			}.bind(this),false);
		}

		marker.display(true);
		
		return marker;
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
	addPois: function(iconsList) {
		var resultLength = iconsList.length;
		if(resultLength > 0){
			for(var i = 0; i < resultLength; i++) {
				/* construct the marker and icon */
				var newMarker = {coordinates: iconsList[i].coordinates, 
						title: iconsList[i].title, 
						description: iconsList[i].description
					};
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
		if($defined(this.Popup)) {
			this.Map.removePopup(this.Popup);
			this.Popup.destroy();
		}
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
	
	/**
	 * Switch the map view between photo and street. Presumably 
	 * the hybrid is excluded. 
	 */
	switchView: function(v) {
		if((v == "photo") || (!$defined(v) && this.Map.baseLayer.name == "Whereis Street"))
			this.Map.setBaseLayer(this.Map.whereis_photo_wms);
		else this.Map.setBaseLayer(this.Map.whereis_street_wms);	
	}
});
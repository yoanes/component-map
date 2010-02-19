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
	
	fullScreenMode: false,
	/**
	 * mapCenterOptions should be in JSON format
	 * i.e. {'latitude': 'latValue', 'longitude': 'lonValue', 'zoom': 'zoomLevel' } 
	 * all of the above attributes will be used for setCenter() on load.
	 */
	initialize: function(mapWrapper, mapOptions, viewOptions, poiOptions, directionOptions) {
		window.addEvent('load', function() {
			if(this.injectMapDiv(mapWrapper)) {
				this.Map = new EMS.Services.Map('map-div', {controls: []});			
				this.Geocoder = new EMS.Services.Geocoder();
				this.RouteManager = new EMS.Services.RouteManager(this.Map);
				
				/*
				 * Add the controller. EMS.Control.MobileDefaults() should be 
				 * defined somewhere above.
				*/ 
				this.Control = new EMS.Control.MobileDefaults(); 
				this.Map.addControl(this.Control);
				
				/* parse the mapOption for initial display */
				if($defined(mapOptions)) {
					/* consider the boundingBox first. */
					if($defined(mapOptions.boundingBox)) {
						var bounds = new OpenLayers.Bounds();
						bounds.extend(this.formatLatLon(mapOptions.boundingBox.topLeft));
						bounds.extend(this.formatLatLon(mapOptions.boundingBox.bottomRight));
						this.Map.zoomToExtent(bounds,true);
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
				
				/* parse the directionOptions for initial display 
				if($defined(directionOptions)) {
					var wayPointsLength = directionOptions.wayPoints.length;
					
					for(var i = 0; i < wayPointsLength; i++)
						this.routeHistory.push(this.routeAddress(directionOptions.wayPoints[i]));
						
					this.route(directionOptions.fastest, directionOptions.tolls, directionOptions.transportType);
				}
				*/
				
				this.renderToolbar();
			}
			return true;
		}.bind(this));
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
		newWayPoint.coordinates = this.formatLatLon(wayPointObj.coordinates);
		newWayPoint.street = {name: wayPointObj.streetName};
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
	},
	
	/* this method will overwrite the link on the #mapControls
	 * Ties in closely with the element id. So make sure you update this method when 
	 * you change the ids in render.tag
	 *  */
	renderToolbar: function() {
		/* grab all the buttons */
		var photoBtn = $('photoButton');
		var mapBtn = $('mapButton');
		
		var zoomInBtn = $('zoomInButton');
		var zoomOutBtn = $('zoomOutButton');
		
		var modeBtn = $('modeButton');
		
		/* handle the view toggling buttons. Hide the button that represents the current view */
		if(this.Map.baseLayer.name == "Whereis Street")
			mapBtn.setStyle('display', 'none');
		else photoBtn.setStyle('display', 'none');
		
		photoBtn.addEvent('click', function(e) {
			/* halt the event */
			e.stop();
			/* do the switch*/
			this.switchView('photo');
			
			/* fire off reporting */
			Reporting.to(photoBtn.href, {'lyr': 'p'});
			
			/* toggle the buttons */
			photoBtn.setStyle('display', 'none');
			mapBtn.setStyle('display', 'inline');
			
			return false;
		}.bind(this));
			
		mapBtn.addEvent('click', function(e) {
			/* halt the event */
			e.stop();
			/* do the switch*/
			this.switchView();
			
			/* fire off reporting */
			Reporting.to(mapBtn.href, {'lyr': 'm'});
			
			/* toggle the buttons */
			mapBtn.setStyle('display', 'none');
			photoBtn.setStyle('display', 'inline');
			
			return false;
		}.bind(this));
		
		/* handle the zoom buttons */
		zoomInBtn.addEvent('click', function(e){
			EMS.Util.smoothZoom(this.Map, this.Map.getCenter(), this.Map.getCenter(), this.Map.getZoom() + 1);
			return false;
		}.bind(this));
		
		zoomOutBtn.addEvent('click', function(e){
			EMS.Util.smoothZoom(this.Map, this.Map.getCenter(), this.Map.getCenter(), this.Map.getZoom() - 1);
			return false;
		}.bind(this));
		
		/* handle the full screen mode */
		modeBtn.addEvent('click', function(e){
			if(!this.fullScreenMode) {
				$(this.Map.div).parentNode.setStyle('height', (screen.height - 40) + 'px');
				$(this.Map.div).parentNode.setStyle('width', screen.width + 'px');
				this.fullScreenMode = true;
			}
			
			else {
				$(this.Map.div).parentNode.setStyle('height', '300px');
				$(this.Map.div).parentNode.setStyle('width', '100%');
				
				this.fullScreenMode = false;
			}
			
			this.Map.updateSize();
			
			window.scroll(0,$(this.Map.div).parentNode.offsetTop); 
			return false;
		}.bind(this));
	}
});
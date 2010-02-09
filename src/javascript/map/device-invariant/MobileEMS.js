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
	
	/**
	 * mapCenterOptions should be in JSON format
	 * i.e. {'latitude': 'latValue', 'longitude': 'lonValue', 'zoom': 'zoomLevel' } 
	 * all of the above attributes will be used for setCenter() on load.
	 */
	initialize: function(mapWrapper, mapOptions, poiOptions, directionOptions) {
		window.addEvent('load', function() {
			if(this.injectMapDiv(mapWrapper)) {
				this.Map = new EMS.Services.Map('map-div', {controls: []});				
				this.Geocoder = new EMS.Services.Geocoder();
				this.Itin = new EMS.Services.Itin();
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
				
				/* parse the poiOption for initial display */
				if($defined(poiOptions)) {
					this.addPois(poiOptions);
				}
				
				/* parse the directionOptiosn for initial display */
				if($defined(directionOptions)) {
					
				}
				
				/* ideally this is done in css for quicker display */
				$$('#map-div_OpenLayers_Container div').each(function(item){
						item.setStyle('background', "url('bg.jpg') repeat top left");
					});
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
	
	/**
	 * USE THIS TO BUILD YOUR ADDRESS OBJECT TO AVOID ANY ERROR
	 */
	buildAddress: function(streetNumber, streetName, suburb, state, unstructured) {
		var address = {};

		if($defined(unstructured)) address.unstructured = unstructured;
		if($defined(streetNumber)) address.streetNumber = streetNumber;
		if($defined(streetName)) address.street = streetName;
		if($defined(state)) address.state = state;
		if($defined(suburb)) address.suburb = suburb;

		return address;
	},
	
	/* 
	 * Addr is in format of {streetNumber: value, streetName: value, suburb: value, state: value, unstructured: value}
	 * will build address as object (not json object) and add it to routeHistory[].
	 */
	addToRoute: function(Addr, doClear) {
		/* clear previous points if indicated so */
		if($defined(doClear) && doClear === true) 
			this.routeHistory.empty();

		var query = {};
		query.address = this.buildAddress(Addr.streetNumber, Addr.streetName, Addr.suburb, Addr.state, Addr.unstructured);

		this.Geocoder.findGeocodedAddress(query, function(geoAddress) {
			var gAddr = geoAddress.results[0].address;
			this.routeHistory.push(gAddr);
			/* try to run the getRoute whenever any 2 points are already in array */
			this.getRoute();
		}.bind(this));
	},
	
	/**
	 * TODO: need to parse variables so that the RouteManager.route can be
	 * more flexible
	 * 
	 * Call this by itself to generate route. Make sure routeHistory[] is not
	 * empty. Otherwise this will return nothing.
	 */
	getRoute: function() {
		if(this.routeHistory.length >= 2){
			var rhLength = this.routeHistory.length;
			
			/** 
			 * do check: no consecutive points are meant to be the same
			 **/
			for(var i = 1; i < rhLength; i++){
				var current = this.routeHistory[i];
				var prev = this.routeHistory[i-1];
				if(this.isLatLonEqual(current.streetCoordinates, prev.streetCoordinates)) {
					return; // if true don't do any routing
					break;
				}
            }
			/** 
			 * do routing only if the script reaches this point
			 */
			this.Map.setCenter(this.formatLatLon(this.routeHistory[0].streetCoordinates));
			this.RouteManager.route(this.routeHistory, true, true, "ALL_VEHICLES", this.Map.vlayer, {});
		}
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
	switchView: function() {
		if(this.Map.baseLayer.name == "Whereis Street")
			this.Map.setBaseLayer(this.Map.whereis_photo_wms);
		else this.Map.setBaseLayer(this.Map.whereis_street_wms);
	},
	
	/** test section **/
	doMyRoute: function(fromState, toState, newRoute) {
		this.addToRoute({'unstructured': fromState}, newRoute);
		this.addToRoute({'unstructured': toState});
	},

});
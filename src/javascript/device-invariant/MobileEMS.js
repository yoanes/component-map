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
	initialize: function(mapWrapper, options) {
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
				
				if($defined(options)) {
					if($defined(options.latitude) && $defined(options.longitude)) {
						var EMSLatLon = new EMS.LonLat(options.longitude, options.latitude);
						this.Map.setCenter(EMSLatLon, options.zoom);
					}
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
			
			$(mapWrapper).appendChild(mapDiv);
			return true;
		}
		
		else {
			if(sensis instanceof Logger)
				sensis.warn('mapWrapper element is not defined');
				
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
	/** END **/
	
	/** Marker Utils **/
	createIcon: function(iconOption) {
		if(!$defined(iconOption)) /* nothing on marker. default to crossHair */
			return EMS.Services.StandardIcons.crossHair();
		
		var text = $defined(iconOption.text) ? iconOption.text : "";
		
		switch(iconOption.type) {
			/* start marker */
			case "start": 
				return new EMS.Services.StandardIcons.start();
				break;
				
			/* end marker */
			case "end": 
				return new EMS.Services.StandardIcons.end();
				break;
				
			case "crossHair": 
				return new EMS.Services.StandardIcons.crossHair();
				break;
				
			/* anything with text */
			default: 
				return new EMS.Services.StandardIcons.purplePoi(this.Map.tilePath, text);
				break;
		}
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
		if($defined(this.Popup)) this.Map.removePopup(this.Popup);
		
		var popup = new EMS.Popup(marker.id, marker.lonlat, new OpenLayers.Size(100,50), text, marker.icon, true);
		this.Map.addPopup(popup, true);
		
		this.Popup = popup;
	},
	
	addPoi: function(poiMarker, poiIcon) {
		var icon = this.createIcon(poiIcon); 
		var marker = new OpenLayers.Marker(this.formatLatLon(poiMarker.address.coordinates), icon);
		this.Map.markersLayer.addMarker(marker);
		
		/* add popup when possible */
		if($defined(poiMarker.name) || $defined(poiMarker.description)) {
			marker.events.register("click", marker, function(e) { 
				this.addPopup(marker, this.formatPopupText(poiMarker));
			}.bind(this),false);
		}

		marker.display(true);
		
		return marker;
	},
	
	addPois: function(markersList) {
		/* 
		 * 	you'll be looking at poiResults.results[0].addresses.results variable if 
		 *  the list is generated by combinedSearch.
		 *  for findAddress() you'll be looking at poiResults.results
		 */
		var resultLength = markersList.length;

		if(resultLength == 0) {
			if(sensis instanceof Logger) 
				sensis.log("no results found");
			return;
		}
		for(var i = 0; i < resultLength; i++) {
			this.addPoi(markersList[i], {'text': i + 1});
		}
	},
	
	clearPois: function() {
		this.Map.markersLayer.clearMarkers();
		if($defined(this.Popup)) this.Map.removePopup(this.Popup);
	},
	/** END **/
	
	/** Route Utils **/
	isLatLonEqual: function(latlonObj1, latlonObj2) {
		if(latlonObj1.longitude == latlonObj2.longitude && latlonObj1.latitude == latlonObj2.latitude)
			return true;
		return false;
	},
	
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
	
	/* generate pois for hardcoded addresses. For perf testing purpose only. Same thing for addMultiRoute() */
	addMultiPoi: function(r) {
		if(!$defined(r)) r = 50;
		this.Geocoder.combinedSearch({address: {suburb: 'melbourne', state:'vic'}, keyword: 'cars', size:r}, function(poiResults) {
			this.Map.setCenter(this.formatLatLon(poiResults.results[0].addresses.results[0].address.coordinates));
			this.addPois(poiResults.results[0].addresses.results);
		}.bind(this));
	},
	
	doMyRoute: function(fromState, toState, newRoute) {
		this.addToRoute({'unstructured': fromState}, newRoute);
		this.addToRoute({'unstructured': toState});
	},
	
	addMultiRoute: function() {
		var streets = new Array('Victoria St', 'Blyth St', 'Hope St', 'Cooraminta St', 'Brett St', 'Sydney Rd', 'Albert St', 'Church St', 'Edmends St', 'Thomas St', 'Stewart St');
		
		for(var j = 0; j<streets.length; j++) {
			
	//		sensis.log(streets[j]);
			
			this.Geocoder.findAddress(streets[j], 'Brunswick', 'VIC', function(addresses) {
				if(addresses.results.length == 1) {
					var currentAddress = addresses.results[0].address;
					if(i == 0) this.Map.setCenter(this.formatLatLon(currentAddress.coordinates));
					this.addPoi(currentAddress, {'text': 1 }) ;
				}
			}.bind(this));
			
			this.addToRoute({streetNumber: null, streetName: streets[j], suburb: 'Brunswick', state: 'VIC'});
		}
	}
});
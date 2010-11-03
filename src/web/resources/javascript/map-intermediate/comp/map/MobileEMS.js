MAP = {};
MAP.instances = new Array();

var MobEMS = new Class({
	
	Map: null,

	Extends: Utilities,
	
	nth: null,
	
	/* flag to enable the controllers */
	enable: true,
	
	/* controllers element in intermediate map */
	zoomIn: {element: null, href: null, faded: null },
	zoomOut: {element: null, href: null, faded: null },
	
	panNorth: {element: null, href: null},
	panSouth: {element: null, href: null},
	panWest: {element: null, href: null},
	panEast: {element: null, href: null},
	
	photoMode: {element: null, href: null},
	mapMode: {element: null, href: null},
	
	/**
	 * on init/window load try to grab the map and assign functions to the onclick event
	 * each buttons in the map toolbar
	 */
	initialize: function(mapWrapper, viewOptions) {
		window.addEvent('load', function() {
			/* get the map div */
			this.Map = $(mapWrapper);

			/* grab all the buttons */
			this.zoomIn.element = $('zoomInButton');
			this.zoomIn.href = this.zoomIn.element.href;
			this.zoomIn.faded = $('zoomInFaded');
			
			this.zoomOut.element= $('zoomOutButton');
			this.zoomOut.href = this.zoomOut.element.href;
			this.zoomOut.faded = $('zoomOutFaded');
			
			this.panNorth.element = $('panNorthButton');
			this.panNorth.href = this.panNorth.element.href;
			
			this.panSouth.element = $('panSouthButton');
			this.panSouth.href = this.panSouth.element.href;
			
			this.panWest.element = $('panWestButton');
			this.panWest.href = this.panWest.element.href;
			
			this.panEast.element = $('panEastButton');
			this.panEast.href = this.panEast.element.href;
			
			this.photoMode.element = $('photoButton');
			this.photoMode.href = this.photoMode.element.href;
			
			this.mapMode.element = $('mapButton');
			this.mapMode.href = this.mapMode.element.href;
			
			this.zoomIn.element.href = this.zoomOut.element.href = this.panNorth.element.href = this.panSouth.element.href = this.panWest.element.href = this.panEast.element.href = this.photoMode.element.href = this.mapMode.element.href = "#map";
			
			/* show and hide the appropriate controllers */
			this.viewToggle(viewOptions.layer);
			this.zoomInMaxToggle(false);
			this.zoomOutMaxToggle(false);
			
			/* attach events to the links */
			this.renderToolbar();
			
			return true;
		}.bind(this));
		
		this.nth = MAP.instances.push(this) - 1;
	},
	
	/**
	 * Switch the map view between photo and street. Presumably 
	 * the hybrid is excluded. 
	 */
	viewToggle: function(v) {
		if(v == "photo") {
			this.mapMode.element.style.display = 'inline';
			this.photoMode.element.style.display = 'none';
		}
		else if(v == "map") {
			this.mapMode.element.style.display = 'none';
			this.photoMode.element.style.display = 'inline';
		}
	},
	
	/**
	 * toggle for the zoom in and out
	 */
	zoomInMaxToggle: function(max) {
		if(max) {
			this.zoomIn.faded.style.display = 'inline';
			this.zoomIn.element.style.display = 'none';
		}
		else {
			this.zoomIn.faded.style.display = 'none';
			this.zoomIn.element.style.display = 'inline';
		}
	},
	
	zoomOutMaxToggle: function(max) {
		if(max){
			this.zoomOut.faded.style.display = 'inline';
			this.zoomOut.element.style.display = 'none';
		}
		else {
			this.zoomOut.faded.style.display = 'none';
			this.zoomOut.element.style.display = 'inline';
		}
	},
	
	/**
	 * methods to do bulk updates on the map controls' urls
	 */
	updateURL: function(responseObj) {
		if(!$defined(responseObj)) return;
		
		this.zoomIn.href = responseObj.zoomInBtnUrl;
		this.zoomOut.href = responseObj.zoomOutBtnUrl;
		
		this.panEast.href = responseObj.eastBtnUrl;
		this.panWest.href = responseObj.westBtnUrl;
		this.panNorth.href = responseObj.northBtnUrl;
		this.panSouth.href = responseObj.southBtnUrl;
		
		this.photoMode.href = responseObj.photoBtnUrl;
		this.mapMode.href = responseObj.mapBtnUrl;
	},
	
	/**
	 * go and fetch the new image via ajax
	 */
	getNewMap: function(url) {
		/* create a new object to retrieve a new map url via ajax */
		var MapRequest = new MobileRequest({
			method: 'get',
			url: this.maintainSession(url),
			/* on completion of url retrieval, update the map img */
			onComplete: function(responseText) {
				var jsonResponse = JSON.decode(responseText);
				/* get the new map images */
				this.updateMap(jsonResponse.mapImage);
				/* update the zoom controllers */
				this.zoomInMaxToggle(jsonResponse.minZoom);
				this.zoomOutMaxToggle(jsonResponse.maxZoom);
				/* update the urls */
				this.updateURL(jsonResponse);
				/* re-enable the controllers */
				this.enable = true;
			}.bind(this),
			onFailure: function(response) {
				this.enable = true;
			}.bind(this)
		});
		
		MapRequest.send();
	},
	
	/* method to update the displayed map 
	 * It assumes that 1st child node of this.Map is an image of the map.
	 * */
	updateMap: function(imgSrc) {
		if($defined(imgSrc)) {
			// Use Mootools getChildren instead of childNodes[0]. The latter may allow
			// the array to contain TextNodes, whilst the former will not.
			$(this.Map).getChildren('img')[0].src = imgSrc;
		}
	},
	
	/**
	 * execute a click and disable the controllers
	 */
	execClick: function(url) {
		if(!$defined(url)) return;

		if(this.enable) {
			/* get a new map */
			this.getNewMap(url);
			/* disable the other controls */
			this.enable = false;
		}
	},
	
	/* this method will overwrite the link on the #mapControls
	 * Ties in closely with the element id. So make sure you update this method when 
	 * you change the ids in render.tag
	 *  */
	renderToolbar: function() {
		/* handle the zoom buttons. */
		/* Don't try to attach the event if we are at max zoom in/out. The zoomInBtn and zoomOutBtn will not
		 * be present */

		this.zoomIn.element.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.zoomIn.href);
			return false;
		}.bind(this));

		this.zoomOut.element.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.zoomOut.href);
			return false;
		}.bind(this));

		
		/* handle the pan buttons */
		this.panNorth.element.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.panNorth.href);
			return false;
		}.bind(this));
		
		this.panSouth.element.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.panSouth.href);
			return false;
		}.bind(this));
		
		this.panWest.element.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.panWest.href);
			return false;
		}.bind(this));
		
		this.panEast.element.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.panEast.href);
			return false;
		}.bind(this));
		
		this.photoMode.element.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.photoMode.href);
			this.viewToggle("photo");
			return false;
		}.bind(this));
		
		this.mapMode.element.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.mapMode.href);
			this.viewToggle("map");
			return false;
		}.bind(this));
	}
});
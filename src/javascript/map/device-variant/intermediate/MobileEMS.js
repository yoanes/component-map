MAP = {};
MAP.instances = new Array();

var MobEMS = new Class({
	
	Map: null,

	Extends: Utilities,
	
	nth: null,
	
	/* flag to enable the controllers */
	enable: true,
	
	/* controllers element in intermediate map */
	zoomInBtn: null,
	zoomInFBtn: null,
	
	zoomOutBtn: null,
	zoomOutFBtn: null,
	
	panNorthBtn: null,
	panSouthBtn: null,
	panWestBtn: null,
	panEastBtn: null,
	
	photoBtn: null,
	mapBtn: null,
	
	/**
	 * on init/window load try to grab the map and assign functions to the onclick event
	 * each buttons in the map toolbar
	 */
	initialize: function(mapWrapper, viewOptions) {
		window.addEvent('load', function() {
			/* get the map div */
			this.Map = $(mapWrapper);

			/* grab all the buttons */
			this.zoomInBtn = $('zoomInButton');
			this.zoomInFBtn = $('zoomInFaded');
			
			this.zoomOutBtn = $('zoomOutButton');
			this.zoomOutFBtn = $('zoomOutFaded');
			
			this.panNorthBtn = $('panNorthButton');
			this.panSouthBtn = $('panSouthButton');
			this.panWestBtn = $('panWestButton');
			this.panEastBtn = $('panEastButton');
			
			this.photoBtn = $('photoButton');
			this.mapBtn = $('mapButton');
			
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
			this.mapBtn.style.display = 'inline';
			this.photoBtn.style.display = 'none';
		}
		else if(v == "map") {
			this.mapBtn.style.display = 'none';
			this.photoBtn.style.display = 'inline';
		}
	},
	
	/**
	 * toggle for the zoom in and out
	 */
	zoomInMaxToggle: function(max) {
		if(max) {
			this.zoomInFBtn.style.display = 'inline';
			this.zoomInBtn.style.display = 'none';
		}
		else {
			this.zoomInFBtn.style.display = 'none';
			this.zoomInBtn.style.display = 'inline';
		}
	},
	
	zoomOutMaxToggle: function(max) {
		if(max){
			this.zoomOutFBtn.style.display = 'inline';
			this.zoomOutBtn.style.display = 'none';
		}
		else {
			this.zoomOutFBtn.style.display = 'none';
			this.zoomOutBtn.style.display = 'inline';
		}
	},
	
	/**
	 * methods to do bulk updates on the map controls' urls
	 */
	updateURL: function(responseObj) {
		if(!$defined(responseObj)) return;
		
		this.zoomInBtn.href = responseObj.zoomInBtnUrl;
		this.zoomOutBtn.href = responseObj.zoomOutBtnUrl;
		
		this.panEastBtn.href = responseObj.eastBtnUrl;
		this.panWestBtn.href = responseObj.westBtnUrl;
		this.panNorthBtn.href = responseObj.northBtnUrl;
		this.panSouthBtn.href = responseObj.southBtnUrl;
		
		this.photoBtn.href = responseObj.photoBtnUrl;
		this.mapBtn.href = responseObj.mapBtnUrl;
	},
	
	/**
	 * go and fetch the new image via ajax
	 */
	getNewMap: function(url) {
		/* create a new object to retrieve a new map url via ajax */
		var MapRequest = new Request({
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
			}.bind(this)
		});
		
		MapRequest.send();
	},
	
	/* method to update the displayed map 
	 * It assumes that 1st child node of this.Map is an image of the map.
	 * */
	updateMap: function(imgSrc) {
		if($defined(imgSrc))
			this.Map.childNodes[0].src = imgSrc;
	},
	
	/**
	 * execute a click and disable the controllers
	 */
	execClick: function(hrefEl) {
		if(!$defined(hrefEl)) return;

		if(this.enable) {
			/* grab the url/href value */
			var url2hit= hrefEl.href;
			/* get a new map */
			this.getNewMap(url2hit);
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

		this.zoomInBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.zoomInBtn);
			return false;
		}.bind(this));

		this.zoomOutBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.zoomOutBtn);
			return false;
		}.bind(this));

		
		/* handle the pan buttons */
		this.panNorthBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.panNorthBtn);
			return false;
		}.bind(this));
		
		this.panSouthBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.panSouthBtn);
			return false;
		}.bind(this));
		
		this.panWestBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.panWestBtn);
			return false;
		}.bind(this));
		
		this.panEastBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.panEastBtn);
			return false;
		}.bind(this));
		
		this.photoBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.photoBtn);
			this.viewToggle("photo");
			return false;
		}.bind(this));
		
		this.mapBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			this.execClick(this.mapBtn);
			this.viewToggle("map");
			return false;
		}.bind(this));
	}
});
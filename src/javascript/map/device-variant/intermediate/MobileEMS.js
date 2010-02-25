var MobEMS = new Class({
	
	Map: null,

	Extends: Utilities,
	
	/**
	 * on init/window load try to grab the map and assign functions to the onclick event
	 * each buttons in the map toolbar
	 */
	initialize: function(mapWrapper, viewOptions) {
		window.addEvent('load', function() {
			/* get the map div */
			this.Map = $(mapWrapper);

			this.renderToolbar();
			return true;
		}.bind(this));
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
	
	getNewMap: function(url) {
		/* create a new object to retrieve a new map url via ajax */
		var MapRequest = new Request({
			method: 'get',
			url: this.maintainSession(url),
			/* on completion of url retrieval, update the map img */
			onComplete: function(newMapUrl) {
				this.updateMap(newMapUrl);
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
	
	/* this method will overwrite the link on the #mapControls
	 * Ties in closely with the element id. So make sure you update this method when 
	 * you change the ids in render.tag
	 *  */
	renderToolbar: function() {
		/* grab all the buttons */
		var zoomInBtn = $('zoomInButton');
		var zoomOutBtn = $('zoomOutButton');
		
		var panNorthBtn = $('panNorthButton');
		var panSouthBtn = $('panSouthButton');
		var panWestBtn = $('panWestButton');
		var panEastBtn = $('panEastButton');
		
		var photoBtn = $('photoButton');
		var mapBtn = $('mapButton');
		
		/* handle the zoom buttons. */
		/* Don't try to attach the event if we are at max zoom in/out. The zoomInBtn and zoomOutBtn will not
		 * be present */
		if($defined(zommInBtn)) {
			zoomInBtn.addEvent('click', function(e){
				/* halt the event */
				e.stop();
				/* grab the url */
				var url2hit= zoomInBtn.href;
				
				this.getNewMap(url2hit);
				
				return false;
			}.bind(this));
		}
		
		if($defined(zoomOutBtn)) {
			zoomOutBtn.addEvent('click', function(e){
				/* halt the event */
				e.stop();
				/* grab the url */
				var url2hit= zoomOutBtn.href;
				
				this.getNewMap(url2hit);
				
				return false;
			}.bind(this));
		}
		
		/* handle the pan buttons */
		panNorthBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			/* grab the url */
			var url2hit= panNorthBtn.href;
			
			this.getNewMap(url2hit);
			
			return false;
		}.bind(this));
		
		panSouthBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			/* grab the url */
			var url2hit= panSouthBtn.href;
			
			this.getNewMap(url2hit);
			
			return false;
		}.bind(this));
		
		panWestBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			/* grab the url */
			var url2hit= panWestBtn.href;
			
			this.getNewMap(url2hit);
			
			return false;
		}.bind(this));
		
		panEastBtn.addEvent('click', function(e){
			/* halt the event */
			e.stop();
			/* grab the url */
			var url2hit= panEastBtn.href;
			
			this.getNewMap(url2hit);
			
			return false;
		}.bind(this));
	}
});
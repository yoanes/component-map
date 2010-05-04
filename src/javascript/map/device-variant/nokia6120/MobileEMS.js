var MobEMS = new Class({
	Extends: MobEMSPrototype,
	
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
		
		MapRequest.send('xrw=xhr');
	}
});
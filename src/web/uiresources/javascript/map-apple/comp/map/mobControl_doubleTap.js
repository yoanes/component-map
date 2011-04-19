EMS.Control.DoubleTap = OpenLayers.Class(OpenLayers.Control, {
	
	Device: "webkit", 
	BROWSER_ENGINE: "webkit",
	CLASS_NAME: "EMS.Control.DoubleTap",
	
	active: false,
	
	doubleTapDurationLimit: 1000,
	doubleTapRangeLimit: 50, 
	
	prevTapRecorded: false,
	prevTapTimestamp: 0,
	prevTapCoordinate: null,
	prevTapId: null,
	
	animationTime: 250,
	
	initialize: function() {
		this.active = true;
	},
	
	detect: function(node) {
		var currentTapTimestamp = new Date().getTime();
		if(!this.prevTapRecorded) { 
			this.prevTapTimestamp = currentTapTimestamp;
			this.prevTapCoordinate = {'x': node.pageX, 'y': node.pageY };
			this.prevTapId = node.identifier;
			this.prevTapRecorded = true;
		}
		else { 
			var duration = currentTapTimestamp - this.prevTapTimestamp;
			var range = this.getRangeBetweenTaps({'x': node.pageX, 'y': node.pageY});
			if(this.isValidDoubleTap(duration, range)) {
				this.execDoubleTap(node);
				this.reset();
				return;
			}
			
			this.reset();
		}
	},
	
	getRangeBetweenTaps: function(coord) {
		return {'x': Math.abs(coord.x - this.prevTapCoordinate.x), 
				'y': Math.abs(coord.y - this.prevTapCoordinate.y)};
	},
	
	isValidDoubleTap: function(duration, range) {
		if(duration <= this.doubleTapDurationLimit && 
		   range.x <= this.doubleTapRangeLimit && range.y <= this.doubleTapRangeLimit)
			return true;
		else return false;
	},
	
	execDoubleTap: function(node) {
		/* calculate the coordinate of the center of the double tap */
		var newX = Math.ceil((node.pageX + this.prevTapCoordinate.x) / 2) - $(this.map.div.parentNode).offsetLeft; 
		var newY = Math.ceil((node.pageY + this.prevTapCoordinate.y) / 2) - $(this.map.div.parentNode).offsetTop;
		
		/* if it is the max zoomed in, does nothing */
		if(this.map.getZoom() == 16) return;
		
		/* find the map center both in lonlat and px */
		var centerPx = this.map.getViewPortPxFromLonLat(this.map.getCenter());
		var centerCoord = this.map.getCenter();
		
		/* calculate the distance */
		var distance = new OpenLayers.Pixel(newX - centerPx.x, newY - centerPx.y);
		/* get the next zoom's resolution */
		var nextZoomResolution = this.map.baseLayer.resolutions[this.map.getZoom() + 1];
	
		/* calculate the new center coord on the next zoom level */
		var newCenterCoord = new OpenLayers.LonLat(
			centerCoord.lon + ((distance.x) * nextZoomResolution),
			centerCoord.lat - ((distance.y) * nextZoomResolution)
		);
		
		/* animate the layerContainerDiv so it won't mess up with the panning / zooming
		 * which will animate the viewportDiv
		 */
		
		/* grab the new top and left because we need to translate the origin point relatives to how much
		 * user has panned before doing double tap. The translate 3d properties of the viewport is unusable
		 * as it is most likely has been reverted to null
		 */
		var newLeft = this.map.layerContainerDiv.style.left.toInt();
		var newTop = this.map.layerContainerDiv.style.top.toInt();
		
		this.map.layerContainerDiv.style['-webkit-transition'] = '-webkit-transform ' + this.animationTime + 'ms linear';
	    this.map.layerContainerDiv.style['-webkit-transform-origin'] = (newX-newLeft) + 'px ' + (newY-newTop) + 'px 0';
	    this.map.layerContainerDiv.style['-webkit-transform'] = 'scale3d(2, 2, 1)';

	    /* delay the css reset and actual zoom to allow for animation */
		setTimeout(function(){
			this.map.setCenter(newCenterCoord, this.map.zoom + 1);
			this.map.layerContainerDiv.style['-webkit-transform'] = '';
			this.map.layerContainerDiv.style['-webkit-transition'] = '';
			this.map.layerContainerDiv.style['-webkit-transform-origin'] = '';
		}.bind(this), this.animationTime);
	},
	
	reset: function() {
		this.prevTapRecorded= false;
		this.prevTapTimestamp = 0;
		this.prevTapCoordinate = null;
	},
	
	draw: function() {
		this.map.div.addEventListener('touchstart', function(e){
			/* reset if there are 2 or more touches on the map */
			if(e.touches.length >= 2) {
				this.reset();
				return;
			}
			
			var node = e.touches[0];
			/* catch if double tap */
			this.detect(node);
		}.bind(this), false);
		
		this.map.div.addEventListener('touchmove', function(e){
			this.reset();
		}.bind(this), false);
		
		this.map.div.addEventListener('gesturechange', function(e){
			this.reset();
		}.bind(this), false);
		
	}
});

_MapControls_.push(new EMS.Control.DoubleTap);
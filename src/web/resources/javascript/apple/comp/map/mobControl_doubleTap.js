EMS.Control.DoubleTap = OpenLayers.Class(OpenLayers.Control, {
	
	Device: "webkit", 
	BROWSER_ENGINE: "webkit",
	CLASS_NAME: "EMS.Control.DoubleTap",
	
	active: false,
	
	doubleTapDurationLimit: 2000,
	doubleTapRangeLimit: 100, 
	
	prevTapRecorded: false,
	prevTapTimestamp: 0,
	prevTapCoordinate: null,
	prevTapId: null,
	
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
			if(node.identifier == this.prevTapId) {
				var duration = currentTapTimestamp - this.prevTapTimestamp;
				var range = this.getRangeBetweenTaps({'x': node.pageX, 'y': node.pageY});
				if(this.isValidDoubleTap(duration, range)) {
					this.execDoubleTap(node);
					this.reset();
					return;
				}
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
		var newX = (node.pageX + this.prevTapCoordinate.x) / 2 - $(this.map.div.parentNode).offsetLeft; 
		var newY = (node.pageY + this.prevTapCoordinate.y) / 2 - $(this.map.div.parentNode).offsetTop; 
		var newCenter = this.map.getLonLatFromViewPortPx(new OpenLayers.Pixel(Math.floor(newX), Math.floor(newY))); 
		EMS.Util.smoothZoom(this.map, this.map.getCenter(), newCenter, this.map.getZoom() + 1);
	//	this.map.setCenter(newCenter, this.map.zoom + 1);
	},
	
	reset: function() {
		this.prevTapRecorded= false;
		this.prevTapTimestamp = 0;
		this.prevTapCoordinate = null;
	},
	
	draw: function() {
		this.map.div.addEventListener('touchstart', function(e){
			var node = e.touches[0];
			/* catch if double tap */
			this.detect(node);
		}.bind(this), false);
		
		this.map.div.addEventListener('touchmove', function(e){
			this.reset();
		}.bind(this), false);
	}
});

_MapControls_.push(new EMS.Control.DoubleTap);
EMS.Control.MobileDefaults = OpenLayers.Class(OpenLayers.Control, { 
		
	Device: "Android-OS",
	CLASS_NAME: "EMS.Control.MobileDefaults",
	
	active: false,
	
	X: null,
	Y: null,
	
	dX: null,
	dY: null,
	
	// subControl:{},
	
	initialize: function() {
		this.active = true;
	},
	
	execTouchStart: function(e) {
		e.preventDefault();
		var node = e.touches[0];

		/* catch if double tap */
	// TODO: disable DoubleTap for now because we were getting a mootools recursion error (in Firefox, anyway).
	//	this.subControl.DoubleTap.detect(node);
		
		this.X = node.pageX;
		this.Y = node.pageY;
		
		this.dX = 0;
		this.dY = 0;
	},
	
	execTouchMove: function(e) {
		var node = e.targetTouches[0];

		var movedX = node.pageX;
		var movedY = node.pageY;

		var diffX = this.X - movedX;
		var diffY = this.Y - movedY;

		this.X = movedX;
		this.Y = movedY;

		$(this.map.layerContainerDiv).setStyle('left', $(this.map.layerContainerDiv).getStyle('left').toInt() - diffX + "px");
		$(this.map.layerContainerDiv).setStyle('top',  $(this.map.layerContainerDiv).getStyle('top').toInt() - diffY + "px");

		this.dX += diffX;
		this.dY += diffY;
		
		/* reset double tap detection when user move fingers */
    // TODO: disable DoubleTap for now because we were getting a mootools recursion error (in Firefox, anyway).		
	//	this.subControl.DoubleTap.reset();
	},            
	
	execTouchEnd: function(e) {
		this.map.pan(this.dX, this.dY);
	},
	
	/**
	 * APIMethod: draw
	 */
	
	draw: function() { //see observe()
		this.map.div.ontouchstart = this.execTouchStart.bind(this);
		this.map.div.ontouchmove = this.execTouchMove.bind(this);
		this.map.div.ontouchend = this.execTouchEnd.bind(this); 
		
    // TODO: disable DoubleTap for now because we were getting a mootools recursion error (in Firefox, anyway).		
	//	this.subControl.DoubleTap = new EMS.Control.DoubleTap(this.map);
	},
	
	/**	
	 * APIMethod: destroy	
	 * Constructs contents of the control.	
	 *	
	 * Returns:	
	 * A reference to a div that represents this control.	
	 */    
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
	
		this.handler = null;	
	}
});

EMS.Control.DoubleTap = new Class({
	Extends: OpenLayers.Control,
	
	BROWSER_ENGINE: "webkit",
	CLASS_NAME: "EMS.Control.DoubleTap",
	
	active: false,
	
	doubleTapDurationLimit: 1000,
	doubleTapRangeLimit: 100, 
	
	prevTapRecorded: false,
	prevTapTimestamp: 0,
	prevTapCoordinate: null,
	
	initialize: function(map) {
		this.active = true;
		this.setMap(map);
	},
	
	detect: function(node) {
		var currentTapTimestamp = new Date().getTime();
		if(!this.prevTapRecorded) { 
			if(this.prevTapTimestamp == 0) 
				this.prevTapTimestamp = currentTapTimestamp;
			if(!$defined(this.prevTapCoordinate))
				this.prevTapCoordinate = {'x': node.pageX, 'y': node.pageY };
			this.prevTapRecorded = true;
		}
		else { 
			var duration = currentTapTimestamp - this.prevTapTimestamp;
			var range = this.getRangeBetweenTaps({'x': node.pageX, 'y': node.pageY});
			if(this.isValidDoubleTap(duration, range)) {
				this.execDoubleTap(node.pageX, node.pageY);
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
	
	execDoubleTap: function(x, y) { 
		var newX = (x + this.prevTapCoordinate.x) / 2 - $(this.map.div.parentNode).offsetLeft; 
		var newY = (y + this.prevTapCoordinate.y) / 2 - $(this.map.div.parentNode).offsetTop; 
		var newCenter = this.map.getLonLatFromViewPortPx(new OpenLayers.Pixel(Math.floor(newX), Math.floor(newY))); 
		this.map.setCenter(newCenter, this.map.zoom + 1);
	},
	
	reset: function() {
		this.prevTapRecorded= false;
		this.prevTapTimestamp = 0;
		this.prevTapCoordinate = null;
	}
});
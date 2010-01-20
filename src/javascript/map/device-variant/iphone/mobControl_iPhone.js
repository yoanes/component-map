EMS.Control.MobileDefaults = new Class({
	Extends: OpenLayers.Control,
	
	Device: "Apple",
	CLASS_NAME: "EMS.Control.MobileDefaults",
	
	active: false,
	
	X: null,
	Y: null,
	
	/* attributes to hold the delta x and y */
	dX: 0,
	dY: 0,
	
	/* center point of multi touch */
	cX: null,
	cY: null,
	
	/* center point for zoom scale. in percentage */
	zX: null,
	zY: null,
	
	/* 
	 * attribute to record the states on zoomStart(gestureStart really)
	 * and use it for comparison on zoomEnd(gestureEnd)
	 */
	resolutionState: 0,
	
	/* flag to do extra panning upon zoom */
	panFlag: false,
	
	subControl: {},
	
	initialize: function() {
		this.active = true;
		if(!this.isOS3()) { 
			/** set the transform style for OS 2 */
		//	this.map.viewPortDiv.style['-webkit-transform-style'] = 'preserve-3d';
		}
	},
	
	/* detect if the iphone os is of version 3 or not */
	isOS3: function() {
		var ua = new String(navigator.userAgent); 			
		var pattern = new RegExp('iPhone OS 3', '');
		var isOS3 = ua.search(pattern);
		
		if(isOS3 == -1) return false;
		else return true;
	},
	
	/* return the center point of multi touches with respect to the map viewport */
	getCenterTouch: function(n1, n2, f) {
		var cX = (n1.pageX + n2.pageX) / 2 - $(this.map.div.parentNode).offsetLeft;
		var cY = (n1.pageY + n2.pageY) / 2 - $(this.map.div.parentNode).offsetTop;
		
		if(f == 'percent') 
			/* return the format as percentage */
			return {'x': cX / this.map.size.w * 100, 'y': cY / this.map.size.h * 100};
		else if(!$defined(f) || f == 'integer')
			/* return the actual px */
			return {'x': Math.floor(cX), 'y': Math.floor(cY)};
	},
	
	execTouchStart: function(e) {
		var node = e.touches[0];
		
		if(e.touches.length == 1) {
			this.subControl.DoubleTap.detect(node);
			
			this.X = node.pageX;
			this.Y = node.pageY;
		}
		
		else if(e.touches.length == 2) {
			var node1 = e.touches[1];
			
			var pointCenterTouch = this.getCenterTouch(node, node1);
			var percentCenterTouch = this.getCenterTouch(node, node1, 'percent');
			
			this.cX = pointCenterTouch.x;
			this.cY = pointCenterTouch.y;
			
			this.zX = percentCenterTouch.x;
			this.zY = percentCenterTouch.y;
		}
	},
	
	execTouchMove: function(e) {
		e.preventDefault();
		
		var node = e.touches[0];

		if(e.touches.length == 1) {
			var movedX = node.pageX;
			var movedY = node.pageY;

			var diffX = this.X - movedX;
			var diffY = this.Y - movedY;

			this.X = movedX;
			this.Y = movedY;
			
			if(this.isOS3())
				this.map.pan(diffX, diffY);
	
			else {
				 /* temporarily fake the panning for OS 2. */
				 $(this.map.layerContainerDiv).setStyle('left', $(this.map.layerContainerDiv).getStyle('left').toInt() - diffX + "px");
				 $(this.map.layerContainerDiv).setStyle('top',  $(this.map.layerContainerDiv).getStyle('top').toInt() - diffY + "px");
	
				 this.dX += diffX;
				 this.dY += diffY;
			}
		}
		
		else if(e.touches.length == 2){
			var node1 = e.touches[1];
			/* 
			 * manipulate the viewport on zoom to simulate the panning somehow 
			 * also calculate the center point and how much it has moved 
			 */
			var centerTouch2 = this.getCenterTouch(node, node1);
			
			var diffCX = this.cX - centerTouch2.x;
			var diffCY = this.cY - centerTouch2.y;
			
			this.cX = centerTouch2.x;
			this.cY = centerTouch2.y;
			
			$(this.map.viewPortDiv).setStyle('margin-left', $(this.map.viewPortDiv).getStyle('margin-left').toInt()- diffCX + "px");
			$(this.map.viewPortDiv).setStyle('margin-top',  $(this.map.viewPortDiv).getStyle('margin-top').toInt() - diffCY + "px");
			
			this.dX += diffCX;
			this.dY += diffCY;
			
			this.panFlag = true;
		}
		
		this.subControl.DoubleTap.reset();
	},
	
	execTouchEnd: function(e) {
		/* do the actual panning on OS 2. */
		if(!this.isOS3()) {
			if(e.changedTouches.length == 1) {
				this.map.pan(this.dX, this.dY);
				this.dX = this.dY = 0;
			}
		}
	},             
	
	execGestureStart: function(e) {
		this.resolutionState = this.map.getResolution();
	},
	
	execGestureChange: function(e){
		e.preventDefault();
		
		this.map.viewPortDiv.style['-webkit-transform'] = 'scale(' + e.scale + ')';	
		this.map.viewPortDiv.style['-webkit-transform-origin-x'] = this.zX + '%';	
		this.map.viewPortDiv.style['-webkit-transform-origin-y'] = this.zY + '%';
	},
	
	execGestureEnd: function(e) {
		var scale = e.scale;
		
		if(scale > 1.3){
			if(!this.map.isValidZoomLevel(this.map.zoom + 1)) 
				this.map.viewPortDiv.style['-webkit-transform'] = 'scale(1)';
			else this.map.zoomIn();
		}
		else if(scale < 0.7){
			if(!this.map.isValidZoomLevel(this.map.zoom - 1)) 
				this.map.viewPortDiv.style['-webkit-transform'] = 'scale(1)';
			else this.map.zoomOut();
		}
		else {
			this.map.viewPortDiv.style['-webkit-transform'] = 'scale(1)';
		}
		
		/* do panning if required */
		if(this.panFlag) {
			/* calculate the factor */
			var currentResolutionState = this.map.getResolution();
			var factor = currentResolutionState / this.resolutionState;
			
			/* do panning based on the factor */
			this.map.pan(Math.floor(factor * this.dX), Math.floor(factor * this.dY));
			
			/* reset attributes */
			this.dX = this.dY = 0;
			this.panFlag = false;
			
			/* revert back after panning during zoom */
			$(this.map.viewPortDiv).setStyle('margin-left', "0px");
			$(this.map.viewPortDiv).setStyle('margin-top', "0px");
		}
	},
	
	/**
	 * APIMethod: draw
	 */
	
	draw: function() { //see observe()
		this.map.div.ongesturestart = this.execGestureStart.bind(this);
		this.map.div.ongesturechange = this.execGestureChange.bind(this);
		this.map.div.ongestureend = this.execGestureEnd.bind(this);
		this.map.div.ontouchstart = this.execTouchStart.bind(this);
		this.map.div.ontouchmove = this.execTouchMove.bind(this);
		this.map.div.ontouchend = this.execTouchEnd.bind(this);

		/** register the zoomend event as well. Does the reverting back to original scale */
		this.map.events.register('zoomend', this, function() { 
			this.map.viewPortDiv.style['-webkit-transform'] = 'scale(1)'; 
		});
		
		/** register the subController **/
		this.subControl.Resizer = new EMS.Control.Resizer(this.map);
		this.subControl.DoubleTap = new EMS.Control.DoubleTap(this.map);
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

/* double tap control */
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

/* extra controller to resize the map. A proto from the ux dude */
EMS.Control.Resizer = new Class({
	Extends: OpenLayers.Control,
	
	Device: "Apple",
	CLASS_NAME: "EMS.Control.Resizer",
	
	resizeButtonSize: new OpenLayers.Size(50,50),
	resizeButtonPadding: 5,

	active: false,
	
	mapHeightState: 'min',
	
	initialize: function(map) {
		this.active = true;
		this.setMap(map);
		this.draw();
	},
	
	draw: function() {
		var mapSize = this.map.getSize();
		var resizeControlExactPosition = new OpenLayers.Pixel(
				this.map.size.w - this.resizeButtonSize.w - this.resizeButtonPadding,
				this.map.size.h - this.resizeButtonSize.h - this.resizeButtonPadding
		);
		
		var layerDiv = OpenLayers.Util.createDiv('Resizer_Contoller',
				resizeControlExactPosition,
				this.resizeButtonSize, '', 'absolute','3px solid blue', '', '1'
		);	
		
		layerDiv.innerHTML = "RR";
		$(layerDiv).addEvent('click', function(e) { this.doMapResize(); return false;}.bind(this));
		this.div = layerDiv;

		this.map.div.parentNode.appendChild(this.div);
		
		this.map.div.onorientationchange = this.redraw.bind(this); 
	},
	
	doMapResize: function() {
		if(this.mapHeightState == 'min') {
			$(this.map.div).parentNode.style.height = '400px';
			this.mapHeightState = 'max';
		}
		else {
			$(this.map.div).parentNode.style.height = '300px';
			this.mapHeightState = 'min';
		}
		this.redraw();
	},
	
	redraw: function() {
		$(this.div).dispose();
		this.map.updateSize();
		this.draw();
	},
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});
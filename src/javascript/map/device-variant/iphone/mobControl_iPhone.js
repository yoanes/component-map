EMS.Control.MobileDefaults = new Class({
	Extends: OpenLayers.Control,
	
	Device: "Apple",
	CLASS_NAME: "EMS.Control.MobileDefaults",
	
	active: false,
	
	X: null,
	Y: null,
	
	initialize: function(MapInstance) {
		this.active = true;
		if(this.isOS3()) { 
			/** 
			 * add dX and dY attributes for different algo under OS 2.*
			 */
			this.dX = 0;
			this.dY = 0;
		}
	},
	
	isOS3: function() {
		var ua = new String(navigator.userAgent); 			
		var pattern = new RegExp('iPhone OS 3', '');
		var isOS3 = ua.search(pattern);
		
		if(isOS3 == -1) return false;
		else return true;
	},
	
	execTouchStart: function(e) {
		var node = e.targetTouches[0];

		this.X = node.pageX;
		this.Y = node.pageY;
		
		if(!this.isOS3()) {
			this.dX = 0;
			this.dY = 0;
		}
	},
	
	execTouchMove: function(e) {
		e.preventDefault();
		
		var node = e.targetTouches[0];

		var movedX = node.pageX;
		var movedY = node.pageY;

		var diffX = this.X - movedX;
		var diffY = this.Y - movedY;

		this.X = movedX;
		this.Y = movedY;

		if(this.isOS3() && e.touches.length == 1)
			this.map.pan(diffX, diffY);

		else {
			/* 
			 * temporarily fake the panning for OS 2.*
			 */
			 $('map-div_OpenLayers_Container').setStyle('left', $('map-div_OpenLayers_Container').getStyle('left').toInt() - diffX + "px");
			 $('map-div_OpenLayers_Container').setStyle('top',  $('map-div_OpenLayers_Container').getStyle('top').toInt() - diffY + "px");

			 this.dX += diffX;
			 this.dY += diffY;
		}
	},
	
	execTouchEnd: function(e) {
		/* 
		 * do the actual panning on OS 2.*
		 */
		if(!this.isOS3())
			if(e.changedTouches.length == 1)
				this.map.pan(this.dX, this.dY);
	},             
	
	execGestureChange: function(e){
		e.preventDefault();

		if(!this.isOS3())
			$('map-div_OpenLayers_ViewPort').style['-webkit-transform-style'] = 'preserve-3d';
		
		$('map-div_OpenLayers_Container').style['-webkit-transform'] = 'scale(' + e.scale + ',' + e.scale + ')';	
		$('map-div_OpenLayers_Container').style['-webkit-transform-origin-x'] = e.pageX + 'px';	
		$('map-div_OpenLayers_Container').style['-webkit-transform-origin-y'] = e.pageY + 'px'; 
	},
	
	execGestureEnd: function(e) {
		e.preventDefault();
		var scale = e.scale;
		
		if(scale > 1){
			this.map.zoomIn();	
		}else if(scale < 1){
			this.map.zoomOut();
		}
	},
	
	/**
	 * APIMethod: draw
	 */
	
	draw: function() {
		this.map.div.ongesturechange = this.execGestureChange.bind(this);
		this.map.div.ongestureend = this.execGestureEnd.bind(this); 
		this.map.div.ontouchstart = this.execTouchStart.bind(this);
		this.map.div.ontouchmove = this.execTouchMove.bind(this);
		this.map.div.ontouchend = this.execTouchEnd.bind(this);
		
		/** register the zoomend event as well. Does the reverting back to original scale */
		this.map.events.register('zoomend', this, function() {
			 $('map-div_OpenLayers_Container').style['-webkit-transform'] = 'scale(1,1)'; 
		 });
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
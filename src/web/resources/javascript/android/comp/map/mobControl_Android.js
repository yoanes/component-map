EMS.Control.MobileDefaults = OpenLayers.Class(OpenLayers.Control, { 
		
	Device: "Android-OS",
	CLASS_NAME: "EMS.Control.MobileDefaults",
	
	active: false,
	
	X: null,
	Y: null,
	
	dX: null,
	dY: null,
	
	initialize: function() {
		this.active = true;
	},
	
	execTouchStart: function(e) {
		e.preventDefault();
		var node = e.touches[0];
		
		this.X = node.pageX;
		this.Y = node.pageY;
		
		this.dX = 0;
		this.dY = 0;
		
		return false;
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

		$(this.map.viewPortDiv).setStyle('margin-left', $(this.map.viewPortDiv).getStyle('margin-left').toInt() - diffX + "px");
		$(this.map.viewPortDiv).setStyle('margin-top',  $(this.map.viewPortDiv).getStyle('margin-top').toInt() - diffY + "px");
		
		this.dX += diffX;
		this.dY += diffY;
		
		return false;
	},            
	
	execTouchEnd: function(e) {
		e.preventDefault();
		this.map.pan(this.dX, this.dY, {animate: false});
		
		$(this.map.viewPortDiv).setStyle('margin-left', "0px");
		$(this.map.viewPortDiv).setStyle('margin-top',  "0px");
		
		return false;
	},
	
	/**
	 * APIMethod: draw
	 */
	
	draw: function() { 
		this.map.div.ontouchstart = this.execTouchStart.bind(this);
		this.map.div.ontouchmove = this.execTouchMove.bind(this);
		this.map.div.ontouchend = this.execTouchEnd.bind(this); 
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

_MapControls_.push(new EMS.Control.MobileDefaults);
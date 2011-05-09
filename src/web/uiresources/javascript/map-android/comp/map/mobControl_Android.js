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
	
	/* return the x and y properties from the transflate3d css style */
	getTranslate3dProperties: function() {
		var transform = this.map.viewPortDiv.style.webkitTransform;
		var t3dX = parseInt(transform.replace(/translate3d./,'').replace(/,.*/,''));
		var t3dY = parseInt(transform.replace(/translate3d.*?,/,'').replace(/,0.*/,''));
		
		if (t3dX == null || isNaN(t3dX)) t3dX = 0;
        if (t3dY == null || isNaN(t3dY)) t3dY = 0;
        
        return {x: t3dX, y: t3dY};
	},
	
	execTouchStart: function(e) {
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

		/* use translate 3d to simulate the pan */
		
		/* simulate the panning */
	    var t3dp = this.getTranslate3dProperties();
        this.map.viewPortDiv.style['-webkit-transform'] = 'translate3d(' + (t3dp.x-diffX) + 'px, ' + (t3dp.y-diffY) + 'px, 0)';
        
		this.dX += diffX;
		this.dY += diffY;
		
		return false;
	},            
	
	execTouchEnd: function(e) {
		this.map.pan(this.dX, this.dY, {animate: false});
		
		/* reset */
		this.map.viewPortDiv.style['-webkit-transform'] = '';
		this.dX = this.dY = 0;
		
		return false;
	},
	
	/**
	 * APIMethod: draw
	 */
	
	draw: function() { 
		/* attach the event listener properly */
		this.map.viewPortDiv.addEventListener('touchstart', function(e) { this.execTouchStart(e); }.bind(this), false);
		this.map.viewPortDiv.addEventListener('touchmove', function(e) { this.execTouchMove(e); }.bind(this), false);
		this.map.viewPortDiv.addEventListener('touchend', function(e) { this.execTouchEnd(e); }.bind(this), false);

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

_MapControlsCompulsory_.push(new EMS.Control.MobileDefaults);
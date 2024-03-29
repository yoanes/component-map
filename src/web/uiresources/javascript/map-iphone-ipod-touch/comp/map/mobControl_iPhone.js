EMS.Control.MobileDefaults = OpenLayers.Class(EMS.Control.MobileDefaultsPrototype, { 
	/* override the device name */
	Device: "Apple-iPhone-iPod",
	
	/* no need to take up the scale because when this clause is executed there will be no scaling */
	simulatePan: function(dX, dY) {
		/* simulate the panning */
	    var t3dp = this.getTranslate3dProperties();
	    var tScale = this.getScale3dCSS();
		
	/*	this.map.viewPortDiv.style['-webkit-transition'] = '-webkit-transform 10ms ease-out 0'; */
        this.map.viewPortDiv.style['-webkit-transform'] = 'translate3d(' + (t3dp.x-dX) + 'px, ' + (t3dp.y-dY) + 'px, 0) ' + tScale; 
	    
		/* accumulate the dx and dy*/
		this.dX += dX;
		this.dY += dY;
	},
	
	/* on pan execution, this method will assume the value of properties this.dX and this.dY as 
	 * the actual distance in px the map has to move. If you have other methods that will invoke a 
	 * pan, you should use these 2 variables and accumulate them as necessary. 
	 */
	executePan: function() {
		this.map.pan(this.dX, this.dY, {animate:false});
	},
	
	resetPan: function() {
		/* don't forget to reset :) */
		this.dX = this.dY = 0;
			
		/* revert back after panning during zoom  */
		this.map.viewPortDiv.style['-webkit-transform'] = '';
	},
	
	setupAnimation: function() {  
		/* 2 lines below is to avoid the flickering in the screen */
		this.map.viewPortDiv.style['-webkit-perspective'] = 1000;
		this.map.viewPortDiv.style['-webkit-backface-visibility'] = 'hidden';

	}
});

_MapControlsCompulsory_.push(new EMS.Control.MobileDefaults);
EMS.Control.MobileDefaults = OpenLayers.Class(EMS.Control.MobileDefaultsPrototype, { 
	/* override the device name */
	Device: "Apple-iPad",
	
	simulatePan: function(dX, dY) {
		/* simulate the panning */
		$(this.map.viewPortDiv).setStyle('margin-left', $(this.map.viewPortDiv).getStyle('margin-left').toInt() - dX + "px");
		$(this.map.viewPortDiv).setStyle('margin-top',  $(this.map.viewPortDiv).getStyle('margin-top').toInt() - dY + "px");
		
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
		$(this.map.viewPortDiv).setStyle('margin-left', "0px");
		$(this.map.viewPortDiv).setStyle('margin-top', "0px");
	},
	
	setupAnimation: function() {  
		this.map.layerContainerDiv.style['-webkit-transition'] = '-webkit-transform 10ms ease-out 0';
	}
});

_MapControls_.push(new EMS.Control.MobileDefaults);
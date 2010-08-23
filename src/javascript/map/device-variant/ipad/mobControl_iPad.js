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
	}
	
	/* comment out this section. Seems to introduce a bug on ipad that causes the text in any input field text
	 * to dissapear intermittently. 
	 * 
	setupAnimation: function() {  
		this.map.viewPortDiv.style['-webkit-transition-property'] = '-webkit-transform, margin-left, margin-top';
		this.map.viewPortDiv.style['-webkit-transition-duration'] = '10ms, 1ms, 1ms';
		this.map.viewPortDiv.style['-webkit-transition-timing-function'] = 'ease-in-out, linear, linear';
		this.map.viewPortDiv.style['-webkit-transition-delay'] = '0 0 0';  
	}  */
});

_MapControls_.push(new EMS.Control.MobileDefaults);
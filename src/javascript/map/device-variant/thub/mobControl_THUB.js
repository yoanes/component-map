EMS.Control.MobileDefaults = new Class({
	Extends: OpenLayers.Control,
	
	Device: "PC-Linux",
	CLASS_NAME: "EMS.Control.MobileDefaults",
	
	active: false,
		
	enabled: false,
	
	initialize: function() {
		this.active = true;
	},
	
	/**
	 * APIMethod: draw
	 */
	
	draw: function() { 
		$('zoomIn').addEvent('click', function(e){
			this.map.zoomIn();
			return false;
		}.bind(this));
	
		$('zoomOut').addEvent('click', function(e){
			this.map.zoomOut();
			return false;
		}.bind(this));
		
		$('panNorth').addEvent('mousedown', function(e){
			this.enablePan();
			this.map.pan(0,-50);
			return false;
		}.bind(this));
	
		$('panNorth').addEvent('mouseup', function(e){
			this.disablePan();
			return false;
		}.bind(this));
			
		$('panSouth').addEvent('mousedown', function(e){
			this.enablePan();
			this.map.pan(0,50);
			return false;
		}.bind(this));
		
		$('panEast').addEvent('mousedown', function(e){
			this.enablePan();
			this.map.pan(100,0);
			return false;
		}.bind(this));
		
		$('panWest').addEvent('mousedown', function(e){
			this.enablePan();
			this.map.pan(-100,0);
			return false;
		}.bind(this));
		
		$('viewChange').addEvent('click', function(e){
			MEMS.switchView();
			return false;
		}.bind(this));
	},
	
	enablePan: function() {
		this.enabled = true;
	},
	
	disablePan: function() {
		this.enabled = false;
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
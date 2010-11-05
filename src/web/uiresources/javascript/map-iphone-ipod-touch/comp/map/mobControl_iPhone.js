EMS.Control.MobileDefaults = OpenLayers.Class(EMS.Control.MobileDefaultsPrototype, { 

	/* override just the simulate. In all iPhone prior to OS 4, we want to do the panning
	 * as user move their finger. So there's no need for any simulation, hence simulation will
	 * behave as the actual executePan.
	 */
	simulatePan: function(dX, dY) {
		this.map.pan(dX, dY, {animate:false});
		this.dX = this.dY = 0;
	}
	
});

_MapControls_.push(new EMS.Control.MobileDefaults);
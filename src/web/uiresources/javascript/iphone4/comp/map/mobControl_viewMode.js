EMS.Control.ViewMode = OpenLayers.Class(EMS.Control.ViewModePrototype, {
	
	calcDimension: function() {
		/* set the used images sizes to half of it */
		this.photoImage.height = this.photoImage.height / 2;
		this.photoImage.width = this.photoImage.width / 2;
		
		this.mapImage.height = this.mapImage.height / 2;
		this.mapImage.width = this.mapImage.width / 2;
	
		return new OpenLayers.Size(this.photoImage.width, this.photoImage.height);
	}
	
});

_MapControls_.push(new EMS.Control.ViewMode);
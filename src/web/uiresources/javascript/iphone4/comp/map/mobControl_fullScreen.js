EMS.Control.FullScreen = OpenLayers.Class(EMS.Control.FullScreenPrototype, {
	
	calcDimension: function() {
		/* set the used images sizes to half of it */
		this.growImage.height = this.growImage.height / 2;
		this.growImage.width = this.growImage.width / 2;
		
		this.shrinkImage.height = this.shrinkImage.height / 2;
		this.shrinkImage.width = this.shrinkImage.width / 2;
		
		return new OpenLayers.Size(this.growImage.width, this.growImage.height);
	}
	
});

_MapControls_.push(new EMS.Control.FullScreen);
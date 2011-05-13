EMS.Control.LocateMe = OpenLayers.Class(EMS.Control.LocateMePrototype, {
	
	calcDimension: function() {
		/* set the used images sizes to half of it */
		this.locateImage.height = this.locateImage.height / 2;
		this.locateImage.width = this.locateImage.width / 2;
		
		this.loadingImage.height = this.loadingImage.height / 2;
		this.loadingImage.width = this.loadingImage.width / 2;
		
		return new OpenLayers.Size(this.locateImage.width, this.locateImage.height);
	}
	
});
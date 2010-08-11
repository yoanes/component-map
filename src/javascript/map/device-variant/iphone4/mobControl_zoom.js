EMS.Control.Zoom = OpenLayers.Class(EMS.Control.ZoomPrototype, {
	
	calcDimension: function() {
		/* set the used images sizes to half of it */
		this.zoomInImage.height = this.zoomInImage.height / 2;
		this.zoomInImage.width = this.zoomInImage.width / 2;
		
		this.zoomOutImage.height = this.zoomOutImage.height / 2;
		this.zoomOutImage.width = this.zoomOutImage.width / 2;
		
		return new OpenLayers.Size(this.zoomInImage.width + this.zoomOutImage.width, this.zoomInImage.height);
	}
});

_MapControls_.push(new EMS.Control.Zoom);
EMS.Control.Ornaments = OpenLayers.Class(EMS.Control.OrnamentsPrototype, {
	
	calcDimension: function() {
		/* set the used images sizes to half of it */
		this.northImage.width /= 2;
		this.logoImage.width /= 2;
		
		var north = new OpenLayers.Size(this.northImage.width, this.northImage.height);
		var logo = new OpenLayers.Size(this.logoImage.width, this.logoImage.height);
		
		return {'north': north, 'logo': logo};
	}
	
});

 _MapControlsCompulsory_.push(new EMS.Control.Ornaments);
EMS.Control.OrnamentsPrototype = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Apple",
	CLASS_NAME: "EMS.Control.Ornaments",
	
	ornamentButtonPadding: 5,

	active: false,

	northSize: null,
	northImage: null,
	northDiv: null,
	
	logoSize: null,
	logoImage: null,
	logoDiv: null,
	
	positions: {},
	
	initialize: function() {
		this.active = true;
		
		this.northImage = new Image();
		this.northImage.src = _MapOrnamentsPath_ + 'north.image';
		this.northImage.id = 'northOrnament';
		
		this.logoImage = new Image();
		this.logoImage.src = _MapOrnamentsPath_ + 'copyright.image';
		this.logoImage.id = 'copyrightOrnament';
	},
	
	calcPosition: function() {
		this.positions.north = new OpenLayers.Pixel(this.ornamentButtonPadding, this.ornamentButtonPadding);
		this.positions.logo = new OpenLayers.Pixel(this.map.size.w - this.logoSize.w - this.ornamentButtonPadding, 0);
	},
	
	calcDimension: function() {
		var north = new OpenLayers.Size(this.northImage.width, this.northImage.height);
		var logo = new OpenLayers.Size(this.logoImage.width, this.logoImage.height);
		
		return {'north': north, 'logo': logo};
	},
	
	draw: function() {
		/* initialize the button size on draw */
		var dim = this.calcDimension();
		this.northSize = dim.north;
		this.logoSize = dim.logo;
		
		this.calcPosition();	
		
		this.northDiv = OpenLayers.Util.createDiv('NorthLogo',
				this.positions.north, this.northSize, '', 'absolute','0px none', '', '1');
		
		this.logoDiv = OpenLayers.Util.createDiv('CopyrightLogo',
				this.positions.logo, this.logoSize, '', 'absolute','0px none', '', '1');
		
		/* set this right otherwise the image can't be seen */
		this.northDiv.style.zIndex = '750';
		this.logoDiv.style.zIndex = '750';
		
		/* append the images */
		this.northDiv.appendChild(this.northImage);
		this.logoDiv.appendChild(this.logoImage);
		
		/* do resize when the device is tilted */
		window.addEventListener('orientationchange', function(e) {this.rePosition();}.bind(this), false);
		
		/* do resize when the map is resized */
		this.map.div.addEventListener('resize', function(e) {this.rePosition();}.bind(this), false);
		
		this.map.div.appendChild(this.northDiv);
		this.map.div.appendChild(this.logoDiv);
	},
	
	rePosition: function() {
		this.calcPosition();
		
		this.northDiv.style.top = this.positions.north.y + 'px';
		this.northDiv.style.left = this.positions.north.x + 'px';
		
		this.logoDiv.style.top = this.positions.logo.y + 'px';
		this.logoDiv.style.left = this.positions.logo.x + 'px';
	},
	
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});
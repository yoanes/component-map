EMS.Control.LocateMePrototype = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Apple",
	CLASS_NAME: "EMS.Control.LocateMe",
	
	locateButtonSize: null,
	locateButtonPaddingLeft: 44,
	locateButtonPaddingBottom: 5,

	active: false,
	
	div: null,
	locateImage: null,
	
	mapNth: null,
	
	initialize: function() {
		this.active = true;
		
		this.locateImage = new Image();
		this.locateImage.src = _MapControlsPath_ + 'locate.image';
		this.locateImage.style.display = 'block';
		this.locateImage.id = 'mapLocateMe';
	},
	
	calcPosition: function() {
		return new OpenLayers.Pixel(
			this.locateButtonPaddingLeft,
			this.map.size.h - this.locateButtonSize.h - this.locateButtonPaddingBottom
		);
	},
	
	calcDimension: function() {
		return new OpenLayers.Size(this.locateImage.width, this.locateImage.height);
	},
	
	draw: function() {
		/* initialize the button size on draw */
		this.locateButtonSize = this.calcDimension();
		
		var locateControlExactPosition = this.calcPosition();
		
		this.div = OpenLayers.Util.createDiv('LocateMe_Controller',
				locateControlExactPosition, this.locateButtonSize, 
				'', 'absolute','0px none', '', '1');	
		
		/* set this right otherwise the image can't be seen */
		this.div.style.zIndex = '750';
		
		/* append the images */
		this.div.appendChild(this.locateImage);

		/* do view change on click of the button */
		this.div.addEventListener('touchend', function(e) {
			e.stopPropagation(); 
			this.followUser();
		}.bind(this), false);
		
		/* do resize when the device is tilted */
		window.addEventListener('orientationchange', function(e) {this.rePosition();}.bind(this), false);
		
		/* do resize when the map is resized */
		this.map.div.addEventListener('resize', function(e) {this.rePosition();}.bind(this), false);
		
		this.map.div.appendChild(this.div);
	},
	
	followUser: function() {
		/* default to the zeroth instance of the map */
		if(this.mapNth == null) this.mapNth = 0;
		MAP.instances[this.mapNth].DeviceLocation.autoLocate();
	},
	
	setMapNth: function(nth) {
		this.mapNth = nth;
	},
	
	rePosition: function() {	
		var newPosition = this.calcPosition();
		
		this.div.style.top = newPosition.y + 'px';
		this.div.style.left = newPosition.x + 'px';
	},
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});
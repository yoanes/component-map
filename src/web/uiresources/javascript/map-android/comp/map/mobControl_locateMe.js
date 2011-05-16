EMS.Control.LocateMe = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Android-OS",
	CLASS_NAME: "EMS.Control.LocateMe",
	
	locateButtonSize: null,
	locateButtonPaddingLeft: 44,
	locateButtonPaddingBottom: 5,

	active: false,
	
	div: null,
	locateImage: null,
	loadingImage: null,
	
	/* loading location flag, not necessarily indicate that the gps is following user or not */
	isLoading: false,
	
	firstLocationFound: false,

	mapNth: null,
	
	initialize: function() {
		this.active = true;
		
		this.locateImage = new Image();
		this.locateImage.src = _MapControlsPath_ + 'locate.image';
		this.locateImage.style.display = 'block';
		this.locateImage.id = 'mapLocateMe';
		
		this.loadingImage = new Image();
		this.loadingImage.src = _MapControlsPath_ + 'loading.image';
		this.loadingImage.style.display = 'none';
		this.loadingImage.id = 'mapLoadingLocation';
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
		this.div.appendChild(this.loadingImage);

		/* do view change on click of the button */
		this.div.addEventListener('touchend', function(e) {
			e.stopPropagation(); 
			if(this.isLoading) { 
				MAP.instances[this.mapNth].DeviceLocation.softReset();
				this.firstLocationFound = false;
	        }
			else { 
				this.followUser(); 
				/* don't toggle if the first location has been found */
				if(this.firstLocationFound) return;
			}
			this.toggle();
		}.bind(this), false);
		
		this.map.div.addEventListener('locationFound', function(e) {
			if(!this.firstLocationFound) {
				this.toggle();
				this.firstLocationFound = true;
			}
		}.bind(this), false);
		
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
	
	toggle: function() {
		if(this.isLoading) {
			this.locateImage.style.display = 'block';
			this.loadingImage.style.display = 'none';
			this.isLoading = false;
		}
		
		else {
			this.locateImage.style.display = 'none';
			this.loadingImage.style.display = 'block';
			this.isLoading = true;
		}
	},
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});
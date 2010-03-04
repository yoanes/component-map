EMS.Control.ViewMode = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Advance",
	CLASS_NAME: "EMS.Control.FullScreen",
	
	viewButtonSize: null,
	viewButtonPadding: 5,

	active: false,
	
	inPhotoMode: false,
	
	div: null,
	photoImage: null,
	mapImage: null,
	
	initialize: function(map) {
		this.active = true;
		
		this.photoImage = new Image();
		this.photoImage.src = 'http://dsb.sensis.com.au/imageserver/wm/images/mapProto/photo.png';
		this.photoImage.style.display = 'block';
		this.photoImage.id = 'grow';
		
		this.mapImage = new Image();
		this.mapImage.src = 'http://dsb.sensis.com.au/imageserver/wm/images/mapProto/map.png';
		this.mapImage.style.display = 'none';
		this.mapImage.id = 'shrink';
	},
	
	draw: function() {
		/* initialize the button size on draw */
		if(this.viewButtonSize == null)
			this.viewButtonSize = new OpenLayers.Size(this.photoImage.width, this.photoImage.height);
		
		var mapSize = this.map.getSize();
		var viewControlExactPosition = new OpenLayers.Pixel(
				0 + this.viewButtonPadding,
				this.map.size.h - this.viewButtonSize.h - this.viewButtonPadding
		);
		
		this.div = OpenLayers.Util.createDiv('ViewMode_Contoller',
				viewControlExactPosition, this.viewButtonSize, 
				'', 'absolute','0px none', '', '1');	
		
		/* set this right otherwise the image can't be seen */
		this.div.setStyle('z-index', '750');
		
		/* append the images */
		this.div.appendChild(this.photoImage);
		this.div.appendChild(this.mapImage);

		/* do view change on click of the button */
		this.div.addEventListener('touchend', function(e) {this.doViewChange();}.bind(this), false);
		
		/* do resize when the device is tilted */
		this.div.addEventListener('orientationchange', function(e) {this.redraw();}.bind(this), false);
		
		/* do resize when the map is resized */
		this.div.addEventListener('resize', function(e) {this.redraw();}.bind(this), false);
		
		this.map.div.appendChild(this.div);
	},
	
	doViewChange: function() {
		if(this.inPhotoMode) {
			this.map.setBaseLayer(this.map.whereis_street_wms);
			this.toggle();
			this.inPhotoMode = false;
		}
		else {
			this.map.setBaseLayer(this.map.whereis_photo_wms);
			this.toggle();
			this.inPhotoMode = true;
		}
	},
	
	redraw: function() {
		$(this.div).dispose();
		this.map.updateSize();
		this.draw();
	},
	
	toggle: function() {
		if(this.inPhotoMode) {
			this.photoImage.style.display = 'block';
			this.mapImage.style.display = 'none';
		}
		
		else {
			this.photoImage.style.display = 'none';
			this.mapImage.style.display = 'block';
		}
	},
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});

_MapControls_.push(new EMS.Control.ViewMode);
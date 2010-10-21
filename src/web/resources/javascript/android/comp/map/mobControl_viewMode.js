EMS.Control.ViewMode = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Android-OS",
	CLASS_NAME: "EMS.Control.ViewMode",
	
	viewButtonSize: null,
	viewButtonPadding: 5,

	active: false,
	
	inPhotoMode: false,
	
	div: null,
	photoImage: null,
	mapImage: null,
	
	updateServerURL: null,
	
	initialize: function() {
		this.active = true;
		
		this.photoImage = new Image();
		this.photoImage.src = _MapControlsPath_ + 'photo.png';
		this.photoImage.style.display = 'block';
		this.photoImage.id = 'grow';
		
		this.mapImage = new Image();
		this.mapImage.src = _MapControlsPath_ + 'map.png';
		this.mapImage.style.display = 'none';
		this.mapImage.id = 'shrink';
	},
	
	calcPosition: function() {
		return new OpenLayers.Pixel(
			this.viewButtonPadding,
			this.map.size.h - this.viewButtonSize.h - this.viewButtonPadding
		);
	},
	
	draw: function() {
		/* initialize the button size on draw */
		this.viewButtonSize = new OpenLayers.Size(this.photoImage.width, this.photoImage.height);
		if($('stateChangeUrl')) {
			this.updateServerURL = $('stateChangeUrl').href;
		}
		
		var viewControlExactPosition = this.calcPosition();
		
		this.div = OpenLayers.Util.createDiv('ViewMode_Controller',
				viewControlExactPosition, this.viewButtonSize, 
				'', 'absolute','0px none', '', '1');	
		
		/* set this right otherwise the image can't be seen */
		this.div.style.zIndex = '750';
		
		/* append the images */
		this.div.appendChild(this.photoImage);
		this.div.appendChild(this.mapImage);

		/* do view change on click of the button */
		this.div.addEventListener('touchend', function(e) {this.doViewChange();}.bind(this), false);
		
		/* do resize when the map is resized */
		window.addEventListener('resize', function(e) {this.rePosition();}.bind(this), false);
		
		this.map.div.appendChild(this.div);
	},
	
	doViewChange: function() {
		if(this.inPhotoMode) { 
			/* switch to map view */
			this.switchTo("map");		
			Reporting.to(this.updateServerURL, {lyr: 'm'});
		}
		else {
			/* switch to photo view */
			this.switchTo("photo");
			Reporting.to(this.updateServerURL, {lyr: 'p'});
		}
	},
	
	switchTo: function(v) {
		if(v == "photo") {
			/* switch to photo view */
			this.map.whereis_photo_wms.setVisibility(1);
			this.map.whereis_street_wms.setVisibility(0);
			this.inPhotoMode = true;
			this.toggle();
		}
		else if(v == "map") {
			/* switch to map view */
			this.map.whereis_street_wms.setVisibility(1);
			this.map.whereis_photo_wms.setVisibility(0);
			this.inPhotoMode = false;
			this.toggle();
		}
	},
	
	rePosition: function() {	
		var newPosition = this.calcPosition();
		
		this.div.style.top = newPosition.y + 'px';
		this.div.style.left = newPosition.x + 'px';
	},
	
	toggle: function() {
		if(this.inPhotoMode) {
			this.photoImage.style.display = 'none';
			this.mapImage.style.display = 'block';
		}
		
		else {
			this.photoImage.style.display = 'block';
			this.mapImage.style.display = 'none';
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
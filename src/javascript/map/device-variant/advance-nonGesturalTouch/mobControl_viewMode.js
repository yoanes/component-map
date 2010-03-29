EMS.Control.ViewMode = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Advance-NonGesturalTouch",
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
		this.updateServerURL = $('stateChangeUrl').href;
		
		var viewControlExactPosition = this.calcPosition();
		
		this.div = OpenLayers.Util.createDiv('ViewMode_Contoller',
				viewControlExactPosition, this.viewButtonSize, 
				'', 'absolute','0px none', '', '1');	
		
		/* set this right otherwise the image can't be seen */
		this.div.style.zIndex = '750';
		
		/* append the images */
		this.div.appendChild(this.photoImage);
		this.div.appendChild(this.mapImage);

		/* do view change on click of the button */
		this.div.addEventListener('click', function(e) {this.doViewChange();}.bind(this), false);
		
		/* do resize when the map is resized */
		this.div.addEventListener('resize', function(e) {this.rePosition();}.bind(this), false);
		
		this.map.div.appendChild(this.div);
	},
	
	doViewChange: function() {
		if(this.inPhotoMode) { 
			/* switch to map view */
			this.map.whereis_street_wms.setVisibility(1);
			this.map.whereis_photo_wms.setVisibility(0);
			this.toggle();
			this.inPhotoMode = false;
			
			Reporting.to(this.updateServerURL, {lyr: 'm'});
		}
		else {
			/* switch to photo view */
			this.map.whereis_photo_wms.setVisibility(1);
			this.map.whereis_street_wms.setVisibility(0);
			this.toggle();
			this.inPhotoMode = true;
			
			Reporting.to(this.updateServerURL, {lyr: 'p'});
		}
	},
	
	rePosition: function() {
		var newPosition = this.calcPosition();
		
		this.div.style.top = newPosition.y + 'px';
		this.div.style.left = newPosition.x + 'px';
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
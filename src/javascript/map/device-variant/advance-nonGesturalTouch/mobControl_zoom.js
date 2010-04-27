EMS.Control.Zoom = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Advance-NonGesturalTouch",
	CLASS_NAME: "EMS.Control.Zoom",
	
	zoomButtonSize: null,
	zoomButtonPadding: 5,

	active: false,
	
	div: null,
	zoomInImage: null,
	zoomOutImage: null,
	
	ziDiv: null,
	zoDiv: null,
	
	controlEnabled: true,
	
	initialize: function() {
		this.active = true;
		
		this.zoomInImage = new Image();
		this.zoomInImage.src = _MapControlsPath_ + 'in.png';
		this.zoomInImage.id = 'in';
		
		this.zoomOutImage = new Image();
		this.zoomOutImage.src = _MapControlsPath_ + 'out.png';
		this.zoomOutImage.id = 'out';
	},
	
	calcPosition: function() {
		return new OpenLayers.Pixel(
			this.map.size.w - this.zoomButtonSize.w - this.zoomButtonPadding,
			this.map.size.h - this.zoomButtonSize.h - this.zoomButtonPadding
		);
	},
	
	draw: function() {
		/* initialize the button size on draw */
		this.zoomButtonSize = new OpenLayers.Size(this.zoomInImage.width + this.zoomOutImage.width, this.zoomInImage.height);
		
		var zoomControlExactPosition = this.calcPosition();
		
		/* width of the whole div is twice the zoomButtonSize. */
		this.div = OpenLayers.Util.createDiv('Zoom_Controller',
				zoomControlExactPosition, this.zoomButtonSize, 
				'', 'absolute','0px none', '', '1');	
		
		/* set this right otherwise the image can't be seen */
		this.div.style.zIndex = '750';
		
		this.map.events.register("zoomend", this, function() {
			this.ziDiv.style.opacity = this.zoDiv.style.opacity = '1';
			this.controlEnabled = true;
		});
		
		/* create a div for zoom In */
		this.ziDiv = new Element('div');
		this.ziDiv.appendChild(this.zoomInImage);
		this.ziDiv.style.display = 'inline';
		this.ziDiv.addEventListener('click', function(e) {
			if(this.map.isValidZoomLevel(this.map.getZoom() + 1)) {
				if(this.controlEnabled) {
					this.controlEnabled = false;
					this.ziDiv.style.opacity = '0.3';
					this.map.zoomIn();
				}
			} 
			else this.ziDiv.style.opacity = '0.3';
		}.bind(this), false);

		/* and another one for zoom Out */
		this.zoDiv = new Element('div');
		this.zoDiv.appendChild(this.zoomOutImage);
		this.zoDiv.style.display = 'inline';
		this.zoDiv.addEventListener('click', function(e) {
			if(this.map.isValidZoomLevel(this.map.getZoom() - 1)) {
				if(this.controlEnabled) {
					this.controlEnabled = false;
					this.zoDiv.style.opacity = '0.3';
					this.map.zoomOut();
				}
			}
			else this.zoDiv.style.opacity = '0.3';
		}.bind(this), false);
		
		/* append these zoom divs */
		this.div.appendChild(this.ziDiv);
		this.div.appendChild(this.zoDiv);
		
		this.map.div.appendChild(this.div);
	},
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});

_MapControls_.push(new EMS.Control.Zoom);
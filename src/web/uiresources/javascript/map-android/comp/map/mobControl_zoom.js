EMS.Control.Zoom = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Android-OS",
	CLASS_NAME: "EMS.Control.Zoom",
	
	zoomButtonSize: null,
	zoomButtonPadding: 5,

	active: false,
	
	div: null,
	zoomInImage: null,
	zoomOutImage: null,
	
	initialize: function() {
		this.active = true;
		
		this.zoomInImage = new Image();
		this.zoomInImage.src = _MapControlsPath_ + 'in.image';
		this.zoomInImage.id = 'in';
		
		this.zoomOutImage = new Image();
		this.zoomOutImage.src = _MapControlsPath_ + 'out.image';
		this.zoomOutImage.id = 'out';
	},
	
	calcPosition: function() {
		return new OpenLayers.Pixel(
			this.map.size.w/2 - this.zoomButtonSize.w/2 ,
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
		
		/* create a div for zoom In */
		var ziDiv = new Element('div');
		ziDiv.id = 'onMapZoomIn';
		ziDiv.appendChild(this.zoomInImage);
		ziDiv.style.display = 'inline';
	/*	ziDiv.addEventListener('touchend', function(e) {EMS.Util.smoothZoom(this.map, this.map.getCenter(), this.map.getCenter(), this.map.getZoom() + 1);}.bind(this), false); */
		ziDiv.addEventListener('touchend', function(e) {e.stopPropagation(); this.map.zoomIn();}.bind(this), false);
		
		/* and another one for zoom Out */
		var zoDiv = new Element('div');
		zoDiv.id = 'onMapZoomOut';
		zoDiv.appendChild(this.zoomOutImage);
		zoDiv.style.display = 'inline';
	/*	zoDiv.addEventListener('touchend', function(e) {EMS.Util.smoothZoom(this.map, this.map.getCenter(), this.map.getCenter(), this.map.getZoom() - 1);}.bind(this), false); */
		zoDiv.addEventListener('touchend', function(e) {e.stopPropagation(); this.map.zoomOut();}.bind(this), false);
		
		/* append these zoom divs */
		this.div.appendChild(ziDiv);
		this.div.appendChild(zoDiv);
		
		/* reposition when the map is resized */
		window.addEventListener('resize', function(e) {this.rePosition();}.bind(this), false);
		
		this.map.div.appendChild(this.div);
		
		this.map.events.register('zoomend', this, function() { 		
			if(this.map.getZoom() == 0)
				$('onMapZoomOut').style.opacity = '0.3';
			else if(this.map.getZoom() == 16)
				$('onMapZoomIn').style.opacity = '0.3';
			else 
				$('onMapZoomIn').style.opacity = $('onMapZoomOut').style.opacity = '1';
		});
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

_MapControlsCompulsory_.push(new EMS.Control.Zoom);
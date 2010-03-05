EMS.Control.Zoom = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Advance-NonGesturalTouch",
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
		this.zoomInImage.src = _MapImgPath_ + 'in.png';
		this.zoomInImage.id = 'in';
		
		this.zoomOutImage = new Image();
		this.zoomOutImage.src = _MapImgPath_ + 'out.png';
		this.zoomOutImage.id = 'out';
	},
	
	draw: function() {
		/* initialize the button size on draw */
		if(this.zoomButtonSize == null)
			this.zoomButtonSize = new OpenLayers.Size(this.zoomInImage.width, this.zoomInImage.height);
		
		var mapSize = this.map.getSize();
		var zoomControlExactPosition = new OpenLayers.Pixel(
				this.map.size.w - (this.zoomButtonSize.w * 2) - this.zoomButtonPadding,
				this.map.size.h - this.zoomButtonSize.h - this.zoomButtonPadding
		);
		
		/* width of the whole div is twice the zoomButtonSize. */
		this.div = OpenLayers.Util.createDiv('Zoom_Contoller',
				zoomControlExactPosition, this.zoomButtonSize * 2, 
				'', 'absolute','0px none', '', '1');	
		
		/* set this right otherwise the image can't be seen */
		this.div.setStyle('z-index', '750');
		
		/* create a div for zoom In */
		var ziDiv = new Element('div');
		ziDiv.appendChild(this.zoomInImage);
		ziDiv.setStyle('display', 'inline');
		ziDiv.addEventListener('click', function(e) {EMS.Util.smoothZoom(this.map, this.map.getCenter(), this.map.getCenter(), this.map.getZoom() + 1);}.bind(this), false);

		/* and another one for zoom Out */
		var zoDiv = new Element('div');
		zoDiv.appendChild(this.zoomOutImage);
		zoDiv.setStyle('display', 'inline');
		zoDiv.addEventListener('click', function(e) {EMS.Util.smoothZoom(this.map, this.map.getCenter(), this.map.getCenter(), this.map.getZoom() - 1);}.bind(this), false);
		
		/* append these zoom divs */
		this.div.appendChild(ziDiv);
		this.div.appendChild(zoDiv);
		
		/* reposition when the map is resized */
		this.div.addEventListener('resize', function(e) {this.redraw();}.bind(this), false);
		
		this.map.div.appendChild(this.div);
	},
	
	redraw: function() {
		$(this.div).dispose();
		this.map.updateSize();
		this.draw();
	},
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});

_MapControls_.push(new EMS.Control.Zoom);
EMS.Control.MobileDefaults = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Advance",
	CLASS_NAME: "EMS.Control.FullScreen",
	
	controlButtonSize: null,
	controlButtonPadding: 5,

	active: false,
	
	distanceX: 50,
	distanceY: 50,
	
	nDiv: null,
	sDiv: null,
	wDiv: null,
	eDiv: null,
	
	northImage: null,
	southImage: null,
	westImage: null,
	eastImage: null,
	
	initialize: function() {
		this.active = true;
		
		this.northImage = new Image();
		this.northImage.src = _MapImgPath_ + 'north.png';
		this.northImage.id = 'north';
		
		this.southImage = new Image();
		this.southImage.src = _MapImgPath_ + 'south.png';
		this.southImage.id = 'south';
		
		this.westImage = new Image();
		this.westImage.src = _MapImgPath_ + 'west.png';
		this.westImage.id = 'west';
		
		this.eastImage = new Image();
		this.eastImage.src = _MapImgPath_ + 'east.png';
		this.eastImage.id = 'east';
	},
	
	draw: function() {
		/* initialize the button size on draw */
		if(this.controlButtonSize == null)
			this.controlButtonSize = new OpenLayers.Size(this.northImage.width, this.northImage.height); 
		
		var northExactPosition = new OpenLayers.Pixel(
				(this.map.size.w/2) - (this.controlButtonSize.w/2),
				this.controlButtonPadding
		);
		
		var southExactPosition = new OpenLayers.Pixel(
				(this.map.size.w/2) - (this.controlButtonSize.w/2),
				this.map.size.h - this.controlButtonSize.h - this.controlButtonPadding
		);
		
		var westExactPosition = new OpenLayers.Pixel(
				this.controlButtonPadding,
				(this.map.size.h/2) - (this.controlButtonSize.h/2)
		);
		
		var eastExactPosition = new OpenLayers.Pixel(
				this.map.size.w - this.controlButtonSize.w - this.controlButtonPadding,
				(this.map.size.h/2) - (this.controlButtonSize.h/2)
		);
		
		this.nDiv = OpenLayers.Util.createDiv('PanNorth_Contoller',
				northExactPosition, this.controlButtonSize, 
				'', 'absolute','0px none', '', '1');
		
		this.sDiv = OpenLayers.Util.createDiv('PanSouth_Contoller',
				southExactPosition, this.controlButtonSize, 
				'', 'absolute','0px none', '', '1');
		
		this.wDiv = OpenLayers.Util.createDiv('PanWest_Contoller',
				westExactPosition, this.controlButtonSize, 
				'', 'absolute','0px none', '', '1');
		
		this.eDiv = OpenLayers.Util.createDiv('PanEast_Contoller',
				eastExactPosition, this.controlButtonSize, 
				'', 'absolute','0px none', '', '1');
		
		/* set this right otherwise the image can't be seen */
		this.nDiv.setStyle('z-index', '750');
		this.sDiv.setStyle('z-index', '750');
		this.wDiv.setStyle('z-index', '750');
		this.eDiv.setStyle('z-index', '750');
		
		/* append the images */
		this.nDiv.appendChild(this.northImage);
		this.sDiv.appendChild(this.southImage);
		this.wDiv.appendChild(this.westImage);
		this.eDiv.appendChild(this.eastImage);

		/* do pan on click of these buttons */
		this.nDiv.addEventListener('click', function(e) {this.map.pan(0, this.distanceY);}.bind(this), false);
		this.sDiv.addEventListener('click', function(e) {this.map.pan(0, -(this.distanceY));}.bind(this), false);
		this.wDiv.addEventListener('click', function(e) {this.map.pan(-(this.distanceX), 0);}.bind(this), false);
		this.eDiv.addEventListener('click', function(e) {this.map.pan(this.distanceX, 0);}.bind(this), false);
		
		/* do resize when the device is tilted. Only attached to nDiv under the assumption that
		 * these controllers can't live on the page without each other
		 *  */
		this.nDiv.addEventListener('orientationchange', function(e) {this.redraw();}.bind(this), false);
		
		/* do resize when the map is resized. Only attached to nDiv under the assumption that
		 * these controllers can't live on the page without each other
		 *  */
		this.nDiv.addEventListener('resize', function(e) {this.redraw();}.bind(this), false);
		
		this.map.div.appendChild(this.nDiv);
		this.map.div.appendChild(this.sDiv);
		this.map.div.appendChild(this.wDiv);
		this.map.div.appendChild(this.eDiv);
	},
	
	redraw: function() {
		/* dispose all divs */
		$(this.nDiv).dispose();
		$(this.sDiv).dispose();
		$(this.wDiv).dispose();
		$(this.eDiv).dispose();
		
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

_MapControls_.push(new EMS.Control.MobileDefaults);
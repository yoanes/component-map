EMS.Control.FullScreen = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Advance",
	CLASS_NAME: "EMS.Control.FullScreen",
	
	resizeButtonSize: null,
	resizeButtonPadding: 5,

	active: false,
	
	inFullscreenMode: false,
	
	defaultHeight: null,
	defaultWidth: null,
	
	div: null,
	growImage: null,
	shrinkImage: null,
	
	initialize: function() {
		this.active = true;
		
		this.growImage = new Image();
		this.growImage.src = _MapImgPath_ + 'maxi.png';
		this.growImage.style.display = 'block';
		this.growImage.id = 'grow';
		
		this.shrinkImage = new Image();
		this.shrinkImage.src = _MapImgPath_ + 'mini.png';
		this.shrinkImage.style.display = 'none';
		this.shrinkImage.id = 'shrink';
	},
	
	draw: function() {
		/* initialize the height width and button size on draw */
		if(this.defaultHeight == null)
			this.defaultHeight =  $(this.map.div.parentNode).getStyle('height').toInt();
		if(this.defaultWidth == null)
			this.defaultWidth =  $(this.map.div.parentNode).getStyle('width').toInt();
		if(this.resizeButtonSize == null)
			this.resizeButtonSize = new OpenLayers.Size(this.growImage.width, this.growImage.height);
		
		var mapSize = this.map.getSize();
		var resizeControlExactPosition = new OpenLayers.Pixel(
				this.map.size.w - this.resizeButtonSize.w - this.resizeButtonPadding,
				this.map.size.h - this.resizeButtonSize.h - this.resizeButtonPadding
		);
		
		this.div = OpenLayers.Util.createDiv('FullScreen_Contoller',
				resizeControlExactPosition, this.resizeButtonSize, 
				'', 'absolute','0px none', '', '1');	
		
		/* set this right otherwise the image can't be seen */
		this.div.setStyle('z-index', '750');
		
		/* append the images */
		this.div.appendChild(this.growImage);
		this.div.appendChild(this.shrinkImage);

		/* do resize on click of the resize button */
		this.div.addEventListener('touchend', function(e) {this.doMapResize();}.bind(this), false);
		
		/* do resize when the device is tilted */
		this.div.addEventListener('orientationchange', function(e) {this.redraw();}.bind(this), false);
		
		this.map.div.appendChild(this.div);
	},
	
	/* on map resize make sure we carter the height and width of the map */
	doMapResize: function() {
		if(this.inFullscreenMode) {
			$(this.map.div.parentNode).setStyle('height', this.defaultHeight + 'px');
			$(this.map.div.parentNode).setStyle('width', this.defaultWidth + 'px');
			this.inFullscreenMode = false;
		}
		
		/* on full screen mode event, add extra 20 px for the map height and do window.scroll) */
		else {
			$(this.map.div.parentNode).setStyle('height', (screen.height + 20) + 'px');
			$(this.map.div.parentNode).setStyle('width', screen.width + 'px');
			this.inFullscreenMode = true;
			
			window.scroll(0, this.map.div.parentNode.offsetTop);
		}
		
		this.redraw();
		this.toggle();
	},
	
	redraw: function() {
		$(this.div).dispose();
		this.map.updateSize();
		this.draw();
	},
	
	toggle: function() {
		if(this.inFullscreenMode) {
			this.shrinkImage.style.display = 'block';
			this.growImage.style.display = 'none';
		}
		
		else {
			this.shrinkImage.style.display = 'none';
			this.growImage.style.display = 'block';
		}
	},
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});

_MapControls_.push(new EMS.Control.FullScreen);
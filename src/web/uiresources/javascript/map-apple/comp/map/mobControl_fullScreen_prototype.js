EMS.Control.FullScreenPrototype = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Apple",
	CLASS_NAME: "EMS.Control.FullScreen",
	
	resizeButtonSize: null,
	resizeButtonPadding: 5,

	active: false,
	
	inFullscreenMode: false,
	
	div: null,
	growImage: null,
	shrinkImage: null,
	
	/* overwrite the following 4 for unique devices */
/*	fullLandscapeWidth: '480px',  */
	fullLandscapeHeight: '269px',

/*	fullPortraitWidth: window.innerWidth + 'px',  
	fullPortraitHeight: window.innerHeight + 'px', */
	
	fullPortraitHeight: '416px',
	
	initialize: function() {
		this.active = true;
		
		this.growImage = new Image();
		this.growImage.src = _MapControlsPath_ + 'maxi.image';
		this.growImage.style.display = 'block';
		this.growImage.id = 'grow';
		
		this.shrinkImage = new Image();
		this.shrinkImage.src = _MapControlsPath_ + 'mini.image';
		this.shrinkImage.style.display = 'none';
		this.shrinkImage.id = 'shrink';
	},
	
	calcPosition: function() {
		return new OpenLayers.Pixel(
			this.map.size.w - this.resizeButtonSize.w - this.resizeButtonPadding,
			this.map.size.h - this.resizeButtonSize.h - this.resizeButtonPadding
		);	
	},
	
	calcDimension: function() {
		return new OpenLayers.Size(this.growImage.width, this.growImage.height);
	},
	
	draw: function() {
		/* initialize the button size on draw */
		this.resizeButtonSize = this.calcDimension();
		
		var resizeControlExactPosition = this.calcPosition();
		
		this.div = OpenLayers.Util.createDiv('FullScreen_Controller',
				resizeControlExactPosition, this.resizeButtonSize, 
				'', 'absolute','0px none', '', '1');	
		
		/* set this right otherwise the image can't be seen */
		this.div.style.zIndex = '750';
		
		/* append the images */
		this.div.appendChild(this.growImage);
		this.div.appendChild(this.shrinkImage);

		/* do resize on click of the resize button */
		this.div.addEventListener('click', function(e) {e.preventDefault(); e.stopPropagation(); this.doMapResize();}.bind(this), false);
		
		/* do resize when the device is tilted */
		window.addEventListener('orientationchange', function(e) {
			this.reOrientate(); 
		}.bind(this), false);
		
		this.map.div.appendChild(this.div);
	},
	
	adjustToFullScreen: function() {
		if(window.orientation == 0) {
			$(this.map.div.parentNode).style.height = this.fullPortraitHeight;
	/*		$(this.map.div.parentNode).style.width = this.fullPortraitWidth;   */
		} 
		else {
			$(this.map.div.parentNode).style.height =  this.fullLandscapeHeight;
	/*		$(this.map.div.parentNode).style.width = this.fullLandscapeWidth;  */
		}
		window.scroll(0, this.map.div.parentNode.offsetTop);
	},
	
	/* on map resize make sure we carter the height and width of the map */
	doMapResize: function() {
		if(this.inFullscreenMode) {
			$(this.map.div.parentNode).setAttribute('style', '');
			this.inFullscreenMode = false;
		}
		
		/* on full screen mode event, add extra 20 px for the map height and do window.scroll) */
		else {
			this.adjustToFullScreen();	
			this.inFullscreenMode = true;
		}
		
		this.map.updateSize();
		
		this.toggle();
		
		this.broadcastPosition();
	},
	
	reOrientate: function() {
		if(this.inFullscreenMode) {
			this.adjustToFullScreen();	
		}
		this.map.updateSize();
		this.broadcastPosition();
	},
	
	rePosition: function() {	
		var newPosition = this.calcPosition();
		
		this.div.style.top = newPosition.y + 'px';
		this.div.style.left = newPosition.x + 'px';
	},
	
	broadcastPosition: function() {
		var mcl = this.map.controls.length;
		for(var i = 0; i < mcl; i++) {
			try{this.map.controls[i].rePosition();}
			catch(e){}
		}
	},
	
	toggle: function() {
		if(this.inFullscreenMode) {
			this.shrinkImage.style.display = 'block';
			this.growImage.style.display = 'none';
		}
		
		else {
			this.shrinkImage.style.display = 'none';
			this.growImage.style.display = 'block';
			
			this.executeClickToEnable();
		}
	},
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	},
	
	executeClickToEnable: function() {
		var mcl = this.map.controls.length;
		for(var i = 0; i < mcl; i++) {
			if(this.map.controls[i].CLASS_NAME == "EMS.Control.ClickToEnable") {
				try{this.map.controls[i].close();}
				catch(e){}
				break;
			}
		}
	}
});
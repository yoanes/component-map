EMS.Control.ClickToEnable = OpenLayers.Class(OpenLayers.Control, {
	
	Device: "webkit", 
	BROWSER_ENGINE: "webkit",
	CLASS_NAME: "EMS.Control.ClickToEnable",
	
	active: false,
	
	div: null,
	
	style: {
		backgroundColor: 'black',
		height: 'inherit',
		width: '100%',
		position: 'absolute',
		left: 0,
		top: 0,
		zIndex: 751,
		opacity: 0.6,
		
	},
	
	initialize: function() {
		this.active = true;
		this.div = new Element('div');
		this.div.id = "mapWindowOff";
	},
	
	draw: function() {
		this.div.setStyles(this.style);
		
		this.div.addEventListener('click', function() {
			this.open();
			this.executeFullScreen();
		}.bind(this), false);

		this.div.addEventListener('orientationchange', function() {
            this.map.updateSize();
		}.bind(this), true);

		this.map.div.parentNode.appendChild(this.div);
	},
	
	open: function() {
		this.div.style.height = '0px';
		this.div.style['-webkit-transition-property'] = 'height';
		this.div.style['-webkit-transition-duration'] = '500ms';
		this.div.style['-webkit-transition-timing-function'] = 'ease-out';
	},
	
	close: function() {
		this.div.style.height = '100%';
		this.div.style['-webkit-transition-property'] = 'height';
		this.div.style['-webkit-transition-duration'] = '500ms';
		this.div.style['-webkit-transition-timing-function'] = 'ease-in';
	},
	
	executeFullScreen: function() {
		var mcl = this.map.controls.length;
		for(var i = 0; i < mcl; i++) {
			if(this.map.controls[i].CLASS_NAME == "EMS.Control.FullScreen") {
				try{this.map.controls[i].doMapResize();}
				catch(e){}
				break;
			}
		}
	}
});
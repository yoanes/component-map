EMS.Control.FullScreen = OpenLayers.Class(EMS.Control.FullScreenPrototype, { 
	
/*	fullLandscapeWidth: '565px',  */
	fullLandscapeHeight: '250px',
	
/*	fullPortraitWidth: '315px',   */
	fullPortraitHeight: '360px',
	
});

_MapControls_.push(new EMS.Control.FullScreen);
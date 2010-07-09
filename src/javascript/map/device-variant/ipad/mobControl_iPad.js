EMS.Control.MobileDefaults = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Apple-iPad",
	CLASS_NAME: "EMS.Control.MobileDefaults",
	
	active: false,
	
	/* track the node0 */
	X0: null,
	Y0: null,
	id0: null,
	
	/* track the node1 */
	X1: null,
	Y1: null,
	id1: null,
	
	/* track the center pinch */
	cX: null,
	cY: null,
	
	/* center point for zoom scale. in percentage */
	zX: null,
	zY: null,
	
	/* track for the number of pixel's moved */
	dX: null,
	dY: null,
	
	/* flag whether we are zooming or not */
	scale: null,

	/* var to hold the last scale to calculate the zoom fraction.
	 * ties in closely with the zooming flag
	 *  */
	lastScale: null,
	
	/* flag to indicate that a gestureEnd has fired but the map zoom()
	 * hasn't been executed. This will trigger a separate logic for 
	 * panning/zooming
	 */
	zooming: false,
	
	initialize: function() { 
		this.active = true;
	},
	
	/* Util method. Returns the center point of multi touches with respect to the map viewport */
	getCenterTouch: function(n1, n2, f) {
		var cX = (n1.pageX + n2.pageX) / 2 - $(this.map.div.parentNode).offsetLeft;
		var cY = (n1.pageY + n2.pageY) / 2 - $(this.map.div.parentNode).offsetTop;
		
		if(f == 'percent') 
			/* return the format as percentage */
			return {'x': cX / this.map.size.w * 100, 'y': cY / this.map.size.h * 100};
		else if(!$defined(f) || f == 'integer')
			/* return the actual px */
			return {'x': Math.floor(cX), 'y': Math.floor(cY)};
	},
	
	/* panning is always with respect to movement of the first node. Always calculate the original center pinch
	 * for zoom animation.
	 *  */
	execTouchStart: function(e) {
		e.preventDefault();
		
		var node = e.touches[0];
		
		this.X0 = node.pageX;
		this.Y0 = node.pageY;
		this.id0 = node.identifier;
		
		if(e.touches.length == 2) { 
			var node1 = e.touches[1];
			
			this.X1 = node1.pageX;
			this.Y1 = node1.pageY;
			this.id1 = node1.identifier;
				
			/* grab the center touch on initial gesture start 
			 * this is so that the locked view doesn't jump and confuse user
			 * */
			if(!this.zooming) {
				var pointCenterTouch = this.getCenterTouch(node, node1);
				this.cX = pointCenterTouch.x;
				this.cY = pointCenterTouch.y;
				
				var percentCenterTouch = this.getCenterTouch(node, node1, 'percent');
				this.zX = percentCenterTouch.x;
				this.zY = percentCenterTouch.y;
			}
		}
	},
	
	/* calculate the difference between nodes. Guides the panning movement. */
	execTouchMove: function(e) {
		e.preventDefault();

		var node = e.touches[0];
		
		var movedX = node.pageX;
		var movedY = node.pageY;

		/* always default to the first touch node */
		if(node.identifier == this.id0) { 
			var diffX = this.X0 - movedX;
			var diffY = this.Y0 - movedY;
			
			this.X0 = movedX;
			this.Y0 = movedY;
		}
		
		/* otherwise checks if the second touch node is all that's left 
		 * this case only happens post zooming
		 */
		else if(node.identifier == this.id1) { 
			var diffX = this.X1 - movedX;
			var diffY = this.Y1 - movedY;
			
			this.X1 = movedX;
			this.Y1 = movedY;
		}
		
		/* differ panning on ipad to later stage of event as it will be massive overkill when we do
		 * panning on the fly. Especially when the map is big *like in whereis
		 *  */
		if(this.scale == null) {
			/* simulate the panning */
			$(this.map.viewPortDiv).setStyle('margin-left', $(this.map.viewPortDiv).getStyle('margin-left').toInt() - diffX + "px");
			$(this.map.viewPortDiv).setStyle('margin-top',  $(this.map.viewPortDiv).getStyle('margin-top').toInt() - diffY + "px");
			
			/* accumulate the dx and dy*/
			this.dX += diffX;
			this.dY += diffY;
		}
		
		else {
			/* handles the dx dy for 1st level of zoom and overide the calculated dx dy with
			 * the difference between the center touch. Gives a more accurate view.
			 */
			if(e.touches.length == 2 && !this.zooming){ 
				var node1 = e.touches[1];
				
				/* please do update the X1 and Y1 so the map won't jump if the 1st node is removed */
				this.X1 = node1.pageX;
				this.Y1 = node1.pageY;
				
				this.X0 = node.pageX;
				this.Y0 = node.pageY;
				
				/* calculate the pan based on the center pinch movement */
				var centerTouch2 = this.getCenterTouch(node, node1);
				
				var diffX = this.cX - centerTouch2.x;
				var diffY = this.cY - centerTouch2.y;
				
				this.cX = centerTouch2.x;
				this.cY = centerTouch2.y;
				
				/* accumulate the diff */
				this.dX += diffX;
				this.dY += diffY;
			}
			
			else { 
				/* if touches only equals to 1 then the movement is actually effected by the scale 
				 * and remember the scale is always the reverse for panning and zooming
				 * 
				 * this case should handle the case where (this.zooming && e.touches.length == 2). 
				 * this case the panning factor is based on the first node changes. Not the center touch.
				 * */
				this.dX += diffX / this.scale;
				this.dY += diffY / this.scale;
			}
			
			/* simulate the panning while zooming */
			$(this.map.viewPortDiv).setStyle('margin-left', $(this.map.viewPortDiv).getStyle('margin-left').toInt() - diffX + "px");
			$(this.map.viewPortDiv).setStyle('margin-top',  $(this.map.viewPortDiv).getStyle('margin-top').toInt() - diffY + "px");
		}
	},
	
	/* this is where the zoom in or out actually happens */
	execTouchEnd: function(e) {
		e.preventDefault();
		
		if(this.scale != null) {
			/* only zoom if there's no finger left on screen. That way we consider all touches 
			 * so the final view gives a more accurate expectation of what the user will get 
			 * upon zooming
			 */
			if(e.touches.length == 0) {
				/* get the absolute center map in px */
				var centerMap = new OpenLayers.Pixel(this.map.getSize().w / 2, this.map.getSize().h / 2 );

				var CXY = new OpenLayers.Pixel(this.cX, this.cY);
				
				/* find the distance between the center pinch and the center map in px */
				var distance = new OpenLayers.Pixel(CXY.x - centerMap.x, CXY.y - centerMap.y);

				/* calculate the location of the original center pinch */
				var shadowCXY = new OpenLayers.Pixel(this.cX + this.dX, this.cY + this.dY);
				/* get the coord from the original center pinch */
				var shadowCXYCoord = this.map.getLonLatFromPixel(shadowCXY);
				
				/* find the closest zoom point based on the final scale */
				var nextZoom = this.map.getZoomForResolution(this.map.getResolution() / this.scale);
				var nextMapRes = this.map.baseLayer.resolutions[nextZoom];				
				
				/* find the new center point */
				var newCoordCenter = new OpenLayers.LonLat(
						shadowCXYCoord.lon - ((distance.x) * nextMapRes),
						/* the dY is the opposite */ 
						shadowCXYCoord.lat + ((distance.y) * nextMapRes)
				);
				
				/* do a map recenter. Don't do zoomIn() or zoomOut() because it always does it 
				 * from the center of the map which might have changed because the user pans
				 * while zooming. 
				 *  */
				this.map.setCenter(newCoordCenter, nextZoom);
				
				/* don't forget to reset :) */
				this.dX = this.dY = 0;
				
				/* revert back after panning during zoom  */
				$(this.map.viewPortDiv).setStyle('margin-left', "0px");
				$(this.map.viewPortDiv).setStyle('margin-top', "0px");

				this.map.viewPortDiv.style['-webkit-transform'] = 'scale(1)';
				
				this.scale = null;
				
				this.zooming = false;
				this.lastScale = null;
			}
			 
			 /* set the zooming flag to true */
			else if (e.touches.length == 1) {
				this.zooming = true;
			}
		}
		else {
			/* do the actual pan */
			this.map.pan(this.dX, this.dY, {animate:false});
			
			/* don't forget to reset :) */
			this.dX = this.dY = 0;
			
			/* revert back after panning during zoom  */
			$(this.map.viewPortDiv).setStyle('margin-left', "0px");
			$(this.map.viewPortDiv).setStyle('margin-top', "0px");
		}
	},             
	
	/* update the scale flag */
	execGestureStart: function(e) { 
		e.preventDefault();
		if(!this.zooming)
			this.scale = e.scale;
		else this.lastScale = e.scale;
	},
	
	/* do animation and nothing else */
	execGestureChange: function(e){
		e.preventDefault(); 
		if(this.zooming) {
			var scaleDiff = this.lastScale - e.scale;
			this.scale *= (1 - scaleDiff);
			this.lastScale = e.scale;
		}
		else this.scale = e.scale;
		
		this.map.viewPortDiv.style['-webkit-transform'] = 'scale(' + this.scale + ')';	
		this.map.viewPortDiv.style['-webkit-transform-origin-x'] = this.zX + '%';	
		this.map.viewPortDiv.style['-webkit-transform-origin-y'] = this.zY + '%';
	},
	
	/* finalise the scale factor. Don't zoom in or out at this stage as the users are likely
	 * to do pan while zoom. This happens most of the time because their bloody fingers won't leave
	 * the screen at the same time.
	 *  */
	execGestureEnd: function(e) {
		 e.preventDefault();
		 /* finalise the scale only for initial zoom */
		 if(!this.zooming) {
			 this.scale = e.scale;
		 }
	},
	
	/**
	 * APIMethod: draw
	 */
	
	draw: function() { //see observe()
		/* add extra css3 things to smooth things out */
		this.map.layerContainerDiv.style['-webkit-transform'] = 'translate3d(0,0,0)';
		this.map.layerContainerDiv.style['-webkit-transition'] = '-webkit-transform 10ms ease-out 0';
	
		this.map.div.ongesturestart = this.execGestureStart.bind(this);
		this.map.div.ongesturechange = this.execGestureChange.bind(this);
		this.map.div.ongestureend = this.execGestureEnd.bind(this);
		this.map.div.ontouchstart = this.execTouchStart.bind(this);
		this.map.div.ontouchmove = this.execTouchMove.bind(this);
		this.map.div.ontouchend = this.execTouchEnd.bind(this);

		/** register the zoomend event as well. Does the reverting back to original scale */
		this.map.events.register('zoomend', this, function() { 
			this.map.viewPortDiv.style['-webkit-transform'] = 'scale(1)'; 
		});
	},
	
	/**	
	 * APIMethod: destroy	
	 * Constructs contents of the control.	
	 *	
	 * Returns:	
	 * A reference to a div that represents this control.	
	 */    
	
	destroy: function() {
		if (this.handler) {
			this.handler.destroy();
		}
		this.handler = null;	
	}
});

_MapControls_.push(new EMS.Control.MobileDefaults);
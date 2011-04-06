EMS.Control.MobileDefaultsPrototype = OpenLayers.Class(OpenLayers.Control, { 
	
	Device: "Apple-iPhone",
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
	
	simulatePan: function(dX, dY) {},
	
	executePan: function() {},
	
	resetPan: function() {},
	
	setupAnimation: function() {},
	
	/* return the x and y properties from the transflate3d css style */
	getTranslate3dProperties: function() {
		var transform = this.map.viewPortDiv.style.webkitTransform;
		var t3dX = parseInt(transform.replace(/translate3d./,'').replace(/,.*/,''));
		var t3dY = parseInt(transform.replace(/translate3d.*?,/,'').replace(/,0.*/,''));
		
		if (t3dX == null || isNaN(t3dX)) t3dX = 0;
        if (t3dY == null || isNaN(t3dY)) t3dY = 0;
        
        return {x: t3dX, y: t3dY};
	},
	
	/* return the css style string for translate3d */
	getTranslate3dCSS: function() {
		var transform = this.map.viewPortDiv.style.webkitTransform;
		var tTranslate = transform.match(/translate3d.*?\)/);
		return tTranslate == null ? "" : tTranslate;
	},
	
	/* return the css style string for scale3d */
	getScale3dCSS: function() {
		var transform = this.map.viewPortDiv.style.webkitTransform;
		var tScale = transform.match(/scale3d.*?\)/);
		return tScale == null? "" : tScale;
	},
	
	getCalculatableScale: function() {
		return this.scale == null ? 1 : this.scale;
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
		
		/* we assume that all apple devices want to differ the panning. Not the case for Iphone OS 3.1. 
		 * See the actual implementation of this controller
		 *  */
		if(this.scale == null) { this.simulatePan(diffX, diffY); }
		
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
				 
				/* accumulate the diff */
				this.dX += diffX;
	            this.dY += diffY;
			}
			
			/* grab the transform properties */
			var t3dp = this.getTranslate3dProperties();
			var tScale = this.getScale3dCSS();
			/* simulate the panning while zooming */
			this.map.viewPortDiv.style['-webkit-transform'] = 'translate3d(' + Math.floor(t3dp.x-diffX) + 'px, ' + Math.floor(t3dp.y-diffY) + 'px, 0) ' + tScale;
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
				this.map.viewPortDiv.style['-webkit-transform'] = '';
				this.map.viewPortDiv.style['-webkit-transform-origin'] = '';
				
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
			/* assume device wants to do the actual pan now */
			this.executePan();
			/* don't forget to reset :) */
			this.resetPan();
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

		var t3dp = this.getTranslate3dProperties();
		/* set the origin for transformation here */
		this.map.viewPortDiv.style['-webkit-transform-origin-x'] = this.zX + '%';
        this.map.viewPortDiv.style['-webkit-transform-origin-y'] = this.zY + '%';

		var tTranslate = this.getTranslate3dCSS();
		this.map.viewPortDiv.style['-webkit-transform'] = tTranslate + ' scale3d(' + this.scale + ', ' + this.scale + ', 1)';
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
		this.setupAnimation();
		
		this.map.div.addEventListener('touchstart', function(e){
			this.execTouchStart(e);
		}.bind(this), false);
		
		this.map.div.addEventListener('touchmove', function(e){
			this.execTouchMove(e);
		}.bind(this), false);
		
		this.map.div.addEventListener('touchend', function(e){
			this.execTouchEnd(e);
		}.bind(this), false);
		
		this.map.div.addEventListener('gesturestart', function(e){
			this.execGestureStart(e);
		}.bind(this), false);
		
		this.map.div.addEventListener('gesturechange', function(e){
			this.execGestureChange(e);
		}.bind(this), false);
		
		this.map.div.addEventListener('gestureend', function(e){
			this.execGestureEnd(e);
		}.bind(this), false);

		/** register the zoomend event as well. Does the reverting back to original scale */
		this.map.events.register('zoomend', this, function() { 
			this.map.viewPortDiv.style['-webkit-transform'] = '';
			this.map.viewPortDiv.style['-webkit-transform-origin'] = '';
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

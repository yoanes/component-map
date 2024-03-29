MobEMS.implement({
	/* holds the dock */
	Dock: null,
	
	AppCallback: null,
	
	/* extend the Dock object with necessary functionality that EMS doesn't provide */
	extendDock: function() {
		if(this.Dock == null) return;
		
		this.Dock.PoiPopupPair = new Array();
		this.Dock.currentFrontPoi = null;
		
		/* because the dock only host the popup content
		 * we need to way to find the corresponding poi on the map given a popup content
		 * this method will be called every time the addPopup() is called (with Dock)
		 */
		this.Dock.bindPoiWithPopup = function(interactivePoi) {
			/* only bind if the popup exist */
			if($(interactivePoi.id)) {
				var newPair = {};
				newPair.poi = interactivePoi.div;
				newPair.popup = $(interactivePoi.id);
				
				this.PoiPopupPair.push(newPair);
			}
		}.bind(this.Dock);
		
		/* method to query the already built PoiPopup pair */
		this.Dock.findPoiGivenPopup = function(popupId) {
			if(this.PoiPopupPair.length > 0) {
				for(var i = 0; i < this.PoiPopupPair.length; i++) {
					if(this.PoiPopupPair[i].popup.id == popupId) {
						return this.PoiPopupPair[i].poi;
						break;
					}
				}
			}
			return null;
		}.bind(this.Dock);
		
		/* this method will bring the poi above all other pois when 
		 * it is called. Ideally this is called on tap of the poi or
		 * onShow on the Dock
		 * 
		 * This will also set a css class called 'highlight' to the poi Div
		 * which the app is free to define
		 */
		this.Dock.bringPoiToFront = function(poiDiv) {
			/* safe guard the method so we won't try to set any style 
			 * to a nonexistent div
			 */
			if(this.currentFrontPoi != null) {
				/* and remove the additional highlight class */
				$(this.currentFrontPoi).removeClass('highlight');
				this.currentFrontPoi = null;
			}
			/* if there's no poi to be highlighted return */
			if(poiDiv == null) return;
			
			this.currentFrontPoi = poiDiv;
			/* add the highlight class to the poi */
			$(poiDiv).addClass('highlight');
		}.bind(this.Dock);
		
		/* retrieve popup index from dock.contents array property */
		this.Dock.getPopupIndexById =  function(id) {
			var popupContents = this.contents;
			if(popupContents instanceof Array) {
				var popupContentsLength = popupContents.length;
				for(var i = 0; i < popupContentsLength; i++) {
					if(popupContents[i].id == id) {
						return i;
						break;
					}
				}
			}
			return -1;
		}.bind(this.Dock);
	},

	/* we need to hook the default callback for the dock and wrap the app's function within it 
	 * otherwise we will have bunch of issue with YM requirement 
	 * */
	DockCallback: function(idx) {
		/* pass the poi to the app call back */
		var poi = this.Dock.findPoiGivenPopup(this.Dock.contents[this.Dock.contentIndex].id);
		this.Dock.bringPoiToFront(poi);
		if(this.AppCallback instanceof Function)
			this.AppCallback(idx, poi);
		return;
	},
	
	addDock: function(map, dOpt) {
		/* set it to AppCallback */
		this.AppCallback = dOpt;
		/* instantiate */
		this.Dock = new EMS.Control.DockedInfoBox();
		/* extend the Dock with our own utilities method */
		this.extendDock();
		/* always pass in the child node as an array */
		this.Dock.setContents($('mapPopup').getChildren(), this.DockCallback.bind(this));
	
		/* dock is a controller so add it to our map but we need to put it outside the viewport like anyother 
		 * map controls 
		 * 
		 * try to detect if the app declare a dom with id = mapDockBox. If it is declared we'll
		 * dump the box there. Otherwise it will be embedded to the map it self.
		 * */
		if($('mapDockBox')) {
			$('mapDockBox').style.position = 'relative';
			$('mapDockBox').appendChild(this.Dock.draw());
			/* we need to target the Dock div and set the style to relative (by default it is 
			 * absolute positioned at the bottom of the inside of the map)
			 * We can't target the Dock's id however, because it will depend on many factors, hence the
			 * id will be randomly generated at run time. Thus we'll target that dom
			 * by targeting the last element appended to the #mapDockBox
			 */
			var mapDockBoxChildren = $('mapDockBox').getChildren();
			$('mapDockBox').childNodes[mapDockBoxChildren.length - 1].style.position = 'relative';
		}
		else map.div.appendChild(this.Dock.draw()); 
	},
	
	loadDockWithContentFromIndex: function(idx) {
		if(this.Dock.contents instanceof Array) {
			this.Dock.loadContentsForIndex(idx);
		}
	}
});
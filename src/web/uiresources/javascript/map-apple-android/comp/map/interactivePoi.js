MobEMS.implement({
	/* holds the dock */
	Dock: null,
	
	/* holds the detected pois with the same lat lon */
	MultiPois: new Array(),
	
	addPoi: function(map, marker, icon) {
		var iconText = new String();
		
		/* if no type is set then go back to the old static image poi */
		if(icon.type == '') {
			return this.__addPoi(marker, icon);
		}
		
		/* try to use number for SLIM / THICK poi 
		 * these 2 types of poi will just truncate the text with elipsis
		 * which means you'll only get "A..." if we use text 
		 * We need to detect if app wants a SLIM/THICK poi with blank text 
		 * (a fat dot is displayed instead) so if the app supply text == "."
		 * then we'll treat it as if it asks for a blank poi of THICK/SLIM type
		 */
		else if(icon.type == 'SLIM' || icon.type == 'THICK') {
			if(icon.text != ".") {
				var iconText = this.grabNumberedTextFromUrl(icon.url);
				/* if no number is found use the blank poi */
				if(!iconText) iconText = ""; 
			}
			else iconText = "";
		}
		
		/* for TEXT InteractiveIcon type set text */
		else iconText = icon.text;
			
		var interactiveIcon = new EMS.InteractiveIcon(map,{
			markerStyle:EMS.InteractiveMarkerStyles[icon.type], text:iconText, title:icon.title
		}); 
		var interactiveMarker = new OpenLayers.Marker(this.formatLatLon(marker.coordinates), interactiveIcon);
		
		this.Map.markersLayer.addMarker(interactiveMarker);
		
		/* add popup if possible */
		if($(icon.id))
			this.addPopup(icon.id, interactiveIcon, interactiveMarker);
		
		return interactiveMarker ;
	},
	
	addPois: function(map, iconsList) {
		var resultLength = iconsList.length;
		if(resultLength > 0){			
			var distinctPois = this.detectUniquePoi(iconsList);
			var distinctPoisLength = distinctPois.length;
			
			for(var i = 0; i < distinctPoisLength; i++) {
				/* construct the marker and icon */
				var newMarker = {coordinates: iconsList[i].coordinates};
				var newIcon = {
						id: distinctPois[i].id,
						title: distinctPois[i].title,
						text: distinctPois[i].text,
						type: distinctPois[i].type,
						url: distinctPois[i].url,
						width: distinctPois[i].width,
						height: distinctPois[i].height,
						offsetX: distinctPois[i].offsetX,
						offsetY: distinctPois[i].offsetY
					};
				this.addPoi(map, newMarker, newIcon);
			}
		}
	},
	
	detectUniquePoi: function(iconList) {
		/* place holder for pois with unique coordinates */
		var pois = new Array();
		
		for(var i = 0; i < iconList.length; i++) {
			if(pois.length == 0) {
				pois.push(iconList[i]);
				continue;
			}
			
			/* if current poi is a multi poi or has the same coords as another poi
			 * that's already in the array, this poi will not have its own icon. Instead
			 * it will modify the icon already in the list into a multi poi. 
			 * 
			 * A Multi poi default to SLIM_MULTI unless one of them is a THICK poi then
			 * it will morph into MULTI
			 */
			var poisIndex = this.isPoiInArray(iconList[i], pois);
			if(poisIndex) {
				if(iconList[i].type == 'THICK') 
					pois[poisIndex].type = 'MULTI';
				else {
					/* only updates if the type is not multi yet */
					if(pois[poisIndex].type != 'MULTI')
						pois[poisIndex].type = 'SLIM_MULTI';
				}
				
				/* initate the next pointer for the soon to be child node */
				iconList[i].next = null;
				/* if the iconList[index] not in multiPois array then push it there */
				var multipoiIndex = this.isPoiInArray(pois[poisIndex], this.MultiPois);
				if(!multipoiIndex) {
					/* attach i as the childNode of indx */
					pois[poisIndex].next = iconList[i];
					this.MultiPois.push(pois[poisIndex]);
				}
				else { 
					/* grab the next */
					var nextSpot = this.MultiPois[multipoiIndex].next;
					/* find the next spot in the link */
					while(nextSpot.next != null) {
						nextSpot = nextSpot.next;
					}
					nextSpot.next = iconList[i];
				}
				continue;
			}
			/* only push to pois when it is a unique poi */
			else pois.push(iconList[i]);
		}
		
		return pois;
	},
	
	/* check if there's any other poi in the neighbourhood with the same lat-lon */
	isPoiInArray: function(icon, iconList) {
		for(var i = 0; i < iconList.length; i++) {
			if(this.isLatLonEqual(icon.coordinates, iconList[i].coordinates)) {
				return i;
				break;
			}
		}
		
		return false;
	},
	
	/* utility method to grab the poi number from the image url * */	
	grabNumberedTextFromUrl: function(url) {
		var pattern = '^(.*\/)+([0-9]+)\_(.*)+\.image$';
		var filter = new RegExp(pattern, 'i');
		var number = url.replace(filter, "$2");
		
		return number == url.length ? false : number;
	},
	
	addPopup: function(id, icon, marker) {
		/* extends the icon object with id, popup content, and marker */
		icon.id = id;
		icon.marker = marker; 
		icon.popup = $(id);
		
		/* if no dock is specified */
		if(this.Dock == null) {
			/* create event to open popup */
			icon.div.addEventListener('touchend', function() {
				this.popupDiv = this.showPopup(this.popup, true);
				/* create event to close popup */ 
				this.popupDiv.addEventListener('touchend', function(){
					this.hidePopup();
				}.bind(this), false);
			}.bind(icon), false);
		} 
		
		else {
			icon.div.addEventListener('touchend', function() {
				this.loadDockWithContentFromIndex(this.getPopupIndexById(icon.id));
			}.bind(this), false);
		}
	}, 

	addDock: function(map, dOpt) {
		/* instantiate */
		this.Dock = new EMS.Control.DockedInfoBox();
		/* if there's only 1 popup then use just that */
		if($('mapPopup').getChildren().length == 1)
			this.Dock.setContents(($('mapPopup').getChildren())[0], dOpt);
		/* otherwise pass in the whole childNodes array */
		else this.Dock.setContents($('mapPopup').getChildren(), dOpt);
	
		/* dock is a controller so add it to our map but we need to put it outside the viewport like anyother 
		 * map controls 
		 * 
		 * try to detect if the app declare a dom with id = mapDockBox. If it is declared we'll
		 * dump the box there. Otherwise it will be embedded to the map it self.
		 * */
		if($('mapDockBox'))
			$('mapDockBox').appendChild(this.Dock.draw());
		else map.div.appendChild(this.Dock.draw()); 
	},
	
	getPopupIndexById: function(id) {
		var popupContents = this.Dock.contents;
		if(this.Dock.contents instanceof Array) {
			var popupContentsLength = popupContents.length;
			for(var i = 0; i < popupContentsLength; i++) {
				if(popupContents[i].id == id) {
					return i;
					break;
				}
			}
		}
		return -1;
	},
	
	loadDockWithContentFromIndex: function(idx) {
		if(this.Dock.contents instanceof Array) {
			this.Dock.loadContentsForIndex(idx);
		}
	}
});

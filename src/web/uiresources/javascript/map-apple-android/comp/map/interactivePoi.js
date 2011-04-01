MobEMS.implement({
	/* holds the dock */
	Dock: null,
	
	/* holds the detected pois with the same lat lon */
	MultiPois: new Array(),

	/* multi pois pagination vars */
	MultiPoisCurrentIcon: null,
	MultiPoisCurrentPoi: null,
	
	/* holds the interactive icon that's currently popping */
	CurrentlyPopping: null,
	
	PopupHeight: {'MINI': 110, 'SMALL': 210, 'LARGE': 246},
	
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
			
		if(icon.type == 'MULTI-TEXT') {
			icon.type = 'TEXT';
			/* parse in the marker because it is the only obj with coordinate as this poin of time
			 * later on the interactiveIcon will be extended with the coordinates property from the marker
			 * 
			 * at this point we just want to know the total node that under this multipoi
			 */
			var iconTitle = this.getMultiPoiLength(this.isPoiInArray(marker, this.MultiPois)) + ' Locations';
		}
		else iconTitle = icon.title;
		
		var interactiveIcon = new EMS.InteractiveIcon(map,{
			markerStyle:EMS.InteractiveMarkerStyles[icon.type], text:iconText, title:iconTitle
		}); 
		var interactiveMarker = new OpenLayers.Marker(this.formatLatLon(marker.coordinates), interactiveIcon);
		
		this.Map.markersLayer.addMarker(interactiveMarker);
		
		/* add popup if possible */
		if($(icon.id)) {
			interactiveIcon.coordinates = marker.coordinates;
			this.addPopup(icon.id, interactiveIcon, interactiveMarker);
		}
		
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
	
	/* this method will filter out the iconlist down to the unique poi
	 * unique as in each poi will have a unique lat lon
	 * 
	 * those pois identified with duplicate lat lon will be shelved in the 
	 * MultiPois array. Note that at this point all the pois are not of Interactive Poi type.
	 * They are just plainjane json.
	 * 
	 * these unique pois will later on be converted into interactive pois while
	 * those that got shelved into MultiPois will be stay as it is (not converted to 
	 * interactive poi at all)
	 */
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
			 * If any of them is of TEXT then we set it temporarily into MULTI-TEXT which later 
			 * on should be converted to TEXT type.
			 */
			var poisIndex = this.isPoiInArray(iconList[i], pois);
			if(poisIndex !== false) {
				if(iconList[i].type == 'THICK') 
					pois[poisIndex].type = 'MULTI';
				else if(iconList[i].type == 'TEXT')
					pois[poisIndex].type = 'MULTI-TEXT';
				else {
					/* only updates if the type is not multi yet */
					if(pois[poisIndex].type != 'MULTI' || pois[poisIndex].type != 'MULTI-TEXT')
						pois[poisIndex].type = 'SLIM_MULTI';
				}
				
				/* initate the prev and next pointer for the soon to be child node */
				iconList[i].next = null;
				iconList[i].prev = null;
				
				/* if the iconList[index] not in multiPois array then push it there */
				var multipoiIndex = this.isPoiInArray(pois[poisIndex], this.MultiPois);
				if(multipoiIndex === false) {
					/* attach i as the childNode of indx */
					pois[poisIndex].next = iconList[i];
					iconList[i].prev = pois[poisIndex];
					
					/* prev is null since this poisIndex is the head of the list */
					pois[poisIndex].prev = null;
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
					iconList[i].prev = nextSpot;
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
		/* store ref to the map instance and if it is a multi interactive poi*/
		icon.mapNth = this.nth;
		icon.isMulti = false;
		
		/* if no dock is specified */
		if(this.Dock == null) {
			var popupSize = this.getSizeForPopup(icon); 
			if(popupSize == EMS.InteractiveMarkerPopupSizes.SMALL_WITH_HEADER ||
				popupSize == EMS.InteractiveMarkerPopupSizes.LARGE_WITH_HEADER || 
				popupSize == "MINI_MULTI") {
				
				if(popupSize == "MINI_MULTI") popupSize = EMS.InteractiveMarkerPopupSizes.MINI;
				icon.popup = this.buildMultiPoiPopup(icon);
				icon.isMulti = true;
			}
			
			/* create event to open popup */
			icon.div.addEventListener('touchend', function(e) {
				/* stop the event to propagate */
				e.stopPropagation();
				
				/* initiate the MultiPoisCurrentPoi and MultiPoisCurrentIcon
				 * hide other popup if it is showing
				 */
				(function(icon){
					/* close the currently popping poi if possible */
					if(this.CurrentlyPopping != null) 
						this.CurrentlyPopping.hidePopup();
					
					/* replace with the latest or newest */
					this.CurrentlyPopping = icon;
					
					if(icon.isMulti) {
						/* set the pointer for pagination on multi poi */
						this.MultiPoisCurrentPoi = icon;
						this.MultiPoisCurrentIcon = this.MultiPois[icon.multiPoisIndex];
					}
				}.bind(MAP.instances[this.mapNth]))(this);
				
				this.popupDiv = this.showPopup(this.popup, true, popupSize);
				
				(function() {
					/* re align the current icon if it is a multi poi */
					if(this.CurrentlyPopping.isMulti){
						var idx = parseInt($(this.MultiPoisCurrentPoi.paginationIndexSpan).innerHTML);
						this.MultiPoisCurrentIcon = this.getMultiPoiAt(this.MultiPoisCurrentPoi.multiPoisIndex, idx-1);
					}
				}.bind(MAP.instances[this.mapNth]))(this);
				
				/* create event to close popup */ 
				this.popupDiv.addEventListener('touchend', function(){
					this.hidePopup();
				}.bind(this), false)
			}.bind(icon), false);
		} 
		
		else {
			icon.div.addEventListener('touchend', function() {
				this.loadDockWithContentFromIndex(this.getPopupIndexById(icon.id));
			}.bind(this), false);
		}
	},

	getPopupHeightWhenWidthIs202: function(popupDiv) {
		var tempWidth = popupDiv.style.width;
		
		popupDiv.style.width = '202px';
		var popupHeight = popupDiv.getSize().y;
		
		popupDiv.style.width = tempWidth;
		return popupHeight;
	},
	
	getSizeForPopup: function(icon) {
		var isAMultiPoi = this.isPoiInArray(icon, this.MultiPois);
		
		if(isAMultiPoi === false) {
			/* set the width the 202 so we can get a more accurate height. 202 is the max width of the popup */

			var popupHeight = this.getPopupHeightWhenWidthIs202(icon.popup);
			
			if(popupHeight <= this.PopupHeight.MINI)
				return EMS.InteractiveMarkerPopupSizes.MINI;
			else if(popupHeight <= this.PopupHeight.SMALL)
				return EMS.InteractiveMarkerPopupSizes.SMALL;
			else return EMS.InteractiveMarkerPopupSizes.LARGE;
		}
		
		else {
			popupHeight = this.getLargestPopup(this.MultiPois[isAMultiPoi]);
			if(popupHeight <= this.PopupHeight.MINI)
				return "MINI_MULTI";
			else if(popupHeight <= this.PopupHeight.SMALL)
				return EMS.InteractiveMarkerPopupSizes.SMALL_WITH_HEADER;
			else return EMS.InteractiveMarkerPopupSizes.LARGE_WITH_HEADER;
		}
	},
	
	getLargestPopup: function(icon) {
		var height = null;
		
		if(!$defined(icon.popup)) icon.popup = $(icon.id);
		
		height = this.getPopupHeightWhenWidthIs202(icon.popup);
		
		var currentIcon = icon;
		while(currentIcon.next != null) {
			var next = currentIcon.next;
			
			var nextHeight = this.getPopupHeightWhenWidthIs202($(next.id));
			
			height = Math.max(height, nextHeight);
			
			currentIcon = next;
		}
		
		return height;
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
		if($('mapDockBox')) {
			$('mapDockBox').style.position = 'relative';
			$('mapDockBox').appendChild(this.Dock.draw());
			$('EMS.Control.DockedInfoBox_261').style.position = 'relative';
		}
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
	},
	
	buildMultiPoiPagination: function(icon) {
		/* generate the next DOM */
		var next = new Element('div');
		next.setAttribute('class', 'poiPopupNext');
		next.innerHTML = "Next";
		next.addEventListener('touchend', function(e) {
			e.stopPropagation();
			this.multiPoiGoNext(icon);
		}.bind(this), false);
		
		/* generate the prev DOM */
		var prev = new Element('div');
		prev.setAttribute('class', 'poiPopupPrev');
		prev.innerHTML = "Prev";
		prev.addEventListener('touchend', function(e) {
			e.stopPropagation();
			this.multiPoiGoPrev(icon);
		}.bind(this), false);
		
		/* generate the pagination text DOM */
		var showing = new Element('div');
		showing.setAttribute('class', 'poiPopupShowing');
		
		/* generate the number for the currently shown multi poi */
		var currentShowing = new Element('span');
		currentShowing.setAttribute('id', icon.paginationIndexSpan);
		currentShowing.innerHTML = "1";
		
		showing.appendChild(currentShowing);
		showing.appendChild(document.createTextNode(" of " + icon.multiPoisLength));
		
		var pagination = new Element('div');
		pagination.setAttribute('class', 'poiPopupPagination');
		
		var hurdle = new Element('div');
		hurdle.style.clear = 'both';
			
		pagination.appendChild(prev);
		pagination.appendChild(showing);
		pagination.appendChild(next);
		pagination.appendChild(hurdle);
		
		return pagination;
	},
	
	buildMultiPoiPopup: function(icon) {
		/* generate userContentDiv id and pagination id and next id and prev id */
		icon.userContentDiv = icon.id + "-userContent";
		icon.paginationDiv = icon.id + "-pagination";
		icon.paginationIndexSpan = icon.paginationDiv + "-index";
		
		/* get the total length and index in the multiPois array */
		icon.multiPoisIndex = this.isPoiInArray(icon, this.MultiPois);
		icon.multiPoisLength = this.getMultiPoiLength(icon.multiPoisIndex);
		icon.multiPoisIcon = this.MultiPois[icon.multiPoisIndex];
		
		/* build the pagination */
		var pagination = this.buildMultiPoiPagination(icon);
		pagination.id = icon.paginationDiv;
	
		/* generate the initial popup content */
		var userContent = new Element('div');
		userContent.id = icon.userContentDiv;
		userContent.appendChild($(icon.id).clone());
		
		var popup = new Element('div');
		popup.appendChild(pagination);
		popup.appendChild(userContent);
		
		return popup;
	},
	
	getMultiPoiLength: function(idx) {
		var i = 1;
		var current = this.MultiPois[idx];
		while(current.next != null) {
			i++;
			current = current.next;
		}
		return i;
	},
	
	getMultiPoiAt: function(idx, pidx) {
		var i = 0;
		var current = this.MultiPois[idx];
		while(i != pidx) {
			i++;
			current = current.next;
		}
		return current;
	},
	
	multiPoiGoPrev: function() {		
		if(this.MultiPoisCurrentIcon.prev == null) return;
		else {
			$(this.MultiPoisCurrentPoi.userContentDiv).empty();
			$(this.MultiPoisCurrentPoi.userContentDiv).appendChild($(this.MultiPoisCurrentIcon.prev.id).clone());
			var currIndex = parseInt($(this.MultiPoisCurrentPoi.paginationIndexSpan).innerHTML);
			$(this.MultiPoisCurrentPoi.paginationIndexSpan).innerHTML = --currIndex;
			this.MultiPoisCurrentIcon = this.MultiPoisCurrentIcon.prev;
		}
	},
	
	multiPoiGoNext: function() { 
		if(this.MultiPoisCurrentIcon.next == null) return;
		else {
			$(this.MultiPoisCurrentPoi.userContentDiv).empty();
			$(this.MultiPoisCurrentPoi.userContentDiv).appendChild($(this.MultiPoisCurrentIcon.next.id).clone());
			var currIndex = parseInt($(this.MultiPoisCurrentPoi.paginationIndexSpan).innerHTML);
			$(this.MultiPoisCurrentPoi.paginationIndexSpan).innerHTML = ++currIndex;
			this.MultiPoisCurrentIcon = this.MultiPoisCurrentIcon.next;
		}
	}
});

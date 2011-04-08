/* Copyright (c) 2006-2008 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */

/**
     List of marker styles which we support. These should be used as constants to set up the
     markerStyle option of this marker, the style chosen will affect how this marker looks in it's default form.
   */
EMS.InteractiveMarkerStyles = {
    SLIM:  1,
    THICK:  2,
    TEXT: 3,
    SLIM_MULTI: 4,
    MULTI: 5,
    SLIM_CIRCLE: 6,
    THICK_CIRCLE: 7
};
/**
     List of popup sizes which we support. By default we'll use the SMALL size.
   */
EMS.InteractiveMarkerPopupSizes = {
    MINI: 1,
    SMALL: 2,
    SMALL_WITH_HEADER: 3,
    LARGE: 4,
    LARGE_WITH_HEADER: 5
};

/**
 * Class: EMS.InteractiveIcon
 *
 * The icon represents a graphical icon on the screen.  Typically used in
 * conjunction with a <OpenLayers.Marker> to represent markers on a screen.
 *
 * An icon has a url, size and position.  It also contains an offset which
 * allows the center point to be represented correctly.  This can be
 * provided either as a fixed offset or a function provided to calculate
 * the desired offset.
 *
  */
EMS.InteractiveIcon = OpenLayers.Class({
    /**
     * Property: defaultOptions
     * Contains all the default values for everything. Can be overridden on initialization.
     */
    defaultOptions: {
        markerStyle: EMS.InteractiveMarkerStyles.SLIM,
        dockedInfoBoxId: null,
        title: "",
        text: "",
        popupEdgeMargin : 10,
        fadeDuration: 20
    },

    /**
     * Property: size
     * {<OpenLayers.Size>}
     */
    size: null,

    /**
     * Property: slidePx - internally used to know where the "center" point on a marker is relative to the
     * center of it's width and height.
     * {<OpenLayers.Size>}
     */
    slidePx: null,

    /**
     * Property: offset
     * {<OpenLayers.Pixel>} distance in pixels to offset the image when being rendered
     */
    offset: null,

    /**
     * Property: div
     * {DOMElement}
     */
    div: null,

    /**
     * Property: px
     * {<OpenLayers.Pixel>}
     */
    px: null,

    /**
        Internal property used to know whether this object is currently displaying it's popup or not.
       */
    _showingPopup: false,

    /**
        Internal properties used to hold the dom which this icon originally contained, this allows us to
        implement the showPopup method and still provide the user with the ability to restore the
        original view using hidePopup.
       */
    _originalDiv: null,
    _popupDiv: null,
    _popupBox: null,


    /**
     * Constructor: EMS.InteractiveIcon
     * Creates an icon, which is a special div.
     *
     */
    initialize: function(map,options) {
        options = options ? options : {};
        this.options = OpenLayers.Util.extend(this.defaultOptions, options);

        var id = OpenLayers.Util.createUniqueID("EMS_InteractiveIcon_");


        this._showingPopup = false;

        switch (this.options.markerStyle) {
            case EMS.InteractiveMarkerStyles.SLIM:
              this.div = this.createSlimPoi();
              this.size = new OpenLayers.Size(30,33);
              this.slidePx = new OpenLayers.Size(15,24);
              break;
            case EMS.InteractiveMarkerStyles.THICK:
              this.div = this.createThickPoi();
              this.size = new OpenLayers.Size(50,33);
              this.slidePx = new OpenLayers.Size(8,24);
              break;
            case EMS.InteractiveMarkerStyles.TEXT:
              this.div = this.createTextPoi();
              this.size = new OpenLayers.Size(201,50);
              this.slidePx = new OpenLayers.Size(0,20);
              break;
            case EMS.InteractiveMarkerStyles.SLIM_MULTI:
              this.div = this.createSlimMultiPoi();
              this.size = new OpenLayers.Size(30,33);
              this.slidePx = new OpenLayers.Size(15,24);
              break;
            case EMS.InteractiveMarkerStyles.MULTI:
              this.div = this.createThickMultiPoi();
              this.size = new OpenLayers.Size(50,33);
              this.slidePx = new OpenLayers.Size(8,24);
              break;
            case EMS.InteractiveMarkerStyles.SLIM_CIRCLE:
              this.div = this.createSlimCirclePoi();
              this.size = new OpenLayers.Size(20,20);
              this.slidePx = new OpenLayers.Size(2,-26);
            break;
            case EMS.InteractiveMarkerStyles.THICK_CIRCLE:
              this.div = this.createThickCirclePoi();
              this.size = new OpenLayers.Size(25,25);
              this.slidePx = new OpenLayers.Size(2,-35);
            break;

            default:
        }

        this.div.icon = this;
        this._originalDiv = this.div;

        this.map = map;
    },

    /** Internal utility method to create div and append it as a child of another */
    createDiv: function(withClass,appendTo) {
        var elem = document.createElement("div");
        elem.setAttribute("class", withClass);
        if (appendTo) {
            appendTo.appendChild(elem);
        }
        return elem;
    },

    /** internal function to create dom elements for a specific type of poi */
    createMarkerPoi: function() {
        // create main poi elements
        var iconDiv = this.createDiv("marker");

        // poi triangle bottom bits
        var shadowDiv = this.createDiv("marker-shadow",iconDiv);
        var mainShadow = this.createDiv("main-shadow",shadowDiv);
        var triangleShadow = this.createDiv("triangle-shadow",shadowDiv);

        // top bit of poi
        var clipDiv = this.createDiv("clip",iconDiv);

        // elements for the "box"/main bit of the poi
        var boxDiv = this.createDiv("box",clipDiv);
        var insideDiv = this.createDiv("inside",boxDiv);

        if (this.options.text != '') {
            var textDiv = this.createDiv("text",insideDiv);
            textDiv.innerText = this.options.text;
        } else {
            var textDiv = this.createDiv("text point",insideDiv);
            textDiv.innerText = '.';
        }

        // triangular point on the box
        var triangleDiv = this.createDiv("triangle-box",iconDiv);
        var triangleInside = this.createDiv("inside",triangleDiv);

        this.textDiv = textDiv;

        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createSlimPoi: function() {
        var iconDiv = this.createMarkerPoi();
        iconDiv.setAttribute('class','marker mini');
        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createThickPoi: function() {
        var iconDiv = this.createMarkerPoi();
        iconDiv.setAttribute('class','marker fat');
        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createMultiMarkerComponent: function(tehClazz,containsText) {
        var compDiv = this.createDiv(tehClazz);

        // top bit of poi
        var clipDiv = this.createDiv("clip",compDiv);

        // elements for the "box"/main bit of the poi
        var boxDiv = this.createDiv("box",clipDiv);
        var insideDiv = this.createDiv("inside",boxDiv);

        if (containsText) {
            var textDiv = this.createDiv("text point",insideDiv);
            textDiv.innerText = '...';
        }

        // triangular point on the box
        var triangleDiv = this.createDiv("triangle-box",compDiv);
        var triangleInside = this.createDiv("inside",triangleDiv);

        return compDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createMultiMarkerPoi: function() {
        // create main poi elements
        var iconDiv = this.createDiv("marker");

        // poi triangle bottom bits
        var shadowDiv = this.createDiv("marker-shadow",iconDiv);
        var mainShadow = this.createDiv("main-shadow",shadowDiv);
        var triangleShadow = this.createDiv("triangle-shadow",shadowDiv);

        iconDiv.appendChild(this.createMultiMarkerComponent('multi-component back'));
        iconDiv.appendChild(this.createMultiMarkerComponent('multi-component middle'));
        iconDiv.appendChild(this.createMultiMarkerComponent('multi-component front',true));

        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createSlimMultiPoi: function() {
        var iconDiv = this.createMultiMarkerPoi();
        iconDiv.setAttribute('class','marker mini multi');
        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createThickMultiPoi: function() {
        var iconDiv = this.createMultiMarkerPoi();
        iconDiv.setAttribute('class','marker fat multi');
        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createTextPoi: function() {
        // create main poi elements
        var iconDiv = this.createDiv("poi");

        // elements for the "box"/main bit of the poi
        var boxDiv = this.createDiv("box",iconDiv);

        var insideDiv = this.createDiv("inside",boxDiv);

        var addedTitle = false;
        if (this.options.title != '') {
            var titleDiv = this.createDiv("title",insideDiv);
            titleDiv.innerText = this.options.title;
            this.titleDiv = titleDiv;
            addedTitle = true;
        }

        var textDiv = this.createDiv("text",insideDiv);
        textDiv.innerText = this.options.text;
        if (addedTitle) {
            textDiv.setAttribute('class','text with-title');
        }

        // html placeholder for an optional call to action
        // the default css leaves this div empty and invisible but some brands which to use a
        // 'detail disclosure' style icon on the right of the box. The inclusion of this element allows them
        // to style it and the box element in css such that a background image is displayed
        var disclosureDiv = this.createDiv("disclosure",boxDiv);
        this.disclosureDiv = disclosureDiv;

        // triangular point on the box
        var triangleDiv = this.createDiv("triangle-box",iconDiv);
        var triangleInside = this.createDiv("inside",triangleDiv);

        // cover for dodgy gradient edge on android
        var triangleCover = this.createDiv("triangle-cover",iconDiv);

        this.textDiv = textDiv;

        return iconDiv;
    },

    /** internal function to create dom elements for a specific type of poi */
    createPopup: function(contents) {
        // create main poi elements
        var iconDiv = this.createDiv("poi");

        // elements for the "box"/main bit of the poi
        var boxDiv = this.createDiv("box",iconDiv);
        this._popupBox = boxDiv;

        var insideDiv = this.createDiv("inside",boxDiv);

        if (contents) {
            var contentsContainer = this.createDiv("popup-contents",insideDiv);
            contentsContainer.appendChild(contents);
        }

        // triangular point on the box
        var triangleDiv = this.createDiv("triangle-box",iconDiv);
        var triangleInside = this.createDiv("inside",triangleDiv);

        // cover for dodgy gradient edge on android
        var triangleCover = this.createDiv("triangle-cover",iconDiv);

        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createCirclePoi: function() {
        // create main poi elements
        var iconDiv = this.createDiv("non-geo-marker");

        // poi shadow
        var shadowDiv = this.createDiv("marker-shadow",iconDiv);

        // top bit of poi
        var clipDiv = this.createDiv("clip",iconDiv);

        // inside div with fill
        var insideDiv = this.createDiv("inside",iconDiv);

        if (this.options.text != '') {
            var textDiv = this.createDiv("text",insideDiv);
            textDiv.innerText = this.options.text;
        } else {
            var textDiv = this.createDiv("text point",insideDiv);
            textDiv.innerText = '.';
        }

        this.textDiv = textDiv;

        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createSlimCirclePoi: function() {
        var iconDiv = this.createCirclePoi();
        iconDiv.setAttribute('class','non-geo-marker mini');
        return iconDiv;
    },
    /** internal function to create dom elements for a specific type of poi */
    createThickCirclePoi: function() {
        var iconDiv = this.createCirclePoi();
        iconDiv.setAttribute('class','non-geo-marker fat');
        return iconDiv;
    },

    /**
      * Method: showPopup
      * Displays a popup in place of this icon.
      *
      * Parameters:
      * contents: the dom element to be displayed as contents inside this popup
      * moveMap: boolean indicating whether we should try to move the map to accommodate the grown poi
      * popupSize: optional size  to be used rather than the default popup size, this should come from the
      *                  EMS.InteractiveMarkerPopupSizes enum.
       */
    showPopup: function(contents,moveMap,popupSize) {

        // make sure the map doesn't have any weird webkit translation applied before we do this
        // otherwise the calculations become inaccurate.
        if (map.resetPan) {
            this.map.resetPan();
        }

        if (!this.marker) {
            return; // we need a marker for this to work properly , client must set
        }

        if (!this._showingPopup) {
            if (!this._popupDiv) {
                this._popupDiv = this.createPopup(contents);
                var newClass = this._popupDiv.getAttribute('class') + ' popup';
                this._popupDiv.setAttribute('class',newClass);

                if (!popupSize) {
                    popupSize = EMS.InteractiveMarkerPopupSizes.SMALL;
                }

                switch (popupSize) {
                    case EMS.InteractiveMarkerPopupSizes.MINI:
                        this._popupDiv.size = new OpenLayers.Size(202,110);
                        this._popupDiv.slidePx = new OpenLayers.Size(0,54);
                        this._popupBox.setAttribute('class','box mini')
                        break;

                    default:
                    case EMS.InteractiveMarkerPopupSizes.SMALL:
                        this._popupDiv.size = new OpenLayers.Size(202,210);
                        this._popupDiv.slidePx = new OpenLayers.Size(0,104);
                        this._popupBox.setAttribute('class','box small')
                        break;

                    case EMS.InteractiveMarkerPopupSizes.SMALL_WITH_HEADER:
                        this._popupDiv.size = new OpenLayers.Size(202,244);
                        this._popupDiv.slidePx = new OpenLayers.Size(0,118);
                        this._popupBox.setAttribute('class','box small-with-head')
                        break;

                    case EMS.InteractiveMarkerPopupSizes.LARGE:
                        this._popupDiv.size = new OpenLayers.Size(206,246);
                        this._popupDiv.slidePx = new OpenLayers.Size(0,120);
                        this._popupBox.setAttribute('class','box large')
                        break;

                    case EMS.InteractiveMarkerPopupSizes.LARGE_WITH_HEADER:
                        this._popupDiv.size = new OpenLayers.Size(202,280);
                        this._popupDiv.slidePx = new OpenLayers.Size(2,134);
                        this._popupBox.setAttribute('class','box large-with-head')
                        break;
                }

                this._originalDiv.size = this.size;
                this._originalDiv.slidePx = this.slidePx;
            }

            this.fadeIn(this._popupDiv);
            this.fadeOut(this._originalDiv);

            // get rid of the old marker
            this.map.markersLayer.removeMarker(this.marker);

            // reset the size and slide now that we're changing icons
            this.size = this._popupDiv.size;
            this.slidePx = this._popupDiv.slidePx;

            // add the new marker in
            this.div = this._popupDiv;
            this.map.markersLayer.addMarker(this.marker);


            if (moveMap) {
                var moveX = moveY = 0;
                var currentPx = this.map.getPixelFromLonLat(this.marker.lonlat);

                var margin = this.options.popupEdgeMargin;
                var distanceT = currentPx.y - this.size.h;
                var distanceL = currentPx.x - 0.5 * this.size.w;
                var distanceR = this.map.getSize().w - (currentPx.x + 0.5 * this.size.w);

                if (distanceT < margin) {
                    moveY = distanceT - margin;
                }

                if (distanceL < margin ) {
                    moveX = distanceL - margin;
                } else if (distanceR < margin) {
                    moveX =  margin - distanceR;
                }

                this.map.pan(moveX,moveY);
            }

            this._showingPopup = true;
            return this._popupDiv;


        }

        return null;
    },
    /**
      * Method: hidePopup
      * Hides any popup which was displayed in place of this icon
      */
    hidePopup: function() {
        if (this._showingPopup) {
            // get rid of the old marker
            this.map.markersLayer.removeMarker(this.marker);

            // reset the size and slide now that we're changing icons
            this.size = this._originalDiv.size;
            this.slidePx = this._originalDiv.slidePx;

            this.fadeOut(this._popupDiv);
            this.fadeIn(this._originalDiv);

            // add the new marker in
            this.div = this._originalDiv;
            this.map.markersLayer.addMarker(this.marker);

            this._showingPopup = false;
        }
    },

    fadeTween:function(element,fromOpacity,toOpacity) {
        element.style.opacity = fromOpacity;

        var tween = new OpenLayers.Tween(OpenLayers.Easing.Linear.easeIn);
        this.animationTween = tween;
        tween.type = 'fade';

        var from = {opacity: fromOpacity};
        var to = {opacity: toOpacity};

        var duration = this.options.fadeDuration;

        var callbacks = {
            eachStep: function(value) {
                // grab the opacity and make sure it's valid, this lets us set large start values in the tween for later fading
                var opacity = value.opacity;
                if (opacity > 1.0) opacity = 1.0;
                else if (opacity < 0) opacity = 0.0;

                element.style.opacity = opacity;
            }
        }
        tween.start(from, to, duration, {callbacks: callbacks});
    },
    fadeIn:function(element) {
        this.fadeTween(element,0.0,1.0);
    },
    fadeOut:function(element) {
        this.fadeTween(element,1.0,0.0);
    },

    /**
     * Method: destroy
     * Nullify references and remove event listeners to prevent circular
     * references and memory leaks
     */
    destroy: function() {
        // erase any drawn elements
        this.erase();

        OpenLayers.Event.stopObservingElement(this._originalDiv);
        this._originalDiv.innerHTML = "";
        this._originalDiv = null;

        OpenLayers.Event.stopObservingElement(this._popupDiv);
        this._popupDiv.innerHTML = "";
        this._popupDiv = null;
    },


    /**
     * Method: draw
     * Move the div to the given pixel.
     *
     * Parameters:
     * px - {<OpenLayers.Pixel>}
     *
     * Returns:
     * {DOMElement} A new DOM Image of this icon set at the location passed-in
     */
    draw: function(px) {
        this.moveTo(px);
        return this.div;
    },

    /**
     * Method: erase
     * Erase the underlying image element.
     *
     */
    erase: function() {
        if (this.div != null && this.div.parentNode != null) {
            OpenLayers.Element.remove(this.div);
        }
    },

    /**
     * Method: moveTo
     * move icon to passed in px.
     *
     * Parameters:
     * px - {<OpenLayers.Pixel>}
     */
    moveTo: function (px) {
        //if no px passed in, use stored location
        if (px != null) {
            this.px = px;
        }

        if (this.div != null) {
            if (this.px == null) {
                this.display(false);
            } else {
                if (this.calculateOffset) {
                    this.offset = this.calculateOffset(this.size);
                }
                var offsetPx = this.px.offset(this.offset);
                OpenLayers.Util.modifyDOMElement(this.div, null, offsetPx);
            }
        }
    },


    calculateOffset: function() {
        var offset = new OpenLayers.Pixel(-(this.size.w/2) - this.slidePx.w, -(this.size.h/2) - this.slidePx.h);
        return offset;
    },

    /**
     * APIMethod: isDrawn, this method is essentially the same as the standard OpenLayers icon isDrawn
     * with the references to "imageDiv" changed to use "div" which is our equivallent.
      *
     * Returns:
     * {Boolean} Whether or not the icon is drawn.
     */
    isDrawn: function() {
        // nodeType 11 for ie, whose nodes *always* have a parentNode
        // (of type document fragment)
        var isDrawn = (this.div && this.div.parentNode &&
                       (this.div.parentNode.nodeType != 11));

        return isDrawn;
    },


    CLASS_NAME: "EMS.InteractiveIcon"
});

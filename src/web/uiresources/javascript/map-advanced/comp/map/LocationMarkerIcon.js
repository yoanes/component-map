/* Copyright (c) 2006-2008 MetaCarta, Inc., published under the Clear BSD
 * license.  See http://svn.openlayers.org/trunk/openlayers/license.txt for the
 * full text of the license. */


/**
 * Class: EMS.LocationMarkerIcon
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
EMS.LocationMarkerIcon = OpenLayers.Class({
   /**
     * Property: defaultOptions
     * Contains all the default values for everything. Can be overridden on initialization.
     */
    defaultOptions: {
        centerImageLocating: _MapImgPath_ + 'location-center.image',
        centerImageLocated:  _MapImgPath_ + 'location-center.image',
        pulseRadiusWhenFound: 80,
        pulseTime: 150,
        pulseDelay: 2000,
        supportsScale3d: false,
        locatingOpacity: 0.6,
        locatingPulseMinimum: 32,
    },

    /**
     * Property: size
     * {<OpenLayers.Size>}
     */
    size: new OpenLayers.Size(100,100),

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
     * Property: size
     * {<OpenLayers.Size>}
     */
    cssSize: new OpenLayers.Size(100,100),

    /**
     * Constructor: EMS.LocationMarkerIcon
     * Creates an icon, which is a special div.
     *
     */
    initialize: function(map,options) {
        options = options ? options : {};
        this.options = OpenLayers.Util.extend(this.defaultOptions, options);

        this.offset = options.offset ? offset : new OpenLayers.Pixel(-(this.size.w/2), -(this.size.h/2));

        var id = OpenLayers.Util.createUniqueID("EMS_LocationIcon_");

        var markerDiv = document.createElement("div");
        markerDiv.setAttribute("class", "location-marker");

        var strokeDiv = document.createElement("div");
        strokeDiv.setAttribute("class", "stroke");
        markerDiv.appendChild(strokeDiv);

        var gDiv = document.createElement("div");
        gDiv.setAttribute("class", "gradient");
        markerDiv.appendChild(gDiv);

        var imageElem = OpenLayers.Util.createImage(null,null,new OpenLayers.Size(20,23), this.options.centerImageLocating, null, null, null, false);
        imageElem.style.position = 'absolute';
        markerDiv.appendChild(imageElem);

        // hold on to all our important components for later scaling/function changes
        this.div = markerDiv;
        this.gradientDiv = gDiv;
        this.strokeDiv = strokeDiv;
        this.imageElem = imageElem;

        // init map
        this.map = map;
    },

    /**
     * Method: destroy
     * Nullify references and remove event listeners to prevent circular
     * references and memory leaks
     */
    destroy: function() {
        // erase any drawn elements
        this.erase();

        OpenLayers.Event.stopObservingElement(this.div.firstChild);
        this.div.innerHTML = "";
        this.div = null;
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


     /**
        * Method: doLocatedAnimationStep
        * does a single step of the located animation (internal).
        */
    doLocatedAnimationStep: function() {
        var tween = new OpenLayers.Tween(OpenLayers.Easing.Linear.easeIn);
        this.animationTween = tween;
        tween.type = 'located';

        var from = {scaleAmt:0, opacity: 3.0};
        var to = {scaleAmt:this.options.pulseRadiusWhenFound / this.cssSize.w, opacity: 0.0};

        var duration = this.options.pulseTime;

        var myIcon = this;

        var callbacks = {
            eachStep: function(value) {
                // grab the opacity and make sure it's valid, this lets us set large start values in the tween for later fading
                var opacity = value.opacity;
                if (opacity > 1.0) opacity = 1.0;
                myIcon.setBackgroundOpacity(opacity);
                myIcon.scaleBy(value.scaleAmt);
            },
            done: function() {
                myIcon.animationTimeout = setTimeout(function(){myIcon.doLocatedAnimationStep();},myIcon.options.pulseDelay);
            }
        }
        tween.start(from, to, duration, {callbacks: callbacks});
    },
     /**
        * Method: startLocatedAnimation
        * causes the location marker to start it's "located" animation.
        */
    startLocatedAnimation: function() {
        this.stopAnimations();
        this.imageElem.src = this.options.centerImageLocated;
        this.doLocatedAnimationStep();
    },

     /**
        * Method: doLocatingAnimationStep
        * internal - animate a step of the "finding" animation. (internal)
        */
    doLocatingAnimationStep: function() {
        var tween = new OpenLayers.Tween(OpenLayers.Easing.Linear.easeIn);
        this.animationTween = tween;
        tween.type = 'locating';

        var pulseAmt = 8;
        if (!this.nextFindAnimationShouldGrow) {
            pulseAmt = pulseAmt * -1;
            this.nextFindAnimationShouldGrow = true;
        } else {
            this.nextFindAnimationShouldGrow = false;
        }
        var from = {scale:this.lastAnimatedAccuracy - pulseAmt};
        var to = {scale:this.locationAccuracy + pulseAmt};

        this.lastAnimatedAccuracy = this.locationAccuracy;
        this.lastAnimatedResolution = this.map.getResolution();

        var duration = 100;

        var myIcon = this;

        var callbacks = {
            eachStep: function(value) {
                if (myIcon.map.getResolution() == myIcon.lastAnimatedResolution) {
                    myIcon.scaleBy(value.scale / myIcon.cssSize.w);
                } else {
                    myIcon.stopAnimations();
                    myIcon.animationTimeout = setTimeout(function(){myIcon.doLocatingAnimationStep();},10);
                }
            },
            done: function() {
                myIcon.animationTimeout = setTimeout(function(){myIcon.doLocatingAnimationStep();},10);
            }
        }
        tween.start(from, to, duration, {callbacks: callbacks});
    },


    /**
         Used internally to update the pixels used for animations based on teh current zoom.
         newVal - new accuracy in meters.
       */
    setAccuracyPixelsBasedOnScale: function() {
        // convert our degrees scale to pixels, divide by 1000 to get km's then by 111.12 km per degree
        this.locationAccuracy = this.locationAccuracyInMeters / 1000 / 111.12 / this.map.getResolution();
        if (this.locationAccuracy < this.options.locatingPulseMinimum) {
            this.locationAccuracy = this.options.locatingPulseMinimum;
        }
    },
    /**
         Updates the determined accuracy for this location marker. This affects the size of the animated "blips" shown.
         newVal - new accuracy in meters.
       */
    setLocationAccuracy: function(newVal) {
        this.locationAccuracyInMeters = newVal;
        this.setAccuracyPixelsBasedOnScale();
    },

     /**
        * Method: startLocatingAnimation
        * causes the location marker to start it's "locating"/finding animation.
        */
    startLocatingAnimation: function() {
        this.stopAnimations();

        if (this.locationAccuracy > 0) {
        this.imageElem.src = this.options.centerImageLocating;
            this.setBackgroundOpacity(this.options.locatingOpacity);

            this.lastAnimatedAccuracy = this.locationAccuracy;
            this.nextFindAnimationShouldGrow = true;
            this.doLocatingAnimationStep();
        }
    },

     /**
        * Method: stopAnimations
        * stops any currently playing animations.
        */
    stopAnimations: function() {
        if (this.animationTween) {
            this.lastAnimationTween = this.animationTween;
            this.animationTween = null;
            this.lastAnimationTween.stop();
        }
        if (this.animationTimeout) {
            clearTimeout(this.animationTimeout);
            this.animationTimeout = null;
        }

    },
     /**
        * Method: stopAnimations
        * re-starts any currently playing animations which were stopped.
        */
    startAnimations: function() {
        this.animationTween = this.lastAnimationTween;
        if (this.animationTween) {
            this.animationTween.start(this.animationTween.begin,this.animationTween.finish,this.animationTween.duration,{callbacks: this.animationTween.callbacks});
            //this.animationTween.play();
        }
    },


    /**
      * Method: setBackgroundOpacity
      * sets the background gradient opacity to help with our animations.
      */
    setBackgroundOpacity: function(opacity) {
        this.gradientDiv.style.opacity = opacity;
    },


     /**
        * Method: scaleBy
        * scales the rendered output of some of the svg components, used internally in the various animations.
        */
    scaleBy: function(scaleAmt) {
        // scale3d works best on iPhone, doesn't work on android
        if (this.options.supportsScale3d) {
            this.gradientDiv.style.webkitTransform = 'scale3d(' + scaleAmt + ',' + scaleAmt + ',' + scaleAmt + ')';
        } else {
            this.gradientDiv.style.webkitTransform = 'scale(' + scaleAmt + ')';
        }
    },


    CLASS_NAME: "EMS.LocationMarkerIcon"
});

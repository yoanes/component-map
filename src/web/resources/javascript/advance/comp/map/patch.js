/**
 * Patch file for checkerboard background in mobile profile 
 */



EMS.BackgroundLayer = OpenLayers.Class( EMS.IndexedLayer, {

    DEFAULT_PARAMS: {service: "EMS", version: "1.1.1"},

    initialize: function(name, url, params, options) {
        EMS.IndexedLayer.prototype.initialize.apply(this, arguments);
        this.div.background='url("' + EMS.BackgroundLayer.TILE + '")'; 
    },

    getURL: function (bounds) {
        return this.getFullRequestString({});
    },
    
    setVisibility: function(visibility) {
         this.visibility = true;
         this.display(true);
         this.redraw();
    },

    CLASS_NAME: "EMS.BackgroundLayer"

});

EMS.BackgroundLayer.TILE = EMS.Util.getImagesLocation() + "tile_bg_200x200.gif";  
EMS.BackgroundLayer.prototype.DEFAULT_PARAMS = {service: "MOB", version: "1.1.1"};

OpenLayers.Renderer.SVG.prototype.supported = function() {
        var svgFeature = "http://www.w3.org/TR/SVG11/feature#";
        return (document.implementation && 
           (document.implementation.hasFeature("org.w3c.svg", "1.0") || 
            document.implementation.hasFeature(svgFeature + "SVG", "1.1") || 
            document.implementation.hasFeature(svgFeature + "BasicStructure", "1.1") ));
};

EMS.Services.Map.prototype.initialize = function (div, options) {

    this.active = true;
    this.tilePath = EMS.tilePath;

    options = options ? options : {};

    options = OpenLayers.Util.extend(
        {
            maxExtent: new OpenLayers.Bounds(100, -50, 170, -3),
            maxResolution: 0.1,
            numZoomLevels: 17,
            miniControls: false,
            showMapPhotoButtons: true,
            showMaxExtent: true
        }, options);

    var addControlsAfterLayer = false;
    if (options.controls == null) {
        options.controls = [];
        addControlsAfterLayer = true;
    }

    OpenLayers.Map.prototype.initialize.apply(this, [div, options]);

    var tilePath;
    // tilePath can either be a single url or an array of urls
    if (EMS.tilePath instanceof Array) {
        tilePath = new Array(EMS.tilePath.length);
        for ( var i = 0; i < EMS.tilePath.length; i++) {
            tilePath[i] = EMS.tilePath[i] + "/tile?";
        }
    }
    else {
        tilePath = EMS.tilePath + "/tile?";
    }
    
    if (options.layers == null) {

        // added bg layer, and set it visible        
        this.bgLayer = new EMS.BackgroundLayer("Background tiles",
        EMS.BackgroundLayer.TILE, {layers: 'background', format: "image/gif", CACHE: "TRUE", VERSION: '1.0.5'}, {displayOutsideMaxExtent: true, visible: true});
        this.bgLayer.isBaseLayer = true;
        this.bgLayer.setVisibility(true);
        this.whereis_street_wms = new EMS.IndexedLayer("Whereis Street",
        tilePath, {layers: 'street', format: "image/gif", TRANSPARENT: true, CACHE: "TRUE", VERSION: '1.0.5'}, {displayOutsideMaxExtent: true, visible: true, transparent: true});
        this.whereis_street_wms.setVisibility(true);
        this.whereis_street_wms.transitionEffect = "resize";
        
        this.whereis_photo_wms = new EMS.IndexedLayer("Whereis Photo",
                tilePath, {layers: 'photo', format: "image/jpg", TRANSPARENT: true, CACHE: "TRUE", VERSION: '1.0.5'}, {displayOutsideMaxExtent: true, visible: false, transparent: true});
        this.whereis_photo_wms.setVisibility(false);
        this.whereis_photo_wms.transitionEffect = "resize";
        this.whereis_hybrid_wms = new EMS.IndexedLayer("Whereis Hybrid",
                tilePath, {layers: 'hybrid', format: "image/gif", TRANSPARENT: true, CACHE: "TRUE", VERSION: '1.0.5'}, {displayOutsideMaxExtent: true, transparent: true});
        this.whereis_hybrid_wms.setVisibility(false);
        this.whereis_hybrid_wms.transitionEffect = "resize";

        this.vlayer = new EMS.Layer.Vector("Route", {
            renderers:["Canvas", "SVG", "VML"]
        });

        this.markersLayer = new OpenLayers.Layer.Markers( "Markers" );

        if (this.vlayer!= null) {
            this.addLayers([this.bgLayer, this.whereis_street_wms, this.whereis_photo_wms, this.whereis_hybrid_wms,
                    this.vlayer, this.markersLayer]);
        } else {
            this.addLayers([this.bgLayer, this.whereis_street_wms, this.whereis_photo_wms, this.whereis_hybrid_wms,
                    this.markersLayer]);
        }
        
    }

    this.setBaseLayer(this.bgLayer, false);

    if (addControlsAfterLayer) {
        this.addControl( this.zoombar = new EMS.Control.ZoomBar(
                        this.whereis_street_wms,
                        this.whereis_photo_wms,
                        this.whereis_hybrid_wms, !options.miniControls, options.showMapPhotoButtons));
        this.addControl( this.mouseDefaults = new EMS.Control.MouseDefaults() );
        this.addControl( this.panNorth = new EMS.Control.PanButton('n') );
        this.addControl( this.panSouth = new EMS.Control.PanButton('s') );
        this.addControl( this.panEast = new EMS.Control.PanButton('e') );
        this.addControl( this.panWest = new EMS.Control.PanButton('w') );
        this.addControl( this.copyRight = new EMS.Control.Copyright({smallCopyright: options.miniControls}) );
        this.addControl( this.scalebar = new EMS.Control.Scale());

        var ovLayer = new EMS.IndexedLayer("Street",
                tilePath, {layers: 'street', format: "image/gif", cache: "TRUE", version: '1.0.5'}, {displayOutsideMaxExtent: true, visible: true});
        ovLayer.buffer = 0;
        this.addControl( this.overviewmap = new EMS.Control.OverviewMap( {layers: [ovLayer], mapOptions: {maxResolution: 1.6, numZoomLevels: 21, maxExtent: new OpenLayers.Bounds(100, -50, 170, -3)}} ) );
    }

    
    if(this.center == undefined && options.showMaxExtent ){
            this.zoomToMaxExtent();
    }

    var callback = OpenLayers.Function.bind(function(results) {
        if (results.authorized) {
            // ping every 5 minutes
            if (!options.noping)
                this.timeoutVar = setTimeout(OpenLayers.Function.bind(this.ping, this), this.pingPeriod);

            if (options.onInit) {
                options.onInit(this);
            }
        } else {
            EMS.token = null;
            this.destroy();
            alert("Authentication problem");
        }
    }, this);
    var domain = EMS.Util.getDomain();
    EMS.Ajax.json("/json/auth/init", callback, {parameters:{token: EMS.token, domain: domain}});
};

OpenLayers._scriptName = "EMS.js";
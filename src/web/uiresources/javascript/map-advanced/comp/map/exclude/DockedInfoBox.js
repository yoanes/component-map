/**
 * Class: EMS.DockedInfoBox
 *
 * Docked Information box which displays html content. 
 *
 * Inherits from:
 *  - <OpenLayers.Control>
 */
EMS.Control.DockedInfoBox = OpenLayers.Class( OpenLayers.Control, {

    /** Property: element */
    element: null,

  /** Property contents
     *   - internally used to hold our contents which can be either a single dom element or an array of them
     */
    contents: null,

    /** internal counter to keep track of the current item we're showing if the contents was an array */
    contentIndex: null,

    /** references to our next/prev elements if present */
    prevDiv:null,
    nextDiv:null,

    /** holds a reference to an optionally provided callback function which will be called when a user
        * navigates back/forward through the items in this docked box. This gives callers the ability to highlight
        * pois or do anything like that
        */
     contentsChangedCallback: null,

    /**
     * Constructor: EMS.Control.DockedInfoBox
     * Returns a new docked box control.
     *
     * Parameters:
     *  options:  {<DOMElement>} Options for control.
     */
    initialize: function(options) {
        OpenLayers.Control.prototype.initialize.apply(this, arguments);
    },

    /**
     * APIMethod: destroy
     * Cleanly delete the resources used by this control.
     *
     *
     */
    destroy: function() {
        OpenLayers.Event.stopObservingElement(this.prevDiv);
        OpenLayers.Event.stopObservingElement(this.nextDiv);

        OpenLayers.Control.prototype.destroy.apply(this, arguments);
        OpenLayers.Event.stopObservingElement(this.div);
    },

   /**
     * Function setContents
     *
     * Sets up this box to show the given contents.

      * Parameter:
     * {DOMElement} contents - a single dom contents piece
     *   OR
     * {Array} contents - a bunch of contents which will be displayed with forward/back controls
     *
     * <OPTIONAL> contentsChangedCallback - method to be called when contents are changed using prev/next.
     */
    setContents:function(contents, contentsChangedCallback) {
        if (contents instanceof Array) {
            // set our index at the start of the array
            this.contentIndex = 0;

            if (contentsChangedCallback && contentsChangedCallback instanceof Function) {
                this.contentsChangedCallback = contentsChangedCallback;
            }
        }

        this.contents = contents;
    },


    /** Internal utility to load the contents at a given index in the contents array */
    loadContentsForIndex:function(idx) {
        if (idx < 0) idx = 0;
        if (idx >= this.contents.length) idx = this.contents.length - 1;

        this.contentIndex = idx;
        this.contentDiv.innerHTML = '';
        this.contentDiv.appendChild(this.contents[this.contentIndex]);

        var prevClass = (idx == 0)?'prev inactive':'prev';
        var nextClass = (idx == this.contents.length - 1)?'next inactive':'next';
        this.prevDiv.setAttribute('class',prevClass);
        this.nextDiv.setAttribute('class',nextClass);

        if (this.contentsChangedCallback) {
            this.contentsChangedCallback(idx);
        }
    },

   /**
     * APIMethod: draw.
     *
     * Returns:
     * {DOMElement} div containing control.
     */
    draw: function() {
        OpenLayers.Control.prototype.draw.apply(this, arguments);

        if (!this.element) {
            this.div.className = 'dock';

            var inside = document.createElement("div");
            inside.setAttribute('class','inside');
            this.div.appendChild(inside);
            this.inside = inside;

            if (this.contents instanceof Array) {
                // add previous and next buttons to our element
                var btn = document.createElement("div");
                btn.setAttribute('class','prev');
                this.inside.appendChild(btn);

                var btnIcon = document.createElement("div");
                btnIcon.setAttribute('class',"icon");
                btn.appendChild(btnIcon);

                this.prevDiv = btn;
            }

            var contentDiv = document.createElement("div");
            contentDiv.setAttribute('class','content');
            this.inside.appendChild(contentDiv);
            this.contentDiv = contentDiv;

            if (this.contents instanceof Array) {
                var btn = document.createElement("div");
                btn.setAttribute('class','next');
                this.inside.appendChild(btn);

                var btnIcon = document.createElement("div");
                btnIcon.setAttribute('class',"icon");
                btn.appendChild(btnIcon);

                this.nextDiv = btn;

               this.prevDiv.addEventListener('touchend', function() {
                        this.loadContentsForIndex(this.contentIndex - 1);
                }.bind(this), false);
                this.nextDiv.addEventListener('touchend', function() {
                        this.loadContentsForIndex(this.contentIndex + 1);
                }.bind(this), false);


//                this.prevDiv.events = new OpenLayers.Events(this.prevDiv, this.prevDiv, null);
//                this.prevDiv.events.register('click',this,function() {
//                    this.loadContentsForIndex(this.contentIndex - 1);
//                });
//
//                this.nextDiv.events = new OpenLayers.Events(this.nextDiv, this.nextDiv, null);
//                this.nextDiv.events.register('click',this,function() {
//                    this.loadContentsForIndex(this.contentIndex + 1);
//                });

                this.loadContentsForIndex(0);
            } else {
                this.contentDiv.appendChild(this.contents);
            }
        }

        return this.div;
    },

    CLASS_NAME: "EMS.Control.DockedInfoBox"
});

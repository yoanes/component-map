PATH_TO_TILES = _MapImgPath_;

OpenLayers.Util.onImageLoadError = function() {
    this._attempts = (this._attempts) ? (this._attempts + 1) : 1;
    if(this._attempts <= OpenLayers.IMAGE_RELOAD_ATTEMPTS) {
        this.src = this.src;
    } else {

        var sizeLabel = "";

        if (OpenLayers.Map.TILE_WIDTH == 200) {
            sizeLabel = "_200x200";
        }

        if(this.src.indexOf("LAYERS=street") >= 0){
            this.src = PATH_TO_TILES + "no_data_map" + sizeLabel + ".png";
        } else{
            this.src = PATH_TO_TILES + "no_data" + sizeLabel + ".png";
        }
    }
    this.style.display = "";
};
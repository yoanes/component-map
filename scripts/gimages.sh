#!/bin/bash
#
# Content Rendering Framework script for generating scaled images.
#
# CRF Version: 2.0.0
#
# $Revision$
#
# Note that this script was developed to run on multiple platforms, including Windows. As such, you will notice a 
# common idiom of '|tr -d "\r"' is used to strip out carriage return characters that Windows is so fond of.

#set -x

# ==============================================================================
# Signal traps.

trap "echo -e \"\n\nCleanup ...removing temporary files ... \" && rm -f $$.tmp*" EXIT

# ==============================================================================
# Source includes.

scriptDir=`dirname "$0"`
source "$scriptDir/common.sh"

# ==============================================================================
# Define default vars.
debug=1
debugFile=$$.tmp.debug

lastRunPropertiesFile="gimages-last-run.properties"

defaultUiResourcesDir="src/web/uiresources"
uiResourcesDir="$defaultUiResourcesDir"

defaultPixelsIncrement=5
pixelsIncrement="$defaultPixelsIncrement"

defaultMinimumPixels=5
minimumPixels="$defaultMinimumPixels"

defaultOverwriteWithoutPrompting=false
overwriteWithoutPrompting="$defaultOverwriteWithoutPrompting"

defaultImageBasepath=""
imageBasepath="$defaultImageBasepath"

# ==============================================================================
# Functions.

function echoUsedVariables {
    $echoCmd "Using variables:"
    displayUsedVariable "uiResourcesDir" "$uiResourcesDir" "$defaultUiResourcesDir"
    displayUsedVariable "pixelsIncrement" "$pixelsIncrement" "$defaultPixelsIncrement"
    displayUsedVariable "minimumPixels" "$minimumPixels" "$defaultMinimumPixels"
    displayUsedVariable "imageBasepath" "$imageBasepath" "$defaultImageBasepath"
    displayUsedVariable "overwriteWithoutPrompting" "$overwriteWithoutPrompting" "$defaultOverwriteWithoutPrompting"
    $echoCmd 
}

function writeLastRunPropertiesFile {
    $catCmd <<END > $uiResourcesDir/images/$lastRunPropertiesFile
date=`date +"%d/%m/%Y %k:%M:%S"`
uiResourcesDir=$uiResourcesDir
pixelsIncrement=$pixelsIncrement
minimumPixels=$minimumPixels
overwriteWithoutPrompting=$overwriteWithoutPrompting
END
    $echoCmd ""
    $echoCmd "Generated $uiResourcesDir/images/$lastRunPropertiesFile"
}

function getImageDimensions {
    local imagePath="$1"
    $identifyCmd $imagePath |cut -f 3 -d " " |tr -d "\r"
}

function getWidthFromImageDimensions {
    local imageDimensions="$1"
    $echoCmd -n $imageDimensions |cut -f 1 -d "x" |tr -d "\r"
}

function getHeightFromImageDimensions {
    local imageDimensions="$1"
    $echoCmd -n $imageDimensions |cut -f 2 -d "x" |tr -d "\r"
}

function usage {
    $echoCmd "Usage: $0 [-r <uiresources directory>] [-f <extra find expression] [-i <pixels increment between images>] [-m <minimum pixel width of images>] [-o]"
}

function getExtension {
    local filePath="$1"
    $exprCmd "$filePath" ':' '.*\(\.[A-Za-z][A-Za-z][A-Za-z]\)$'
}

function scaleImages {
    local basePath="$1"

    if [ -z "$basePath" ]
    then
        local sourceImages=`findAllSourceImagesToScale`
    else
        local sourceImages=`findSourceImagesToScaleFromBasePath "$basePath"`
    fi

    if [ -z "$sourceImages" ]
    then
        $echoCmd "No images found."
        return 0
    fi

    if [ "$debug" -eq 0 ]
    then
        $echoCmd >> "$debugFile"
        $echoCmd "Source images: $sourceImages" >> "$debugFile"
    fi

    for currImage in $sourceImages
    do
        local imageBaseName=`basename $currImage |tr -d "\r"`
        local imageDimensions=`getImageDimensions $currImage`
        local imageWidth=`getWidthFromImageDimensions $imageDimensions`
        local imageHeight=`getHeightFromImageDimensions $imageDimensions` 
        local currImageDir=`dirname $currImage |tr -d "\r"`

        $echoCmd "Processing image: \"$currImage\""
        if [ "$debug" -eq 0 ]
        then
            $echoCmd "imageBaseName: \"$imageBaseName\"" >> "$debugFile"
            $echoCmd "imageDimensions: \"$imageDimensions\"" >> "$debugFile"
            $echoCmd "width: \"$imageWidth\"" >> "$debugFile"
            $echoCmd "height: \"$imageHeight\"" >> "$debugFile"
            $echoCmd "currImageDir: \"$currImageDir\"" >> "$debugFile"
        fi

        local newImageWidth=$(($imageWidth - $pixelsIncrement))
        while [ $newImageWidth -gt $minimumPixels ]
        do
            scaleSingleImage $currImage $imageBaseName $currImageDir $newImageWidth 

            if [ `getExtension $currImage` = ".png" -o `getExtension $currImage` = ".PNG" ]
            then
                currGifImageWithLowerCaseExtension=${currImage%.*}.gif
                currGifImageWithUpperCaseExtension=${currImage%.*}.GIF
                if [ ! -e "$currGifImageWithLowerCaseExtension" -a ! -e "$currGifImageWithUpperCaseExtension" ]
                then
                    scaleSingleImage $currImage $imageBaseName $currImageDir $newImageWidth "gif"
                fi
            fi

            local newImageWidth=$(($newImageWidth - $pixelsIncrement))
            
            # TODO: temp pause for debugging.
            #read blah
        done

        # Generate a hash of the image just processed. This can be later used to tell if the image is
        # consistent with the last time that the images were generated.
        # We use the openssl command for better portability between linux and Mac OS X. The latter
        # lacks md5sum and the md5 command that it ships with is very basic.  
        currImageHashFile="${currImage}.md5"
        computeMd5 $currImage > $currImageHashFile
        $echoCmd "Generated hash file \"$currImageHashFile\""
        $echoCmd ""

        # TODO: temp pause for debugging.
        #read blah
    done
 
}

function scaleSingleImage {
    local currImage="$1"
    local imageBaseName="$2"
    local currImageDir="$3"
    local newImageWidth="$4"
    local outputExtension="$5"

    if [ -z "$outputExtension" ]
    then
        local tempFile=$$.tmpImage
    else
        local tempFile=$$.tmpImage.$outputExtension
    fi

    if [ `getExtension $currImage` = ".png" -o `getExtension $currImage` = ".PNG" -a \( "$outputExtension" = ".gif" -o "$outputExtension" = ".GIF" \) ]
    then
        # Look for optional GIF background color property.
        local currImageLocalPropertiesFile=${currImage%.*}.properties
        if [ -e $currImageLocalPropertiesFile ]
        then
            local backgroundColorLine=`grep "background.color" $currImageLocalPropertiesFile`
            local backgroundColor=${backgroundColorLine#background.color=}
        fi
        if [ -z "$backgroundColor" ]
        then
            local backgroundColor=#FFFFFF
        fi
    fi

    if [ "$debug" -eq 0 ]
    then
        $echoCmd "background.color value: '$backgroundColor'" 
    fi

    # Resize the image to a temp file first because we want to inspect its dimensions
    # to create the real output path. We already know what the width will be but not the
    # height. We _could_ try calculating it ourselves but 1) calculations in bash are a 
    # pain and 2) we might be off by +/-1px if we calculated it.
    if [ -z "$backgroundColor" ]
    then
        $convertCmd -resize ${newImageWidth}x -unsharp 0x1 $currImage $tempFile
    else
        $convertCmd -background "$backgroundColor" "$currImage" -resize ${newImageWidth}x -unsharp 0x1 -extent 0x0 "$tempFile"
    fi

    local newImageDimensions=`getImageDimensions $tempFile`
    local newImageHeight=`getHeightFromImageDimensions $newImageDimensions`

    local outputImagePath="$currImageDir/w$newImageWidth/h$newImageHeight/$imageBaseName"
    if [ -n "$outputExtension" ]
    then
        local outputImagePath="${outputImagePath%.*}.$outputExtension"
    fi

    mkdir -p `dirname $outputImagePath |tr -d "\r"`
    if [ "$overwriteWithoutPrompting" = "true" ]
    then
        mv -f $tempFile $outputImagePath
    else 
        mv $tempFile $outputImagePath
    fi
    
    $echoCmd "Generated image \"$outputImagePath\""
}

# ==============================================================================
# Processing

while getopts ":r:i:m:p:ohd" option 
do  case "$option" in
        r) uiResourcesDir=$OPTARG
           ;;
        i) pixelsIncrement=$OPTARG
           ;;
        m) minimumPixels=$OPTARG
           ;;
        p) imageBasepath=$OPTARG
           ;;
        o) overwriteWithoutPrompting=true
           ;;
        d) debug=0
           ;;
        h|*) usage
           exit 1
           ;;
    esac
done

echoUsedVariables
scaleImages "$imageBasepath"
writeLastRunPropertiesFile

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="base" uri="/au/com/sensis/mobile/web/component/core/base/base.tld"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<c:set var="device" value="${requestScope['mapComponentDevice']}" />
<c:set var="map" value="${requestScope['mapComponentMap']}" />
<c:set var="zoomInUrl" value="${requestScope['mapComponentZoomInUrl']}" />
<c:set var="zoomOutUrl" value="${requestScope['mapComponentZoomOutUrl']}" />
<c:set var="panNorthUrl" value="${requestScope['mapComponentPanNorthUrl']}" />
<c:set var="panSouthUrl" value="${requestScope['mapComponentPanSouthUrl']}" />
<c:set var="panEastUrl" value="${requestScope['mapComponentPanEastUrl']}" />
<c:set var="panWestUrl" value="${requestScope['mapComponentPanWestUrl']}" />
<c:set var="photoLayerUrl" value="${requestScope['mapComponentPhotoLayerUrl']}" />
<c:set var="mapLayerUrl" value="${requestScope['mapComponentMapLayerUrl']}" />
<c:set var="clientSideGeneratedMapStateChangeUrl" value="${requestScope['mapComponentClientSideGeneratedMapStateChangeUrl']}" />

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="au.com.sensis.mobile.web.component.map.map-component" />    

<c:if test="${not empty map && map.mapImageRetrieved}"> 

    <a href="#map" name="map"></a>
    <div id="mapWindow">    
        <img src="${map.mapUrl.imageUrl}" id="map" />
    </div>
    <div id="mapControls">
        <div id="zoomControls">
            <crf:img device="${context.device}" src="comp/map/controls/mc_in_g.image" id="zoomInFaded" alt="+"
            ></crf:img
            ><a id="zoomInButton" href="${zoomInUrl}" class="mapControl"
                ><crf:img device="${context.device}" src="comp/map/controls/mc_in.image" id="mapZoomIn" alt="+"
                ></crf:img
            ></a><crf:img device="${context.device}" src="comp/map/controls/mc_out_g.image" id="zoomOutFaded" alt="-"
            ></crf:img
            ><a id="zoomOutButton" href="${zoomOutUrl}" class="mapControl"
                ><crf:img device="${context.device}" src="comp/map/controls/mc_out.image" id="mapZoomOut" alt="-"
                ></crf:img
            ></a
        ></div
        ><div id="directionControls"
            ><a id="panNorthButton" href="${panNorthUrl}" class="mapControl"
                ><crf:img device="${context.device}" src="comp/map/controls/mc_up.image" id="mapPanNorthImage" alt=" /\ "
                ></crf:img
            ></a
            ><a id="panSouthButton" href="${panSouthUrl}" class="mapControl"
                ><crf:img device="${context.device}" src="comp/map/controls/mc_down.image" id="mapPanSouthImage" alt=" \/ "
                ></crf:img
            ></a
            ><a id="panWestButton" href="${panWestUrl}" class="mapControl"
                ><crf:img device="${context.device}" src="comp/map/controls/mc_left.image" id="mapPanWestImage" alt=" &#60; "
                ></crf:img
            ></a
            ><a id="panEastButton" href="${panEastUrl}" class="mapControl"
                ><crf:img device="${context.device}" src="comp/map/controls/mc_right.image" id="mapPanEastImage" alt=" &#62; "
                ></crf:img
            ></a>
        </div>
    
        <div id="viewControls">
    		<c:set var="photoLayerLabel">
                <fmt:message key="comp.photoLayer.label"/>
            </c:set>
                     
            <a id="photoButton" href="${photoLayerUrl}">
                <crf:img device="${context.device}" src="comp/map/controls/photo.image" alt="${photoLayerLabel}"></crf:img>
            </a>
           
            <c:set var="mapLayerLabel">
                <fmt:message key="comp.mapLayer.label"/>
            </c:set>
                 
            <a id="mapButton" href="${mapLayerUrl}">
                <crf:img device="${context.device}" src="comp/map/controls/map.image" alt="${mapLayerLabel}"></crf:img>
            </a>          
        </div>
    </div>
</c:if>
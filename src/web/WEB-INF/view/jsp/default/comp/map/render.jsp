<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="base" uri="/au/com/sensis/mobile/web/component/core/base/base.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/core/logging/logging.tld"%>
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

<%-- Cater to less funky phones that need the server to generate the map. --%>
<c:if test="${not empty map}"> 
	<div id="mapWindow">     
    	<img src="${map.mapUrl.imageUrl}" id="map" />
	</div>
   
    <div id="mapControls"
        ><div id="mapZoomControls"
            ><c:choose>
               <c:when test="${map.zoomDetails.atMinimumZoom}"
                    ><crf:img device="${device}" src="comp/map/controls/mc_in_g.image" id="mapZoomInFaded">+</crf:img
               ></c:when>
               <c:otherwise
                    ><a id="mapZoomInButton" href="${zoomInUrl}" class="mapControl"
                        ><crf:img device="${device}" src="comp/map/controls/mc_in.image" id="mapZoomIn"
                        >+</crf:img></a
               ></c:otherwise>
            </c:choose>
            
            <c:choose>
               <c:when test="${map.zoomDetails.atMaximumZoom}"
                    ><crf:img device="${device}" src="comp/map/controls/mc_out_g.image" id="mapZoomOutFaded">-</crf:img
               ></c:when>
               <c:otherwise
                    ><a id="mapZoomOutButton" href="${zoomOutUrl}" class="mapControl"
                        ><crf:img device="${device}" src="comp/map/controls/mc_out.image" id="mapZoomOut"
                        >-</crf:img></a
               ></c:otherwise>
            </c:choose
        ></div
        ><div id="mapDirectionControls"
            ><a id="mapPanNorthButton" href="${panNorthUrl}" class="mapControl"
                ><crf:img device="${device}" src="comp/map/controls/mc_up.image" id="mapPanNorthImage"
                > /\ </crf:img
            ></a
            ><a id="mapPanSouthButton" href="${panSouthUrl}" class="mapControl"
                ><crf:img device="${device}" src="comp/map/controls/mc_down.image" id="mapPanSouthImage"
            > \/ </crf:img
            ></a
            ><a id="mapPanWestButton" href="${panWestUrl}" class="mapControl"
                ><crf:img device="${device}" src="comp/map/controls/mc_left.image" id="mapPanWestImage"
            > &#60; </crf:img
            ></a
            ><a id="mapPanEastButton" href="${panEastUrl}" class="mapControl"
                ><crf:img device="${device}" src="comp/map/controls/mc_right.image" id="mapPanEastImage"
                > &#62; </crf:img
            ></a
        ></div
        ><jsp:include page="/WEB-INF/view/jsp/comp/map/viewControls.crf"
    /></div>
</c:if>

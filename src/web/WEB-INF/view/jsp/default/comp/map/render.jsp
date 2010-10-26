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

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />    
<logging:debug logger="${logger}" message="Entering render.tag" />

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="au.com.sensis.mobile.web.component.map.map-component" />    

<%-- Figure out the name of the current component.--%>
<c:set var="componentName">
    <fmt:message key="comp.name" />
</c:set>

<%-- Cater to less funky phones that need the server to generate the map. --%>

<c:if test="${not empty map}"> 
	<div id="mapWindow">     
    	<crf:img device="${context.device}" src="${map.mapUrl.imageUrl}" id="map"></crf:img>
	</div>
   
    <div id="mapControls">
        <div id="zoomControls">
            <c:choose>
               <c:when test="${map.zoomDetails.atMinimumZoom}">
                    <crf:img device="${context.device}" src="/comp/map/images/zoomIn_faded.mimg" id="mapZoomInFaded">+</crf:img>
               </c:when>
               <c:otherwise>
                    <a id="zoomInButton" href="${zoomInUrl}" class="mapControl">
                        <crf:img device="${context.device}" src="/comp/map/images/zoomIn.mimg" id="mapZoomIn">+</crf:img>
                    </a>
               </c:otherwise>
            </c:choose>
            
            <c:choose>
               <c:when test="${map.zoomDetails.atMaximumZoom}">
                    <crf:img device="${context.device}" src="/comp/map/images/zoomOut_faded.mimg" id="mapZoomOutFaded">-</crf:img>
               </c:when>
               <c:otherwise>
                    <a id="zoomOutButton" href="${zoomOutUrl}" class="mapControl">
                        <crf:img device="${context.device}" src="/comp/map/images/zoomOut.mimg" id="mapZoomOut">-</crf:img>
                    </a>
               </c:otherwise>
            </c:choose>
        </div>
            

        <div id="directionControls">
            <a id="panNorthButton" href="${panNorthUrl}" class="mapControl">
                <crf:img device="${context.device}" src="/comp/map/images/north.mimg" id="mapPanNorthImage"> /\ </crf:img>
            </a>
			
            <a id="panSouthButton" href="${panSouthUrl}" class="mapControl">
                <crf:img device="${context.device}" src="/comp/map/images/south.mimg" id="mapPanSouthImage"> \/ </crf:img>
            </a>

            <a id="panWestButton" href="${panWestUrl}" class="mapControl">
                <crf:img device="${context.device}" src="/comp/map/images/west.mimg" id="mapPanWestImage"> &#60; </crf:img>
            </a>

            <a id="panEastButton" href="${panEastUrl}" class="mapControl">
                <crf:img device="${context.device}" src="/comp/map/images/east.mimg" id="mapPanEastImage"> &#62; </crf:img>
            </a>
        </div>
        
        <div id="viewControls">
            <c:choose>
                <c:when test="${map.mapLayer}">
                    <a href="${photoLayerUrl}">
                        <crf:img device="${context.device}" src="/comp/map/images/photo.mimg">
                            <fmt:message key="comp.photoLayer.label"/>
                        </crf:img>
                    </a>
                </c:when>
                
                <c:when test="${map.photoLayer}">
                    <a href="${mapLayerUrl}">
                        <crf:img device="${context.device}" src="/comp/map/images/map.mimg">
                            <fmt:message key="comp.mapLayer.label"/>
                        </crf:img>
                    </a>
                </c:when>
                
                <c:otherwise>
                    <%--
                      - In the event that the mapLayer is anything else (such as "PhotoWithStreets"),
                      - display the Map view link.   
                      --%>
                    <a href="${mapLayerUrl}">
                        <crf:img src="/comp/map/images/map.mimg" device="{device.context}>
                            <fmt:message key="comp.mapLayer.label"/>
                        </crf:img>
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</c:if>

<logging:debug logger="${logger}" message="Exiting render.tag" />
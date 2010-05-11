<%@ tag body-content="empty" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/logging/logging.tld"%>

<%@ attribute name="map" required="true"
    type="au.com.sensis.mobile.web.component.map.model.Map"  
    description="Map returned by the MapDelegate." %>
    
<%@ attribute name="zoomInUrl" required="true" 
    description="Zoom in URL to be used for server side maps." %>
<%@ attribute name="zoomOutUrl" required="true" 
    description="Zoom out URL to be used for server side maps." %>
    
<%@ attribute name="panNorthUrl" required="true" 
    description="Pan north URL to be used for server side maps." %>
<%@ attribute name="panSouthUrl" required="true" 
    description="Pan south URL to be used for server side maps." %>
<%@ attribute name="panEastUrl" required="true" 
    description="Pan east URL to be used for server side maps." %>
<%@ attribute name="panWestUrl" required="true" 
    description="Pan west URL to be used for server side maps." %>
    
<%@ attribute name="photoLayerUrl" required="true" 
    description="URL to be used for generating photo layer server side maps." %>
<%@ attribute name="mapLayerUrl" required="true" 
    description="URL to be used for generating map layer server side maps." %>
    
<%@ attribute name="clientSideGeneratedMapStateChangeUrl" required="false" 
    description="URL to be used by high end maps to indicate that the state of the map has changed. 
    Request params indicate what state has changed. If ommitted, state changes will not be reported." %>

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />    
<logging:debug logger="${logger}" message="Entering render.tag" />

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="au.com.sensis.mobile.web.component.map.map-component" />    

<c:choose>
    <c:when test="${not empty map && map.mapImageRetrievalDeferredToClient}">
        <%--
          - We know that the client will generate the map itself (eg. by accessing 
          - EMS directly for JavaScript enhanced maps)
          --%>
        <c:if test="${fn:length(fn:trim(clientSideGeneratedMapStateChangeUrl)) gt 0}">
            <%-- Optional URL for client to communicate state changes to the server.  --%>
            <a id="stateChangeUrl" href="${clientSideGeneratedMapStateChangeUrl}"></a>
        </c:if>
        
        <div id="mapWindow">
            &#160;    
		</div>

    </c:when>
    
    <c:otherwise>
        <%-- Cater to less funky phones that need the server to generate the map. --%>
        
        <c:if test="${not empty map}"> 
			<div id="mapWindow">     
            	<object src="${map.mapUrl.imageUrl}" id="map" srctype="image/png">
               		<param name="mcs-transcode" value="false"/>
                </object>
			</div>
           
            <div id="mapControls">
	            <div id="zoomControls">
	                <c:choose>
	                   <c:when test="${map.zoomDetails.atMinimumZoom}">
	                        <object src="/comp/map/images/zoomIn_faded.mimg" id="mapZoomInFadedImage">+</object>
	                   </c:when>
	                   <c:otherwise>
	                        <a id="zoomInButton" href="${zoomInUrl}" class="mapControl">
	                            <object src="/comp/map/images/zoomIn.mimg" id="mapZoomInImage">+</object>
	                        </a>
	                   </c:otherwise>
	                </c:choose>
	                
	                <c:choose>
	                   <c:when test="${map.zoomDetails.atMaximumZoom}">
	                        <object src="/comp/map/images/zoomOut_faded.mimg" id="mapZoomOutFadedImage">-</object>
	                   </c:when>
	                   <c:otherwise>
	                        <a id="zoomOutButton" href="${zoomOutUrl}" class="mapControl">
	                            <object src="/comp/map/images/zoomOut.mimg" id="mapZoomOutImage">-</object>
	                        </a>
	                   </c:otherwise>
	                </c:choose>
	            </div>
                    
        
                <div id="directionControls">
	                <a id="panNorthButton" href="${panNorthUrl}" class="mapControl">
	                    <object src="/comp/map/images/north.mimg" id="mapPanNorthImage"> /\ </object>
	                </a>
					
	                <a id="panSouthButton" href="${panSouthUrl}" class="mapControl">
	                    <object src="/comp/map/images/south.mimg" id="mapPanSouthImage"> \/ </object>
	                </a>
		
	                <a id="panWestButton" href="${panWestUrl}" class="mapControl">
	                    <object src="/comp/map/images/west.mimg" id="mapPanWestImage"> &#60; </object>
	                </a>
	
	                <a id="panEastButton" href="${panEastUrl}" class="mapControl">
	                    <object src="/comp/map/images/east.mimg" id="mapPanEastImage"> &#62; </object>
	                </a>
                </div>
                
                <div id="viewControls">
                    <c:choose>
                        <c:when test="${map.mapLayer}">
                            <a href="${photoLayerUrl}">
                                <object src="/comp/map/images/photo.mimg">
                                    <fmt:message key="comp.photoLayer.label"/>
                                </object>
                            </a>
                        </c:when>
                        
                        <c:when test="${map.photoLayer}">
                            <a href="${mapLayerUrl}">
                                <object src="/comp/map/images/map.mimg">
                                    <fmt:message key="comp.mapLayer.label"/>
                                </object>
                            </a>
                        </c:when>
                        
                        <c:otherwise>
                            <%--
                              - In the event that the mapLayer is anything else (such as "PhotoWithStreets"),
                              - display the Map view link.   
                              --%>
                            <a href="${mapLayerUrl}">
                                <object src="/comp/map/images/map.mimg">
                                    <fmt:message key="comp.mapLayer.label"/>
                                </object>
                            </a>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<logging:debug logger="${logger}" message="Exiting render.tag" />
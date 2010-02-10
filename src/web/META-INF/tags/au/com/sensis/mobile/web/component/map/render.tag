<%@ tag body-content="empty" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
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

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />    
<logging:info logger="${logger}" message="Entering render.tag" />

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="au.com.sensis.mobile.web.component.map.map-component" />    
    
<c:choose>
    <c:when test="${not empty map && map.mapImageRetrievalDeferredToClient}">
        <%--
          - Optional case: if it is known that the client will generate the map itself (eg. by accessing 
          - EMS directly for JavaScript enhanced maps), the backend may be set up to not bother
          - generating a map itself at all. At the moment (21 Jan 2010) this is not the case,
          - since doing so prevents the application from degrading gracefully if the client device has
          - JavaScript disabled.
          --%>
        
        <%--TODO: inline style should be a class. --%>
        <div id="mapWindow" style="margin-top:10px;height:300px;position:relative">
            &#160;
        </div>
    </c:when>
    
    <c:otherwise>
        <%-- Cater to less funky phones that need the server to generate the map. --%>
        
        <c:if test="${not empty map}"> 
            <div id="mapWindow" class="map" style="margin-top:10px;height:300px;position:relative">
        
                <div class="mapArea">
        
                    <object src="${map.mapUrl.imageUrl}" id="mapImage"
                            srctype="image/png">
                        <param name="mcs-transcode" value="false"/>
                    </object>
        
                </div>
        
                <div class="mapControls">
        
                    <div id="mapZoomControls">
                        <c:choose>
                           <c:when test="${map.atMinimumZoom}">
                                <object src="/comp/map/images/furniture/zoomIn_faded.mimg" id="mapZoomInFadedImage">+</object>
                           </c:when>
                           <c:otherwise>
                                <a id="mapZoomInButton" href="${zoomInUrl}" class="mapControl">
                                    <object src="/comp/map/images/furniture/zoomIn.mimg" id="mapZoomInImage">+</object>
                                </a>
                           </c:otherwise>
                        </c:choose>
                        
                        <c:choose>
                           <c:when test="${map.atMaximumZoom}">
                                <object src="/comp/map/images/furniture/zoomOut_faded.mimg" id="mapZoomOutFadedImage">-</object>
                           </c:when>
                           <c:otherwise>
                                <a id="mapZoomOutButton" href="${zoomOutUrl}" class="mapControl">
                                    <object src="/comp/map/images/furniture/zoomOut.mimg" id="mapZoomOutImage">-</object>
                                </a>
                           </c:otherwise>
                        </c:choose>
                    </div>
                    
        
                    <div class="mapDirectionControls">
                        <a id="mapPanNorthButton" href="${panNorthUrl}" class="mapControl">
                            <object src="/comp/map/images/furniture/panNorth.mimg" id="mapPanNorthImage"> /\ </object>
                        </a>
        
                        <a id="mapPanSouthButton" href="${panSouthUrl}" class="mapControl">
                            <object src="/comp/map/images/furniture/panSouth.mimg" id="mapPanSouthImage"> \/ </object>
                        </a>
        
                        <a id="mapPanWestButton" href="${panWestUrl}" class="mapControl">
                            <object src="/comp/map/images/furniture/panWest.mimg" id="mapPanWestImage"> &#60; </object>
                        </a>
        
                        <a id="mapPanEastButton" href="${panEastUrl}" class="mapControl">
                            <object src="/comp/map/images/furniture/panEast.mimg" id="mapPanEastImage"> &#62; </object>
                        </a>
                    </div>
        
                </div>
                
                <div id="viewControls">
                    <c:choose>
                        <c:when test="${map.mapLayer}">
                            <a href="${photoLayerUrl}">
                                <object src="/comp/map/images/furniture/photoLayer.mimg">
                                    <fmt:message key="comp.photoLayer.label"/>
                                </object>
                            </a>
                        </c:when>
                        <c:when test="${map.photoLayer}">
                            <a href="${mapLayerUrl}">
                                <object src="/comp/map/images/furniture/mapLayer.mimg">
                                    <fmt:message key="comp.mapLayer.label"/>
                                </object>
                            </a>
                        </c:when>
                    </c:choose>
                </div>
                
        
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<logging:info logger="${logger}" message="Exiting render.tag" />
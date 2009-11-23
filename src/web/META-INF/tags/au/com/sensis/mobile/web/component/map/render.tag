<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/logging/logging.tld"%>

<%--
  - Work around for Tomcat 5.0.28 to ensure that the JSP Expression Language is processed. 
  - Should also work with Tomcat 6.  
  --%>
<%@ tag isELIgnored="false" %>

<%@ attribute name="mapResult" required="true"
    type="au.com.sensis.mobile.web.component.map.model.MapResult"  
    description="MapResult returned by the MapDelegate." %>
    
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
    
<logging:info logger="au.com.sensis.mobile.web.component.map" message="Entering render.tag" />
    
<c:choose>
    <c:when test="${not empty mapResult && mapResult.mapRetrievalClientResponsible}">
        <%-- Cater to funky phones that can generate the map "themselves" (eg. via an AJAX call). --%>
        
        <%--TODO: inline style should be a class. --%>
        <div id="mapWindow" style="margin-top:10px;height:300px;position:relative">
            &#160;
        </div>
    </c:when>
    
    <c:otherwise>
        <%-- Cater to less funky phones that need the server to generate the map. --%>
        
        <c:if test="${not empty mapResult}"> 
            <div class="map">
        
                <div class="mapArea">
        
                    <object src="${mapResult.mapUrl}" id="mapImage"
                            srctype="image/png">
                        <param name="mcs-transcode" value="false"/>
                    </object>
        
                </div>
        
                <div class="mapControls">
        
                    <div class="zoomControls">
                        <a id="mapZoomInButton" href="${zoomInUrl}">
                            <object src="/comp/map/images/furniture/mapInIcon.mimg" id="zoomInImage" />
                        </a>
        
                        <a id="mapZoomOutButton" href="${zoomOutUrl}">
                            <object src="/comp/map/images/furniture/mapOutIcon.mimg" id="zoomOutImage" />
                        </a>
                    </div>
        
                    <div class="panControls">
                        <a id="mapPanNorthButton" href="${panNorthUrl}">
                            <object src="/comp/map/images/furniture/mapNorthIcon.mimg" id="moveNorthImage" />
                        </a>
        
                        <a id="mapPanSouthButton" href="${panSouthUrl}">
                            <object src="/comp/map/images/furniture/mapSouthIcon.mimg" id="moveSouthImage" />
                        </a>
        
                        <a id="mapPanWestButton" href="${panWestUrl}">
                            <object src="/comp/map/images/furniture/mapWestIcon.mimg" id="moveEastImage" />
                        </a>
        
                        <a id="mapPanEastButton" href="${panEastUrl}">
                            <object src="/comp/map/images/furniture/mapEastIcon.mimg" id="moveWestImage" />
                        </a>
                    </div>
        
                </div>
        
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<logging:info logger="au.com.sensis.mobile.web.component.map" message="Exiting render.tag" />
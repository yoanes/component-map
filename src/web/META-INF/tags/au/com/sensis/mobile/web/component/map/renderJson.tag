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
<logging:debug logger="${logger}" message="Entering renderJson.tag" />

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="au.com.sensis.mobile.web.component.map.map-component" />    

<%-- Figure out the name of the current component.--%>
<c:set var="componentName">
    <fmt:message key="comp.name" />
</c:set>

<%-- Render the JSON response. --%>    
{
    mapImage: '<c:out value="${map.mapUrl.imageUrl}" escapeXml="false"/>' ,

    eastBtnUrl: '<c:out value="${panEastUrl}" escapeXml="false" />' ,
    westBtnUrl: '<c:out value="${panWestUrl}" escapeXml="false" />' ,
    northBtnUrl: '<c:out value="${panNorthUrl}" escapeXml="false" />' ,
    southBtnUrl: '<c:out value="${panSouthUrl}" escapeXml="false" />' ,

    photoBtnUrl: '<c:out value="${photoLayerUrl}" escapeXml="false" />' ,
    mapBtnUrl: '<c:out value="${mapLayerUrl}" escapeXml="false" />' ,

    zoomInBtnUrl: '<c:out value="${zoomInUrl}" escapeXml="false" />' ,
    zoomOutBtnUrl: '<c:out value="${zoomOutUrl}" escapeXml="false" />' , 

    maxZoom: <c:out value="${map.zoomDetails.atMaximumZoom}" escapeXml="false" /> ,
    minZoom: <c:out value="${map.zoomDetails.atMinimumZoom}" escapeXml="false" />
}

<logging:debug logger="${logger}" message="Exiting renderJson.tag" />
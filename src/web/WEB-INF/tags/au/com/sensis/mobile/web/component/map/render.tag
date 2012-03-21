<%@ tag isELIgnored="false" trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="base" uri="/au/com/sensis/mobile/web/component/core/base/base.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/core/logging/logging.tld"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<%@ attribute name="device" required="true"
    type="au.com.sensis.devicerepository.Device"  
    description="Device of the current user." %>
    
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

<%--
  - Set attributes into request scope, then include a JSP so that we can route through the 
  - ContentRenderingFramework.
  --%>
<c:set var="mapComponentDevice" scope="request" value="${device}" />
<c:set var="mapComponentMap" scope="request" value="${map}" />
<c:set var="mapComponentZoomInUrl" scope="request" value="${zoomInUrl}" />
<c:set var="mapComponentZoomOutUrl" scope="request" value="${zoomOutUrl}" />
<c:set var="mapComponentPanNorthUrl" scope="request" value="${panNorthUrl}" />
<c:set var="mapComponentPanSouthUrl" scope="request" value="${panSouthUrl}" />
<c:set var="mapComponentPanEastUrl" scope="request" value="${panEastUrl}" />
<c:set var="mapComponentPanWestUrl" scope="request" value="${panWestUrl}" />
<c:set var="mapComponentPhotoLayerUrl" scope="request" value="${photoLayerUrl}" />
<c:set var="mapComponentMapLayerUrl" scope="request" value="${mapLayerUrl}" />
<c:set var="mapComponentClientSideGeneratedMapStateChangeUrl" scope="request" value="${clientSideGeneratedMapStateChangeUrl}" />

<jsp:doBody var="popup"/>
<c:set var="mapComponentPopup" scope="request" value="${popup}" />

<jsp:include page="/WEB-INF/view/jsp/comp/map/render.crf" />

<logging:debug logger="${logger}" message="Exiting render.tag" />

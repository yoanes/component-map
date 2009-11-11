<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="core" uri="/au/com/sensis/mobile/web/component/core/core.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/logging/logging.tld"%>

<%--
  - Work around for Tomcat 5.0.28 to ensure that the JSP Expression Language is processed. 
  - Should also work with Tomcat 6.  
  --%>
<%@ tag isELIgnored="false" %>

<%@ attribute name="baseCompMcsPath" required="true" type="java.lang.String"%>

<%-- 
  - MapResult returned by the MapDelegate.
  --%>
<%@ attribute name="mapResult" required="true"
    type="au.com.sensis.mobile.web.component.map.model.MapResult"  %>

<%-- Setup components that we depend on. --%>
<core:setup baseCompMcsPath="${baseCompMcsPath}"/>
<logging:setup baseCompMcsPath="${baseCompMcsPath}"/>

<%-- Themes for current component. --%>
<%-- TODO: really should use something equivalent to core:script to avoid duplicates. --%>
<link rel="mcs:theme" href="${baseCompMcsPath}/map/map.mthm"/>
<link rel="mcs:theme"  href="${baseCompMcsPath}/map/imageSizeCategory.mthm"/>

<%-- Scripts for current component. --%>
<%-- TODO: Openlayers not needed in stage since env differs to prod. Would be better if env is the same. --%>
<core:script src="${baseCompMcsPath}/map/scripts/OpenLayers.mscr"></core:script>
<core:script src="${baseCompMcsPath}/map/scripts/EMS.mscr"></core:script>
<core:script src="${baseCompMcsPath}/map/scripts/CommMode.mscr"></core:script>
<core:script src="${baseCompMcsPath}/map/scripts/map-component.mscr"></core:script>

<c:if test="${not empty mapResult && mapResult.mapRetrievalClientResponsible}">
    <core:script name="create-map" type="text/javascript">
        var MEMS = new MobEMS('mapWindow', {
            'longitude': <c:out value="${mapResult.mapState.coordinates.longitude}"/>, 
            'latitude': <c:out value="${mapResult.mapState.coordinates.latitude}"/>, 
            'zoom': <c:out value="${mapResult.mapState.zoomLevel}"/>
            });
    </core:script>
</c:if>
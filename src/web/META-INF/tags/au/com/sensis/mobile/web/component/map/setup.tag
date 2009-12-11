<%@ tag body-content="empty" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="core" uri="/au/com/sensis/mobile/web/component/core/core.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/logging/logging.tld"%>

<%-- 
  - MapResult returned by the MapDelegate.
  --%>
<%@ attribute name="mapResult" required="true"
    type="au.com.sensis.mobile.web.component.map.model.MapResult"  %>

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />
<logging:info logger="${logger}" message="Entering setup.tag" />

<core:compMcsBasePath var="compMcsBasePath" />

<%-- Setup components that we depend on. --%>
<core:setup />
<logging:setup />

<%-- Themes for current component. --%>
<core:link rel="mcs:theme" href="${compMcsBasePath}/map/map.mthm" />
<core:link rel="mcs:theme"  href="${compMcsBasePath}/map/imageSizeCategory.mthm"/>

<%-- Scripts for current component. --%>
<%-- TODO: Openlayers not needed in stage since env differs to prod. Would be better if env is the same. --%>
<core:script src="${compMcsBasePath}/map/scripts/OpenLayers.mscr"></core:script>
<core:script src="${compMcsBasePath}/map/scripts/EMS.mscr"></core:script>
<core:script src="${compMcsBasePath}/map/scripts/CommMode.mscr"></core:script>
<core:script src="${compMcsBasePath}/map/scripts/map-component.mscr"></core:script>

<c:if test="${not empty mapResult && mapResult.mapRetrievalClientResponsible}">
    <core:script name="create-map" type="text/javascript">
        var MEMS = new MobEMS('mapWindow', {
            'longitude': <c:out value="${mapResult.mapState.coordinates.longitude}"/>, 
            'latitude': <c:out value="${mapResult.mapState.coordinates.latitude}"/>, 
            'zoom': <c:out value="${mapResult.mapState.zoomLevel}"/>
            });
    </core:script>
</c:if>

<logging:info logger="${logger}" message="Exiting setup.tag" />
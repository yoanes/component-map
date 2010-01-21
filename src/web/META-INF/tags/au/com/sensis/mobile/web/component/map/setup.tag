<%@ tag body-content="empty" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="core" uri="/au/com/sensis/mobile/web/component/core/core.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/logging/logging.tld"%>

<%@ attribute name="mapUrlHolder" required="true"
    type="au.com.sensis.mobile.web.component.map.model.MapUrlHolder"  
    description="MapUrlHolder returned by the MapDelegate." %>

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

<c:if test="${not empty mapUrlHolder}">
    <core:script name="create-map" type="text/javascript">
        if(typeof(MobEMS) != 'undefined') {
        
            var MEMS = new MobEMS('mapWindow', {
                'longitude': <c:out value="${mapUrlHolder.mapUrl.mapCentre.longitude}"/>, 
                'latitude': <c:out value="${mapUrlHolder.mapUrl.mapCentre.latitude}"/>, 
                'zoom': <c:out value="${mapUrlHolder.mapUrl.zoom}"/>
                });
        }
    </core:script>
</c:if>

<logging:info logger="${logger}" message="Exiting setup.tag" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="base" uri="/au/com/sensis/mobile/web/component/core/base/base.tld"%>
<%@ taglib prefix="ems" uri="/au/com/sensis/mobile/web/component/core/ems/ems.tld"%>
<%@ taglib prefix="util" uri="/au/com/sensis/mobile/web/component/core/util/util.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/core/logging/logging.tld"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<%-- Retrieve attributes from request. --%>
<c:set var="device" value="${requestScope['mapComponentDevice']}" />
<c:set var="map" value="${requestScope['mapComponentMap']}" />

<%-- Themes for current component. --%>
<crf:link rel="stylesheet" type="text/css" href="comp/map/map.css" device="${device}"/>

<%-- Intermediate map themes and JavaScript. --%>
<c:if test="${not empty map && map.mapImageRetrieved}">
    <%-- Setup components that we depend on. --%>
    <base:setup device="${device}" />
    <util:setup device="${device}" />
    <logging:setup device="${device}" />
    
    <%-- Themes for current component: none required at the moment. --%>
    
    <%-- Scripts for current component. --%>
    <crf:script src="comp/map/package" type="text/javascript" device="${device}"></crf:script>
    
    <crf:script name="create-intermediate-map" type="text/javascript" device="${device}">
        if(typeof(MobEMS) != 'undefined') {
            new MobEMS(
                'mapWindow',
                {
                    layer: '<c:out value="${map.jsMapLayer}"/>'
                }                
            );
        }
    </crf:script>
</c:if>
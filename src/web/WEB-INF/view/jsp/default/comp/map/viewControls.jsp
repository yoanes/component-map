<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="base" uri="/au/com/sensis/mobile/web/component/core/base/base.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/core/logging/logging.tld"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<c:set var="device" value="${requestScope['mapComponentDevice']}" />
<c:set var="map" value="${requestScope['mapComponentMap']}" />
<c:set var="photoLayerUrl" value="${requestScope['mapComponentPhotoLayerUrl']}" />
<c:set var="mapLayerUrl" value="${requestScope['mapComponentMapLayerUrl']}" />

<div id="mapViewControls"
    ><c:choose>
        <c:when test="${map.mapLayer}"
            ><a href="${photoLayerUrl}"
                ><crf:img device="${device}" src="comp/map/controls/photo.image"
                ><fmt:message key="comp.photoLayer.label"/></crf:img
                ></a
        ></c:when>
        
        <c:when test="${map.photoLayer}"
            ><a href="${mapLayerUrl}"
                ><crf:img device="${device}" src="comp/map/controls/map.image"
                ><fmt:message key="comp.mapLayer.label"/></crf:img
                ></a
        ></c:when>

        <%--
          - In the event that the mapLayer is anything else (such as "PhotoWithStreets"),
          - display the Map view link.   
          --%>
        <c:otherwise
            ><a href="${mapLayerUrl}"
                ><crf:img src="comp/map/controls/map.image" device="${device}"
                ><fmt:message key="comp.mapLayer.label"/></crf:img
                ></a
        ></c:otherwise>
    </c:choose
></div>

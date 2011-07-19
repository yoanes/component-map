<%@ page trimDirectiveWhitespaces="true" %>

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
<c:set var="emsJsUrl" value="${requestScope['mapComponentEmsJsUrl']}" />
<c:set var="highEndMapControls" value="${requestScope['mapComponentHighEndMapControls']}" />

<%-- Themes for current component. --%>
<crf:link rel="stylesheet" type="text/css" href="comp/map/map.css" device="${device}"/>

<%-- Hi end map themes and JavaScript. --%>
<c:if test="${not empty map && map.mapImageRetrievalDeferredToClient}">
    <%-- Setup components that we depend on. --%>
    <base:setup device="${device}"/>
    <ems:setup device="${device}" emsJsUrl="${emsJsUrl}" />
    <util:setup device="${device}" />
    <logging:setup device="${device}" />

    <crf:imgSrcPrefix var="imgSrcPrefix" />
    <crf:script name="advanced-map-js-config" type="text/javascript" device="${device}">
       _MapImgSrcPrefix_ = '<c:out value="${imgSrcPrefix}" escapeXml="false"/>';
       _MapControlsCompulsory_ = new Array();
       _MapControlsOptional_ = [<c:out value="${highEndMapControls}" escapeXml="false" />];
       _MapImgPath_ = _MapImgSrcPrefix_ + 'comp/map/';
       _MapControlsPath_ = _MapImgPath_ + 'onmapcontrols/';
       _MapOrnamentsPath_ = _MapImgPath_ + 'ornaments/';
    </crf:script>
    
    <%-- Scripts for current component. --%>
    <crf:script src="comp/map/package" type="text/javascript" device="${device}"></crf:script>  
</c:if>

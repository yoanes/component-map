<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<%-- Retrieve attributes from request. --%>
<c:set var="device" value="${requestScope['mapComponentDevice']}" />

<%-- Themes for current component. --%>
<crf:link rel="stylesheet" type="text/css" href="comp/map/map.css" device="${device}"/>

<%-- 
  - Define background images in an internal stylesheet as per the CRF 2.x convention to allow these 
  - images URLs to contain the application version number. 
  --%>
<crf:imgSrcPrefix var="imgSrcPrefix" />
<style type="text/css">
    #mapControls {
        background-image:url("${imgSrcPrefix}comp/map/controls/mc_bg.image")
    }
</style>
  
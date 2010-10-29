<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<%-- Retrieve attributes from request. --%>
<c:set var="device" value="${requestScope['mapComponentDevice']}" />

<%-- Themes for current component. --%>
<crf:link rel="stylesheet" type="text/css" href="comp/map/map.css" device="${device}"/>
  
<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- Retrieve attributes from request. --%>
<c:set var="map" value="${requestScope['mapComponentMap']}" />
<c:set var="clientSideGeneratedMapStateChangeUrl" value="${requestScope['mapComponentClientSideGeneratedMapStateChangeUrl']}" />
<c:set var="popup" value="${requestScope['mapComponentPopup']}" />

<c:if test="${not empty map && map.mapImageRetrievalDeferredToClient}">

    <%--
      - We know that the client will generate the map itself (eg. by accessing 
      - EMS directly for JavaScript enhanced maps)
      --%>
    <c:if test="${fn:length(fn:trim(clientSideGeneratedMapStateChangeUrl)) gt 0}">
        <%-- Optional URL for client to communicate state changes to the server.  --%>
        <a id="mapStateChangeUrl" href="${clientSideGeneratedMapStateChangeUrl}"></a>
    </c:if>
    
    <div id="mapWindow">
        &#160;    
    </div>

    <div id="mapPopup"><c:out value="${popup}" escapeXml="false" /></div>
</c:if>

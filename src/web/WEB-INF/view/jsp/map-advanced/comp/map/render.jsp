<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<%-- Retrieve attributes from request. --%>
<c:set var="clientSideGeneratedMapStateChangeUrl" value="${requestScope['mapComponentClientSideGeneratedMapStateChangeUrl']}" />

<%--
  - We know that the client will generate the map itself (eg. by accessing 
  - EMS directly for JavaScript enhanced maps)
  --%>
<c:if test="${fn:length(fn:trim(clientSideGeneratedMapStateChangeUrl)) gt 0}">
    <%-- Optional URL for client to communicate state changes to the server.  --%>
    <a id="stateChangeUrl" href="${clientSideGeneratedMapStateChangeUrl}"></a>
</c:if>

<div id="mapWindow">
    &#160;    
</div>
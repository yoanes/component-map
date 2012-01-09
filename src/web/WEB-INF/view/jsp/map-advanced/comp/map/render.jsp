<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<%-- Retrieve attributes from request. --%>
<c:set var="map" value="${requestScope['mapComponentMap']}" />
<c:set var="clientSideGeneratedMapStateChangeUrl" value="${requestScope['mapComponentClientSideGeneratedMapStateChangeUrl']}" />

<crf:imgSrcPrefix var="imgSrcPrefix" />
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
    
	<crf:script name="create-hiEnd-map" type="text/javascript" device="${device}">
        (function () {	    
	        var icons = [
	            <c:forEach var="resolvedIcon" items="${map.resolvedIcons}" varStatus="resolvedIconLoopStatus">
	                <c:if test="${resolvedIconLoopStatus.index > 0}">,</c:if>
		    	{
	                url: '<c:out value="${imgSrcPrefix}${resolvedIcon.icon.clientImgSrcPath}" />' ,
	                width: <c:out value="${resolvedIcon.icon.width}" /> ,                    
	                height: <c:out value="${resolvedIcon.icon.height}" /> ,                    
	                offsetX: <c:out value="${resolvedIcon.icon.offsetX}" /> ,                    
	                offsetY: <c:out value="${resolvedIcon.icon.offsetY}" /> ,
	                coordinates: {
	                	latitude: <c:out value="${resolvedIcon.point.latitude}" /> ,
	                    longitude: <c:out value="${resolvedIcon.point.longitude}" />
	                }
	        	}
	            </c:forEach>
	        ];
	        
	        <c:choose>
	            <c:when test="${map.routeMap}">
	                var directions = {
	                    wayPoints: [
	                        {
	                            coordinates: { 
	                                latitude: <c:out value="${map.routeDetails.waypoints.start.latitude}" />,
	                                longitude: <c:out value="${map.routeDetails.waypoints.start.longitude}" />
	                            }
	                        },
	                        { 
	                            coordinates: { 
	                                latitude: <c:out value="${map.routeDetails.waypoints.end.latitude}" />,
	                                longitude: <c:out value="${map.routeDetails.waypoints.end.longitude}" />
	                            }
	                        }
	                    ],
	                    fastest: <c:out value="${map.routeDetails.routingOption.fastest}" />,
	                    tolls: <c:out value="${map.routeDetails.routingOption.tolls}" />,
	                    transportType: '<c:out value="${map.routeDetails.emsJsTransportType}" />'
	                };
	            </c:when>
	            <c:otherwise>
	                var directions = null;
	            </c:otherwise>
	        </c:choose>
	        
	        <c:choose>
	            <c:when test="${map.boundingBoxDefined}">
	                var mapOptions = {
	                    'boundingBox': {
	                        'topLeft': {
	                            'latitude': <c:out value="${map.mapUrl.boundingBox.topLeft.latitude}"/>, 
	                            'longitude': <c:out value="${map.mapUrl.boundingBox.topLeft.longitude}"/>
	                        },
	                        'bottomRight': {
	                            'latitude': <c:out value="${map.mapUrl.boundingBox.bottomRight.latitude}"/>, 
	                            'longitude': <c:out value="${map.mapUrl.boundingBox.bottomRight.longitude}"/>
	                        }    
	                    } 
	                };
	                <%--
	                  - Special case for POI maps. The bounding box is considered the minimum area
	                  - to be viewed. Whereas the zoom might be more zoomed out to give the user more context
	                  - for where the POIs are. 
	                  --%>
	                <c:if test="${map.boundingBoxEmsJavaScriptZoomInThreshold != null}">
	                    mapOptions.zoomInThreshold = <c:out value="${map.boundingBoxEmsJavaScriptZoomInThreshold}"/>;
	                </c:if>
	            </c:when>
	            <c:otherwise>
	                var mapOptions = {                
	                    'longitude': <c:out value="${map.mapUrl.mapCentre.longitude}"/>, 
	                    'latitude': <c:out value="${map.mapUrl.mapCentre.latitude}"/>, 
	                    'zoom': <c:out value="${map.zoomDetails.emsJavaScriptZoom}"/>
	                };
	            </c:otherwise>
	        </c:choose> 
		   	
		        new MobEMS('mapWindow', 
		            {
		                layer: '<c:out value="${map.jsMapLayer}"/>'
		            },                
		            mapOptions,
		            icons, 
		            directions
		        );
		        
	    })();
	</crf:script>
</c:if>

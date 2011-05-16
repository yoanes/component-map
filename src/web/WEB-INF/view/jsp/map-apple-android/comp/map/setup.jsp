<%@ page trimDirectiveWhitespaces="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="base" uri="/au/com/sensis/mobile/web/component/core/base/base.tld"%>
<%@ taglib prefix="ems" uri="/au/com/sensis/mobile/web/component/core/ems/ems.tld"%>
<%@ taglib prefix="util" uri="/au/com/sensis/mobile/web/component/core/util/util.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/core/logging/logging.tld"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>
<%@ taglib prefix="devicelocation" uri="/au/com/sensis/mobile/web/component/devicelocation/devicelocation.tld" %>

<%-- Retrieve attributes from request. --%>
<c:set var="device" value="${requestScope['mapComponentDevice']}" />
<c:set var="map" value="${requestScope['mapComponentMap']}" />
<c:set var="emsJsUrl" value="${requestScope['mapComponentEmsJsUrl']}" />
<c:set var="iconImagesSrcPrefix" value="${requestScope['mapComponentIconImagesSrcPrefix']}" />
<c:set var="useMyLocation" value="${requestScope['mapComponentUseMyLocation']}" />
<c:set var="useDockForPopup" value="${requestScope['mapComponentUseDockForPopup']}" />
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

    <crf:script name="advanced-map-js-config" type="text/javascript" device="${device}">
       _MapImgSrcPrefix_ = '<c:out value="${iconImagesSrcPrefix}" escapeXml="false"/>';
       _MapControlsCompulsory_ = new Array();
       _MapControlsOptional_ = [<c:out value="${highEndMapControls}" escapeXml="false" />];
       _MapImgPath_ = _MapImgSrcPrefix_ + 'comp/map/';
       _MapControlsPath_ = _MapImgPath_ + 'onmapcontrols/';
       _MapOrnamentsPath_ = _MapImgPath_ + 'ornaments/';
    </crf:script>
    
    <%-- Scripts for current component. --%>
    <crf:script src="comp/map/package" type="text/javascript" device="${device}"></crf:script>

    <c:if test="${useMyLocation != null}">
    	<devicelocation:setup device="${device}" />
    </c:if>    

    <crf:script name="create-hiEnd-map" type="text/javascript" device="${device}">
        if(typeof(MobEMS) != 'undefined') {
        
            var icons = [
                <c:forEach var="resolvedIcon" items="${map.resolvedIcons}" varStatus="resolvedIconLoopStatus">
                    <c:if test="${resolvedIconLoopStatus.index > 0}">,</c:if>
		    	{
		    		id: '<c:out value="${resolvedIcon.icon.interactive.id}" />',
					title: '<c:out value="${resolvedIcon.icon.interactive.title}" />',
					text: '<c:out value="${resolvedIcon.icon.interactive.description}" />',
					type: '<c:out value="${resolvedIcon.icon.interactive.type}" />',
		    		url: '<c:out value="${resolvedIcon.icon.clientImgSrcPath}" />' ,
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
        
            <%-- 
		     - add dock attribute if specified. Note: dock can either be null or true or a name of a function
		     - note: we can safely assume that mapOptions have been declared above
		     --%>
			<c:if test="${useDockForPopup != null }">
			   	mapOptions.dock = <c:out value="${useDockForPopup}"/>; 
		   	</c:if>
		   	
            new MobEMS('mapWindow', 
                {
                    layer: '<c:out value="${map.jsMapLayer}"/>'
                },                
                mapOptions,
                icons, 
                directions
            );
        }
    </crf:script>
</c:if>

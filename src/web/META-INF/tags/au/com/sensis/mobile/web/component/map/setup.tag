<%@ tag body-content="empty" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="core" uri="/au/com/sensis/mobile/web/component/core/core.tld"%>
<%@ taglib prefix="ems" uri="/au/com/sensis/mobile/web/component/ems/ems.tld"%>
<%@ taglib prefix="util" uri="/au/com/sensis/mobile/web/component/util/util.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/logging/logging.tld"%>

<%@ attribute name="map" required="true"
    type="au.com.sensis.mobile.web.component.map.model.Map"  
    description="Map returned by the MapDelegate." %>

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />
<logging:debug logger="${logger}" message="Entering setup.tag" />

<core:compMcsBasePath var="compMcsBasePath" />

<c:if test="${not empty map && map.mapImageRetrievalDeferredToClient}">
    <%-- Setup components that we depend on. --%>
    <core:setup />
    <ems:setup />
    <util:setup />
    <logging:setup />
    
    <%-- Themes for current component. --%>
    <core:link rel="mcs:theme" href="${compMcsBasePath}/map/map.mthm" />
    <core:link rel="mcs:theme"  href="${compMcsBasePath}/map/imageSizeCategory.mthm"/>
    
    <%-- Scripts for current component. --%>
    <core:script src="${compMcsBasePath}/map/scripts/map-component-jsconfig.mscr"></core:script>
    <core:script src="${compMcsBasePath}/map/scripts/map-component.mscr"></core:script>
    
    <core:script name="create-map" type="text/javascript">
        if(typeof(MobEMS) != 'undefined') {
        
            var icons = [
                <c:forEach var="resolvedIcon" items="${map.resolvedIcons}" varStatus="resolvedIconLoopStatus">
                    <c:if test="${resolvedIconLoopStatus.index > 0}">,</c:if>
                    {
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
        
            var MEMS = new MobEMS('mapWindow', 
                {
                    layer: '<c:out value="${map.jsMapLayer}"/>',
                    photoLayerAnchorId: 'photoButton',
                    mapLayerAnchorId: 'mapButton',
                    zoomInAnchorId: 'zoomInButton',
                    zoomOutAnchorId: 'zoomOutButton'
                },                
                mapOptions,
                icons, 
                directions
            );
        }
    </core:script>
</c:if>    

<logging:debug logger="${logger}" message="Exiting setup.tag" />
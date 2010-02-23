<%@ tag body-content="empty" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="core" uri="/au/com/sensis/mobile/web/component/core/core.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/logging/logging.tld"%>
<%@ taglib prefix="util" uri="/au/com/sensis/mobile/web/component/util/util.tld"%>

<%@ attribute name="map" required="true"
    type="au.com.sensis.mobile.web.component.map.model.Map"  
    description="Map returned by the MapDelegate." %>

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />
<logging:info logger="${logger}" message="Entering setup.tag" />

<core:compMcsBasePath var="compMcsBasePath" />

<%-- Setup components that we depend on. --%>
<core:setup />
<util:setup />
<logging:setup />

<%-- Themes for current component. --%>
<core:link rel="mcs:theme" href="${compMcsBasePath}/map/map.mthm" />
<core:link rel="mcs:theme"  href="${compMcsBasePath}/map/imageSizeCategory.mthm"/>

<%-- Scripts for current component. --%>
<%-- TODO: Openlayers not needed in stage since env differs to prod. Would be better if env is the same. --%>
<core:script src="${compMcsBasePath}/map/scripts/OpenLayers.mscr"></core:script>
<core:script src="${compMcsBasePath}/map/scripts/EMS.mscr"></core:script>
<core:script src="${compMcsBasePath}/map/scripts/CommMode.mscr"></core:script>
<core:script src="${compMcsBasePath}/map/scripts/map-component.mscr"></core:script>

<c:if test="${not empty map}">
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
                                }, 
                                <%--TODO --%>
                                streetName: ''<%--'<c:out value="${map.routeDetails.waypoints.start.street.name}" />' --%> 
                            },
                            { 
                                coordinates: { 
                                    latitude: <c:out value="${map.routeDetails.waypoints.end.latitude}" />,
                                    longitude: <c:out value="${map.routeDetails.waypoints.end.longitude}" />
                                }, 
                                <%--TODO --%>
                                streetName: ''<%--'<c:out value="${map.addressWaypoints.end.street.name}" />'--%> 
                            }
                        ],
                        fastest: <c:out value="${map.routeDetails.routingOption.fastest}" />,
                        tolls: <c:out value="${map.routeDetails.routingOption.tolls}" />,
                        transportType: <c:out value="${map.routeDetails.emsJsTransportType}" />
                    };
                </c:when>
                <c:otherwise>
                    var directions = { };
                </c:otherwise>
            </c:choose>
        
            var MEMS = new MobEMS('mapWindow', {
                    'longitude': <c:out value="${map.mapUrl.mapCentre.longitude}"/>, 
                    'latitude': <c:out value="${map.mapUrl.mapCentre.latitude}"/>, 
                    'zoom': <c:out value="${map.emsZoom}"/>
                },
                {
                    layer: '<c:out value="${map.jsMapLayer}"/>',
                    photoLayerAnchorId: 'photoButton',
                    mapLayerAnchorId: 'mapButton',
                    zoomInAnchorId: 'zoomInButton',
                    zoomOutAnchorId: 'zoomOutButton'
                },                
                icons, 
                directions
            );
        }
    </core:script>
</c:if>

<logging:info logger="${logger}" message="Exiting setup.tag" />
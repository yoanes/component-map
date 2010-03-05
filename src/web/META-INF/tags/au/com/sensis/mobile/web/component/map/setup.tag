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
<core:script src="${compMcsBasePath}/map/scripts/map-component-jsconfig.mscr"></core:script>
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
                                
                                <%-- 
                                  - The EMS JavaScript interface requires the streetname corresponding to the coordinates.
                                  - However, it doesn't actually need the value to be real (!) and it is never displayed
                                  - in our mobiles apps. Therefore, we just pass in a dummy value.  
                                  --%>
                                streetName: 'UNKNOWN' 
                            },
                            { 
                                coordinates: { 
                                    latitude: <c:out value="${map.routeDetails.waypoints.end.latitude}" />,
                                    longitude: <c:out value="${map.routeDetails.waypoints.end.longitude}" />
                                }, 
                                
                                <%-- 
                                  - The EMS JavaScript interface requires the streetname corresponding to the coordinates.
                                  - However, it doesn't actually need the value to be real (!) and it is never displayed
                                  - in our mobiles apps. Therefore, we just pass in a dummy value.  
                                  --%>
                                streetName: 'UNKNOWN'
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
                </c:when>
                <c:otherwise>
                    var mapOptions = {                
                        'longitude': <c:out value="${map.mapUrl.mapCentre.longitude}"/>, 
                        'latitude': <c:out value="${map.mapUrl.mapCentre.latitude}"/>, 
                        'zoom': <c:out value="${map.zoomDetails.emsZoom}"/>
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

<logging:info logger="${logger}" message="Exiting setup.tag" />
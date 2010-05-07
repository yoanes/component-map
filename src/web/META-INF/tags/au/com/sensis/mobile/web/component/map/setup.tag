<%@ tag body-content="empty" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="core" uri="/au/com/sensis/mobile/web/component/core/core.tld"%>
<%@ taglib prefix="ems" uri="/au/com/sensis/mobile/web/component/ems/ems.tld"%>
<%@ taglib prefix="util" uri="/au/com/sensis/mobile/web/component/util/util.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/logging/logging.tld"%>

<%@ attribute name="device" required="true"
    type="au.com.sensis.wireless.common.volantis.devicerepository.api.Device"  
    description="Device of the current user." %>
    
<%@ attribute name="map" required="true"
    type="au.com.sensis.mobile.web.component.map.model.Map"  
    description="Map returned by the MapDelegate." %>

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />
<logging:debug logger="${logger}" message="Entering setup.tag" />

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="au.com.sensis.mobile.web.component.map.map-component" />    

<%-- Figure out the name of the current component.--%>
<c:set var="componentName">
    <fmt:message key="comp.name" />
</c:set>
<core:deviceConfig var="deviceConfig" device="${device}" 
    registryBeanName="${componentName}.comp.deviceConfigRegistry"/>

<core:compMcsBasePath var="compMcsBasePath" />

<%-- Themes for current component. --%>
<core:link rel="mcs:theme" href="${compMcsBasePath}/map/map.mthm" />

<%-- Hi end map themes and JavaScript. --%>
<c:if test="${not empty map && map.mapImageRetrievalDeferredToClient && deviceConfig.enableHiEndMap}">
    <%-- Setup components that we depend on. --%>
    <core:setup />
    <ems:setup />
    <util:setup />
    <logging:setup />
    
    <%-- Themes for current component. --%>
    <core:link rel="mcs:theme" href="${compMcsBasePath}/map/hiMap.mthm" />
    
    <%-- Scripts for current component. --%>
    <core:script src="${compMcsBasePath}/map/scripts/map-component-jsconfig.mscr"></core:script>
    <core:script src="${compMcsBasePath}/map/scripts/map-component-hiEnd.mscr"></core:script>
    <core:script src="${compMcsBasePath}/map/scripts/map-component-tilePath.mscr"></core:script>
    
    <core:script name="create-hiEnd-map" type="text/javascript">
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
        
            new MobEMS('mapWindow', 
                {
                    layer: '<c:out value="${map.jsMapLayer}"/>'
                },                
                mapOptions,
                icons, 
                directions
            );
        }
    </core:script>
</c:if>    

<%-- Intermediate map themes and JavaScript. --%>
<c:if test="${not empty map && map.mapImageRetrieved && deviceConfig.enableIntermediateMap}">
    <%-- Setup components that we depend on. --%>
    <core:setup />
    <util:setup />
    <logging:setup />
    
    <%-- Themes for current component: none required at the moment. --%>
    
    <%-- Scripts for current component. --%>
    <core:script src="${compMcsBasePath}/map/scripts/map-component-intermediate.mscr"></core:script>
    
    <core:script name="create-intermediate-map" type="text/javascript">
        if(typeof(MobEMS) != 'undefined') {
            new MobEMS(
                'mapWindow',
                {
                    layer: '<c:out value="${map.jsMapLayer}"/>'
                }                
            );
        }
    </core:script>
</c:if>    

<logging:debug logger="${logger}" message="Exiting setup.tag" />
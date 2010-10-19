<%@ tag body-content="empty" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="base" uri="/au/com/sensis/mobile/web/component/core/base/base.tld"%>
<%@ taglib prefix="ems" uri="/au/com/sensis/mobile/web/component/core/ems/ems.tld"%>
<%@ taglib prefix="util" uri="/au/com/sensis/mobile/web/component/core/util/util.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/core/logging/logging.tld"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<%@ attribute name="device" required="true"
    type="au.com.sensis.wireless.common.volantis.devicerepository.api.Device"  
    description="Device of the current user." %>
    
<%@ attribute name="map" required="true"
    type="au.com.sensis.mobile.web.component.map.model.Map"  
    description="Map returned by the MapDelegate." %>
    
<%@ attribute name="emsJsUrl" required="true"
    description="URL of the EMS JavaScript library." %>

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />
<logging:debug logger="${logger}" message="Entering setup.tag" />

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="au.com.sensis.mobile.web.component.map.map-component" />    

<%-- Figure out the name of the current component.--%>
<c:set var="componentName">
    <fmt:message key="comp.name" />
</c:set>

<base:compMcsBasePath var="compMcsBasePath" />

<%-- Themes for current component. --%>
<crf:link rel="stylesheet" type="text/css" href="comp/map/map.css" device="${device}"/>

<%-- Hi end map themes and JavaScript. --%>
<c:if test="${not empty map && map.mapImageRetrievalDeferredToClient && deviceConfig.enableHiEndMap}">
    <%-- Setup components that we depend on. --%>
    <base:setup device="${device}"/>
    <ems:setup device="${device}" emsJsUrl="${emsJsUrl}" />
    <util:setup device="${device}" />
    <logging:setup device="${device}" />
    
    <%-- Styles for current component. --%>
    <%-- TODO: once we split setup.tag into two, the need for a specific hiMap.css will go away. --%>
    <crf:link href="comp/map/hiMap.css" rel="stylesheet" type="text/css" device="${device}"/>
    
    <%-- Scripts for current component. --%>
    <crf:script src="comp/map/package" type="text/javascript" device="${device}"></crf:script>
    
    <crf:script name="create-hiEnd-map" type="text/javascript" device="${device}">
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
    </crf:script>
</c:if>    

<%-- Intermediate map themes and JavaScript. --%>
<c:if test="${not empty map && map.mapImageRetrieved && deviceConfig.enableIntermediateMap}">
    <%-- Setup components that we depend on. --%>
    <base:setup device="${device}" />
    <util:setup device="${device}" />
    <logging:setup device="${device}" />
    
    <%-- Themes for current component: none required at the moment. --%>
    
    <%-- Scripts for current component. --%>
    <crf:script src="comp/map/package" type="text/javascript" device="${device}"></crf:script>
    
    <crf:script name="create-intermediate-map" type="text/javascript" device="${device}">
        if(typeof(MobEMS) != 'undefined') {
            new MobEMS(
                'mapWindow',
                {
                    layer: '<c:out value="${map.jsMapLayer}"/>'
                }                
            );
        }
    </crf:script>
</c:if>    

<logging:debug logger="${logger}" message="Exiting setup.tag" />
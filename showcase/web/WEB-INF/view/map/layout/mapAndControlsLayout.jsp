<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<c:set var="mapControlsActionName">
    <tiles:insertAttribute name="mapControlsActionName"/>
</c:set>
<c:set var="clientSideGeneratedMapStateChangeActionName">
    <tiles:insertAttribute name="clientSideGeneratedMapStateChangeActionName"/>
</c:set>

<div id="map">
    <s:url id="baseMapUrl" namespace="/map" action="%{#attr.mapControlsActionName}" includeParams="all"
            includeContext="true" escapeAmp="false">
    
        <%--
          - Current state of the map so that further manipulations will be relative to this. 
          --%>
        <s:param name="mclat"><s:property value="#attr.map.mapUrl.mapCentre.latitude"/></s:param>
        <s:param name="mclon"><s:property value="#attr.map.mapUrl.mapCentre.longitude"/></s:param>
        <s:param name="tllat"><s:property value="#attr.map.mapUrl.boundingBox.topLeft.latitude"/></s:param>
        <s:param name="tllon"><s:property value="#attr.map.mapUrl.boundingBox.topLeft.longitude"/></s:param>
        <s:param name="brlat"><s:property value="#attr.map.mapUrl.boundingBox.bottomRight.latitude"/></s:param>
        <s:param name="brlon"><s:property value="#attr.map.mapUrl.boundingBox.bottomRight.longitude"/></s:param>
        <s:param name="mz"><s:property value="#attr.map.mapUrl.zoom"/></s:param>                
        <s:param name="lyr" value="#attr.map.mapLayerShortCode" />
        
        <%--
          - Hack (but hey, this is just a showcase): for POI searches only, make sure that the search key 
          - is carried around. For other maps, this has no effect.
          --%>
        <s:param name="search" value="%{model.search}" />
        
        <%--
          - Hack (but hey, this is just a showcase): for location searches only, make sure that the cursor 
          - type is carried around. For other maps, this has no effect.
          --%>
        <s:param name="ct" value="%{model.cursorType}" />
    </s:url>

    <c:choose>
        <c:when test="${map.routeMap}">
            <s:url id="browseMapUrl" value="%{baseMapUrl}" includeContext="false" escapeAmp="false">
                <s:param name="rh"><s:property value="#attr.map.routeDetails.emsRouteHandle.identifier"/></s:param>
                <s:param name="rop"><s:property value="#attr.map.routeDetails.routingOption.shortName"/></s:param>
                <s:param name="rslat"><s:property value="#attr.map.routeDetails.waypoints.start.latitude"/></s:param>
                <s:param name="rslon"><s:property value="#attr.map.routeDetails.waypoints.start.longitude"/></s:param>
                <s:param name="relat"><s:property value="#attr.map.routeDetails.waypoints.end.latitude"/></s:param>
                <s:param name="relon"><s:property value="#attr.map.routeDetails.waypoints.end.longitude"/></s:param>
            </s:url>        
        </c:when>
        <c:otherwise>
            <s:url id="browseMapUrl" value="%{baseMapUrl}" includeContext="false" escapeAmp="false">
                <s:param name="moclat"><s:property value="#attr.map.originalMapCentre.latitude"/></s:param>
                <s:param name="moclon"><s:property value="#attr.map.originalMapCentre.longitude"/></s:param>
            </s:url>
        </c:otherwise>
    </c:choose>
    
    <s:url id="zoomInUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mzi'}" />
    </s:url>

    <s:url id="zoomOutUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mzo'}" />
    </s:url>
    
    <s:url id="panNorthUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mpn'}" />
    </s:url>

    <s:url id="panSouthUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mps'}" />
    </s:url>

    <s:url id="panWestUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mpw'}" />
    </s:url>

    <s:url id="panEastUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mpe'}" />
    </s:url>
    
    <s:url id="photoLayerUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'phv'}" />
    </s:url>
    
    <s:url id="mapLayerUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mpv'}" />
    </s:url>

    <s:url id="stateChangeUrl" namespace="/map" action="%{#attr.clientSideGeneratedMapStateChangeActionName}"
            includeContext="true"/>

    <map:render device="${context.device}" map="${map}" 
        zoomInUrl="${zoomInUrl}" zoomOutUrl="${zoomOutUrl}" 
        panNorthUrl="${panNorthUrl}" panSouthUrl="${panSouthUrl}"
        panEastUrl="${panEastUrl}" panWestUrl="${panWestUrl}"
        photoLayerUrl="${photoLayerUrl}" mapLayerUrl="${mapLayerUrl}"
        clientSideGeneratedMapStateChangeUrl="${stateChangeUrl}" />
</div>

<div>
    <c:if test="${map.routeMap}">
        <c:forEach var="legStepDetail" items="${map.routeDetails.allLegStepDetails}" varStatus="loopStatus">
            <s:url id="legStepUrl" namespace="/map" action="getRouteLegStepMap" includeContext="true" escapeAmp="true">
                <s:param name="lyr" value="#attr.map.mapLayerShortCode" />
        
                <s:param name="rh"><s:property value="#attr.map.routeDetails.emsRouteHandle.identifier"/></s:param>
                <s:param name="rop"><s:property value="#attr.map.routeDetails.routingOption.shortName"/></s:param>
                <s:param name="rslat"><s:property value="#attr.map.routeDetails.waypoints.start.latitude"/></s:param>
                <s:param name="rslon"><s:property value="#attr.map.routeDetails.waypoints.start.longitude"/></s:param>
                <s:param name="relat"><s:property value="#attr.map.routeDetails.waypoints.end.latitude"/></s:param>
                <s:param name="relon"><s:property value="#attr.map.routeDetails.waypoints.end.longitude"/></s:param>
            
                <s:param name="rlsclon" value="#attr.legStepDetail.centre.longitude"/>
                <s:param name="rlsclat" value="#attr.legStepDetail.centre.latitude"/>
            </s:url>
        
            <div>
                <c:out value="${loopStatus.count}"/>.
                <a href="${legStepUrl}">
                    <c:out value="${legStepDetail.instruction}"/> [<c:out value="${legStepDetail.distanceMetres}"/>m]
                </a>
            </div>
        </c:forEach> 
    </c:if>
</div>

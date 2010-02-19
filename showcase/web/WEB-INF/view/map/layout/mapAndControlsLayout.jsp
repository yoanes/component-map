<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<c:set var="mapControlsActionName">
    <tiles:insertAttribute name="mapControlsActionName"/>
</c:set>
<c:set var="clientSideGeneratedMapStateChangeActionName">
    <tiles:insertAttribute name="clientSideGeneratedMapStateChangeActionName"/>
</c:set>

<div id="map">
    <s:url id="browseMapUrl" namespace="/map" action="%{#attr.mapControlsActionName}" includeParams="all"
            includeContext="true" escapeAmp="false">
    
        <%-- Reset action param so that we no longer think we are executing an action. --%>
        <s:param name="act"></s:param>
        
        <%--
          - Current state of the map so that further manipulations will be relative to this. 
          --%>
        <s:param name="moclat"><s:property value="#attr.map.originalMapCentre.latitude"/></s:param>
        <s:param name="moclon"><s:property value="#attr.map.originalMapCentre.longitude"/></s:param>
        <s:param name="mclat"><s:property value="#attr.map.mapUrl.mapCentre.latitude"/></s:param>
        <s:param name="mclon"><s:property value="#attr.map.mapUrl.mapCentre.longitude"/></s:param>
        <s:param name="tllat"><s:property value="#attr.map.mapUrl.boundingBox.topLeft.latitude"/></s:param>
        <s:param name="tllon"><s:property value="#attr.map.mapUrl.boundingBox.topLeft.longitude"/></s:param>
        <s:param name="brlat"><s:property value="#attr.map.mapUrl.boundingBox.bottomRight.latitude"/></s:param>
        <s:param name="brlon"><s:property value="#attr.map.mapUrl.boundingBox.bottomRight.longitude"/></s:param>
        <s:param name="mz"><s:property value="#attr.map.mapUrl.zoom"/></s:param>                
        <s:param name="lyr" value="#attr.map.mapLayerShortCode" />
    </s:url>

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

    <map:render map="${map}" zoomInUrl="${zoomInUrl}" zoomOutUrl="${zoomOutUrl}" 
        panNorthUrl="${panNorthUrl}" panSouthUrl="${panSouthUrl}"
        panEastUrl="${panEastUrl}" panWestUrl="${panWestUrl}"
        photoLayerUrl="${photoLayerUrl}" mapLayerUrl="${mapLayerUrl}"
        clientSideGeneratedMapStateChangeUrl="${stateChangeUrl}" />
</div>
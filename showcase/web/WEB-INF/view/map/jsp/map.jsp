<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<div id="map">
    <%--
      - Do not specify action or value attribute of s:url. We submit to the same URL that
      - rendered this page. This is important since different URLs can render this same
      - page. We also ensure that the submission uses the exact same parameters that were
      - used to render this page by setting includeParams to "all".
      --%>
    <s:url id="browseMapUrl" namespace="/map" action="manMap" includeParams="all"
            includeContext="true" escapeAmp="false">
    
        <%-- Reset action param so that we no longer think we are executing an action. --%>
        <s:param name="act"></s:param>
        
        <%--
          - Current state of the map so that further manipulations will be relative to this. 
          --%>
        <s:param name="moclat"><s:property value="#attr.mapUrlHolder.originalMapCentre.latitude"/></s:param>
        <s:param name="moclon"><s:property value="#attr.mapUrlHolder.originalMapCentre.longitude"/></s:param>
        <s:param name="mclat"><s:property value="#attr.mapUrlHolder.mapUrl.mapCentre.latitude"/></s:param>
        <s:param name="mclon"><s:property value="#attr.mapUrlHolder.mapUrl.mapCentre.longitude"/></s:param>
        <s:param name="tllat"><s:property value="#attr.mapUrlHolder.mapUrl.boundingBox.topLeft.latitude"/></s:param>
        <s:param name="tllon"><s:property value="#attr.mapUrlHolder.mapUrl.boundingBox.topLeft.longitude"/></s:param>
        <s:param name="brlat"><s:property value="#attr.mapUrlHolder.mapUrl.boundingBox.bottomRight.latitude"/></s:param>
        <s:param name="brlon"><s:property value="#attr.mapUrlHolder.mapUrl.boundingBox.bottomRight.longitude"/></s:param>
        <s:param name="mz"><s:property value="#attr.mapUrlHolder.mapUrl.zoom"/></s:param>                
    </s:url>

    <s:url id="zoomInUrl" value="%{browseMapUrl}" includeContext="false">
        <%--
          - Specific lyr here since we need a specific value for photoLayerUrl 
          - and mapLayerUrl below. 
          --%>
        <s:param name="lyr" value="#attr.mapUrlHolder.mapLayerShortCode" />
        <s:param name="act" value="%{'mzi'}" />
    </s:url>

    <s:url id="zoomOutUrl" value="%{browseMapUrl}" includeContext="false">
        <%--
          - Specific lyr here since we need a specific value for photoLayerUrl 
          - and mapLayerUrl below. 
          --%>
        <s:param name="lyr" value="#attr.mapUrlHolder.mapLayerShortCode" />
        <s:param name="act" value="%{'mzo'}" />
    </s:url>
    
    <s:url id="panNorthUrl" value="%{browseMapUrl}" includeContext="false">
        <%--
          - Specific lyr here since we need a specific value for photoLayerUrl 
          - and mapLayerUrl below. 
          --%>
        <s:param name="lyr" value="#attr.mapUrlHolder.mapLayerShortCode" />    
        <s:param name="act" value="%{'mpn'}" />
    </s:url>

    <s:url id="panSouthUrl" value="%{browseMapUrl}" includeContext="false">
        <%--
          - Specific lyr here since we need a specific value for photoLayerUrl 
          - and mapLayerUrl below. 
          --%>
        <s:param name="lyr" value="#attr.mapUrlHolder.mapLayerShortCode" />    
        <s:param name="act" value="%{'mps'}" />
    </s:url>

    <s:url id="panWestUrl" value="%{browseMapUrl}" includeContext="false">
        <%--
          - Specific lyr here since we need a specific value for photoLayerUrl 
          - and mapLayerUrl below. 
          --%>
        <s:param name="lyr" value="#attr.mapUrlHolder.mapLayerShortCode" />    
        <s:param name="act" value="%{'mpw'}" />
    </s:url>

    <s:url id="panEastUrl" value="%{browseMapUrl}" includeContext="false">
        <%--
          - Specific lyr here since we need a specific value for photoLayerUrl 
          - and mapLayerUrl below. 
          --%>
        <s:param name="lyr" value="#attr.mapUrlHolder.mapLayerShortCode" />    
        <s:param name="act" value="%{'mpe'}" />
    </s:url>
    
    <s:url id="photoLayerUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="lyr" value="%{'p'}" />
        <%-- No action required here because we are not panning or zooming.--%>
    </s:url>
    
    <s:url id="mapLayerUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="lyr" value="%{'m'}" />
        <%-- No action required here because we are not panning or zooming.--%>
    </s:url>

    Map goes here:
    <map:render mapUrlHolder="${mapUrlHolder}" zoomInUrl="${zoomInUrl}" zoomOutUrl="${zoomOutUrl}" 
        panNorthUrl="${panNorthUrl}" panSouthUrl="${panSouthUrl}"
        panEastUrl="${panEastUrl}" panWestUrl="${panWestUrl}"
        photoLayerUrl="${photoLayerUrl}" mapLayerUrl="${mapLayerUrl}"/>
</div>
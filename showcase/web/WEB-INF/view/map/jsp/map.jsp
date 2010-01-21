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
        <s:param name="act" value="%{'mzi'}" />
    </s:url>

    <s:url id="zoomOutUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mzo'}" />
    </s:url>
    
    <s:url id="moveNorthUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mpn'}" />
    </s:url>

    <s:url id="moveSouthUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mps'}" />
    </s:url>

    <s:url id="moveWestUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mpw'}" />
    </s:url>

    <s:url id="moveEastUrl" value="%{browseMapUrl}" includeContext="false">
        <s:param name="act" value="%{'mpe'}" />
    </s:url>
        

    Map goes here:
    <map:render mapUrlHolder="${mapUrlHolder}" zoomInUrl="${zoomInUrl}" zoomOutUrl="${zoomOutUrl}" 
        panNorthUrl="${moveNorthUrl}" panSouthUrl="${moveSouthUrl}"
        panEastUrl="${moveEastUrl}" panWestUrl="${moveWestUrl}"/>
</div>
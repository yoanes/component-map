<%@ taglib prefix="c" uri="/WEB-INF/taglibs/c.tld"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<%--
  - Work around for Tomcat 5.0.28 to ensure that the JSP Expression Language is processed. 
  - Configuring this in web.xml using a jsp-property-group didn't seem to work (not sure why). 
  - Should also work with Tomcat 6.  
  --%>
<%@ tag isELIgnored="false" %>

<%-- true if the map should be displayed. --%>
<%@ attribute name="display" required="true" type="java.lang.Boolean"%>

<%-- 
  - Criteria that was used to retrieve the map. Needed to build zoom and pan URLs so 
  - that the URLs contain the state that the zoom/pan should be relative to.
  --%>
<%@ attribute name="mapResult" required="true"
    type="au.com.sensis.mobile.web.component.map.model.MapResult"  %>

<%-- If map available show the Map. --%>
<c:if test="${display}">
    <div class="map">

        <div class="mapArea">

            <object src="${mapResult.mapUrl}" id="mapImage"
                    srctype="image/png">
                <param name="mcs-transcode" value="false"/>
            </object>

        </div>

        <div class="mapControls">

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
                <s:param name="mclat"><s:property value="#attr.mapResult.mapState.coordinates.latitude"/></s:param>
                <s:param name="mclon"><s:property value="#attr.mapResult.mapState.coordinates.longitude"/></s:param>
                <s:param name="tllat"><s:property value="#attr.mapResult.mapState.mapBoundingBox.topLeft.latitude"/></s:param>
                <s:param name="tllon"><s:property value="#attr.mapResult.mapState.mapBoundingBox.topLeft.longitude"/></s:param>
                <s:param name="brlat"><s:property value="#attr.mapResult.mapState.mapBoundingBox.bottomRight.latitude"/></s:param>
                <s:param name="brlon"><s:property value="#attr.mapResult.mapState.mapBoundingBox.bottomRight.longitude"/></s:param>
                <s:param name="mz"><s:property value="#attr.mapResult.mapState.zoomLevel"/></s:param>                
            </s:url>

            <div class="zoomControls">
                <s:url id="zoomInUrl" value="%{browseMapUrl}" includeContext="false">
                    <s:param name="act" value="%{'mzi'}" />
                </s:url>
                <a id="mapZoomInButton" href="${zoomInUrl}">
                    <object src="/comp/map/images/furniture/mapInIcon.mimg" id="zoomInImage" />
                </a>

                <s:url id="zoomOutUrl" value="%{browseMapUrl}" includeContext="false">
                    <s:param name="act" value="%{'mzo'}" />
                </s:url>
                <a id="mapZoomOutButton" href="${zoomOutUrl}">
                    <object src="/comp/map/images/furniture/mapOutIcon.mimg" id="zoomOutImage" />
                </a>
            </div>

            <div class="panControls">
                <s:url id="moveNorthUrl" value="%{browseMapUrl}" includeContext="false">
                    <s:param name="act" value="%{'mpn'}" />
                </s:url>
                <a id="mapPanNorthButton" href="${moveNorthUrl}">
                    <object src="/comp/map/images/furniture/mapNorthIcon.mimg" id="moveNorthImage" />
                </a>

                <s:url id="moveSouthUrl" value="%{browseMapUrl}" includeContext="false">
                    <s:param name="act" value="%{'mps'}" />
                </s:url>
                <a id="mapPanSouthButton" href="${moveSouthUrl}">
                    <object src="/comp/map/images/furniture/mapSouthIcon.mimg" id="moveSouthImage" />
                </a>

                <s:url id="moveWestUrl" value="%{browseMapUrl}" includeContext="false">
                    <s:param name="act" value="%{'mpw'}" />
                </s:url>
                <a id="mapPanWestButton" href="${moveWestUrl}">
                    <object src="/comp/map/images/furniture/mapWestIcon.mimg" id="moveEastImage" />
                </a>

                <s:url id="moveEastUrl" value="%{browseMapUrl}" includeContext="false">
                    <s:param name="act" value="%{'mpe'}" />
                </s:url>
                <a id="mapPanEastButton" href="${moveEastUrl}">
                    <object src="/comp/map/images/furniture/mapEastIcon.mimg" id="moveWestImage" />
                </a>
            </div>

        </div>

    </div>

</c:if>
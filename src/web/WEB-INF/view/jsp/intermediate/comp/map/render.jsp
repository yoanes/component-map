<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="base" uri="/au/com/sensis/mobile/web/component/core/base/base.tld"%>
<%@ taglib prefix="logging" uri="/au/com/sensis/mobile/web/component/core/logging/logging.tld"%>
<%@ taglib prefix="crf" uri="/au/com/sensis/mobile/crf/crf.tld"%>

<c:set var="device" value="${requestScope['mapComponentDevice']}" />
<c:set var="map" value="${requestScope['mapComponentMap']}" />
<c:set var="zoomInUrl" value="${requestScope['mapComponentZoomInUrl']}" />
<c:set var="zoomOutUrl" value="${requestScope['mapComponentZoomOutUrl']}" />
<c:set var="panNorthUrl" value="${requestScope['mapComponentPanNorthUrl']}" />
<c:set var="panSouthUrl" value="${requestScope['mapComponentPanSouthUrl']}" />
<c:set var="panEastUrl" value="${requestScope['mapComponentPanEastUrl']}" />
<c:set var="panWestUrl" value="${requestScope['mapComponentPanWestUrl']}" />
<c:set var="photoLayerUrl" value="${requestScope['mapComponentPhotoLayerUrl']}" />
<c:set var="mapLayerUrl" value="${requestScope['mapComponentMapLayerUrl']}" />
<c:set var="clientSideGeneratedMapStateChangeUrl" value="${requestScope['mapComponentClientSideGeneratedMapStateChangeUrl']}" />

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />    
<logging:debug logger="${logger}" message="Entering render.tag" />

<%-- Set the default resource bundle for the current tag file. --%>    
<fmt:setBundle basename="au.com.sensis.mobile.web.component.map.map-component" />    

<%-- Figure out the name of the current component.--%>
<c:set var="componentName">
    <fmt:message key="comp.name" />
</c:set>

<a href="#map" name="map"></a>
    <div id="mapWindow">     
        <object src="${map.mapUrl.imageUrl}" id="map" srctype="image/png">
            <param name="mcs-transcode" value="false"/>
        </object>
    </div>

    <div id="mapControls">
        <div id="zoomControls">
            <object src="/comp/map/images/zoomIn_faded.mimg" id="zoomInFaded">+</object>
            
            <a id="zoomInButton" href="${zoomInUrl}" class="mapControl">
                <object src="/comp/map/images/zoomIn.mimg" id="mapZoomIn">+</object>
            </a>
            
            <object src="/comp/map/images/zoomOut_faded.mimg" id="zoomOutFaded">-</object>
            <a id="zoomOutButton" href="${zoomOutUrl}" class="mapControl">
                <object src="/comp/map/images/zoomOut.mimg" id="mapZoomOut">-</object>
            </a>
        </div>
            

        <div id="directionControls">
            <a id="panNorthButton" href="${panNorthUrl}" class="mapControl">
                <object src="/comp/map/images/north.mimg" id="mapPanNorthImage"> /\ </object>
            </a>
            
            <a id="panSouthButton" href="${panSouthUrl}" class="mapControl">
                <object src="/comp/map/images/south.mimg" id="mapPanSouthImage"> \/ </object>
            </a>

            <a id="panWestButton" href="${panWestUrl}" class="mapControl">
                <object src="/comp/map/images/west.mimg" id="mapPanWestImage"> &#60; </object>
            </a>

            <a id="panEastButton" href="${panEastUrl}" class="mapControl">
                <object src="/comp/map/images/east.mimg" id="mapPanEastImage"> &#62; </object>
            </a>
        </div>
    
    
        <div id="viewControls">
              <a id="photoButton" href="${photoLayerUrl}">
                  <object src="/comp/map/images/photo.mimg">
                      <fmt:message key="comp.photoLayer.label"/>
                  </object>
              </a>
          
              <a id="mapButton" href="${mapLayerUrl}">
                  <object src="/comp/map/images/map.mimg">
                      <fmt:message key="comp.mapLayer.label"/>
                  </object>
              </a>
        </div>
    </div>


<logging:debug logger="${logger}" message="Exiting render.tag" />
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
    
<%@ attribute name="iconImagesSrcPrefix" required="true"
    description="Prefix to use for advanced maps to retrieve icon images directly." %>

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />
<logging:debug logger="${logger}" message="Entering setup.tag" />

<%--
  - Set attributes into request scope, then include a JSP so that we can route through the 
  - ContentRenderingFramework.
  --%>
<c:set var="mapComponentDevice" scope="request" value="${device}" />
<c:set var="mapComponentMap" scope="request" value="${map}" />
<c:set var="mapComponentEmsJsUrl" scope="request" value="${emsJsUrl}" />
<c:set var="mapComponentIconImagesSrcPrefix" scope="request" value="${iconImagesSrcPrefix}" />

<jsp:include page="/WEB-INF/view/jsp/comp/map/setup.crf" />

<logging:debug logger="${logger}" message="Exiting setup.tag" />
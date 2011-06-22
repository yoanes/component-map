<%@ tag body-content="empty" isELIgnored="false" trimDirectiveWhitespaces="true" %>

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
    
<%@ attribute name="useMyLocation" required="false"
	description="Flag to include the devicelocation component plus all features associated with it." %>

<%@ attribute name="useDockForPopup" required="false"
	description="Flag to determine where the popup will go. 
		     - If it's not set then the popup will be displayed in the poi itself.
		     - Set it to true if you want the popup to go to the dock instead of updating the poi.
		     - Or pass a JS method to be called on content changed - your method will be called everytime a new popup is loaded
			to the dock. Your method will receive the current index of the popup content's array as the argument." %>

<%@ attribute name="highEndMapControls" required="false"
	description="Comma separated list of map controls you want to be included with the map on initialization." %>

<logging:logger var="logger" name="au.com.sensis.mobile.web.component.map" />
<logging:debug logger="${logger}" message="Entering setup.tag" />

<%--
  - Set attributes into request scope, then include a JSP so that we can route through the 
  - ContentRenderingFramework.
  --%>
<c:set var="mapComponentDevice" scope="request" value="${device}" />
<c:set var="mapComponentMap" scope="request" value="${map}" />
<c:set var="mapComponentEmsJsUrl" scope="request" value="${emsJsUrl}" />
<c:set var="mapComponentUseMyLocation" scope="request" value="${useMyLocation}" />
<c:set var="mapComponentUseDockForPopup" scope="request" value="${useDockForPopup}" />
<c:set var="mapComponentHighEndMapControls" scope="request" value="${highEndMapControls}" />

<jsp:include page="/WEB-INF/view/jsp/comp/map/setup.crf" />

<logging:debug logger="${logger}" message="Exiting setup.tag" />

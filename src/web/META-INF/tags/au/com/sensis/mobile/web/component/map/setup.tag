<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="core" uri="/au/com/sensis/mobile/web/component/core/core.tld"%>

<%--
  - Work around for Tomcat 5.0.28 to ensure that the JSP Expression Language is processed. 
  - Configuring this in web.xml using a jsp-property-group didn't seem to work (not sure why). 
  - Should also work with Tomcat 6.  
  --%>
<%@ tag isELIgnored="false" %>

<%@ attribute name="baseCompMcsPath" required="true" type="java.lang.String"%>

<%-- Setup components that we depend on. --%>
<core:setup baseCompMcsPath="${baseCompMcsPath}"/>

<%-- Themes for current component. --%>
<%-- TODO: really should use something equivalent to core:script to avoid duplicates. --%>
<link rel="mcs:theme" href="${baseCompMcsPath}/map/map.mthm"/>
<link rel="mcs:theme"  href="${baseCompMcsPath}/map/imageSizeCategory.mthm"/>

<%-- Scripts for current component. --%>
<core:script src="${baseCompMcsPath}/map/scripts/map-component.mscr"></core:script>
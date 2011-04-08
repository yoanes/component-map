<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<crf:link rel="stylesheet" type="text/css" href="mapComponentShowcase.css" device="${context.device}"/>

<%-- Set the default resource bundle for the current JSP. --%>    
<fmt:setBundle basename="project-environment" />    
<c:set var="emsJsUrl">
    <fmt:message key="env.ems.service.js" /><fmt:message key="env.ems.token" />
</c:set>
<c:set var="iconImagesSrcPrefix">
    <fmt:message key="env.ems.client.map.icons.img.src.prefix" />
</c:set>

<deviceport:setup device="${context.device}" />
<c:choose>
	<c:when test="${docked}">
		<map:setup device="${context.device}" map="${map}" emsJsUrl="${emsJsUrl}" 
    		iconImagesSrcPrefix="${iconImagesSrcPrefix}" useMyLocation="true" useDockForPopup="true"/>
	</c:when>
	<c:otherwise>
		<map:setup device="${context.device}" map="${map}" emsJsUrl="${emsJsUrl}" 
    		iconImagesSrcPrefix="${iconImagesSrcPrefix}" useMyLocation="true"/>
	</c:otherwise>
</c:choose>
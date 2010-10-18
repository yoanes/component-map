<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<%-- Set the default resource bundle for the current JSP. --%>    
<fmt:setBundle basename="project-environment" />    
<c:set var="emsJsUrl">
    <fmt:message key="env.ems.service.js" /><fmt:message key="env.ems.token" />
</c:set>

<deviceport:setup device="${context.device}" />
<map:setup device="${context.device}" map="${map}" emsJsUrl="${emsJsUrl}"/>


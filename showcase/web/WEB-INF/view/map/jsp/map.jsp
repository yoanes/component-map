<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<div id="map">
    Map goes here:
    <map:render display="${not empty mapResult}" mapResult="${mapResult}"/>
</div>
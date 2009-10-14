<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<div id="mapForm">

    <xf:group class="mapInput">

        <xf:input ref="ml" model="mapFormModel" class="mapLocationInput"
                id="location">
            <xf:label class="locationMessage">Map Location
            </xf:label>
        </xf:input>

        <xf:submit submission="mapFormSubmission" model="mapFormModel"
                class="mapButton"
                title="Get Map/>">
            <xf:label class="mapButtonLabel">
                <s:text name="Get Map"/>
            </xf:label>
        </xf:submit>
    </xf:group>
</div>

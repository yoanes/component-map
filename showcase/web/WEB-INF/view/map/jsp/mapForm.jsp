<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<div id="mapLocationForm">
    <h4>Map a &lt;suburb state&gt;. eg. melbourne vic</h4>
    <xf:group class="mapInput" model="mapFormModel">

        <xf:input ref="ml" model="mapFormModel" class="mapLocationInput"
                id="location">
            <xf:label class="locationMessage">
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

<div id="poisForm">
    <h4>Render (hard coded) POIs</h4>
    <xf:group class="mapInput" model="poisFormModel">

        <xf:submit submission="poisFormSubmission" model="poisFormModel"
                class="mapButton"
                title="Render POIs/>">
            <xf:label class="mapButtonLabel">
                <s:text name="Render POIs"/>
            </xf:label>
        </xf:submit>
    </xf:group>
</div>

<div id="routeForm">
    <h4>Render route between Melb and Camberwell</h4>
    <xf:group class="mapInput" model="routeFormModel">

        <xf:submit submission="routeFormSubmission" model="routeFormModel"
                class="mapButton"
                title="Render Route/>">
            <xf:label class="mapButtonLabel">
                <s:text name="Render Route"/>
            </xf:label>
        </xf:submit>
    </xf:group>
</div>

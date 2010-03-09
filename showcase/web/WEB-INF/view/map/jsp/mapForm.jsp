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
                title="Get Map">
            <xf:label class="mapButtonLabel">
                <s:text name="Get Map"/>
            </xf:label>
        </xf:submit>
    </xf:group>
</div>

<div id="poisForm">
    <h4>Render (hard coded) POIs</h4>
    <xf:group class="mapInput" model="poisFormModel">
    
        <xf:select1 model="poisFormModel" ref="search" >
            <xf:label></xf:label>
            <xf:item>
              <xf:label>WM cars near Melbourne VIC</xf:label>
              <xf:value>carsNearMelbourneVic</xf:value>
            </xf:item>
            <xf:item>
              <xf:label>WM bars near Toorak VIC</xf:label>
              <xf:value>barsNearToorakVic</xf:value>
            </xf:item>
            <xf:item>
              <xf:label>WM bassett-smith valuers near 140 Church St Brighton VIC</xf:label>
              <xf:value>bassettSmithValuersNear140ChurchStBrightonVic</xf:value>
            </xf:item>
        </xf:select1>
    
        <xf:submit submission="poisFormSubmission" model="poisFormModel"
                class="mapButton"
                title="Get POIs">
            <xf:label class="mapButtonLabel">
                <s:text name="Get POIs"/>
            </xf:label>
        </xf:submit>
    </xf:group>
</div>

<div id="routeForm">
    <h4>Get route</h4>
    <xf:group class="mapInput" model="routeFormModel">
    
        <xf:input ref="rsa" model="routeFormModel" class="mapLocationInput"
                id="routeStartAddress">
            <xf:label class="locationMessage">Start</xf:label>
        </xf:input>
        
        <xf:input ref="rea" model="routeFormModel" class="mapLocationInput"
                id="routeEndAddress">
            <xf:label class="locationMessage">End</xf:label>
        </xf:input>

        <xf:submit submission="routeFormSubmission" model="routeFormModel"
                class="mapButton"
                title="Render Route/>">
            <xf:label class="mapButtonLabel">
                <s:text name="Get Route"/>
            </xf:label>
        </xf:submit>
    </xf:group>
</div>

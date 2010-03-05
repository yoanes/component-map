<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp" />

<xf:model id="mapFormModel">

    <xf:instance>
        <si:instance>

            <si:item name="ml">
                <s:property value="location" />
            </si:item>

        </si:instance>
    </xf:instance>

    <xf:submission id="mapFormSubmission"
            action="<s:url namespace='/map' action='getMap' />"/>

</xf:model>

<xf:model id="poisFormModel">

    <xf:instance>
        <si:instance>
            <si:item name="search"></si:item>
        </si:instance>
    </xf:instance>

    <xf:submission id="poisFormSubmission"
            action="<s:url namespace='/map' action='getPois'/>"/>
</xf:model>

<xf:model id="routeFormModel">

    <xf:instance>
        <si:instance>
            <si:item name="rsa">
                <s:property value="routeStartAddress" />
            </si:item>

            <si:item name="rea">
                <s:property value="routeEndAddress" />
            </si:item>

        </si:instance>
    </xf:instance>

    <xf:submission id="routeFormSubmission"
            action="<s:url namespace='/map' action='getRoute' />"/>

</xf:model>

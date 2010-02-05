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

        </si:instance>
    </xf:instance>

    <xf:submission id="poisFormSubmission"
            action="<s:url namespace='/map' action='getPois' />"/>

</xf:model>

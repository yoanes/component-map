<jsp:directive.include file="/WEB-INF/view/common/jsp/configInclude.jsp"/>

<div id="mapLocationForm">
    <h4>Map a &lt;suburb state&gt;. eg. melbourne vic</h4>
    <form action="<s:url namespace='/map' action='getMap' includeParams="none"/>" method="post">
    
        <div>
            <label for="ctId" class="locationMessage">Cursor Type</label>
            <select id="ctId" name="ct">
                <c:choose>
                    <c:when test="${cursorTypeAsString eq 'POI_BLANK'}">
                        <option value="CROSS_HAIR">Cross Hair</option>
                        <option value="POI_BLANK" selected>Blank POI</option>
                    </c:when>
                    <c:otherwise>
                        <option value="CROSS_HAIR" selected>Cross Hair</option>
                        <option value="POI_BLANK">Blank POI</option>
                    </c:otherwise>
                </c:choose>
        
            </select>
        </div>

        <div>
            <label for="location" class="locationMessage">Location</label>
            <input type="text" id="location" name="ml" class="mapLocationInput" value="<s:property value='location' />" />
        </div>

        <input type="submit" class="mapButton" value="<s:text name='Get Map'/>" />
    </form>
</div>

<div id="poisForm">
    <h4>Render (hard coded) POIs</h4>
    
    <c:choose>
        <c:when test="${'barsNearToorakVic' eq search}">
            <c:set var="barsNearToorakVicSelected" value="selected"/>        
        </c:when>
        <c:when test="${'bassettSmithValuersNear140ChurchStBrightonVic' eq search}">
            <c:set var="bassettSmithValuersNear140ChurchStBrightonVicSelected" value="selected"/>        
        </c:when>
        <c:when test="${'cafeNearTullamarineVic' eq search}">
            <c:set var="cafeNearTullamarineVicSelected" value="selected"/>        
        </c:when>
        <c:when test="${'bassettSmithValuersNear142ChurchStBrightonVic' eq search}">
            <c:set var="bassettSmithValuersNear142ChurchStBrightonVicSelected" value="selected"/>        
        </c:when>
        <c:when test="${'restaurantsNear3006' eq search}">
            <c:set var="restaurantsNear3006Selected" value="selected"/>        
        </c:when>
        <c:otherwise>
            <c:set var="carsNearMelbourneVicSelected" value="selected"/>
        </c:otherwise>    
    </c:choose>
    
    <form action="<s:url namespace='/map' action='getPois' includeParams="none"/>" method="post">
        <select name="search" >
     	    <option ${carsNearMelbourneVicSelected} value="carsNearMelbourneVic">WM cars near Melbourne VIC</option>
          
     	    <option ${barsNearToorakVicSelected} value="barsNearToorakVic">WM bars near Toorak VIC</option>
          
     	    <option ${bassettSmithValuersNear140ChurchStBrightonVicSelected} 
                value="bassettSmithValuersNear140ChurchStBrightonVic"
                >WM bassett-smith valuers near 140 Church St Brighton VIC</option>
                
     	    <option ${cafeNearTullamarineVicSelected} value="cafeNearTullamarineVic">WM cafe near Tullamarine VIC</option>
          
     	    <option ${bassettSmithValuersNear142ChurchStBrightonVicSelected} value="bassettSmithValuersNear142ChurchStBrightonVic"
                >WM bassett-smith valuers near 142 Church St Brighton VIC</option>
                
     	    <option ${restaurantsNear3006Selected} value="restaurantsNear3006">WM restaurants near 3006 (10 results per page)</option>
        </select>

        <input type="submit" class="mapButton" value="<s:text name='Get POIs'/>"/>    
    </form>
</div>

<div id="routeForm">
    <h4>Get route</h4>
    
    <c:choose>
        <c:when test="${'roadNoTolls' eq routingOption.shortName}">
            <c:set var="roadNoTollsSelected" value="selected"/>        
        </c:when>
        <c:when test="${'foot' eq routingOption.shortName}">
            <c:set var="footSelected" value="selected"/>        
        </c:when>
        <c:otherwise>
            <c:set var="roadTollsSelected" value="selected"/>
        </c:otherwise>    
    </c:choose>
    
    <form action="<s:url namespace='/map' action='getRoute' includeParams="none"/>" method="post">
    
        <div>
            <label for="routingOptionId" class="locationMessage">Travel By</label>
            <select id="routingOptionId" name="rop" class="mapLocationInput">
                <option ${roadTollsSelected} value="roadTolls">Car (tolls)</option>
                <option ${roadNoTollsSelected} value="roadNoTolls">Car (no tolls)</option>
                <option ${footSelected} value="foot">Walking</option>
        	</select>
        </div>
    
        <div>
            <label for="routeStartAddress" class="locationMessage">Start</label>
            <input type="text" name="rsa" class="mapLocationInput" id="routeStartAddress"
                value="<s:property value='routeStartAddress' />"/>
        </div>            

        <div>
            <label for="routeEndAddress" class="locationMessage">End</label>
            <input type="text" name="rea" class="mapLocationInput" id="routeEndAddress"
                value="<s:property value='routeEndAddress' />"/>
        </div>
        
        <input type="submit" class="mapButton" value="<s:text name='Get Route'/>"/>    
    </form>
</div>

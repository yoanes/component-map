package au.com.sensis.mobiles.map.showcase.selenium;

import au.com.sensis.mobiles.map.showcase.selenium.fixture.HomePage;

/**
 * TODO: placeholder to test build scripts. Test currently fails.
 *
 * Tests the map page.
 * In order to run this test start tomcat, then start the selenium server, run this as a JUnit test.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class MapIntegrationTestCase extends AbstractSeleniumIntegrationTestCase {

    /**
     * Opens the map page for an address.
     */
    public void testGetMap() throws Exception {
        openHome();

        final HomePage homePage =
            (HomePage) getPageFixtureFactory().createPageFixture(HomePage.class);
//        homePage.clickOnGetMap();
    }
}

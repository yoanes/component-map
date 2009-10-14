package au.com.sensis.mobiles.map.showcase.selenium;


/**
 * Super class for all selenium test cases.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public abstract class AbstractSeleniumIntegrationTestCase
    extends au.com.sensis.wireless.test.selenium.AbstractSeleniumIntegrationTestCase {

    /**
     * TODO: shouldn't have to override.
     */
    @Override
    protected void openHome() {
        openUrl("/map-component-showcase/home.action");
    }
}

package au.com.sensis.mobiles.map.showcase.selenium.fixture;

import static junit.framework.Assert.assertTrue;

import com.thoughtworks.selenium.Selenium;

/**
 * Assertions and actions for the Map Component Showcase Home Page.
 *
 * @author Adrian.Koh2@sensis.com.au (based on Heather's work in Whereis Mobile)
 */
public class HomePage extends AbstractPageFixture {

    public HomePage(final Selenium selenium) {
        super(selenium);
    }

    public void clickOnGetAMap() {
        getBrowser().click("getAMap");
        waitForPage();
    }

    @Override
    protected void assertBody() {
        // top links
        assertTrue(getBrowser().isTextPresent("Map Location"));
    }
}

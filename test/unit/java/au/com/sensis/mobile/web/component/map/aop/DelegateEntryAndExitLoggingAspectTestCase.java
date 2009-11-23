package au.com.sensis.mobile.web.component.map.aop;

import org.junit.Test;

import au.com.sensis.mobile.web.component.logging.aop.AbstractEntryAndExitLoggingAspect;
import au.com.sensis.mobile.web.component.logging.aop.BaseEntryAndExitLoggingAspectTestCase;

/**
 * Unit test {@link DelegateEntryAndExitLoggingAspect}.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DelegateEntryAndExitLoggingAspectTestCase extends
        BaseEntryAndExitLoggingAspectTestCase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected AbstractEntryAndExitLoggingAspect createObjectUnderTest() {
        return new DelegateEntryAndExitLoggingAspect();
    }

    @Test
    public void testHandleLog4jNDC() throws Throwable {
        super.setupForDoHandleLog4jNDC();

        replay();

        ((DelegateEntryAndExitLoggingAspect) getObjectUnderTest())
                .handleLog4jNDC(getProceedingJoinPointStub());

        assertForDoHandleLog4jNDC();
    }
}

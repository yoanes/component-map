package au.com.sensis.mobile.web.component.map.aop;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * AOP Aspect that logs the entry and exit into and out of delegates in the current component.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
@Aspect
public class DelegateEntryAndExitLoggingAspect
        extends
        au.com.sensis.mobile.web.component.core.aop.AbstractEntryAndExitLoggingAspect {

    private static Logger logger = Logger.getLogger(DelegateEntryAndExitLoggingAspect.class);

    /**
     * AOP Pointcut matching all delegates in the current component.
     */
    @Pointcut("bean(map.*Delegate)")
    protected final void mapDelegate() { }

    /**
     * AOP Around advice that delegates to
     * {@link #doHandleLog4jNDC(ProceedingJoinPoint)}.
     *
     * @param proceedingJoinPoint
     *            {@link ProceedingJoinPoint} representing the join point being
     *            advised.
     * @return Object returned from the join point.
     * @throws Throwable
     *             Thrown if any error occurs.
     * @see {@link #doHandleLog4jNDC(ProceedingJoinPoint)}
     */
    @Around("mapDelegate()")
    public final Object handleLog4jNDC(
            final ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        return super.doHandleLog4jNDC(proceedingJoinPoint);
    }

    /**
     * @return Logger used for this class.
     */
    @Override
    protected final Logger getLogger() {
        return logger;
    }
}

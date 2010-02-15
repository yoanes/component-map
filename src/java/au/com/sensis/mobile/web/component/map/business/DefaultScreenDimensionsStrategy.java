package au.com.sensis.mobile.web.component.map.business;

import au.com.sensis.mobile.web.component.map.business.MapDelegateImpl.ScreenDimensionsStrategy;
import au.com.sensis.wireless.manager.mapping.ScreenDimensions;
import au.com.sensis.wireless.web.mobile.MobileContext;

/**
 * Default {@link ScreenDimensionsStrategy} based on WhereisMobile.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class DefaultScreenDimensionsStrategy implements
        ScreenDimensionsStrategy {

    /**
     * {@inheritDoc}
     */
    public ScreenDimensions createScreenDimensions(
            final MobileContext mobileContext) {
        if (mobileContext.getDevice() == null) {
            throw new IllegalStateException(
                    "mobileContext.getDevice() is null. This should never happen !!!");
        } else {
            return new ScreenDimensions(mobileContext.getDevice().getPixelsX(),
                    mobileContext.getDevice().getPixelsY());
        }
    }

}

package au.com.sensis.mobile.web.component.map.device;

import au.com.sensis.mobile.web.component.core.device.AbstractDeviceConfigRegistry;
import au.com.sensis.mobile.web.component.map.device.generated.DeviceConfigType;
import au.com.sensis.wireless.common.utils.jaxb.XMLBinder;

/**
 * Registry of device configuration for the current component.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapDeviceConfigRegistry extends AbstractDeviceConfigRegistry<DeviceConfigType> {

    public MapDeviceConfigRegistry(final String deviceConfigClasspath, final XMLBinder xmlBinder,
            final DeviceConfigType defaultDeviceConfig) {
        super(deviceConfigClasspath, xmlBinder, defaultDeviceConfig);
    }


}

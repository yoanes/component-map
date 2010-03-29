package au.com.sensis.mobile.web.component.map.device;

import au.com.sensis.mobile.web.component.core.device.AbstractDeviceConfigRegistry;
import au.com.sensis.mobile.web.component.core.device.generated.AbstractDeviceConfig;
import au.com.sensis.mobile.web.component.map.device.generated.DeviceConfig;
import au.com.sensis.wireless.common.utils.jaxb.XMLBinder;

/**
 * Registry of device configuration for the current component.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class MapDeviceConfigRegistry extends AbstractDeviceConfigRegistry {

    /**
     * Default constructor.
     *
     * @param deviceConfigClasspath
     *            device-config.xml file to use on the classpath.
     * @param xmlBinder
     *            {@link XMLBinder} to use to parse the config file.
     */
    public MapDeviceConfigRegistry(final String deviceConfigClasspath,
            final XMLBinder xmlBinder) {
        super(deviceConfigClasspath, xmlBinder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<? extends AbstractDeviceConfig> getDeviceConfigType() {
        return DeviceConfig.class;
    }


}

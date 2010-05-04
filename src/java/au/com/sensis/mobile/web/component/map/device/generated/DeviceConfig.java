//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.3-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.05.03 at 12:15:57 PM EST 
//


package au.com.sensis.mobile.web.component.map.device.generated;

import javax.annotation.Generated;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import au.com.sensis.mobile.web.component.core.device.generated.AbstractDeviceConfig;


/**
 * 
 *                 Defines a set of bundles.
 *             
 * 
 * <p>Java class for DeviceConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DeviceConfig">
 *   &lt;complexContent>
 *     &lt;extension base="{http://mobile.sensis.com.au/web/component/core/device}AbstractDeviceConfig">
 *       &lt;sequence>
 *         &lt;element name="generateServerSideMap" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="enableIntermediateMap" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="enableHiEndMap" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DeviceConfig", propOrder = {
    "generateServerSideMap",
    "enableIntermediateMap",
    "enableHiEndMap"
})
@Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
public class DeviceConfig
    extends AbstractDeviceConfig
{

    @XmlElement(defaultValue = "true")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    protected boolean generateServerSideMap = true;
    @XmlElement(defaultValue = "false")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    protected boolean enableIntermediateMap = false;
    @XmlElement(defaultValue = "false")
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    protected boolean enableHiEndMap = false;

    /**
     * Gets the value of the generateServerSideMap property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public boolean isGenerateServerSideMap() {
        return generateServerSideMap;
    }

    /**
     * Sets the value of the generateServerSideMap property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public void setGenerateServerSideMap(boolean value) {
        this.generateServerSideMap = value;
    }

    /**
     * Gets the value of the enableIntermediateMap property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public boolean isEnableIntermediateMap() {
        return enableIntermediateMap;
    }

    /**
     * Sets the value of the enableIntermediateMap property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public void setEnableIntermediateMap(boolean value) {
        this.enableIntermediateMap = value;
    }

    /**
     * Gets the value of the enableHiEndMap property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public boolean isEnableHiEndMap() {
        return enableHiEndMap;
    }

    /**
     * Sets the value of the enableHiEndMap property.
     * 
     */
    @Generated(value = "com.sun.tools.xjc.Driver", date = "2010-05-03T12:15:57+10:00", comments = "JAXB RI v2.1.3-b01-fcs")
    public void setEnableHiEndMap(boolean value) {
        this.enableHiEndMap = value;
    }

}

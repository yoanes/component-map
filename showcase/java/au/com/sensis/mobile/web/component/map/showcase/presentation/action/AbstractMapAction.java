package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import au.com.sensis.mobile.web.component.core.util.XmlHttpRequestDetector;
import au.com.sensis.mobile.web.component.map.business.MapDelegate;
import au.com.sensis.mobile.web.component.map.model.Map;
import au.com.sensis.mobile.web.component.map.showcase.business.logic.LocationDelegate;
import au.com.sensis.mobile.web.component.map.showcase.presentation.form.MapForm;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Base class for all map actions.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public abstract class AbstractMapAction extends BusinessAction implements
        ModelDriven<MapForm>, ServletRequestAware {

    private XmlHttpRequestDetector xmlHttpRequestDetector;

    private HttpServletRequest httpServletRequest;

    private Map map;

    private LocationDelegate locationDelegate;

    private MapDelegate mapDelegate;

    /**
     * Returns either a standard success result name or an ajax success result name.
     *
     * @return Either a standard success result name or an ajax success result name.
     */
    protected final String successOrAjaxSuccess() {
        if (getXmlHttpRequestDetector().isXmlHttpRequest(getHttpServletRequest())) {
            return ResultName.AJAX_SUCCESS_RESULT;
        } else {
            return au.com.sensis.mobile.web.testbed.ResultName.SUCCESS;
        }
    }

    /**
     * @return the xmlHttpRequestDetector
     */
    public XmlHttpRequestDetector getXmlHttpRequestDetector() {
        return xmlHttpRequestDetector;
    }

    /**
     * @param xmlHttpRequestDetector the xmlHttpRequestDetector to set
     */
    public void setXmlHttpRequestDetector(
            final XmlHttpRequestDetector xmlHttpRequestDetector) {
        this.xmlHttpRequestDetector = xmlHttpRequestDetector;
    }

    /**
     * @see ServletRequestAware#setServletRequest(HttpServletRequest)
     * @param httpServletRequest {@link HttpServletRequest}
     */
    public void setServletRequest(final HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    /**
     * @return the httpServletRequest
     */
    private HttpServletRequest getHttpServletRequest() {
        return httpServletRequest;
    }

    /**
     * @return the map
     */
    public Map getMap() {
        return map;
    }

    /**
     * @param map
     *            the map to set
     */
    public void setMap(final Map map) {
        this.map = map;
    }

    /**
     * @return the locationDelegate
     */
    public LocationDelegate getLocationDelegate() {
        return locationDelegate;
    }

    /**
     * @param locationDelegate
     *            the locationDelegate to set
     */
    public void setLocationDelegate(final LocationDelegate locationDelegate) {
        this.locationDelegate = locationDelegate;
    }

    /**
     * @return the mapDelegate
     */
    public MapDelegate getMapDelegate() {
        return mapDelegate;
    }

    /**
     * @param mapDelegate
     *            the mapDelegate to set
     */
    public void setMapDelegate(final MapDelegate mapDelegate) {
        this.mapDelegate = mapDelegate;
    }
}

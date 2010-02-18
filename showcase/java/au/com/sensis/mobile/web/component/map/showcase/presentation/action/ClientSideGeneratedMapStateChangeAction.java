package au.com.sensis.mobile.web.component.map.showcase.presentation.action;

import org.apache.log4j.Logger;

import au.com.sensis.mobile.web.component.map.showcase.presentation.form.ManipulateMapForm;
import au.com.sensis.mobile.web.testbed.ResultName;
import au.com.sensis.mobile.web.testbed.presentation.framework.BusinessAction;

import com.opensymphony.xwork2.ModelDriven;

/**
 * Demonstrates how to accept a state change notification for a client side
 * generated map.
 *
 * @author Adrian.Koh2@sensis.com.au
 */
public class ClientSideGeneratedMapStateChangeAction extends BusinessAction
        implements ModelDriven<ManipulateMapForm> {

    private static Logger logger =
            Logger.getLogger(ClientSideGeneratedMapStateChangeAction.class);

    private ManipulateMapForm manipulateMapForm;

    /**
     * Executes this action and returns a result name.
     *
     * @return result name.
     */
    public String execute() {
        if (logger.isInfoEnabled()) {
            logger.info("ClientSideGeneratedMapStateChangeAction called "
                    + "with state change payload: " + getModel().toString());
            logger.info("In a real app, you would update session state "
                    + "with the state change info.");
        }

        return ResultName.SUCCESS;
    }

    /**
     * @return Model being used to drive this action.
     * @see com.opensymphony.xwork2.ModelDriven#getModel()
     */
    public ManipulateMapForm getModel() {
        if (manipulateMapForm == null) {
            manipulateMapForm = new ManipulateMapForm();
        }
        return manipulateMapForm;
    }
}

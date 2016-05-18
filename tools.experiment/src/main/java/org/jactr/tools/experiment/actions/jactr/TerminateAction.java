package org.jactr.tools.experiment.actions.jactr;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.tools.experiment.actions.AbstractAction;
import org.jactr.tools.experiment.impl.IVariableContext;

public class TerminateAction extends AbstractAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(TerminateAction.class);
  
  public TerminateAction(ACTRRuntime runtime)
  {
	super(runtime);
  }

  public void fire(IVariableContext context)
  {
    IController controller = getRuntime().getController();
    if(controller!=null && controller.isRunning())
      controller.stop();
  }

}

package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.AbstractAction;
import org.jactr.tools.experiment.impl.IVariableContext;

public class EndExperimentAction extends AbstractAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(EndExperimentAction.class);
  
  private final IExperiment _experiment;
  
  public EndExperimentAction(ACTRRuntime runtime, IExperiment experiment)
  {
	super(runtime);
    _experiment = experiment;
  }

  public void fire(IVariableContext context)
  {
    _experiment.stop();
  }

}

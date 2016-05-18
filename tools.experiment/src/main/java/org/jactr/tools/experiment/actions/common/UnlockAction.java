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

public class UnlockAction extends AbstractAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(UnlockAction.class);
  
  private IExperiment _experiment;
  private String _name;
  
  public UnlockAction(ACTRRuntime runtime, String lockName, IExperiment experiment)
  {
	super(runtime);
    _experiment = experiment;
    _name = lockName;
  }

  public void fire(IVariableContext context)
  {
    String lock = _experiment.getVariableResolver().resolve(_name, context).toString();
   _experiment.getLockManager().unlock(lock); 
  }

}

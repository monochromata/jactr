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

public class FireAction extends AbstractAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(FireAction.class);
  private String _triggerName;
  private IExperiment _experiment;
  
  
  public FireAction(ACTRRuntime runtime, IExperiment experiment, String trigger)
  {
	super(runtime);
    _experiment = experiment;
    _triggerName = trigger;
  }

  public void fire(IVariableContext context)
  {
    String triggerName = _experiment.getVariableResolver().resolve(_triggerName, context).toString();
    _experiment.getTriggerManager().fire(triggerName, context);
  }

}

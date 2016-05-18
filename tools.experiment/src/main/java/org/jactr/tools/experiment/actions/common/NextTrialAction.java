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
import org.jactr.tools.experiment.trial.ITrial;

public class NextTrialAction extends AbstractAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(NextTrialAction.class);

  private IExperiment                _experiment;

  private String                     _trialName;

  public NextTrialAction(ACTRRuntime runtime, IExperiment experiment, String trialName)
  {
	super(runtime);
    _experiment = experiment;
    _trialName = trialName;
  }

  public void fire(IVariableContext context)
  {
    String trialName = _experiment.getVariableResolver().resolve(_trialName,
        context).toString();
    for (ITrial trial : _experiment.getTrials())
      if (trial.getId().equals(trialName))
      {
        _experiment.setNextTrial(trial);
        return;
      }
    
    throw new IllegalArgumentException(String.format("Could not find trial %s", trialName));
  }

}

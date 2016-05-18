package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.jactr.StopModelAction;
import org.w3c.dom.Element;

public class StopModelHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "stop-model";
  }

  public IAction process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    return new StopModelAction(runtime, experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
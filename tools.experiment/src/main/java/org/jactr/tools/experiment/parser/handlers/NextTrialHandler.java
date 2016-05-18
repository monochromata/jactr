package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.NextTrialAction;
import org.w3c.dom.Element;

public class NextTrialHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "next-trial";
  }

  public IAction process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    return new NextTrialAction(runtime, experiment, element.getAttribute("id"));
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
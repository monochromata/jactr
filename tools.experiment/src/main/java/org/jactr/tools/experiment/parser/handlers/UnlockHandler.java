package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.UnlockAction;
import org.w3c.dom.Element;

public class UnlockHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "unlock";
  }

  public IAction process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    return new UnlockAction(runtime, element.getAttribute("name"), experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.jactr.ClearAction;
import org.w3c.dom.Element;

public class ClearModelHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "clear";
  }

  public IAction process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    return new ClearAction(runtime, element.getAttribute("models"), element
        .getAttribute("buffers"), element.getAttribute("modules"),
        experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
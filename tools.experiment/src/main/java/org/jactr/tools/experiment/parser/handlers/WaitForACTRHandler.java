package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.jactr.WaitForACTRAction;
import org.w3c.dom.Element;

public class WaitForACTRHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "wait-for-actr";
  }

  public IAction process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    boolean waitForStart = true;
    if (element.hasAttribute("start"))
      waitForStart = Boolean.parseBoolean(element.getAttribute("start"));

    return new WaitForACTRAction(runtime, waitForStart);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
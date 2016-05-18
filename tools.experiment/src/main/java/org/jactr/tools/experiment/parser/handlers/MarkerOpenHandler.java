package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.jactr.MarkerAction;
import org.w3c.dom.Element;

public class MarkerOpenHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "marker-open";
  }

  public IAction process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    return new MarkerAction(runtime,
    		element.getAttribute("models"),
    		element.getAttribute("type"),
    		element.getAttribute("name"),
    		true, experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
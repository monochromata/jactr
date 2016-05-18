package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.RecordAction;
import org.w3c.dom.Element;

public class RecordHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "record";
  }

  public IAction process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    return new RecordAction(runtime, element, experiment);
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
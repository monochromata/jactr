package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.w3c.dom.Element;

public interface INodeHandler<T>
{

  public String getTagName();
  
  public T process(ACTRRuntime runtime, Element element, IExperiment experiment);
  
  public boolean shouldDecend();
}

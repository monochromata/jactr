package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.parser.ExperimentParser;
import org.jactr.tools.experiment.trial.ITrial;
import org.w3c.dom.Element;

public class ActionHandlerHandler implements INodeHandler<ITrial>
{
  
  /**
   * 
   */
  private final ExperimentParser experimentParser;

  /**
   * @param experimentParser
   */
  public ActionHandlerHandler(ExperimentParser experimentParser)
  {
    this.experimentParser = experimentParser;
  }

  public String getTagName()
  {
    return "action-handler";
  }

  public ITrial process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    /*
     * instantiate a new action handler..
     */
    String className = element.getAttribute("class");
    try
    {
      @SuppressWarnings("unchecked")
      INodeHandler<IAction> handler = (INodeHandler<IAction>) getClass()
          .getClassLoader().loadClass(className).getConstructor(ACTRRuntime.class)
          .newInstance(runtime);
      this.experimentParser.addActionHandler(handler);
    }
    catch (Exception e)
    {
      ExperimentParser.LOGGER.error("Could not create new action-handler for " + className,
          e);
    }
    return null;
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
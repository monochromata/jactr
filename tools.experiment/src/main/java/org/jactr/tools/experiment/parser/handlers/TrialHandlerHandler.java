package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.parser.ExperimentParser;
import org.jactr.tools.experiment.trial.ITrial;
import org.w3c.dom.Element;

public class TrialHandlerHandler implements INodeHandler<ITrial>
{
  /**
   * 
   */
  private final ExperimentParser experimentParser;

  /**
   * @param experimentParser
   */
  public TrialHandlerHandler(ExperimentParser experimentParser)
  {
    this.experimentParser = experimentParser;
  }

  public String getTagName()
  {
    return "trial-handler";
  }

  public ITrial process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    /*
     * instantiate a new action handler..
     */
    String className = element.getAttribute("class");
    try
    {
      INodeHandler<ITrial> handler = (INodeHandler<ITrial>) getClass()
          .getClassLoader().loadClass(className).newInstance();
      this.experimentParser.addTrialHandler(handler);
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
    // TODO Auto-generated method stub
    return false;
  }
}
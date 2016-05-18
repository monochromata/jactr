package org.jactr.tools.experiment.parser.handlers;

import org.jactr.core.runtime.ACTRRuntime;
/*
 * default logging
 */
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.parser.ExperimentParser;
import org.jactr.tools.experiment.trial.ITrial;
import org.jactr.tools.experiment.trial.impl.Trial;
import org.w3c.dom.Element;

public class TrialHandler implements INodeHandler<ITrial>
{
  /**
   * 
   */
  private final ExperimentParser experimentParser;

  /**
   * @param experimentParser
   */
  public TrialHandler(ExperimentParser experimentParser)
  {
    this.experimentParser = experimentParser;
  }

  public String getTagName()
  {
    return "trial";
  }

  public ITrial process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    String id = element.getAttribute("id");
    return new Trial(id + (this.experimentParser._trialCount++), experiment);
  }

  public boolean shouldDecend()
  {
    return true;
  }
}
package org.jactr.tools.experiment.bootstrap;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.reality.CommonReality;
import org.commonreality.reality.impl.DefaultReality;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;

public class Main
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory.getLog(Main.class);

  /**
   * @param args
   */
  public static void main(String[] args)
  {
	final DefaultReality reality = DefaultReality.newInstanceThatNeedsToBePreparedWithACommonReality();
	final CommonReality cr = new CommonReality(reality);
	reality.prepare(cr);
	final ACTRRuntime runtime = new ACTRRuntime(cr, ACTRRuntime.DEFAULT_WORKING_DIRECTORY);
    final IExperiment experiment = StartModelExperiments.loadExperiment(runtime, args[0], null);
    experiment.start();
  }

}

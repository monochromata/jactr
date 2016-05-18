package org.jactr.tools.instantiation;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.instrument.AbstractInstrument;

@Deprecated
public class InstantiationTracker extends AbstractInstrument
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(InstantiationTracker.class);

  public InstantiationTracker(ACTRRuntime runtime)
  {
	  super(runtime);
  }
  
  public void initialize()
  {
  }

  public void install(IModel model)
  {
  }

  public void uninstall(IModel model)
  {
  }

}

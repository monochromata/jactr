package org.jactr.tools.shell;

import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.instrument.AbstractInstrument;

public class BeanShellInterface extends AbstractInstrument
{

  public BeanShellInterface(ACTRRuntime runtime)
  {
	  super(runtime);
  }
	
  public void initialize()
  {
  }

  final public void install(IModel model)
  {
    RuntimeListener.setEnabled(model.getRuntime(), true);
  }

  final public void uninstall(IModel model)
  {
    RuntimeListener.setEnabled(model.getRuntime(), false);
  }

}

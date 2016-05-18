package org.jactr.tools.experiment.actions;

import org.jactr.tools.experiment.impl.IVariableContext;

/**
 * TODO
 * 
 * <p>Classes implementing this interface must provide a 1-argument constructor
 * consuming an instance of {@link org.jactr.core.runtime.ACTRRuntime}.</p>
 */
public interface IAction
{
  public void fire(IVariableContext context);
}

package org.jactr.tools.experiment.actions;

import org.jactr.core.runtime.ACTRRuntime;

/**
 * An abstract base class for actions with a 1-argument constructor consuming
 * an {@link ACTRRuntime} instance.
 */
public abstract class AbstractAction implements IAction {

	private final ACTRRuntime _runtime;

	public AbstractAction(ACTRRuntime runtime)
	{
		_runtime = runtime;
	}
	
	protected ACTRRuntime getRuntime()
	{
		return _runtime;
	}
}

package org.jactr.instrument;

import org.jactr.core.runtime.ACTRRuntime;

/**
 * An abstract base class for instruments.
 */
public abstract class AbstractInstrument implements IInstrument {

	private final ACTRRuntime _runtime;
	
	public AbstractInstrument(ACTRRuntime runtime) {
		_runtime = runtime;
	}
	
	@Override
	public ACTRRuntime getRuntime() {
		return _runtime;
	}
	
}

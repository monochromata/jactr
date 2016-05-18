package org.jactr.core.models;

import org.jactr.core.runtime.ACTRRuntime;

/**
 * A factory for a model that contains only the modules defined by
 * {@link AbstractModelFactory}, with empty declarative memory and
 * no productions, buffers or parameters.
 */
public class BootstrapModelFactory extends AbstractModelFactory {

	public BootstrapModelFactory(ACTRRuntime runtime) {
		super(runtime, "bootstrap");
	}

}

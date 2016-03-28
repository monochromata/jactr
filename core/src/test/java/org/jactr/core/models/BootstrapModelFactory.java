package org.jactr.core.models;

/**
 * A factory for a model that contains only the modules defined by
 * {@link AbstractModelFactory}, with empty declarative memory and
 * no productions, buffers or parameters.
 */
public class BootstrapModelFactory extends AbstractModelFactory {

	public BootstrapModelFactory() {
		super("bootstrap");
	}

}

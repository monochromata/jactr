package org.jactr.core.fluent;

/**
 * Signals problems while constructing a jACT-R model.
 * 
 * <p>The exception is unchecked because it will rarely be possible to handle
 * it in production code in a meaningful way. It typically signals access to
 * unknown model elements. This exception should be handled by e.g. a model
 * construction wizard or a model parser. For models created programmatically
 * this exception might go unhandled because the access to model elements will
 * be fixed at the end of development and should work without raising (i.e.
 * not without catching) any exceptions.</p>
 */
public class BuilderException extends RuntimeException {

	private static final long serialVersionUID = -5129210971533673387L;

	public BuilderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BuilderException(String message, Throwable cause) {
		super(message, cause);
	}

	public BuilderException(String message) {
		super(message);
	}

	public BuilderException(Throwable cause) {
		super(cause);
	}

}

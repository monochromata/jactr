package org.jactr.core.fluent.impl;

/**
 * An object that may be in an incomplete state and may be completed once.
 */
public interface ICompletable {

	/**
	 * Complete the object. Subsequent invocations after the first invocation
	 * will not alter the state of the then-completed object.
	 */
	public void complete();
	
}

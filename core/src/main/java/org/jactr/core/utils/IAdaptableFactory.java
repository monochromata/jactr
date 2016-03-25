package org.jactr.core.utils;

/*
 * default logging
 */

/**
 * an interface for creating an adaptor for a given object
 * 
 * @author harrison
 */
public interface IAdaptableFactory
{

	/**
	 * @param sourceObject TODO
	 * @param <T> TODO
	 * @return TODO
	 */
  public <T> T adapt(Object sourceObject);

  /**
   * return true if we should cache this value for the life of the source object
   * 
   * @return TODO
   */
  public boolean shouldCache();

  /**
   * return true if we should use a soft cache (clears on memory pressure)
   * 
   * @return TODO
   */
  public boolean shouldSoftCache();
}

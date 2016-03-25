package org.jactr.tools.itr.ortho;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Map;

/**
 * describes a slice of the orthogonal parameter space search.
 * 
 * @author harrison
 */
public interface ISlice
{

  /**
   * unique id of the slice, this is typically the slice number
   * 
   * @return TODO
   */
  public long getId();

  /**
   * the parameter values associated with the manipulated parameter names.
   * The values in the map are typically just strings
   * 
   * @return TODO
   */
  public Map<String, Object> getParameterValues();
  
  /**
   * @return the first iteration index included in this
   * slice
   */
  public long getFirstIteration();
  
  /**
   * @return the last iteration index included in this slice
   */
  public long getLastIteration();
  
  /**
   * @return return the working directoryies of each run relative to 
   * user.dir
   */
  public Collection<String> getWorkingDirectories();
  
  public Object getProperty(String property);
}

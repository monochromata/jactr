package org.jactr.tools.itr.ortho;

import java.util.Collection;

import org.jactr.core.model.IModel;

/*
 * default logging
 */

public interface ISliceListener
{
  /**
   * called when a new slice is starting
   * 
   * @param slice TODO
   */
  public void startSlice(ISlice slice);
  
  /**
   * called just before the models are to be run, but after
   * their parameters have been set
   * 
   * @param slice TODO
   * @param iteration TODO
   * @param models TODO
   */
  public void startIteration(ISlice slice, long iteration,
      Collection<IModel> models);
  
  public void stopIteration(ISlice slice, long iteration,
      Collection<IModel> models);
  
  /**
   * called after a slice has completed
   * 
   * @param slice TODO
   */
  public void stopSlice(ISlice slice);
}

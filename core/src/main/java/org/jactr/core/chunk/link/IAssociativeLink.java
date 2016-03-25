package org.jactr.core.chunk.link;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.utils.parameter.IParameterized;

/*
 * default logging
 */

/**
 * an associative link between two chunks, j and i, where i (usually) contains a
 * reference to j and j spreads activation to i. The amount of activaqtion is
 * usually scaled by the strength of the association.
 * 
 * @author harrison
 */
public interface IAssociativeLink extends IParameterized
{
  
  static public final String STRENGTH_PARAM = "Strength";

  /**
   * get the chunk containing the reference to j
   * 
   * @return TODO
   */
  public IChunk getIChunk();

  /**
   * get the chunk that is contained by i and will spread activation to it.
   * 
   * @return TODO
   */
  public IChunk getJChunk();

  /**
   * strength of the association
   * 
   * @return TODO
   */
  public double getStrength();
  
  /**
   * copy the parameters from link to this, but only if they have the same i and j
   * 
   * @param link TODO
   * @throws IllegalArgumentException TODO
   */
  public void copy(IAssociativeLink link) throws IllegalArgumentException;
}

package org.jactr.tools.analysis.production.relationships;

/*
 * default logging
 */
import org.antlr.runtime.tree.CommonTree;
import org.jactr.tools.analysis.production.endstates.BufferEndState;

/**
 * describes how two productions are related to each other.
 * 
 * @author harrison
 */
public interface IRelationship
{

  /**
   * @return the base production the {@link #getTailProduction()} is compared to
   */
  public CommonTree getHeadProduction();
  
  /**
   * @return the production that is being compared to {@link #getHeadProduction()}
   */
  public CommonTree getTailProduction();
  
  public double getScore();
  
  public double getScore(String bufferName);
}

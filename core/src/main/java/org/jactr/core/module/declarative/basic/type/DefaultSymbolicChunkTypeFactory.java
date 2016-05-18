package org.jactr.core.module.declarative.basic.type;

/*
 * default logging
 */
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.chunktype.basic.BasicSymbolicChunkType;
import org.jactr.core.runtime.ACTRRuntime;

public class DefaultSymbolicChunkTypeFactory implements
    ISymbolicChunkTypeFactory
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(DefaultSymbolicChunkTypeFactory.class);

  private final ACTRRuntime _runtime;
  
  public DefaultSymbolicChunkTypeFactory(ACTRRuntime runtime)
  {
	_runtime = runtime;
  }

  public ISymbolicChunkType newSymbolicChunkType()
  {
    return new BasicSymbolicChunkType(_runtime);
  }

  public void bind(ISymbolicChunkType symbolic, IChunkType wrapper,
      Collection<IChunkType> parents)
  {
    BasicSymbolicChunkType ct = (BasicSymbolicChunkType) symbolic;
    ct.bind(wrapper, parents);
  }

  public void unbind(ISymbolicChunkType symbolic)
  {
    BasicSymbolicChunkType ct = (BasicSymbolicChunkType) symbolic;
    ct.bind(null, Collections.EMPTY_LIST);
  }

  public void merge(ISymbolicChunkType master, ISymbolicChunkType mergie)
  {
    // noop

  }

  public void dispose(ISymbolicChunkType symbolic)
  {
    symbolic.dispose();
  }

}

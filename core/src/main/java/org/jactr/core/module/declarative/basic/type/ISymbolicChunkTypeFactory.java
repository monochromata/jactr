package org.jactr.core.module.declarative.basic.type;

/*
 * default logging
 */
import java.util.Collection;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;

/**
 * <p>Classes implementing this interface need to supply a 1-argument constructor
 * that consumes an instance of {@link org.jactr.core.runtime.ACTRRuntime}.</p>
 */
public interface ISymbolicChunkTypeFactory
{

  public ISymbolicChunkType newSymbolicChunkType();

  public void bind(ISymbolicChunkType symbolic, IChunkType wrapper,
      Collection<IChunkType> parents);

  public void unbind(ISymbolicChunkType symbolic);

  public void merge(ISymbolicChunkType master, ISymbolicChunkType mergie);

  public void dispose(ISymbolicChunkType symbolic);
}

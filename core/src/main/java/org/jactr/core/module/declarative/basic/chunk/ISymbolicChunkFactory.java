package org.jactr.core.module.declarative.basic.chunk;

/*
 * default logging
 */
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;

/**
 * factory for the creation, binding, and destruction of theoretically motivated
 * symbolic chunk components
 * 
 * @author harrison
 */
public interface ISymbolicChunkFactory
{

  public ISymbolicChunk newSymbolicChunk();

  public void bind(ISymbolicChunk symbolicChunk, IChunk chunkWrapper,
      IChunkType type);

  /**
   * merge the copy into the master
   * 
   * @param master TODO
   * @param copy TODO
   */
  public void merge(ISymbolicChunk master, ISymbolicChunk copy);

  public void unbind(ISymbolicChunk symbolicChunk);

  public void dispose(ISymbolicChunk symbolicChunk);

  /**
   * copy symbolic elements
   * 
   * @param source TODO
   * @param destination TODO
   */
  public void copy(ISymbolicChunk source, ISymbolicChunk destination);
}

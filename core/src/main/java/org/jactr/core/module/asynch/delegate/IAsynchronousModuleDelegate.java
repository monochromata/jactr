package org.jactr.core.module.asynch.delegate;

import java.util.concurrent.Future;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.core.production.condition.ChunkPattern;
import org.jactr.core.production.request.IRequest;

/*
 * default logging
 */

/**
 * interface for module processing delegates that operate asynchronously
 * @author harrison
 *
 */
public interface IAsynchronousModuleDelegate<M extends IAsynchronousModule, R>
{

  /**
   * @return get the module this is installed into
   */
  public M getModule();
  
  /**
   * everything is based on a {@link ChunkPattern}. This will start the
   * processing and return some result, usually an {@link IChunk}
   * 
   * @param request TODO
   * @param processTime TODO
   * @param parameters TODO
   * @return TODO
   */
  public Future<R> process(IRequest request, double processTime, Object ... parameters);
}

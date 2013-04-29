package org.jactr.core.module.retrieval.buffer;

/*
 * default logging
 */
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.buffer.delegate.AsynchronousRequestDelegate;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.chunktype.IllegalChunkTypeStateException;
import org.jactr.core.logging.Logger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.retrieval.IRetrievalModule;
import org.jactr.core.module.retrieval.six.DefaultRetrievalModule6;
import org.jactr.core.production.request.ChunkRequest;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.production.request.IRequest;
import org.jactr.core.queue.ITimedEvent;
import org.jactr.core.queue.timedevents.DelayedBufferInsertionTimedEvent;
import org.jactr.core.slot.ISlot;

public class RetrievalRequestDelegate extends AsynchronousRequestDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(RetrievalRequestDelegate.class);

  private IRetrievalModule           _retrievalModule;

  private boolean                    _includeNullValues;

  public RetrievalRequestDelegate(IRetrievalModule module)
  {
    _retrievalModule = module;
    setUseBlockingTimedEvents(false);
    setAsynchronous(true);
    setDelayStart(false);
  }

  @Override
  public void clear()
  {
    super.clear();
    ITimedEvent previous = getCurrentTimedEvent();
    if (previous != null && !previous.hasAborted() && !previous.hasFired())
      previous.abort();
  }

  private boolean indexedRetrievalsEnabled()
  {
    DefaultRetrievalModule6 rm = (DefaultRetrievalModule6) _retrievalModule
        .getAdapter(DefaultRetrievalModule6.class);
    if (rm != null) return rm.isIndexedRetrievalEnabled();
    return false;
  }

  /**
   * when chunks are expanded into search patterns, this determines whether null
   * slot values will be included in the search pattern, defaults to false
   * 
   * @param includeNulls
   */
  public void setIncludeNullValues(boolean includeNulls)
  {
    _includeNullValues = includeNulls;
  }

  /**
   * test to make sure all the slots are contained in the chunktype
   */
  @Override
  protected boolean isValid(IRequest request, IActivationBuffer buffer)
      throws IllegalArgumentException
  {
    IChunkType chunkType = ((ChunkTypeRequest) request).getChunkType();
    ISymbolicChunkType sChunkType = chunkType.getSymbolicChunkType();
    for (ISlot slot : ((ChunkTypeRequest) request).getSlots())
    {
      // we can ignore meta slots
      boolean valid = slot.getName().startsWith(":");
      try
      {
        if (!valid) valid = null != sChunkType.getSlot(slot.getName());
      }
      catch (IllegalChunkTypeStateException icse)
      {
      }

      if (!valid)
        throw new IllegalArgumentException("No slot named " + slot.getName()
            + " available in " + chunkType);
    }
    return true;
  }

  /**
   * expands chunk requests into chunktype requests, unless indexed retrievals
   * are enabled.
   * 
   * @param request
   * @return
   */
  @Override
  protected IRequest expandRequest(IRequest request)
  {
    if (request instanceof ChunkRequest && !indexedRetrievalsEnabled())
    {
      IChunk chunk = ((ChunkRequest) request).getChunk();
      ChunkTypeRequest ctr = new ChunkTypeRequest(chunk.getSymbolicChunk()
          .getChunkType());
      for (ISlot slot : chunk.getSymbolicChunk().getSlots())
        if (slot.getValue() != null || _includeNullValues) ctr.addSlot(slot);

      request = ctr;
    }

    return request;
  }

  @Override
  protected Object startRequest(IRequest request, IActivationBuffer buffer,
      double requestTime)
  {
    IModel model = buffer.getModel();
    /*
     * abort previous retrieval attempt
     */
    ITimedEvent previousRetrieval = getCurrentTimedEvent();
    if (previousRetrieval != null && !previousRetrieval.hasAborted()
        && !previousRetrieval.hasFired())
    {
      previousRetrieval.abort();
      /*
       * log that we are aborting
       */
      if (Logger.hasLoggers(model) || LOGGER.isDebugEnabled())
      {
        String msg = "Aborted retrieval of " + getPreviousRequest();
        if (Logger.hasLoggers(model))
          Logger.log(model, Logger.Stream.RETRIEVAL, msg);
        if (LOGGER.isDebugEnabled()) LOGGER.debug(msg);
      }
    }

    /*
     * clear the current buffer contents
     */
    buffer.removeSourceChunk(buffer.getSourceChunk());

    setBusy(buffer);

    /*
     * indexed retrievals must be enabled, lets just return the chunk
     */
    if (request instanceof ChunkRequest)
      return ((ChunkRequest) request).getChunk();

    ChunkTypeRequest ctRequest = (ChunkTypeRequest) request;
    return _retrievalModule.retrieveChunk(ctRequest);
  }

  @Override
  protected void abortRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    setFree(buffer);
    super.abortRequest(request, buffer, startValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void finishRequest(IRequest request, IActivationBuffer buffer,
      Object startValue)
  {
    IChunk error = buffer.getModel().getDeclarativeModule().getErrorChunk();
    IChunk result = error;
    boolean indexed = false;

    if (startValue instanceof IChunk)
    {
      result = (IChunk) startValue;
      indexed = true;
    }
    else
      try
      {
        result = ((Future<IChunk>) startValue).get();
      }
      catch (InterruptedException ie)
      {
        // bail
        return;
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to get future of retrieval request " + request, e);
        result = error;
      }

    if (indexed)
      buffer.addSourceChunk(result); // will implicitly reset state
    else
    {
      /*
       * now this is just the harvest of the result from the module. we still
       * need to calculate the retrieval time and post the event to actually
       * make it available
       */
      double retrievalStartTime = getCurrentTimedEvent().getEndTime();
      /*
       * what would the retrieval time be? This call uses the retrieval time
       * method that includes the possibility for partial matching.
       */
      double retrievalTime = _retrievalModule.getRetrievalTimeEquation()
          .computeRetrievalTime(result, (ChunkTypeRequest) request);

      if (retrievalTime > 60)
        if (LOGGER.isWarnEnabled())
          LOGGER
              .warn(String
                  .format(
                      "WARNING retrieval of %s is going to take %.2fs. Check your parameter values!",
                      result, retrievalTime));

      IModel model = buffer.getModel();

      if (Logger.hasLoggers(model) || LOGGER.isDebugEnabled())
      {
        String msg = "Will retrieve " + result + " in " + retrievalTime + " @ "
            + (retrievalStartTime + retrievalTime);
        Logger.log(model, Logger.Stream.RETRIEVAL, msg);
        LOGGER.debug(msg);
      }

      ITimedEvent finish = new DelayedBufferInsertionTimedEvent(buffer, result,
          retrievalStartTime, retrievalStartTime + retrievalTime);

      model.getTimedEventQueue().enqueue(finish);
      setCurrentTimedEvent(finish);
    }
  }

  public boolean willAccept(IRequest request)
  {
    return request instanceof ChunkTypeRequest;
  }

}

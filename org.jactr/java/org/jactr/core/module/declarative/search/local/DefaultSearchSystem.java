/*
 * Created on Oct 12, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.declarative.search.local;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javolution.util.FastList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.search.ISearchSystem;
import org.jactr.core.module.declarative.search.map.BooleanTypeValueMap;
import org.jactr.core.module.declarative.search.map.ITypeValueMap;
import org.jactr.core.module.declarative.search.map.NullTypeValueMap;
import org.jactr.core.module.declarative.search.map.NumericTypeValueMap;
import org.jactr.core.module.declarative.search.map.StringTypeValueMap;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.request.ChunkTypeRequest;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ILogicalSlot;
import org.jactr.core.slot.ISlot;

public class DefaultSearchSystem implements ISearchSystem
{
  /**
   * logger definition
   */
  static public final Log                                   LOGGER = LogFactory
                                                                       .getLog(DefaultSearchSystem.class);

  private ReentrantReadWriteLock                            _lock  = new ReentrantReadWriteLock();

  // private ACTREventDispatcher<IDeclarativeModule,ISearchListener>
  // _eventDispatcher;

  private Map<String, Collection<ITypeValueMap<?, IChunk>>> _slotMap;

  private IDeclarativeModule                                _module;

  public DefaultSearchSystem(IDeclarativeModule module)
  {
    _slotMap = new TreeMap<String, Collection<ITypeValueMap<?, IChunk>>>();
    // _eventDispatcher = new ACTREventDispatcher<IDeclarativeModule,
    // ISearchListener>();
    _module = module;
  }

  public void clear()
  {
    try
    {
      _lock.writeLock().lock();
      for (Collection<ITypeValueMap<?, IChunk>> collection : _slotMap.values())
        if (collection != null)
        {
          for (ITypeValueMap<?, IChunk> tvm : collection)
            tvm.clear();
          collection.clear();
        }
      _slotMap.clear();
    }
    finally
    {
      _lock.writeLock().unlock();
    }
  }

  protected Collection<ITypeValueMap<?, IChunk>> instantiateTypeValueMapCollection()
  {
    return new ArrayList<ITypeValueMap<?, IChunk>>();
  }

  protected ITypeValueMap<?, IChunk> instantiateTypeValueMap(Object value)
  {
    if (value == null) return new NullTypeValueMap<IChunk>();
    if (value instanceof String) return new StringTypeValueMap<IChunk>();
    if (value instanceof Number) return new NumericTypeValueMap<IChunk>();
    if (value instanceof Boolean) return new BooleanTypeValueMap<IChunk>();
    if (value instanceof IChunk) return new ChunkTypeValueMap<IChunk>();
    if (value instanceof IChunkType)
      return new ChunkTypeTypeValueMap<IChunk>();
    if (value instanceof IProduction)
      return new ProductionTypeValueMap<IChunk>();
    if (value instanceof IActivationBuffer)
      return new ActivationBufferTypeValueMap<IChunk>();

    if (LOGGER.isWarnEnabled())
      LOGGER
          .warn("Could not determine what type of value map to provide given "
              + value + " of " + value.getClass());

    return null;
  }

  protected ReentrantReadWriteLock getLock()
  {
    return _lock;
  }

  /**
   * this implementation fails fast
   * 
   * @see org.jactr.core.module.declarative.search.ISearchSystem#findExact(ChunkTypeRequest,
   *      java.util.Comparator)
   */
  public Collection<IChunk> findExact(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule)
  {
    Collection<IChunk> candidates = findExactSingle(pattern);

    if (sortRule != null)
    {
      /*
       * finally, we sort them
       */
      TreeSet<IChunk> sortedResults = new TreeSet<IChunk>(sortRule);
      sortedResults.addAll(candidates);

      return sortedResults;
    }

    return candidates;
  }

  protected Collection<IChunk> findExactPooled(ChunkTypeRequest pattern)
  {
    final HashSet<IChunk> candidates = new HashSet<IChunk>();
    IChunkType chunkType = pattern.getChunkType();
    if (chunkType != null)
      candidates.addAll(chunkType.getSymbolicChunkType().getChunks());

    ExecutorService pool = ExecutorServices.getExecutor(ExecutorServices.POOL);

    FastList<Future<Collection<IChunk>>> results = FastList.newInstance();

    for (ISlot slot : pattern.getConditionalAndLogicalSlots())
    {
      final ISlot fSlot = slot;
      /*
       * submit and snag the future for the results
       */
      results.add(pool.submit(new Callable<Collection<IChunk>>() {
        public Collection<IChunk> call() throws Exception
        {
          return find(fSlot, candidates);
        }

      }));
    }

    /*
     * a search has been invoked for every slot pattern specified. Now we
     * iterate through and block on the results. Since order only matters if
     * this is the first result, we just block on the results and process them
     * in order
     */
    boolean first = chunkType == null;
    boolean zeroResults = false;
    for (Future<Collection<IChunk>> result : results)
      try
      {
        // if we've got nothing by now, cancel all the remaining searches
        if (zeroResults)
          result.cancel(true);
        else
        {
          Collection<IChunk> slotCandidates = result.get();
          if (first)
          {
            candidates.addAll(slotCandidates);
            first = false;
          }
          else
            candidates.retainAll(slotCandidates);
        }

        zeroResults = candidates.size() == 0;
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to process parallel search results :", e);
      }

    FastList.recycle(results);

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    return candidates;
  }

  protected Collection<IChunk> findExactSingle(ChunkTypeRequest pattern)
  {
    /*
     * second pass, ditch all those that don't match our chunktype
     */
    HashSet<IChunk> candidates = new HashSet<IChunk>();
    IChunkType chunkType = pattern.getChunkType();
    if (chunkType != null)
      candidates.addAll(chunkType.getSymbolicChunkType().getChunks());
    /*
     * first things first, find all the candidates based on the content of the
     * pattern
     */
    boolean first = chunkType == null;
    for (ISlot slot : pattern.getConditionalAndLogicalSlots())
    {
      if (first)
      {
        candidates.addAll(find(slot, candidates));
        first = false;
      }
      else
        candidates.retainAll(find(slot, candidates));

      if (candidates.size() == 0) break;
    }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    return candidates;
  }

  public Collection<IChunk> findFuzzy(ChunkTypeRequest pattern,
      Comparator<IChunk> sortRule)
  {

    /*
     * second pass, ditch all those that don't match our chunktype
     */
    HashSet<IChunk> candidates = new HashSet<IChunk>();
    IChunkType chunkType = pattern.getChunkType();

    /*
     * old code used slot values if any were provided. If not, it would grab
     * every chunk of the given type. This would reduce the set of candidates if
     * there was only one slot specified (the candidate set would include just
     * those that equaled the specified slot). In reality, we should just grab
     * EVERY chunk of the type, regardless of the pattern
     */
    // boolean noSlots = true;
    // for (IConditionalSlot slot : pattern.getConditionalSlots())
    // {
    // noSlots = false;
    // Collection<IChunk> containers = find(slot);
    // if (chunkType == null)
    // candidates.addAll(containers);
    // else
    // for (IChunk candidate : containers)
    // if (candidate.isA(chunkType)) candidates.add(candidate);
    // }
    //
    // if (noSlots && chunkType != null)
    // candidates.addAll(chunkType.getSymbolicChunkType().getChunks());
    if (chunkType != null)
      candidates.addAll(chunkType.getSymbolicChunkType().getChunks());

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("First pass candidates for " + pattern + " chunks: "
          + candidates);

    if (sortRule != null)
    {
      /*
       * finally, we sort them
       */
      TreeSet<IChunk> sortedResults = new TreeSet<IChunk>(sortRule);
      sortedResults.addAll(candidates);

      return sortedResults;
    }
    return candidates;
  }

  /**
   * current candidates is required in the case of NOT conditions
   * 
   * @param slot
   * @param candidates
   * @return
   */
  protected Collection<IChunk> find(ISlot slot, HashSet<IChunk> candidates)
  {
    HashSet<IChunk> rtn = new HashSet<IChunk>();
    if (slot instanceof IConditionalSlot)
    {
      IConditionalSlot conditionalSlot = (IConditionalSlot) slot;
      switch (conditionalSlot.getCondition())
      {
        case IConditionalSlot.EQUALS:
          if (slot.getName().equals(ISlot.ISA))
            rtn.addAll(((IChunkType) slot.getValue()).getSymbolicChunkType()
                .getChunks());
          else
            rtn.addAll(equals(conditionalSlot));
          break;
        case IConditionalSlot.GREATER_THAN:
          rtn.addAll(greaterThan(conditionalSlot));
          break;
        case IConditionalSlot.GREATER_THAN_EQUALS:
          rtn.addAll(greaterThan(conditionalSlot));
          rtn.addAll(equals(conditionalSlot));
          break;
        case IConditionalSlot.LESS_THAN:
          rtn.addAll(lessThan(conditionalSlot));
          break;
        case IConditionalSlot.LESS_THAN_EQUALS:
          rtn.addAll(lessThan(conditionalSlot));
          rtn.addAll(equals(conditionalSlot));
          break;
        case IConditionalSlot.NOT_EQUALS:
          if (slot.getName().equals(ISlot.ISA))
          {
            rtn.addAll(candidates);
            rtn.removeAll(((IChunkType) slot.getValue()).getSymbolicChunkType()
                .getChunks());
          }
          else
            rtn.addAll(not(conditionalSlot));
          break;
        case IConditionalSlot.WITHIN:
        default:
          if (LOGGER.isWarnEnabled())
            LOGGER.warn("No clue what to do with this search condition "
                + conditionalSlot);
      }
    }
    else if (slot instanceof ILogicalSlot)
    {
      ILogicalSlot logicalSlot = (ILogicalSlot) slot;
      FastList<ISlot> children = FastList.newInstance();
      logicalSlot.getSlots(children);

      switch (logicalSlot.getOperator())
      {
        case ILogicalSlot.AND:
          rtn.addAll(find(children.getFirst(), candidates));
          rtn.retainAll(find(children.getLast(), candidates));
          break;
        case ILogicalSlot.OR:
          rtn.addAll(find(children.getFirst(), candidates));
          rtn.addAll(find(children.getLast(), candidates));
          break;
        case ILogicalSlot.NOT:
          rtn.addAll(candidates);
          rtn.removeAll(find(children.getFirst(), candidates));
      }

      FastList.recycle(children);
      LOGGER.debug("Logical.AND search for " + logicalSlot + " returning "
          + rtn);
    }
    else
      LOGGER.error("Ignoring slot " + slot
          + " because it's neither conditional nor logical");

    if (LOGGER.isDebugEnabled())
      LOGGER
          .debug("Search for " + slot + " yielded " + rtn.size() + " results");

    return rtn;
  }

  protected Collection<IChunk> equals(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
    if (typeValueMap != null) return typeValueMap.get(slot.getValue());
    return Collections.EMPTY_LIST;
  }

  protected Collection<IChunk> lessThan(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
    if (typeValueMap != null)
      try
      {
        return typeValueMap.lessThan(slot.getValue());
      }
      catch (UnsupportedOperationException uoe)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(slot.getValue() + " does not have natural ordering");
      }
    return Collections.EMPTY_LIST;
  }

  protected Collection<IChunk> greaterThan(ISlot slot)
  {
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);
    if (typeValueMap != null)
      try
      {
        return typeValueMap.greaterThan(slot.getValue());
      }
      catch (UnsupportedOperationException uoe)
      {
        if (LOGGER.isDebugEnabled())
          LOGGER.debug(slot.getValue() + " does not have natural ordering");
      }

    return Collections.EMPTY_LIST;
  }

  protected Collection<IChunk> not(ISlot slot)
  {
    /*
     * return values are not only what the approriate typevalue map say they
     * are, but also all the other type value maps.all() we'll start with the
     * obvious part first
     */
    HashSet<IChunk> rtn = new HashSet<IChunk>();
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(
        slot.getName(), slot.getValue(), false);

    if (typeValueMap != null) rtn.addAll(typeValueMap.not(slot.getValue()));

    // now let's snag all the rest
    try
    {
      getLock().readLock().lock();
      for (ITypeValueMap<?, IChunk> tvm : _slotMap.get(slot.getName()
          .toLowerCase()))
        if (tvm != typeValueMap && tvm != null) rtn.addAll(tvm.all());
      return rtn;
    }
    finally
    {
      getLock().readLock().unlock();
    }
  }

  public void index(IChunk chunk)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Indexing " + chunk);

    if (!chunk.isEncoded())
      throw new RuntimeException(chunk
          + " has not been encoded, will not index");

    for (ISlot slot : chunk.getSymbolicChunk().getSlots())
      addIndexing(chunk, slot.getName(), slot.getValue());
  }

  public void unindex(IChunk chunk)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Unindexing " + chunk);

    for (ISlot slot : chunk.getSymbolicChunk().getSlots())
      removeIndexing(chunk, slot.getName(), slot.getValue());
  }

  public void update(IChunk chunk, String slotName, Object oldValue,
      Object newValue)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Updating indexing for " + chunk + "." + slotName);
    if (oldValue == null) oldValue = NullTypeValueMap.NULL;
    if (newValue == null) newValue = NullTypeValueMap.NULL;
    removeIndexing(chunk, slotName, oldValue);
    addIndexing(chunk, slotName, newValue);
  }

  protected void removeIndexing(IChunk chunk, String slotName, Object value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Unindexing " + chunk + "." + slotName + "=" + value);
    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(slotName,
        value, false);
    if (typeValueMap != null) typeValueMap.remove(value, chunk);
  }

  protected void addIndexing(IChunk chunk, String slotName, Object value)
  {
    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Indexing " + chunk + "." + slotName + "=" + value);

    ITypeValueMap<?, IChunk> typeValueMap = getSlotNameTypeValueMap(slotName,
        value, true);

    // this is possible if we can't index the data type
    if (typeValueMap != null) typeValueMap.add(value, chunk);
  }

  /**
   * return the ITypeValueMap for the slot name. if create is true, the write
   * lock will be acquired and if no map exists, it will be created based on the
   * value passed
   * 
   * @param slotName
   * @param create
   * @return
   */
  protected ITypeValueMap<?, IChunk> getSlotNameTypeValueMap(String slotName,
      Object value, boolean create)
  {
    slotName = slotName.toLowerCase();
    ReentrantReadWriteLock lock = getLock();
    Collection<ITypeValueMap<?, IChunk>> typeValueMaps = null;
    ITypeValueMap<?, IChunk> typeValueMap = null;
    if (create)
      try
      {
        lock.writeLock().lock();
        typeValueMaps = _slotMap.get(slotName);
        if (typeValueMaps == null)
        {
          if (LOGGER.isDebugEnabled())
            LOGGER.debug("slot " + slotName
                + " has no type value map collection, creating");
          // create
          typeValueMaps = instantiateTypeValueMapCollection();
          _slotMap.put(slotName, typeValueMaps);

        }

        for (ITypeValueMap<?, IChunk> tvm : typeValueMaps)
          if (tvm.isValueRelevant(value))
          {
            typeValueMap = tvm;
            continue;
          }

        /*
         * no typevaluemap was found, create
         */
        if (typeValueMap == null)
        {
          typeValueMap = instantiateTypeValueMap(value);
          if (typeValueMap != null)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("No type value map exists for current value "
                  + value + ", created " + typeValueMap);
            typeValueMaps.add(typeValueMap);
          }
        }
      }
      finally
      {
        lock.writeLock().unlock();
      }
    else
      try
      {
        lock.readLock().lock();
        typeValueMaps = _slotMap.get(slotName);
        if (typeValueMaps != null)
        {
          for (ITypeValueMap<?, IChunk> tvm : typeValueMaps)
            if (tvm.isValueRelevant(value))
            {
              typeValueMap = tvm;
              continue;
            }
            else if (LOGGER.isDebugEnabled())
              LOGGER.debug(tvm + " is irrelevant to " + value);

          if (typeValueMap == null)
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("No type value map was found for " + slotName
                  + ", returning");
        }
        else if (LOGGER.isDebugEnabled())
          LOGGER.debug("slot " + slotName
              + " has no type value map collection, returning");
      }
      finally
      {
        lock.readLock().unlock();
      }

    if (LOGGER.isDebugEnabled())
      LOGGER.debug("Returning " + typeValueMap + " for " + slotName + "="
          + value);
    return typeValueMap;
  }

  // public void addListener(ISearchListener listener, Executor executor)
  // {
  // _eventDispatcher.addListener(listener, executor);
  // }
  //
  //
  // public void removeListener(ISearchListener listener)
  // {
  // _eventDispatcher.removeListener(listener);
  // }
  //
  // public boolean hasListeners()
  // {
  // return _eventDispatcher.hasListeners();
  // }
}

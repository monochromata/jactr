package org.jactr.core.module.declarative.search.local;

/*
 * default logging
 */
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.slot.ISlot;
import org.jactr.core.utils.collections.SkipListSetFactory;

/**
 * adapts the exact parallel to work as partial. It does this by disabling the
 * not filter support entirely (as they must be considered as populating), and
 * changing the {@link #combineResults(Collection, IChunkFilter, DefaultSearchSystem)} method
 * to only use addAll.
 * 
 * @author harrison
 */
public class PartialParallelSearchDelegate extends ExactParallelSearchDelegate
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(PartialParallelSearchDelegate.class);

  public PartialParallelSearchDelegate()
  {
  }

  /**
   * Overriden to not do any filtering
   * 
   * @param chunkType TODO
   * @param originalSlots TODO
   * @param container TODO
   * @param searchSystem TODO
   * @return TODO
   */
  @Override
  protected IChunkFilter selectSlotsToSearch(IChunkType chunkType,
      Collection<? extends ISlot> originalSlots, List<ISlot> container,
      DefaultSearchSystem searchSystem)
  {
    container.addAll(originalSlots);
    return searchSystem._defaultFilter;
  }

  /**
   * perform set logic to all the slot search results, recycling interim
   * collections
   * 
   * @param slotSearchResults TODO
   * @param chunkFilter TODO
   * @param searchSystem TODO
   * @return TODO
   */
  @Override
  protected SortedSet<IChunk> combineResults(
      Collection<Collection<IChunk>> slotSearchResults,
      IChunkFilter chunkFilter,
      DefaultSearchSystem searchSystem)
  {
    SortedSet<IChunk> candidates = SkipListSetFactory
        .newInstance(searchSystem._chunkNameComparator);

    for (Collection<IChunk> slotSearchResult : slotSearchResults)
    {
      searchSystem.cleanAddAll(candidates, slotSearchResult);

      searchSystem.recycleCollection(slotSearchResult);
    }


    return finalFilter(candidates, chunkFilter);
  }
}

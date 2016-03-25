package org.jactr.core.production.condition.match;

/*
 * default logging
 */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ILogicalSlot;
import org.jactr.core.slot.ISlot;
import org.jactr.core.slot.IUniqueSlotContainer;

/**
 * a failure to match due to a conditional slot mismatch
 * 
 * @author harrison
 */
public class LogicMatchFailure extends AbstractMatchFailure
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER                       = LogFactory
                                                                      .getLog(LogicMatchFailure.class);

  private final IUniqueSlotContainer _container;

  private final ILogicalSlot     _logicalSlot;

  /**
   * call for when the container doesn't actually have the slot
   * 
   * @param container TODO
   * @param lSlot TODO
   */
  public LogicMatchFailure(IUniqueSlotContainer container, ILogicalSlot lSlot)
  {
    this(null, container, lSlot);
  }

  /**
   * failure when the mismatchedSlot in the container does not meet the
   * condition
   * 
   * @param condition TODO
   * @param container TODO
   * @param lSlot TODO
   */
  public LogicMatchFailure(ICondition condition, IUniqueSlotContainer container,
      ILogicalSlot lSlot)
  {
    super(condition);
    _logicalSlot = lSlot;
    _container = container;
  
  }

  public IUniqueSlotContainer getSlotContainer()
  {
    return _container;
  }

  public ILogicalSlot getLogicalSlot()
  {
    return _logicalSlot;
  }

  @Override
  public String toString()
  {
    return String
        .format("Could not resolve logical slot " + _logicalSlot);
  }
}

/*
 * Created on Sep 21, 2004 Copyright (C) 2001-4, Anthony Harrison anh23@pitt.edu
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library; if not, write
 * to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package org.jactr.core.slot;


/**
 * @author harrison
 */
public class ImmutableSlotWrapper extends Object implements ISlot
{
  private ISlot _slot;

  public ImmutableSlotWrapper(ISlot slot)
  {
    _slot = slot;
  }

  /**
   * @see org.jactr.core.slot.ISlot#getValue()
   */
  public Object getValue()
  {
    return _slot.getValue();
  }

  /**
   * @see org.jactr.core.slot.ISlot#getName()
   */
  public String getName()
  {
    return _slot.getName();
  }

  /**
   * @see org.jactr.core.slot.ISlot#equalValues(java.lang.Object)
   */
  public boolean equalValues(Object value)
  {
    return _slot.equalValues(value);
  }

  @Override
  public boolean equals(Object value)
  {
    return _slot.equals(value);
  }

  public ImmutableSlotWrapper clone()
  {
    return new ImmutableSlotWrapper(_slot);
  }

  public boolean isVariable()
  {
    return isVariableValue();
  }

  @Override
  public String toString()
  {
    return _slot.toString();
  }

  public boolean isVariableValue()
  {
    return _slot.isVariableValue();
  }
}

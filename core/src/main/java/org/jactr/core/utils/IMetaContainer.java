/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.core.utils;

import java.util.Collection;

/**
 * interface for an object that can carry arbitrary transient (not persisted to
 * file) data - Chunks permit this for internal bookkeeping
 * 
 * @author harrison
 */
public interface IMetaContainer
{

  /**
   * Gets the MetaData attribute of the MetaContainer object
   * 
   * @param key
   *            Description of Parameter
   * @return The MetaData value
   */
  public Object getMetaData(String key);

  /**
   * Sets the MetaData attribute of the MetaContainer object
   * 
   * @param key
   *            The new MetaData value's key
   * @param value
   *            The new MetaData value
   */
  public void setMetaData(String key, Object value);

  /**
   * @return all the keys
   */
  public Collection<String> getMetaDataKeys();
}

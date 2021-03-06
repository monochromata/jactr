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

package org.jactr.core.chunktype;

import org.jactr.core.utils.IAdaptable;
import org.jactr.core.utils.parameter.IParameterized;

/**
 * this is provided just so that everything follows the same pattern - I am not
 * yet aware of any need for a subsymbolic chunktype
 * 
 * @author harrison
 */
public interface ISubsymbolicChunkType extends IParameterized, IAdaptable
{

  /**
   * Description of the Method
   */
  public void dispose();

  public void encode();
  
  public boolean isEncoded();

  public boolean isDisposed();
}

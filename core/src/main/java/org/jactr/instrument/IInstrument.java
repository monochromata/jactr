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

package org.jactr.instrument;

import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.utils.IInitializable;
import org.jactr.core.utils.IInstallable;

/**
 * An interface for instruments that can be added to models.
 * 
 * <p>Classes implementing this interface need to provide a 1-argument
 * constructor consuming an {@link org.jactr.core.runtime.ACTRRuntime} instance.</p>
 */
public interface IInstrument extends IInstallable, IInitializable
{
  /**
   * @return the runtime the instrument was created for.
   */
  public ACTRRuntime getRuntime();
	
  public void install(IModel model);

  public void uninstall(IModel model);
  
  
  /**
   * perform any dependent initialization. this is called before the model starts,
   * on the model thread. All model data will be available at this time. this 
   * method should excecute as quickly as possible so that all the model can start
   * running sooner. Any long running actions should likely be started during install
   * and harvested by initialize().
   */
  public void initialize();

}


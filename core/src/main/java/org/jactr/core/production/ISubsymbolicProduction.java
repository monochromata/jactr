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

package org.jactr.core.production;

import org.jactr.core.utils.IAdaptable;

public interface ISubsymbolicProduction extends
    org.jactr.core.utils.parameter.IParameterized, IAdaptable
{

  public final static String CREATION_TIME = "CreationTime";

  public final static String FIRING_TIME   = "FiringTime";

  /**
   * how long will it take to execute this production
   * 
   * @return TODO
   */
  public double getFiringTime();

  /**
   * how long will it take to execute this production
   * 
   * @param defAct TODO
   */
  public void setFiringTime(double defAct);

  /**
   * when was this production created
   * 
   * @return TODO
   */
  public double getCreationTime();

  /**
   * when was this production created
   * 
   * @param time TODO
   */
  public void setCreationTime(double time);

  /**
   * Description of the Method
   */
  public void dispose();

  public void encode();
}

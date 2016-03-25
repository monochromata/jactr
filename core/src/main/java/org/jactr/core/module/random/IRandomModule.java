/*
 * Created on Oct 12, 2006
 * Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu (jactr.org) This library is free
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
package org.jactr.core.module.random;

import java.util.Random;

import org.jactr.core.module.IModule;

public interface IRandomModule extends IModule
{

  public static final String SEED_PARAM = "RandomSeed";
  static public final String RANDOM_TIME_PARAM = "TimeRandomizer";

  public void setSeed(long seedValue);
  
  public long getSeed();
  
  public Random getGenerator();
  
  public double logisticNoise(double s);
  
  public boolean randomBoolean();
  
  public double getTimeRandomizer();
  
  /**
   * sets the seed for the time randomizer which will randomize
   * the requested time across the equal distribution [time*((randomizer-1)/randomizer), time*((randomizer+1)/randomizer))
   * 
   * @param randomizer TODO
   */
  public void setTimeRandomizer(double randomizer);
  
  public double randomizedTime(double currentTime);
}



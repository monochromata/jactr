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

package org.jactr.core.module.procedural.four;

import org.jactr.core.production.IProduction;
import org.jactr.core.production.four.ISubsymbolicProduction4;

/*
 * sorts descending based on current expected Gain
 */
public class ProductionGainComparator implements
    java.util.Comparator<IProduction>
{

  protected boolean _randomTieBreaks = false;

  public ProductionGainComparator(boolean randomTieBreaks)
  {
    _randomTieBreaks = randomTieBreaks;
  }

  /*
   * 1 one <two 0 one=two -1 one>two
   */
  public int compare(IProduction one, IProduction two)
  {
    if (one == two) { return 0; }

    ISubsymbolicProduction4 sp1 = (ISubsymbolicProduction4)one.getSubsymbolicProduction();
    ISubsymbolicProduction4 sp2 = (ISubsymbolicProduction4) two.getSubsymbolicProduction();

    double base1 = sp1.getExpectedGain();
    double base2 = sp2.getExpectedGain();

    if (base1 == base2)
    {
      if (!_randomTieBreaks)
      {
        return one.getSymbolicProduction().getName()
            .compareTo(
                two.getSymbolicProduction().getName());
      }
      else if (Math.random() > 0.5)
      {
        return 1;
      }
      else
      {
        return -1;
      }
    }
    else if (base1 < base2)
    {
      return 1;
    }
    else
    {
      return -1;
    }
  }

  public boolean equals(Object one)
  {
    return false;
  }
}


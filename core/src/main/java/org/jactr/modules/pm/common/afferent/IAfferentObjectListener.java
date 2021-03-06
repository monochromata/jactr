/*
 * Created on Jun 26, 2007 Copyright (C) 2001-2007, Anthony Harrison
 * anh23@pitt.edu (jactr.org) This library is free software; you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version. This library is
 * distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.common.afferent;

import org.commonreality.object.IAfferentObject;
import org.commonreality.object.delta.IObjectDelta;

/**
 * 
 */
public interface IAfferentObjectListener
{
  public void afferentObjectUpdated(IAfferentObject object, IObjectDelta delta);

  public void afferentObjectAdded(IAfferentObject object);

  public void afferentObjectRemoved(IAfferentObject object);

  public boolean isInterestedIn(IAfferentObject object);
}

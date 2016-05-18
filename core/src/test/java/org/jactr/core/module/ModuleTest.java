/*
 * Created on Nov 21, 2006
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
package org.jactr.core.module;

import org.commonreality.reality.CommonReality;
import org.commonreality.reality.impl.DefaultReality;
import org.jactr.core.model.IModel;
import org.jactr.core.models.BootstrapModelFactory;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.TestUtils;
import org.junit.After;
import org.junit.Before;

import junit.framework.TestCase;

public abstract class ModuleTest
{

  protected ACTRRuntime _runtime;
  private IModel _model;

  public ModuleTest()
  {
    super();
  }

  @Before
  public void setUp() throws Exception
  {
    _model = loadBootstrapModel();
  }

  @After
  public void tearDown() throws Exception
  {
    _model.dispose();
  }
  
  protected IModel loadBootstrapModel() throws Exception
  {
	  _runtime = TestUtils.getRuntimeWithEmptyDefaultReality();
	  return new BootstrapModelFactory(_runtime).createAndInitializeModel();
  }

  public IModel getModel()
  {
    return _model;
  }

}

/*
 * Created on Mar 6, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.tools.async.tracer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.async.MINATest;
import org.jactr.tools.tracer.RuntimeTracer;

public class RuntimeTracerTest extends MINATest
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory.getLog(RuntimeTracerTest.class);

  private RuntimeTracer    _tracer;

  /**
   * install the runtimeTracer
   * @see org.jactr.tools.async.MINATest#configureModel(org.jactr.core.model.IModel)
   */
  @Override
  protected void configureModel(ACTRRuntime runtime, IModel model)
  {
    _tracer = new RuntimeTracer(runtime);
    //super.configureModel(model);
    
    if (LOGGER.isDebugEnabled()) LOGGER.debug("installing tracer");
    model.install(_tracer);
  }


}

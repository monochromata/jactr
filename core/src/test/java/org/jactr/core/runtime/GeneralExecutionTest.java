/*
 * Created on Mar 20, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.core.runtime;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.models.AdditionModelFactory;
import org.jactr.core.models.CountModelFactory;
import org.jactr.core.models.FullSemanticModelFactory;
import org.jactr.core.models.SemanticModelFactory;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class GeneralExecutionTest
{
  /**
   * logger definition
   */
  static private final Log LOGGER       = LogFactory
                                            .getLog(GeneralExecutionTest.class);

  private ACTRRuntime _runtime;
  
  @Before
  public void setUp() throws Exception
  {
    _runtime = TestUtils.getRuntimeWithEmptyDefaultReality();
    _runtime.setController(new DefaultController(_runtime));
  }

  @After
  public void tearDown() throws Exception
  {
    _runtime.setController(null);
    _runtime = null;
  }

  protected IModel configureModel(IModel model)
  {
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Attaching logger");
    // org.jactr.core.logging.impl.DefaultModelLogger dml = new
    // DefaultModelLogger();
    // dml.setParameter("all", "err");
    // model.install(dml);
    return model;
  }

  protected void execute(IModel... models) throws Exception
  {
    for (IModel model : models)
      _runtime.addModel(model);

    IController controller = _runtime.getController();

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Running");
    controller.start().get();

    controller.complete().get();
    assertFalse(controller.isRunning());

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Terminated");
    for (IModel model : models)
      assertTrue(controller.getTerminatedModels().contains(model));

    for (IModel model : models)
    {
      _runtime.removeModel(model);
      model.dispose();
    }

  }

  protected void test(IModel... models) throws Exception
  {
    for (IModel model: models)
    {
      configureModel(model);
    }
    execute(models);
  }

  @Test
  public void testBasicSemantic() throws Exception
  {
    test(new SemanticModelFactory(_runtime).createAndInitializeModel());
  }

  @Test
  public void testFullSemantic() throws Exception
  {
    test(new FullSemanticModelFactory(_runtime).createAndInitializeModel());
  }

  @Test
  public void testAddition() throws Exception
  {
    test(new AdditionModelFactory(_runtime).createAndInitializeModel());
  }

  @Test
  public void testCount() throws Exception
  {
    test(new CountModelFactory(_runtime).createAndInitializeModel());
  }

  @Ignore
  @Test
  public void testMultiple() throws Exception
  {
    test(new CountModelFactory(_runtime).createAndInitializeModel(),
        new AdditionModelFactory(_runtime).createAndInitializeModel());
  }

}

/*
 * Created on Jun 24, 2005 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.function.Function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.reality.CommonReality;
import org.commonreality.reality.impl.DefaultReality;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.logging.Logger;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.models.SemanticModelFactory;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.slot.ISlot;
import org.junit.Test;

public class ModelExecutionTest
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ModelExecutionTest.class);

  protected IModel configureModel(ACTRRuntime runtime, IModel model)
  {

    if (LOGGER.isDebugEnabled()) LOGGER.debug("Attaching logger");
    org.jactr.core.logging.impl.DefaultModelLogger dml = new DefaultModelLogger(runtime);

    // dml.setParameter(Logger.CYCLE,"out");
    dml.setParameter(Logger.Stream.TIME.toString(), "out");
    dml.setParameter(Logger.Stream.OUTPUT.toString(), "out");
    // dml.setParameter(Logger.Stream.GOAL.toString(), "err");
    dml.setParameter(Logger.Stream.PROCEDURAL.toString(), "out");
    // dml.setParameter(Logger.CONFLICT_RESOLUTION, "out");
    // dml.setParameter(Logger.CONFLICT_SET, "out");
    // dml.setParameter(Logger.ACTIVATION_BUFFER,"out");
    // dml.setParameter(Logger.MATCHES,"out");
    // dml.setParameter(Logger.EXACT_MATCH,"out");
    // dml.setParameter(Logger.PARTIAL_MATCH,"out");

    model.install(dml);
    
    //model.install(new BeanShellInterface());

    return model;
  }

  private void testModel(ACTRRuntime runtime, IModel model, String goalName, String slotToCheck,
      IChunk slotValue) throws Exception
  {
    LOGGER
        .debug("======================================================================");
    LOGGER.debug("Testing model for goal " + goalName);

    IController controller = runtime.getController();

    IChunk goal = model.getDeclarativeModule().getChunk(goalName).get();
    Object value = goal.getSymbolicChunk().getSlot("object").getValue();
    LOGGER
        .debug("goal slot value " + value + ", " + value.getClass().getName());

    assertNotNull(goal);
    model.getActivationBuffer("goal").addSourceChunk(goal);
    
    model.setParameter(BasicModel.AGE_PARAM, "0");

    IChunk currentGoal = model.getActivationBuffer("goal").getSourceChunk();
    LOGGER.debug("current goal is " + currentGoal);

    assertNotNull(currentGoal);

    /*
     * this can not pass in 6.0 since chunks are actually copies
     */
    // assertTrue("Source chunk is not assigned", goal.equals(model
    // .getActivationBuffer("goal").getSourceChunk()));
    long startTime = System.currentTimeMillis();
    controller.start().get();
    //assertTrue(controller.isRunning());
    controller.complete().get();
    long runTime = System.currentTimeMillis() - startTime;

    LOGGER.info("Run took " + runTime + " ms");

    assertFalse(controller.isRunning());

    // goal should now have... g2.judgement = yes
    ISlot slot = currentGoal
        .getSymbolicChunk().getSlot(slotToCheck);
    LOGGER.debug("Comparing " + slot.getValue() + " to " + slotValue);

    assertTrue("Comparing " + slot.getValue() + " to " + slotValue, slot
      .equalValues(slotValue));
  }

  private void testSemanticModel(Function<ACTRRuntime,IModel> modelSupplier, String slotToCheck)
      throws Exception
  {
    String[] goalNames = { "g1", "g2", "g3" };
    String[] slotValues = { "yes", "yes", "no" };

    for (int i = 0; i < goalNames.length; i++)
    {
      ACTRRuntime runtime = TestUtils.getRuntimeWithEmptyDefaultReality();
      runtime.setController(new DefaultController(runtime));
    	
      IModel model = modelSupplier.apply(runtime);
      configureModel(runtime, model);

      IChunk yes = model.getDeclarativeModule().getChunk("yes").get();
      IChunk no = model.getDeclarativeModule().getChunk("no").get();
      assertNotNull(yes);
      assertNotNull(no);
    	
      runtime.addModel(model);
      assertTrue(runtime.getModels().size() == 1);

      testModel(runtime, model, goalNames[i], slotToCheck, slotValues[i]=="yes"?yes:no);

      runtime.removeModel(model);

      model.dispose();
      runtime.setController(null);
      runtime = null;
    }
  }

  @Test
  public void testJactrSemanticModel() throws Exception
  {
    testSemanticModel(runtime -> { 
	    	try {
				return new SemanticModelFactory(runtime).createAndInitializeModel();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
    	},
        "judgement");
  }

}

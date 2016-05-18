/*
 * Created on Jul 7, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.modules.pm.motor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.net.provider.INetworkingProvider;
import org.commonreality.sensors.ISensor;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.model.IModel;
import org.jactr.core.models.IModelFactory;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.modules.AbstractModuleTest;
import org.junit.Test;

public class MotorModuleTest extends AbstractModuleTest
{
  /**
   * logger definition
   */
  static public final Log    LOGGER               = LogFactory
                                                      .getLog(MotorModuleTest.class);

  String[]                   _productionSequence  = { "start", "start-punch",
      "movement-completed", "retrieve-next-movement", "start-peck",
      "movement-completed", "retrieve-next-movement", "start-punch",
      "movement-completed", "retrieve-next-movement", "start-peck-recoil",
      "movement-completed", "retrieve-next-movement", "retrieval-failed" };

  String[]                   _failedProductions   = { "movement-failed" };

  int                        _productionFireCount = 0;

	protected IModelFactory createModelFactory() {
		return new MotorTestModelFactory(_runtime);
	}
	
	protected ISensor createSensor(final INetworkingProvider netProvider) throws Exception {
		return createKeyboardSensor(_cr, netProvider);
	}
	
	protected String getModelName() {
		return "motor-test";
	}

  protected JFrame openWindow()
  {
    final JFrame frame = new JFrame("TextInput");
    
    
    try
    {
    SwingUtilities.invokeAndWait(new Runnable(){
      public void run()
      {
        JTextArea textArea = new JTextArea();
        frame.getContentPane().add(new JScrollPane(textArea));
        frame.setSize(100, 80);
        frame.setVisible(true);
        textArea.grabFocus();
      }
    });
    }
    catch(Exception e)
    {
      
    }
    
    return frame;
  }

  protected void closeWindow(final JFrame frame) throws Exception
  {
    Thread.sleep(3000);
    SwingUtilities.invokeLater(new Runnable(){
      public void run()
      {
        frame.setVisible(false);
      }
    });
  }

  @Test
  public void test() throws Exception
  {
    assertThat(_runtime.getModels().size(), equalTo(1));
    IModel model = _runtime.getModels().iterator().next();
    assertThat(model, notNullValue());

    // will be null until we start
    assertThat(_runtime.getConnector().getAgent(model), nullValue());

    model.getProceduralModule().addListener(
        new ProceduralModuleListenerAdaptor() {

          @Override
          public void conflictSetAssembled(ProceduralModuleEvent pme)
          {
            if (LOGGER.isDebugEnabled())
              LOGGER.debug("conflict set : " + pme.getProductions());
          }

          @Override
          public void productionWillFire(ProceduralModuleEvent pme)
          {
            IProduction production = pme.getProduction();
            LOGGER.debug(production + " is about to run, checking");
            testProduction(production);

          }
        }, ExecutorServices.INLINE_EXECUTOR);

    JFrame window = openWindow();
    _controller.start().get();
    if (LOGGER.isDebugEnabled()) LOGGER.debug("model run has started");

    _controller.complete().get();
    if (LOGGER.isDebugEnabled()) LOGGER.debug("Model run has completed");
    assertThat("Not all the productions have fired",
        _productionSequence.length, equalTo(_productionFireCount));
    
    closeWindow(window);
  }

  protected void testProduction(IProduction production)
  {
    String pName = production.getSymbolicProduction().getName();
    // fail any in the _failedProductions
    for (String name : _failedProductions)
      if (name.equals(pName))
        fail("production " + name + " should not have fired. should have been "
            + _productionSequence[_productionFireCount]);

    if (!_productionSequence[_productionFireCount].equals(pName))
      fail(pName + " was fired out of sequence, expecting "
          + _productionSequence[_productionFireCount]);
    _productionFireCount++;
  }
}

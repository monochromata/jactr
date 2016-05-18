/*
 * Created on Jul 18, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.modules.pm.vocal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.TestUtils;
import org.jactr.io.CommonIO;
import org.jactr.io.antlr3.builder.JACTRBuilder;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.junit.Before;
import org.junit.Test;

public class VocalLoadTest
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(VocalLoadTest.class);

  private ACTRRuntime _runtime;
  
  @Before
  public void setUp() throws Exception
  {
    _runtime = TestUtils.getRuntimeWithEmptyDefaultReality();
  }

	// TODO: Don't forget to move motor-test.jactr and environment.xml over there, too.
  @Test
  public void testLoad() throws Exception
  {
    CommonTree modelDescriptor = CommonIO.getModelDescriptor(CommonIO
        .parseModel("org/jactr/io/modules/pm/vocal/vocal-test.jactr"));
    Collection<CommonTree> knownBuffers = ASTSupport.getTrees(modelDescriptor,
        JACTRBuilder.BUFFER);

    LOGGER.debug("Descriptor Raw : " + modelDescriptor.toStringTree());
    for (StringBuilder line : CodeGeneratorFactory.getCodeGenerator("jactr")
        .generate(modelDescriptor, true))
      LOGGER.debug(line.toString());

    assertEquals("Not the right number of buffers " + knownBuffers, 6,
        knownBuffers.size());

    CommonIO.compilerTest(modelDescriptor, true, true);
  }

  @Test
  public void testConstruction() throws Exception
  {
    CommonTree desc = CommonIO.getModelDescriptor(CommonIO
        .parseModel("org/jactr/io/modules/pm/vocal/vocal-test.jactr"));
    CommonIO.compilerTest(desc, true, true);

    IModel model = CommonIO.constructorTest(_runtime, desc);
    assertNotNull(model);

    IDeclarativeModule decM = model.getDeclarativeModule();
    assertNotNull(decM);

  }
}

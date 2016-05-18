/*
 * Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jun 12, 2005
 * by developer
 */

package org.jactr.io.resolver;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.TestUtils;
import org.jactr.io.CommonIO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ResolverTest
{

  /**
   * Logger definition
   */

  static private final transient Log LOGGER = LogFactory
                                                .getLog(ResolverTest.class);
  
  private ACTRRuntime _runtime;
  
  String[] _cleanModels = {"org/jactr/io/models/semantic-model.jactr",
      "org/jactr/io/models/semantic-full.jactr"};
  
  @Before
  public void setUp() throws Exception
  {
	  _runtime = TestUtils.getRuntimeWithEmptyDefaultReality();
  }

  @After
  public void tearDown() throws Exception
  {
  }
  
  
  @Test
  public void testGenerator()
  {
    for(int i=0;i<_cleanModels.length;i++)
    {
      resolverTest(_cleanModels[i]);
    }
      
  }

  protected void resolverTest(String fileName)
  {
    LOGGER.info("Parsing "+fileName);
    CommonTree md = CommonIO.parserTest(fileName, true, true);
    
    LOGGER.info("Compiling "+fileName);
    CommonIO.compilerTest(md, true, true);
    
    LOGGER.info("Constructing "+fileName);
    IModel model = CommonIO.constructorTest(_runtime, md);
    
    assertNotNull(model);
    
    //now we create a new ast for the model
    CommonTree newMD = ASTResolver.toAST(model,true);
    
    assertNotNull(newMD);
 
    //this desc should be valid..
    CommonIO.compilerTest(newMD, true, true);
    
    //now lets generate..
    for(StringBuilder line : CommonIO.generateSource(newMD,"jactr"))
      LOGGER.debug(line.toString());
    
    LOGGER.info("Destroying model "+fileName);
    try
    {
      model.dispose();
    }
    catch (Exception e)
    {
       LOGGER.error("Could not dispose of model ",e);
       fail(e.getMessage());
    } 
  }
}

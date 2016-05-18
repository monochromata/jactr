package org.jactr.io.antlr3.parser.lisp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Map;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.io.CommonIO;
import org.jactr.io.IOUtilities;
import org.jactr.core.runtime.TestUtils;
import org.jactr.io.antlr3.compiler.JACTRCompiler;
import org.jactr.io.antlr3.misc.ASTSupport;
import org.jactr.io.parser.IModelParser;
import org.jactr.io.parser.ModelParserFactory;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LispParserTest
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(LispParserTest.class);

  private ACTRRuntime _runtime;
  
  @Before
  public void setUp() throws Exception
  {
	_runtime = TestUtils.getRuntimeWithEmptyDefaultReality();
    ModelParserFactory.addParser("lisp",
        org.jactr.io.antlr3.parser.lisp.LispModelParser.class);
  }
  
  @Ignore
  @Test
  public void testAddition() throws Exception
  {
    CommonTree mDesc = parserTest("org/jactr/io/models/addition.lisp", 6,
        22, 4, 3);
    mDesc = compilerTest(mDesc);
    IModel model = builderTest(mDesc, 6, 21, 4, 3);
    model.dispose();
  }
  
  @Ignore
  @Test
  public void testVisual() throws Exception
  {
    CommonTree mDesc = parserTest("org/jactr/io/models/visual.lisp", 19,
        28, 4, 3);
    mDesc = compilerTest(mDesc);
    IModel model = builderTest(mDesc, 6, 21, 4, 3);
    model.dispose();
  }
  
  @Ignore
  @Test
  public void testCount() throws Exception
  {
    /*
     * 6 chunktypes, 16 chunks, 3 prod, 3 buffers
     */
    CommonTree mDesc = parserTest("org/jactr/io/models/count.lisp", 6, 17,
        3, 3);
    mDesc = compilerTest(mDesc);
    IModel model = builderTest(mDesc, 6, 16, 3, 3);
    model.dispose();
  }
  
  @Ignore
  @Test
  public void testSemantic() throws Exception
  {
    CommonTree mDesc = parserTest("org/jactr/io/models/semantic.lisp", 6,
        63, 4, 3);
    mDesc = compilerTest(mDesc);
    IModel model = builderTest(mDesc, 6, 62, 4, 3);
    model.dispose();
  }

  private CommonTree compilerTest(CommonTree modelDesc) throws Exception
  {
    ArrayList<Exception> warnings = new ArrayList<Exception>();
    ArrayList<Exception> errors = new ArrayList<Exception>();

    IOUtilities.compileModelDescriptor(modelDesc, warnings, errors);

    String name = ASTSupport.getName(modelDesc);

    for(Exception warning : warnings)
      LOGGER.debug("Warning "+name+" ", warning);
    
    for(Exception error : errors)
      LOGGER.debug("Error "+name+" ",error);
    
    if(errors.size()!=0)
      fail("Compilation errors detected ");
    
    return modelDesc;
  }
  
  private IModel builderTest(CommonTree modelDesc, int chunkTypes, int chunks, int productions, int buffers) throws Exception
  {
    ArrayList<Exception> warnings = new ArrayList<Exception>();
    ArrayList<Exception> errors = new ArrayList<Exception>();

    IModel model = IOUtilities.constructModel(_runtime, modelDesc, warnings, errors);

    String name = ASTSupport.getName(modelDesc);

    for(Exception warning : warnings)
      LOGGER.debug("Warning "+name+" ", warning);
    
    for(Exception error : errors)
      LOGGER.debug("Error "+name+" ",error);
    
    if(errors.size()!=0)
      fail("Building errors detected ");
    
    return model;
  }

  
  private CommonTree parserTest(String file, int chunkTypes, int chunks, int productions, int buffers) throws Exception
  {
    IModelParser modelParser = CommonIO
        .parseModel(file);
    CommonTree modelDescriptor = CommonIO.getModelDescriptor(modelParser);

    Map<String, CommonTree> elements = ASTSupport.getMapOfTrees(
        modelDescriptor, JACTRCompiler.CHUNK_TYPE);
    assertEquals("Incorrect number of chunktypes, known :" + elements.keySet(),
        chunkTypes, elements.size());

    elements = ASTSupport.getMapOfTrees(modelDescriptor, JACTRCompiler.CHUNK);
    assertEquals("Incorrect number of chunks, known : " + elements.keySet(),
        chunks, elements.size());

    elements = ASTSupport.getMapOfTrees(modelDescriptor,
        JACTRCompiler.PRODUCTION);
    assertEquals("Incorrect number of productions, known : "
        + elements.keySet(), productions, elements.size());

    elements = ASTSupport.getMapOfTrees(modelDescriptor, JACTRCompiler.BUFFER);
    // expecting imaginal, retrieval, goal
    assertEquals("Incorrect number of buffers, known : " + elements.keySet(),
        buffers, elements.size());

    if (LOGGER.isDebugEnabled())
    {
      for (StringBuilder line : CommonIO
          .generateSource(modelDescriptor, "lisp"))
        LOGGER.debug(line.toString());

      for (StringBuilder line : CommonIO.generateSource(modelDescriptor,
          "jactr"))
        LOGGER.debug(line.toString());
    }
    
    for(Exception warning : modelParser.getParseWarnings())
      LOGGER.debug("Warning "+file+" ", warning);
    
    for(Exception error : modelParser.getParseErrors())
      LOGGER.debug("Error "+file+" ",error);
    
    return modelDescriptor;
  }
}

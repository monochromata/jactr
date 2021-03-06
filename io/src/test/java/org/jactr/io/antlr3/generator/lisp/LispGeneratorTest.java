/*
 * Created on May 31, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
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
package org.jactr.io.antlr3.generator.lisp;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.IOUtilities;
import org.jactr.io.generator.CodeGeneratorFactory;
import org.jactr.io.generator.ICodeGenerator;
import org.junit.Test;

public class LispGeneratorTest
{
  /**
   * logger definition
   */
  static public final Log LOGGER = LogFactory.getLog(LispGeneratorTest.class);

  @Test
  public void test() throws IOException
  {
    final ArrayList<Exception> warnings = new ArrayList<Exception>();
	final ArrayList<Exception> errors = new ArrayList<Exception>();
	CommonTree md = IOUtilities.loadModelFile(
        "org/jactr/io/models/semantic-model.jactr",
        warnings, errors);
	assertThat("There are warnings "+warnings, warnings.size(), equalTo(0));
	assertThat("There are errors "+errors, errors.size(), equalTo(0));
    ICodeGenerator gen = CodeGeneratorFactory.getCodeGenerator("lisp");
    Collection<StringBuilder> code = gen.generate(md, true);
    // TODO: Add assertions
    for (StringBuilder line : code)
      LOGGER.debug(line);
  }
}

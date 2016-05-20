/*
 * Created on Apr 12, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.tools.async.iterative;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.mina.service.ServerService;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.entry.iterative.IterativeMain;
import org.jactr.tools.async.iterative.tracker.IterativeRunTracker;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class NetworkedIterativeListenerTest
{
  /**
   * logger definition
   */
  static private final Log LOGGER = LogFactory
                                      .getLog(NetworkedIterativeListenerTest.class);

  IterativeRunTracker      _tracker;

  IterativeMain            _main;

  @Before
  public void setUp() throws Exception
  {
    _main = new IterativeMain();
    _tracker = new IterativeRunTracker();
    _tracker.setAddressInfo("localhost:6969");
    _tracker.setCredentialInformation("none:password");
    _tracker.setService(new ServerService());
  }

  // TODO: Add assertions
  @Ignore
  @Test
  public void test() throws Exception
  {
    /*
     * start up the tracker
     */
    _tracker.start();

    _main.run(getClass().getClassLoader().getResource(
        "org/jactr/tools/async/iterative/environment.xml"),
    		ACTRRuntime.DEFAULT_WORKING_DIRECTORY);

    /*
     * and stop
     */
    _tracker.stop();
  }

}

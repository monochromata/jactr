/*
 * Created on Apr 11, 2007 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
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
package org.jactr.entry.iterative;

import java.util.Collection;

import org.antlr.runtime.tree.CommonTree;
import org.jactr.core.model.IModel;

/**
 * A handler to interact with the IterativeMain entry point. If the iterative
 * run should be terminated, the listener can throw
 * {@link TerminateIterativeRunException} from any of the methods (except
 * {@link #stop()} ) and the block will exit. Listeners will not be notified of
 * {@link TerminateIterativeRunException}. Even if the exception is thrown,
 * {@link #stop()} will still be called.
 * 
 * 
 */
public interface IIterativeRunListener
{

  public void start(int totalRuns) throws TerminateIterativeRunException;

  public void stop();

  public void preLoad(int currentRunIndex, int totalRuns)
      throws TerminateIterativeRunException;

  /**
   * called before each build of the models in the IterativeMain. This gives the
   * listener a chance to tweak the models before they are constructed
   * 
   * @param currentRunIndex TODO
   * @param totalRuns TODO
   * @param modelDescriptors
   *          all the descriptors of the models to be created.
   * @throws TerminateIterativeRunException
   *           TODO
   */
  public void preBuild(int currentRunIndex, int totalRuns,
      Collection<CommonTree> modelDescriptors)
      throws TerminateIterativeRunException;

  /**
   * called before the run starts
   * 
   * @param currentRunIndex TODO
   * @param totalRuns TODO
   * @param models TODO
   * @throws TerminateIterativeRunException
   *           TODO
   */
  public void preRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException;

  public void postRun(int currentRunIndex, int totalRuns,
      Collection<IModel> models) throws TerminateIterativeRunException;

  /**
   * called if something goes horribly wrong. This can be invoked at any of
   * three locations:
   * 
   * <p>During a model execution (in which case index&gt;=1, model!=null)</p>
   * 
   * <p>If something goes wrong after the run during cleanup (index&gt;=1,
   * model==null)</p>
   * 
   * <p>or at the end if something goes wrong (index==0, model==null). </p>
   * 
   * <p>you cannot throw {@link TerminateIterativeRunException} when model!=null,
   * as it will not be caught by the IterativeMain entry point. If you need to
   * terminate the iterative run entirely because of a model level exception,
   * you should record that the exception occurred and then terminate from
   * postRun()</p>
   * 
   * @param index TODO
   * @param model TODO
   * @param thrown TODO
   * @throws TerminateIterativeRunException
   *           if the iterative run should be killed. TODO
   */
  public void exceptionThrown(int index, IModel model, Throwable thrown)
      throws TerminateIterativeRunException;
}

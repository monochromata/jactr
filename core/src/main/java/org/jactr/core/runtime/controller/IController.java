/*
 * Created on Feb 17, 2004 To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.jactr.core.runtime.controller;

import java.util.Collection;
import java.util.concurrent.Future;

import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;

/**
 * Controls the behavior of the ACTRRuntime.
 * 
 * <p>Implementations of this interface need to provide a 1-argument
 * constructor that consumes an {@link ACTRRuntime}.</p>
 */
public interface IController
{

  /**
   * @return the runtime to which this controller is meant to be attached.
   */
  public ACTRRuntime getRuntime();
  
  /**
   * attach to the runtime - called by the runtime during
   * ACTRRuntime.setController();
   *
   */
  public void attach();
  
  /**
   * detach the runtime - called by the runtime during
   * ACTRRuntime.setController(null);
   *
   */
  public void detach();
  
  /**
   * reset the runtime. stops all running models, performs some clean up, then
   * resets so that the models can be run again. Does not affect the model states at all
   * nor does it signal the reality interface
   */
//  public void reset();
  
  /**
   * run the models..The runtime is not actually running until at least
   * one model has finished initialization.
   *
   * @return TODO
   */
  public Future<Boolean> start();
  
  /**
   * returns a future that can be blocked on until the runtime starts fully (i.e. a model is running)
   * 
   * @return the future
   */
  public Future<Boolean> waitForStart();
  
  
  /**
   * start the models but suspend at the start of the first cycle
   * 
   * @param suspendImmediately TODO
   * @return TODO
   */
  public Future<Boolean> start(boolean suspendImmediately);
  
  /**
   * complete().get() will block until completed
   * 
   * @return TODO
   */
  public Future<Boolean> complete();
  
  /**
   * @return TODO
   */
  public Future<Boolean> waitForCompletion();

  /**
   * stop all the current running models at the nearest possible moment.
   * This is a clean stop.
   * 
   * @return TODO
   */
  public Future<Boolean> stop();
  
  /**
   * force all the models to terminate
   * 
   * @return TODO
   */
  public Future<Boolean> terminate();

  /**
   * pause at the next immediate opportunity
   * 
   * @return TODO
   *
   */
  public Future<Boolean> suspend();
  
  /**
   * @return TODO
   */
  public Future<Boolean> waitForSuspension();

  /**
   * resume from a suspend
   * 
   * @return TODO
   */
  public Future<Boolean> resume();
  
  /**
   * @return TODO
   */
  public Future<Boolean> waitForResumption();

  /**
   * @return TODO
   */
  public boolean isRunning();

  /**
   * @return TODO
   */
  public boolean isSuspended();
  
  /**
   * @return running models
   */
  public Collection<IModel> getRunningModels();
  
  /**
   * @return terminated models
   */
  public Collection<IModel> getTerminatedModels();
  
  /**
   * @return suspended models
   */
  public Collection<IModel> getSuspendedModels();
}
package org.jactr.embed;

/*
 * default logging
 */
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.reality.CommonReality;
import org.commonreality.reality.impl.DefaultReality;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.IConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.controller.debug.DebugController;
import org.jactr.io.parser.ModelParserFactory;
import org.jactr.io.participant.ASTParticipantRegistry;
import org.jactr.scripting.ScriptingManager;

/**
 * The start of a runtime builder to better support embedding. If running
 * outside of the Eclipse environment and using externally contributed
 * modules, you must ensure that classpaths are accessible. This is not
 * reusable, and assumes no one else is attempting to configure the runtime.
 * 
 * @author harrison
 */
public class RuntimeBuilder
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER          = LogFactory
                                                         .getLog(RuntimeBuilder.class);

  private ACTRRuntime                _runtime;
  
  private Collection<Runnable>       _parserInitializers;

  private Collection<Runnable>       _astInitializers;

  private Collection<Runnable>       _scriptInitializers;

  private Supplier<Object>           _applicationDataSupplier;

  private Collection<IModel>         _models;

  private IConnector                 _connector;

  static public RuntimeBuilder newBuilder()
  {
    return new RuntimeBuilder();
  }

  protected RuntimeBuilder()
  {
	final DefaultReality reality = DefaultReality.newInstanceThatNeedsToBePreparedWithACommonReality();
	final CommonReality cr = new CommonReality(reality);
	reality.prepare(cr);
	_runtime = new ACTRRuntime(cr, ACTRRuntime.DEFAULT_WORKING_DIRECTORY);
    _parserInitializers = new ArrayList<Runnable>();
    _astInitializers = new ArrayList<Runnable>();
    _scriptInitializers = new ArrayList<Runnable>();
    _models = new ArrayList<IModel>();
  }
  
  /**
   * add the model to the runtime to be built. This model must have all it's
   * instruments installed already.
   * 
   * @param model TODO
   * @return TODO
   */
  public RuntimeBuilder addModel(IModel model)
  {
    _models.add(model);
    return this;
  }

  /**
   * If you need to add access to custom model parsers, it can be contributed at
   * this point. Accessible via the {@link ModelParserFactory} singleton
   * 
   * @param initializer TODO
   * @return TODO
   */
  public RuntimeBuilder addParserInitializer(Runnable initializer)
  {
    _parserInitializers.add(initializer);
    return this;
  }

  /**
   * If you need to add access to custome ast participants (used by modules and
   * extensions), it is contributed here. Accesible via the
   * {@link ASTParticipantRegistry} singelton
   * 
   * @param intializer TODO
   * @return TODO
   */
  public RuntimeBuilder addASTInitializer(Runnable intializer)
  {
    _astInitializers.add(intializer);
    return this;
  }

  /**
   * If you need to add access to custom scripting support. Accessible via the
   * {@link ScriptingManager} singleton
   * 
   * @param initializer TODO
   * @return TODO
   */
  public RuntimeBuilder addScriptInitializer(Runnable initializer)
  {
    _scriptInitializers.add(initializer);
    return this;
  }

  public RuntimeBuilder setApplicationData(Supplier<Object> supplier)
  {
    _applicationDataSupplier = supplier;
    return this;
  }

  public RuntimeBuilder with(IConnector connector)
  {
    _connector = connector;
    return this;
  }

  /**
   * Terminal build operator that will construct and configure the runtime. It
   * returns the controller that should be used to manage the runtime.
   * 
   * @return TODO
   */
  public IController build()
  {
    buildInternal();
    return defaultController();
  }

  /**
   * Alternatitve terminal build operator that constructs the runtime, returning
   * a debug controller for finer grained runtime control.
   * 
   * @return TODO
   */
  public IController debugBuild()
  {
    buildInternal();
    return debugController();
  }

  protected DefaultController defaultController()
  {
    DefaultController controller = new DefaultController(_runtime);
    _runtime.setController(controller);
    return controller;
  }

  protected DebugController debugController()
  {
    DebugController controller = new DebugController(_runtime);
    _runtime.setController(controller);
    return controller;
  }

  protected void buildInternal()
  {
    resetRuntime();
    bootstrap();
    initialize();
  }

  protected void resetRuntime()
  {
    IController controller = _runtime.getController();
    if (controller != null)
    {
      if (controller.isRunning()) try
      {
        controller.terminate().get();
      }
      catch (Exception e)
      {
        LOGGER.error("Failed to terminate runtime, forcing forward ", e);
      }
      _runtime.setController(null);
    }

    _runtime.setConnector(null);

    /*
     * zero our application data
     */
    _runtime.setApplicationData(null);

    Collection<IModel> models = _runtime.getModels();

    // cleanup any existing models
    models.forEach((m) -> {
      _runtime.removeModel(m);
      m.dispose();
    });

  }

  protected void bootstrap()
  {
    /*
     * TODO defensive exception handling
     */
    for (Runnable init : _scriptInitializers)
      init.run();

    for (Runnable init : _astInitializers)
      init.run();

    for (Runnable init : _parserInitializers)
      init.run();

    if (_applicationDataSupplier != null)
      _runtime.setApplicationData(_applicationDataSupplier.get());
  }

  protected void initialize()
  {
    _runtime.setConnector(_connector);

    for (IModel model : _models)
      _runtime.addModel(model);
  }
  
  /**
   * @return The runtime after bootstrapping and initialization.
   */
  public ACTRRuntime getRuntime()
  {
	  return _runtime;
  }

}

package org.jactr.tools.experiment.actions.common;

/*
 * default logging
 */
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.AbstractAction;
import org.jactr.tools.experiment.actions.AbstractModelAction;
import org.jactr.tools.experiment.impl.IVariableContext;
import org.jactr.tools.experiment.impl.VariableContext;
import org.jactr.tools.experiment.impl.VariableResolver;
import org.jactr.tools.experiment.misc.ModelUtilities;

public class ProxyAction extends AbstractAction
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(ProxyAction.class);

  private AbstractModelAction             _actualAction;

  private String                     _models;

  private IExperiment                _experiment;

  public ProxyAction(ACTRRuntime runtime, String className, String models, IExperiment experiment)
  {
	super(runtime);
    _experiment = experiment;
    _models = models;
    try
    {
      _actualAction = (AbstractModelAction) getClass().getClassLoader().loadClass(
          className).newInstance();
    }
    catch (Exception e)
    {
      throw new IllegalArgumentException(
          "Could not create AbstractModelAction from " + className, e);
    }
  }

  public void fire(IVariableContext context)
  {
    IVariableContext child = new VariableContext(_experiment.getVariableContext());
    
    child.set("=experiment", _experiment);

    Collection<IModel> models = VariableResolver.getModels(getRuntime(), _models,
        _experiment.getVariableResolver(), context);
    if (_models.length() == 0)
    {
      /*
       * local execution
       */
      _actualAction.fire(context);
    }
    else
    {
      for (IModel model : models)
      {
        final IVariableContext finalContext = new VariableContext(child);
        finalContext.set("=model", model);

        ModelUtilities.executeLater(model, new Runnable() {

          public void run()
          {
            try
            {
              _actualAction.fire(finalContext);
            }
            catch (Exception e)
            {
              LOGGER.error("Failed to execute "
                  + _actualAction.getClass().getName(), e);
            }
          }
        });
      }
    }
  }

}

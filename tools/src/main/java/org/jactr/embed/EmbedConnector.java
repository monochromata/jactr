package org.jactr.embed;

/*
 * default logging
 */
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.agents.ThinAgent;
import org.commonreality.reality.CommonReality;
import org.commonreality.time.IClock;
import org.commonreality.util.LockUtilities;
import org.jactr.core.model.IModel;
import org.jactr.core.reality.connector.LocalConnector;
import org.jactr.core.runtime.ACTRRuntime;

public class EmbedConnector extends LocalConnector
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER          = LogFactory
                                                         .getLog(EmbedConnector.class);

  static private final String        EMBED_AGENT_KEY = "embedConnector.thinAgent";

  private final ACTRRuntime          _runtime;
  
  private boolean                    _running        = false;

  private ReentrantReadWriteLock     _lock           = new ReentrantReadWriteLock();

  public EmbedConnector(ACTRRuntime runtime)
  {
    super(runtime);
    _runtime=runtime;
  }

  public EmbedConnector(ACTRRuntime runtime, boolean useIndependentClocks)
  {
    super(runtime, useIndependentClocks);
    _runtime = runtime;
  }

  @Override
  public void connect(IModel model)
  {

    try
    {
      LockUtilities.runLocked(_lock.writeLock(), () -> {
        if (!isConnected(model))
        {
          super.connect(model);
          startThinAgent(model, getClock(model));
        }
      });
    }
    catch (InterruptedException e)
    {
      // totally expected if we are haulting, but very unlikely
      LOGGER.debug("EmbedConnector.connect threw InterruptedException : ", e);
    }
    catch (Exception e)
    {
      LOGGER.error("EmbedConnector.connect threw Exception : ", e);
    }

  }

  @Override
  public void disconnect(IModel model)
  {
    try
    {
      LockUtilities.runLocked(_lock.writeLock(), () -> {
        if (isConnected(model))
        {
          super.disconnect(model);
          stopThinAgent(model);
        }
      });
    }
    catch (InterruptedException e)
    {
      LOGGER.debug("EmbedConnector.connect threw InterruptedException : ", e);
    }
    catch (Exception e)
    {
      LOGGER.error("EmbedConnector.connect threw Exception : ", e);
    }
  }

  public boolean isConnected(IModel model)
  {
    return getAgent(model) != null;
  }

  @Override
  public IAgent getAgent(IModel model)
  {
    return (IAgent) model.getMetaData(EMBED_AGENT_KEY);
  }

  @Override
  public boolean isRunning()
  {
    return _running;
  }

  /**
   * We create and start the thin agents at start, not connect, so that we are
   * sure they are available immediately. It is common to require the thinagent
   * slightly before the model has fully started.
   * 
   * @see org.jactr.core.reality.connector.IConnector#start()
   */
  @Override
  public void start()
  {
    try
    {
      LockUtilities.runLocked(_lock.writeLock(), () -> {
        if (!_running)
          for (IModel model : _runtime.getModels())
            connect(model);
        _running = true;
      });
    }
    catch (InterruptedException e)
    {
      LOGGER.error("EmbedConnector.start threw InterruptedException : ", e);
    }
  }

  @Override
  public void stop()
  {
    try
    {
      LockUtilities.runLocked(_lock.writeLock(), () -> {
        if (_running) for (IModel model : _runtime.getModels())
          disconnect(model);
        _running = false;
      });
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      LOGGER.error("EmbedConnector.start threw InterruptedException : ", e);
    }
  }

  protected void startThinAgent(IModel model, IClock clock)
  {
    ThinAgent agent = new ThinAgent(model.getRuntime().getCommonReality(), model.getName(), clock);

    try
    {

      agent.connect(); // noop

      agent.initialize();

      agent.start();

      model.setMetaData(EMBED_AGENT_KEY, agent);
    }
    catch (Exception e)
    {
      LOGGER
          .error(String.format("Failed to start thin agent for %s", model), e);
    }
  }

  protected void stopThinAgent(IModel model)
  {
    ThinAgent agent = (ThinAgent) model.getMetaData(EMBED_AGENT_KEY);
    model.setMetaData(EMBED_AGENT_KEY, null);

    if (agent != null)
      try
      {
        agent.stop();
        agent.disconnect();
        agent.shutdown();
      }
      catch (Exception e)
      {
        LOGGER.error(String.format("Failed to stop thin agent for %s", model),
            e);
      }
  }
}

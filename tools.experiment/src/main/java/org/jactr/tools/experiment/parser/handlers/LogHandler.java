package org.jactr.tools.experiment.parser.handlers;

/*
 * default logging
 */
import java.util.function.Consumer;

import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.tools.experiment.IExperiment;
import org.jactr.tools.experiment.actions.IAction;
import org.jactr.tools.experiment.actions.common.LogAction;
import org.w3c.dom.Element;

/**
 * checks for {@code <log message="....."/>}, with an optional attribute,
 * destination="out|err|null". The system property "LogAction.destination" can
 * be set to out, err, or null, and will override the log tag defined
 * definition.
 * 
 * <p>out and err redirect to stdout and stderr. null will silently consume all the
 * log messages.</p>
 * 
 * @author harrison
 */
public class LogHandler implements INodeHandler<IAction>
{
  public String getTagName()
  {
    return "log";
  }

  public IAction process(ACTRRuntime runtime, Element element, IExperiment experiment)
  {
    String message = element.getAttribute("message");
    boolean hasDestination = element.hasAttribute("destination")
        || System.getProperty("LogAction.destination") != null;

    if (!hasDestination)
      return new LogAction(runtime, message, experiment);
    else
    {
      String destination = System.getProperty("LogAction.destination", "");

      if (destination.length() == 0) // not defined
        destination = element.getAttribute("destination");

      // default assume "out"
      Consumer<String> consumer = LogAction.STDOUT;

      if (destination.equalsIgnoreCase("err"))
        consumer = LogAction.STDERR;
      else if (destination.equalsIgnoreCase("null")) consumer = LogAction.NULL;

      return new LogAction(runtime, message, experiment, consumer);
    }
  }

  public boolean shouldDecend()
  {
    return false;
  }
}
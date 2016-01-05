package org.jactr.sensors.swing.io;

/*
 * default logging
 */
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jactr.io.participant.impl.BasicASTParticipant;
import org.jactr.sensors.swing.SwingExtension;
import org.jactr.sensors.swing.encoders.ButtonComponentVisualEncoder;
import org.jactr.sensors.swing.encoders.LabelComponentVisualEncoder;
import org.jactr.sensors.swing.encoders.TextFieldComponentVisualEncoder;

public class SwingExtensionASTParticipant extends BasicASTParticipant
{

  public SwingExtensionASTParticipant()
  {
    super("org/commonreality/sensors/swing/jactr/io/swing-types.jactr");
    setInstallableClass(SwingExtension.class);
    Map<String, String> params = new TreeMap<String,String>();
    
    /*
     * we add the default encoders here.. these parameters will be
     * injected if they are missing..
     */
    params.put(ButtonComponentVisualEncoder.class.getName(), "TRUE");
    params.put(LabelComponentVisualEncoder.class.getName(), "TRUE");
    params.put(TextFieldComponentVisualEncoder.class.getName(), "TRUE");
    setParameterMap(params);
  }
}

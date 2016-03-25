package org.jactr.core.module.procedural.six.learning.event;

/*
 * default logging
 */
import java.util.EventListener;

public interface IProceduralLearningModule6Listener extends EventListener
{

  /**
   * a reward has been signalled
   * 
   * @param event TODO
   */
  public void rewarded(ProceduralLearningEvent event);

  public void startReward(ProceduralLearningEvent event);

  /**
   * called after all the rewards have been processed
   * 
   * @param event TODO
   */
  public void stopReward(ProceduralLearningEvent event);
}

package org.jactr.core.module.random;

import static org.junit.Assert.assertEquals;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.stat.descriptive.SummaryStatistics;
import org.jactr.core.module.random.six.DefaultRandomModule;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.TestUtils;
import org.junit.Ignore;
import org.junit.Test;

public class RandomTest
{
  /**
   * Logger definition
   */
  static private final transient Log LOGGER = LogFactory
                                                .getLog(RandomTest.class);

  // TODO: Spuriously fails for reasons like 
  // Failed tests:   testLogistic(org.jactr.core.module.random.RandomTest): expected:<13.159472534785811> but was:<13.009142151454393>
  @Ignore
  @Test
  public void testLogistic()
  {
    final ACTRRuntime runtime = TestUtils.getRuntimeWithEmptyDefaultReality();
	IRandomModule module = new DefaultRandomModule(runtime);

    SummaryStatistics summary = new SummaryStatistics();

    double s = 2;
    double variance = s * s * Math.PI * Math.PI / 3;

    for (int i = 0; i < 100000; i++)
      summary.addValue(module.logisticNoise(s));

    assertEquals(variance, summary.getVariance(), 0.1);
    assertEquals(0, summary.getMean(), 0.1);
  }
}

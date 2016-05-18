package org.jactr.core.runtime;

import org.commonreality.reality.CommonReality;
import org.commonreality.reality.impl.DefaultReality;

/**
 * Utility methods that return {@link ACTRRuntime} instances for testing purposes.
 */
public class TestUtils {

  /**
   * A factory for {@link ACTRRuntime} instances with a {@link CommonReality} that has nothing
   * but a {@link DefaultReality} - i.e. no sensors or actors.
   * 
   * @return the runtime
   */
  public static ACTRRuntime getRuntimeWithEmptyDefaultReality()
  {
    DefaultReality reality = DefaultReality.newInstanceThatNeedsToBePreparedWithACommonReality();
    ACTRRuntime runtime = new ACTRRuntime(new CommonReality(reality), ACTRRuntime.DEFAULT_WORKING_DIRECTORY);
    reality.prepare(runtime.getCommonReality());
    return runtime;
  }
	
}

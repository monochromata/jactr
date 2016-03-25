package org.jactr.core.buffer;

import java.util.Set;

import org.jactr.core.buffer.six.DefaultSourceActivationSpreader;
import org.jactr.core.chunk.IChunk;

/**
 * logic that is responsible for the setting, updating and tracking of source
 * activation for a given buffer. To override the
 * {@link DefaultSourceActivationSpreader} call
 * {@link AbstractActivationBuffer#setActivationSpreader(ISourceActivationSpreader)}.
 * 
 * @author harrison
 */
public interface ISourceActivationSpreader
{

	/**
	 * @return the buffer
	 */
  public IActivationBuffer getBuffer();

  public void spreadSourceActivation();

  public void clearSourceActivation();

  /**
   * 
   * @param container TODO
   * @return TODO
   */
  public Set<IChunk> getActivatedChunks(Set<IChunk> container);
}

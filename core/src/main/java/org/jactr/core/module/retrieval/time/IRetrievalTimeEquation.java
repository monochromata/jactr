/**
 * Copyright (C) 2001-3, Anthony Harrison anh23@pitt.edu This library is free
 * software; you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details. You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package org.jactr.core.module.retrieval.time;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * compute the retrieval time of a chunk. Implementations should also implement
 * static public XXXEquation getInstance()
 * 
 * @author harrison
 */
public interface IRetrievalTimeEquation
{

  /**
   * compute retrieval time of chunk c in the model. This call is used when
   * there is no partial matching.
   * 
   * @param c the chunk
   * @return the retrieval time
   */
  public double computeRetrievalTime(IChunk c);

  /**
   * compute the retrieval time of retrievedChunk relative to retrievalRequest
   * if partial matching is enabled.
   * 
   * @param retrievedChunk TODO
   * @param retrievalRequest TODO
   * @return TODO
   */
  public double computeRetrievalTime(IChunk retrievedChunk,
      ChunkTypeRequest retrievalRequest);
}
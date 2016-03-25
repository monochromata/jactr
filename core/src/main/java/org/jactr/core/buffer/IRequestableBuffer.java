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

package org.jactr.core.buffer;

import org.jactr.core.production.request.IRequest;

/**
 * Marker interface for buffers that can accept {@link IRequest}s that will then
 * be forwarded to the owning module for processing.
 * 
 * @author harrison
 */
public interface IRequestableBuffer extends IActivationBuffer {

	/**
	 * @param request
	 *            the request
	 * @return true if this buffer would accept this request
	 */
	public boolean willAccept(IRequest request);

	/**
	 * Make a request of the module.
	 * 
	 * @param source the request
	 * @param requestTime TODO
	 * @return true if the request was accepted, false if not.
	 * @throws IllegalArgumentException
	 *             if the request is accepted but invalid
	 */
	public boolean request(IRequest source, double requestTime) throws IllegalArgumentException;

}

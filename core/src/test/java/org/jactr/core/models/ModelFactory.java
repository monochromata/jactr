package org.jactr.core.models;

import org.jactr.core.model.IModel;

/**
 * A factory for jATC-R models.
 */
public interface ModelFactory {

	/**
	 * @return A new model with the name configured in the constructor
	 * @throws Exception if the model cannot be constructed
	 */
	IModel createAndInitializeModel() throws Exception;

}
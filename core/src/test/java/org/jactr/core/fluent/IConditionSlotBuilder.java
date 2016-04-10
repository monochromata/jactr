package org.jactr.core.fluent;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;

public interface IConditionSlotBuilder {

	public IConditionSlotsBuilder equalsNull();
	
	/**
	 * 
	 * @param value the string - must not start with an equals sign (=).
	 * @return
	 * @throws IllegalArgumentException if the given string starts with an equals sign (=).
	 * 	Note that {@link #equalsVariable(String)} should be used to compare a slot to a variable value.
	 */
	public IConditionSlotsBuilder equalsString(String value);
	
	/**
	 * 
	 * @param variable the name of the variable - needs to start with an equal sign (=).
	 * @return
	 * @throws IllegalArgumentException if the given string does not start with an equals sign (=).
	 * 	Note that {@link #equalsString(String)} should be used to set a slot to a string value.
	 */
	public IConditionSlotsBuilder equalsVariable(String variable);

	public IConditionSlotsBuilder equalsDouble(double value);

	/**
	 * Add a condition matching the value of current slot to a chunk of the
	 * given name, e.g. {@code slot("goal").equalsChunk("g1")}.
	 * 
	 * @param chunkName
	 *            the name of the chunk
	 * @return the builder to continue building
	 * @throws BuilderException
	 *             If no chunk of the given name exists.
	 */
	public IConditionSlotsBuilder equalsChunk(String chunkName) throws BuilderException;

	public IConditionSlotsBuilder equalsChunk(IChunk value);

	/**
	 * Add a condition matching the value of current slot to a chunk type of the
	 * given name, e.g. {@code slot("goal").equalsChunkType("state")}.
	 * 
	 * @param chunkTypeName
	 *            the name of the chunk type
	 * @return the builder to continue building
	 * @throws BuilderException
	 *             If no chunk type of the given name exists.
	 */
	public IConditionSlotsBuilder equalsChunkType(String chunkTypeName) throws BuilderException;

	public IConditionSlotsBuilder equalsChunkType(IChunkType value);
	
	// TODO: Can notEqualsNull be implemented? public IConditionSlotsBuilder notEequalsNull();
	
	public IConditionSlotsBuilder notEqualsString(String value);
	
	public IConditionSlotsBuilder notEqualsVariable(String variable);

	public IConditionSlotsBuilder notEqualsDouble(double value);

	public IConditionSlotsBuilder notEqualsChunk(String chunkName) throws BuilderException;

	public IConditionSlotsBuilder notEqualsChunk(IChunk value);
	
	public IConditionSlotsBuilder notEqualsChunkType(String chunkTypeName) throws BuilderException;

	public IConditionSlotsBuilder notEqualsChunkType(IChunkType value);

	public IConditionSlotsBuilder eqNull();
	
	public IConditionSlotsBuilder eqString(String value);
	
	public IConditionSlotsBuilder eqVariable(String variable);

	public IConditionSlotsBuilder eqDouble(double value);

	/**
	 * 
	 * @param chunkName
	 * @return
	 * @throws BuilderException
	 */
	public IConditionSlotsBuilder eqChunk(String chunkName);

	public IConditionSlotsBuilder eqChunk(IChunk value);

	/**
	 * 
	 * @param chunkTypeName
	 * @return
	 * @throws BuilderException
	 */
	public IConditionSlotsBuilder eqChunkType(String chunkTypeName);

	public IConditionSlotsBuilder eqChunkType(IChunkType value);
	
	// TODO: Can neqNull be implemented? public IConditionSlotsBuilder neqNull();
	
	public IConditionSlotsBuilder neqString(String value);
	
	public IConditionSlotsBuilder neqVariable(String variable);

	public IConditionSlotsBuilder neqDouble(double value);

	/**
	 * 
	 * @param chunkName
	 * @return
	 * @throws BuilderException
	 */
	public IConditionSlotsBuilder neqChunk(String chunkName);

	public IConditionSlotsBuilder neqChunk(IChunk value);

	/**
	 * 
	 * @param chunkTypeName
	 * @return
	 * @throws BuilderException
	 */
	public IConditionSlotsBuilder neqChunkType(String chunkTypeName);

	public IConditionSlotsBuilder neqChunkType(IChunkType value);

}

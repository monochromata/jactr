package org.jactr.core.fluent;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;

public interface IActionSlotBuilder {

	public IActionSlotsBuilder toNull();
	/**
	 * 
	 * @param value the string - must not start with an equals sign (=).
	 * @return
	 * @throws IllegalArgumentException if the given string starts with an equals sign (=).
	 * 	Note that {@link #toVariable(String)} should be used to set a slot to a variable value.
	 */
	public IActionSlotsBuilder toString(String value);
	/**
	 * 
	 * @param variable the name of the variable - needs to start with an equal sign (=).
	 * @return
	 * @throws IllegalArgumentException if the given string does not start with an equals sign (=).
	 * 	Note that {@link #toString(String)} should be used to set a slot to a string value.
	 */
	public IActionSlotsBuilder toVariable(String variable);
	public IActionSlotsBuilder toDouble(double value);
	/**
	 * 
	 * @param chunkName
	 * @return
	 * @throws BuilderException
	 */
	public IActionSlotsBuilder toChunk(String chunkName);
	public IActionSlotsBuilder toChunk(IChunk chunk);
	/**
	 * 
	 * @param chunkTypeName
	 * @return
	 * @throws BuilderException
	 */
	public IActionSlotsBuilder toChunkType(String chunkTypeName);
	public IActionSlotsBuilder toChunkType(IChunkType chunkType);
	
}

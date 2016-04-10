package org.jactr.core.fluent.impl;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.jactr.core.slot.IConditionalSlot.EQUALS;
import static org.jactr.core.slot.IConditionalSlot.NOT_EQUALS;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.fluent.BuilderException;
import org.jactr.core.fluent.IConditionSlotBuilder;
import org.jactr.core.fluent.IConditionSlotsBuilder;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.DefaultVariableConditionalSlot;
import org.jactr.core.slot.IConditionalSlot;
import org.jactr.core.slot.ISlot;

public class DefaultConditionSlotBuilder implements IConditionSlotBuilder {

	private final AbstractConditionBuilder conditionBuilder;
	private final String name;
	
	public DefaultConditionSlotBuilder(AbstractConditionBuilder conditionBuilder, String name) {
		this.conditionBuilder = conditionBuilder;
		this.name = name;
	}
	
	@Override
	public IConditionSlotsBuilder equalsNull() {
		return eqNull();
	}

	@Override
	public IConditionSlotsBuilder equalsString(String value) {
		return eqString(value);
	}

	@Override
	public IConditionSlotsBuilder equalsVariable(String variable) {
		return eqVariable(variable);
	}

	@Override
	public IConditionSlotsBuilder equalsDouble(double value) {
		return eqDouble(value);
	}

	@Override
	public IConditionSlotsBuilder equalsChunk(String chunkName) {
		return eqChunk(chunkName);
	}

	@Override
	public IConditionSlotsBuilder equalsChunk(IChunk value) {
		return eqChunk(value);
	}

	@Override
	public IConditionSlotsBuilder equalsChunkType(String chunkTypeName) {
		return eqChunkType(chunkTypeName);
	}

	@Override
	public IConditionSlotsBuilder equalsChunkType(IChunkType value) {
		return equalsChunkType(value);
	}
	
	@Override
	public IConditionSlotsBuilder notEqualsString(String value) {
		return neqString(value);
	}

	@Override
	public IConditionSlotsBuilder notEqualsVariable(String variable) {
		return neqVariable(variable);
	}

	@Override
	public IConditionSlotsBuilder notEqualsDouble(double value) {
		return neqDouble(value);
	}

	@Override
	public IConditionSlotsBuilder notEqualsChunk(String chunkName) {
		return neqChunk(chunkName);
	}

	@Override
	public IConditionSlotsBuilder notEqualsChunk(IChunk value) {
		return neqChunk(value);
	}

	@Override
	public IConditionSlotsBuilder notEqualsChunkType(String chunkTypeName) {
		return neqChunkType(chunkTypeName);
	}

	@Override
	public IConditionSlotsBuilder notEqualsChunkType(IChunkType value) {
		return neqChunkType(value);
	}

	@Override
	public IConditionSlotsBuilder eqNull() {
		return addNonVariableSlot(null, EQUALS);
	}
	
	@Override
	public IConditionSlotsBuilder eqString(String value) {
		return testString(value, EQUALS);
	}

	@Override
	public IConditionSlotsBuilder neqString(String value) {
		return testString(value, NOT_EQUALS);
	}

	protected IConditionSlotsBuilder testString(String value, int condition) {
		requireNonNull(value, "String required");
		if(value.trim().startsWith("=")) {
			throw new IllegalArgumentException(
					format("Cannot create comparison for string %1$s starting with =. Use (n)eqVariable to compare a slot to a variable", value));
		}
		return addNonVariableSlot(value, condition);
	}
	
	@Override
	public IConditionSlotsBuilder eqVariable(String variable) {
		return testVariable(variable, EQUALS);
	}

	@Override
	public IConditionSlotsBuilder neqVariable(String variable) {
		return testVariable(variable, NOT_EQUALS);
	}
	
	protected IConditionSlotsBuilder testVariable(String variable, int condition) {
		requireNonNull(variable, "Variable required");
		if(variable.trim().startsWith("=")) {
			return addVariableSlot(variable, condition);
		} else {
			throw new IllegalArgumentException(
					format("Cannot create comparison for variable %1$s that does not start with =. Use (n)eqString(String) for string comparisons.", variable));
		}
	}

	@Override
	public IConditionSlotsBuilder eqDouble(double value) {
		return addNonVariableSlot(value, EQUALS);
	}

	@Override
	public IConditionSlotsBuilder neqDouble(double value) {
		return addNonVariableSlot(value, NOT_EQUALS);
	}

	@Override
	public IConditionSlotsBuilder eqChunk(String chunkName) {
		return testChunk(chunkName, EQUALS);
	}

	@Override
	public IConditionSlotsBuilder neqChunk(String chunkName) {
		return testChunk(chunkName, NOT_EQUALS);
	}
	
	protected IConditionSlotsBuilder testChunk(String chunkName, int condition) {
		requireNonNull(chunkName, "Chunk name required");
		final IChunk chunk = conditionBuilder.getConditionsBuilder().getChunk(chunkName);
		return testChunk(chunk, condition);
	}

	@Override
	public IConditionSlotsBuilder eqChunk(IChunk value) {
		return testChunk(value, IConditionalSlot.EQUALS);
	}

	@Override
	public IConditionSlotsBuilder neqChunk(IChunk value) {
		return testChunk(value, IConditionalSlot.NOT_EQUALS);
	}

	protected IConditionSlotsBuilder testChunk(IChunk value, int condition) {
		requireNonNull(value, "Chunk required");
		return addNonVariableSlot(value, condition);
	}

	@Override
	public IConditionSlotsBuilder eqChunkType(String chunkTypeName) {
		return testChunkType(chunkTypeName, EQUALS);
	}

	@Override
	public IConditionSlotsBuilder neqChunkType(String chunkTypeName) {
		return testChunkType(chunkTypeName, NOT_EQUALS);
	}

	protected IConditionSlotsBuilder testChunkType(String chunkTypeName, int condition) {
		requireNonNull(chunkTypeName, "Chunk type name required");
		final IChunkType chunkType = conditionBuilder.getConditionsBuilder().getChunkType(chunkTypeName);
		return testChunkType(chunkType, condition);
	}

	@Override
	public IConditionSlotsBuilder eqChunkType(IChunkType value) {
		return testChunkType(value, EQUALS);
	}

	@Override
	public IConditionSlotsBuilder neqChunkType(IChunkType value) {
		return testChunkType(value, NOT_EQUALS);
	}

	protected IConditionSlotsBuilder testChunkType(IChunkType value, int condition) {
		requireNonNull(value, "Chunk type required");
		return addNonVariableSlot(value, condition);
	}
	
	private IConditionSlotsBuilder addVariableSlot(Object value, int condition) {
		return addSlot(new DefaultVariableConditionalSlot(name, condition, value));
	}
	
	private IConditionSlotsBuilder addNonVariableSlot(Object value, int condition) {
		return addSlot(new DefaultConditionalSlot(name, condition, value));
	}
	
	private IConditionSlotsBuilder addSlot(ISlot slot) {
		conditionBuilder.addSlot(slot);
		return conditionBuilder;
	}
	
}
package org.jactr.core.fluent.impl;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.fluent.IActionSlotBuilder;
import org.jactr.core.fluent.IActionSlotsBuilder;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.DefaultVariableConditionalSlot;
import org.jactr.core.slot.ISlot;

public class DefaultActionSlotBuilder implements IActionSlotBuilder {

	private final AbstractActionBuilder actionBuilder;
	private final String name;
	
	public DefaultActionSlotBuilder(AbstractActionBuilder actionBuilder, String name) {
		this.actionBuilder = actionBuilder;
		this.name = name;
	}

	@Override
	public IActionSlotsBuilder toNull() {
		return addNonVariableSlot(null);
	}

	@Override
	public IActionSlotsBuilder toString(String value) {
		requireNonNull(value, "String required");
		if(value.trim().startsWith("=")) {
			throw new IllegalArgumentException(
					format("String %1$s starts with a =. Use toVariable(String) instead, to add a variable.", value));
		}
		return addNonVariableSlot(value);
	}

	@Override
	public IActionSlotsBuilder toVariable(String variable) {
		requireNonNull(variable, "Variable required");
		if(variable.trim().startsWith("=")) {
			return addVariableSlot(variable);
		} else {
			throw new IllegalArgumentException(
					format("Variable name %1$s does not start with =. Use toString(String) to add a string value.", variable));
		}
	}

	@Override
	public IActionSlotsBuilder toDouble(double value) {
		return addNonVariableSlot(value);
	}

	@Override
	public IActionSlotsBuilder toChunk(String chunkName) {
		requireNonNull(chunkName, "Chunk name required");
		return toChunk(actionBuilder.getActionsBuilder().getChunk(chunkName));
	}

	@Override
	public IActionSlotsBuilder toChunk(IChunk chunk) {
		requireNonNull(chunk, "Chunk required");
		return addNonVariableSlot(chunk);
	}

	@Override
	public IActionSlotsBuilder toChunkType(String chunkTypeName) {
		requireNonNull(chunkTypeName, "Chunk type name required");
		return toChunkType(actionBuilder.getActionsBuilder().getChunkType(chunkTypeName));
	}

	@Override
	public IActionSlotsBuilder toChunkType(IChunkType chunkType) {
		requireNonNull(chunkType, "Chunk type required");
		return addNonVariableSlot(chunkType);
	}
	
	private IActionSlotsBuilder addVariableSlot(Object value) {
		return addSlot(new DefaultVariableConditionalSlot(name, value));
	}
	
	private IActionSlotsBuilder addNonVariableSlot(Object value) {
		return addSlot(new DefaultConditionalSlot(name, value));
	}
	
	private IActionSlotsBuilder addSlot(ISlot slot) {
		actionBuilder.addSlot(slot);
		return actionBuilder;
	}
}

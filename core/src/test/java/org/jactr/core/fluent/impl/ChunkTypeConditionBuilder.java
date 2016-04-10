package org.jactr.core.fluent.impl;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.condition.ChunkTypeCondition;

public class ChunkTypeConditionBuilder extends AbstractConditionBuilder {
	
	private IChunkType chunkType;
	
	public ChunkTypeConditionBuilder(String buffer, DefaultConditionsBuilder conditionsBuilder, IChunkType chunkType) {
		super(buffer, conditionsBuilder);
		this.chunkType = chunkType;
	}
	
	protected void doComplete() {
		getConditionsBuilder().conditionInternal(new ChunkTypeCondition(getBuffer(), chunkType, getSlots()));
	}
	
}
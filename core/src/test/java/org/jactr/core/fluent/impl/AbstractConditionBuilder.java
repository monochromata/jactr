package org.jactr.core.fluent.impl;

import java.util.function.Function;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.fluent.BuilderException;
import org.jactr.core.fluent.IActionsBuilder;
import org.jactr.core.fluent.IConditionSlotBuilder;
import org.jactr.core.fluent.IConditionSlotsBuilder;
import org.jactr.core.fluent.IConditionsBuilder;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.condition.ICondition;

abstract class AbstractConditionBuilder extends AbstractElementBuilder implements IConditionSlotsBuilder {

	private final DefaultConditionsBuilder conditionsBuilder;
	
	public AbstractConditionBuilder(String buffer, DefaultConditionsBuilder conditionsBuilder) {
		super(buffer);
		this.conditionsBuilder = conditionsBuilder;
	}

	protected DefaultConditionsBuilder getConditionsBuilder() {
		return conditionsBuilder;
	}
	
	@Override
	public IConditionSlotsBuilder chunkType(String buffer, String chunkTypeName) {
		complete();
		return conditionsBuilder.chunkType(buffer, chunkTypeName);
	}

	@Override
	public IConditionSlotsBuilder chunkType(String buffer, IChunkType chunkType) {
		complete();
		return conditionsBuilder.chunkType(buffer, chunkType);
	}

	@Override
	public IConditionSlotsBuilder query(String buffer) {
		complete();
		return conditionsBuilder.query(buffer);
	}
	
	@Override
	public IConditionsBuilder condition(ICondition customCondition) {
		complete();
		return conditionsBuilder.condition(customCondition);
	}

	@Override
	public IConditionsBuilder condition(Function<IConditionsBuilder, IConditionsBuilder> builder) {
		complete();
		return conditionsBuilder.condition(builder);
	}

	@Override
	public IActionsBuilder actions() {
		complete();
		return conditionsBuilder.actions();
	}

	@Override
	public IProduction getProduction() {
		complete();
		return conditionsBuilder.getProductionInternal();
	}

	@Override
	public IConditionSlotBuilder slot(String slotName) {
		return new DefaultConditionSlotBuilder(this, slotName);
	}
}
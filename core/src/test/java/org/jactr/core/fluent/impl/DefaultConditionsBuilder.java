package org.jactr.core.fluent.impl;

import java.util.function.Function;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.fluent.BuilderException;
import org.jactr.core.fluent.IActionsBuilder;
import org.jactr.core.fluent.IConditionsBuilder;
import org.jactr.core.fluent.IConditionSlotsBuilder;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.condition.ICondition;

public class DefaultConditionsBuilder extends BaseBuilder implements IConditionsBuilder {
	
	private final DefaultProductionBuilder productionBuilder;
	
	public DefaultConditionsBuilder(IDeclarativeModule dm, DefaultProductionBuilder productionBuilder) {
		super(dm);
		this.productionBuilder = productionBuilder;
	}

	@Override
	public IConditionSlotsBuilder chunkType(String buffer, String chunkTypeName) {
		return chunkType(buffer, getChunkType(chunkTypeName));
	}
	
	@Override
	public IConditionSlotsBuilder chunkType(String buffer, IChunkType chunkType) {
		return setLastCompletable(new ChunkTypeConditionBuilder(buffer, this, chunkType));
	}
	
	@Override
	public IConditionSlotsBuilder query(String buffer) {
		return setLastCompletable(new QueryConditionBuilder(buffer, this));
	}
	
	@Override
	public IConditionsBuilder condition(ICondition customCondition) {
		completeLastCompletable();
		return conditionInternal(customCondition);
	}
	
	protected IConditionsBuilder conditionInternal(ICondition condition) {
		getProductionInternal().getSymbolicProduction().addCondition(condition);
		return this;
	}
	
	@Override
	public IConditionsBuilder condition(Function<IConditionsBuilder, IConditionsBuilder> builder) {
		completeLastCompletable();
		return builder.apply(this);
	}

	@Override
	public IActionsBuilder actions() {
		completeLastCompletable();
		return productionBuilder.actions();
	}

	@Override
	public IProduction getProduction() {
		completeLastCompletable();
		return getProductionInternal();
	}
	
	protected IProduction getProductionInternal() {
		return productionBuilder.getProductionInternal();
	}
	
}

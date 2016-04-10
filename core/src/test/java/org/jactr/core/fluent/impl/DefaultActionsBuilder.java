package org.jactr.core.fluent.impl;

import java.util.function.Function;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.fluent.BuilderException;
import org.jactr.core.fluent.IActionsBuilder;
import org.jactr.core.fluent.IActionSlotsBuilder;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.action.IAction;

public class DefaultActionsBuilder extends BaseBuilder implements IActionsBuilder {

	private final DefaultProductionBuilder productionBuilder;

	public DefaultActionsBuilder(IDeclarativeModule dm, DefaultProductionBuilder productionBuilder) {
		super(dm);
		this.productionBuilder = productionBuilder;
	}

	@Override
	public IActionSlotsBuilder add(String buffer, String chunkTypeName) {
		return add(buffer, getChunkType(chunkTypeName));
	}

	@Override
	public AbstractActionBuilder add(String buffer, IChunkType chunkType) {
		return setLastCompletable(new AddActionBuilder(buffer, this, chunkType));
	}

	@Override
	public IActionSlotsBuilder modify(String buffer) {
		return setLastCompletable(new ModifyActionBuilder(buffer, this));
	}
	
	@Override
	public IActionsBuilder remove(String buffer) {
		return setLastCompletable(new RemoveActionBuilder(buffer, this));
	}

	@Override
	public IActionSlotsBuilder output(String message) {
		return setLastCompletable(new OutputActionBuilder(this, message));
	}

	@Override
	public IActionsBuilder stop() {
		return setLastCompletable(new StopActionBuilder(this));
	}

	@Override
	public IActionsBuilder action(IAction customAction) {
		completeLastCompletable();
		return actionInternal(customAction);
	}
	
	protected IActionsBuilder actionInternal(IAction action) {
		getProductionInternal().getSymbolicProduction().addAction(action);
		return this;
	}

	@Override
	public IActionsBuilder action(Function<IActionsBuilder, IActionsBuilder> builder) {
		completeLastCompletable();
		return builder.apply(this);
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
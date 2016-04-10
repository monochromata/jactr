package org.jactr.core.fluent.impl;

import java.util.function.Function;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.fluent.BuilderException;
import org.jactr.core.fluent.IActionsBuilder;
import org.jactr.core.fluent.IActionSlotBuilder;
import org.jactr.core.fluent.IActionSlotsBuilder;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.action.IAction;

public abstract class AbstractActionBuilder extends AbstractElementBuilder implements IActionSlotsBuilder {

	private final DefaultActionsBuilder actionsBuilder;

	public AbstractActionBuilder(String buffer, DefaultActionsBuilder actionsBuilder) {
		super(buffer);
		this.actionsBuilder = actionsBuilder;
	}

	protected DefaultActionsBuilder getActionsBuilder() {
		return actionsBuilder;
	}

	@Override
	public IActionSlotsBuilder add(String buffer, String chunkTypeName) {
		complete();
		return actionsBuilder.add(buffer, chunkTypeName);
	}

	@Override
	public IActionSlotsBuilder add(String buffer, IChunkType chunkType) {
		complete();
		return actionsBuilder.add(buffer, chunkType);
	}

	@Override
	public IActionSlotsBuilder modify(String buffer) {
		complete();
		return actionsBuilder.modify(buffer);
	}

	@Override
	public IActionsBuilder remove(String buffer) {
		complete();
		return actionsBuilder.remove(buffer);
	}

	@Override
	public IActionsBuilder output(String message) {
		complete();
		return actionsBuilder.output(message);
	}

	@Override
	public IActionsBuilder stop() {
		complete();
		return actionsBuilder.stop();
	}

	@Override
	public IActionsBuilder action(IAction customAction) {
		complete();
		return actionsBuilder.action(customAction);
	}

	@Override
	public IActionsBuilder action(Function<IActionsBuilder, IActionsBuilder> builder) {
		complete();
		return actionsBuilder.action(builder);
	}

	@Override
	public IProduction getProduction() {
		complete();
		return actionsBuilder.getProductionInternal();
	}

	@Override
	public IActionSlotBuilder set(String slotName) {
		return new DefaultActionSlotBuilder(this, slotName);
	}

}

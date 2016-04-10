package org.jactr.core.fluent.impl;

import org.jactr.core.fluent.IActionsBuilder;
import org.jactr.core.fluent.IConditionsBuilder;
import org.jactr.core.fluent.IProductionBuilder;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.production.IProduction;

public class DefaultProductionBuilder extends BaseBuilder implements IProductionBuilder {

	private final IProduction production;
	private final DefaultConditionsBuilder conditionBuilder;
	private final DefaultActionsBuilder actionBuilder;
	private boolean conditionsInvoked = false;
	private boolean actionsInvoked = false;
	
	public DefaultProductionBuilder(IDeclarativeModule dm, IProduction production) {
		super(dm);
		this.production = production;
		this.conditionBuilder = new DefaultConditionsBuilder(dm, this);
		this.actionBuilder = new DefaultActionsBuilder(dm, this);
	}
	
	@Override
	public IProduction getProduction() {
		conditionBuilder.complete();
		actionBuilder.complete();
		return getProductionInternal();
	}
	
	protected IProduction getProductionInternal() {
		return production;
	}

	@Override
	public IConditionsBuilder conditions() {
		ensureConditionsIsInvokedOnlyOnce();
		return conditionBuilder;
	}

	@Override
	public IActionsBuilder actions() {
		ensureActionsIsInvokedOnlyOnce();
		conditionBuilder.complete();
		return actionBuilder;
	}

	protected void ensureConditionsIsInvokedOnlyOnce() {
		if(conditionsInvoked)
			throw new IllegalStateException("conditions() should be invoked only once.");
		conditionsInvoked = true;
	}

	protected void ensureActionsIsInvokedOnlyOnce() {
		if(actionsInvoked)
			throw new IllegalStateException("actions() should be invoked only once.");
		actionsInvoked = true;
	}
	
}

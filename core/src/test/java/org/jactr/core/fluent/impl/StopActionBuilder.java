package org.jactr.core.fluent.impl;

import org.jactr.core.production.action.StopAction;

public class StopActionBuilder extends AbstractActionBuilder {

	public StopActionBuilder(DefaultActionsBuilder actionsBuilder) {
		super(null, actionsBuilder);
	}

	@Override
	protected void doComplete() {
		getActionsBuilder().actionInternal(new StopAction());
	}

}

package org.jactr.core.fluent.impl;

import org.jactr.core.production.action.RemoveAction;

public class RemoveActionBuilder extends AbstractActionBuilder {
	
	public RemoveActionBuilder(String buffer, DefaultActionsBuilder actionsBuilder) {
		super(buffer, actionsBuilder);
	}

	@Override
	protected void doComplete() {
		getActionsBuilder().actionInternal(new RemoveAction(getBuffer(), getSlots()));
	}

}

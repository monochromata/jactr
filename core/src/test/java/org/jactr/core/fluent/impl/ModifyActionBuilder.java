package org.jactr.core.fluent.impl;

import org.jactr.core.production.action.ModifyAction;

public class ModifyActionBuilder extends AbstractActionBuilder {

	public ModifyActionBuilder(String buffer, DefaultActionsBuilder actionsBuilder) {
		super(buffer, actionsBuilder);
	}

	@Override
	protected void doComplete() {
		getActionsBuilder().actionInternal(new ModifyAction(getBuffer(), getSlots()));
	}

}

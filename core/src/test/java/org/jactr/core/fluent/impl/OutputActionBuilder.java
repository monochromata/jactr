package org.jactr.core.fluent.impl;

import org.jactr.core.production.action.OutputAction;

public class OutputActionBuilder extends AbstractActionBuilder {

	private final String message;
	
	public OutputActionBuilder(DefaultActionsBuilder actionsBuilder, String message) {
		super(null, actionsBuilder);
		this.message = message;
	}

	@Override
	protected void doComplete() {
		getActionsBuilder().actionInternal(new OutputAction(message));
	}

}

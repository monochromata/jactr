package org.jactr.core.fluent.impl;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.action.AddAction;

public class AddActionBuilder extends AbstractActionBuilder {

	private final IChunkType chunkType;
	
	public AddActionBuilder(String buffer, DefaultActionsBuilder actionsBuilder, IChunkType chunkType) {
		super(buffer, actionsBuilder);
		this.chunkType = chunkType;
	}

	@Override
	protected void doComplete() {
		getActionsBuilder().actionInternal(new AddAction(getBuffer(), chunkType, getSlots()));
	}

}

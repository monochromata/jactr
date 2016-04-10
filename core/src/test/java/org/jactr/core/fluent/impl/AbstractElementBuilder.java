package org.jactr.core.fluent.impl;

import java.util.LinkedList;
import java.util.List;

import org.jactr.core.slot.ISlot;

public abstract class AbstractElementBuilder implements ICompletable {

	protected final String buffer;
	private final LinkedList<ISlot> slots = new LinkedList<ISlot>();
	private boolean completed = false;

	protected AbstractElementBuilder(String buffer) {
		this.buffer = buffer;
	}

	protected String getBuffer() {
		return buffer;
	}

	protected List<ISlot> getSlots() {
		return slots;
	}

	protected void addSlot(ISlot slot) {
		slots.add(slot);
	}

	public void complete() {
		if(!completed) {
			doComplete();
			completed = true;
		}
	}
	
	protected abstract void doComplete();

}
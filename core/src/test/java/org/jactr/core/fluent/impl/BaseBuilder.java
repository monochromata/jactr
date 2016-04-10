package org.jactr.core.fluent.impl;

import java.util.concurrent.ExecutionException;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.fluent.BuilderException;
import org.jactr.core.module.declarative.IDeclarativeModule;

public class BaseBuilder implements ICompletable {

	private final IDeclarativeModule dm;
	private ICompletable lastCompletable;
	private boolean completed = false;
	
	public BaseBuilder(IDeclarativeModule dm) {
		this.dm = dm;
	}
	
	protected IChunkType getChunkType(String name) {
		IChunkType chunkType;
		try {
			chunkType = dm.getChunkType(name).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new BuilderException(e.getMessage(), e);
		}
		if(chunkType == null)
			throw new BuilderException("Chunk type not found "+name);
		return chunkType;
	}
	
	protected IChunk getChunk(String name) {
		IChunk chunk;
		try {
			chunk = dm.getChunk(name).get();
		} catch (InterruptedException | ExecutionException e) {
			throw new BuilderException(e.getMessage(), e);
		}
		if(chunk == null)
			throw new BuilderException("Chunk not found: "+name);
		return chunk;
	}
	
	protected <T> T setLastCompletable(T completable) {
		if(completed)
			throw new IllegalStateException("Cannot add child Completables to a completed base builder");
		completeLastCompletable();
		lastCompletable = (ICompletable)completable;
		return completable;
	}
	
	protected void completeLastCompletable() {
		if(lastCompletable != null) {
			lastCompletable.complete();
			lastCompletable = null;
		}
	}
	
	@Override
	public void complete() {
		if(!completed) {
			completeLastCompletable();
			completed = true;
		}
	}
}

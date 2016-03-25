/*
 * Created on Aug 14, 2006 Copyright (C) 2001-6, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.core.module.declarative;

import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.IModule;
import org.jactr.core.module.declarative.associative.IAssociativeLinkageSystem;
import org.jactr.core.module.declarative.basic.AbstractDeclarativeModule;
import org.jactr.core.module.declarative.basic.DefaultDeclarativeModule;
import org.jactr.core.module.declarative.event.IDeclarativeModuleListener;
import org.jactr.core.module.declarative.search.filter.IChunkFilter;
import org.jactr.core.production.request.ChunkTypeRequest;

/**
 * one of two specialized modules, this one handles all declarative memory
 * operations for the model, in particular adding and retrieving of chunks,
 * chunktypes all operation return values are wrapped in {@code Future<>} to better
 * support backend concurrencies.
 * 
 * <p>Clients can implement this interface, but should consider extending
 * {@link AbstractDeclarativeModule} or {@link DefaultDeclarativeModule}. In
 * either case, when the module makes calls to its front facing methods, it
 * should not assume that it is in the installed declarative memory module.
 * Rather it should use {@link #getModel()} and
 * {@link IModel#getDeclarativeModule()}. This permits the local decM to be
 * wrapped by a delegating one (say to access external database stores).</p>
 * 
 * @see java.util.concurrent.Future
 */
public interface IDeclarativeModule extends IModule
{


  /**
   * create a new chunktype to be added after its symbolic contents have been
   * set. typically this will just delegate to the factory methods, but is
   * provided here so that declarative modules can insert custom creators
   * 
   * @param parents
   *            maybe null
   * @param name TODO
   * @return TODO
   */
  public CompletableFuture<IChunkType> createChunkType(
      Collection<IChunkType> parents, String name);

  public CompletableFuture<IChunkType> createChunkType(IChunkType parent,
      String name);

  /**
   * add the chunktype to the model. this chunktype should have been created by
   * createChunkType(). It will call the IChunkType.encode() method, add this
   * chunktype to the parent's list of children (if there is a parent) and then
   * add it to the internal data stores
   * 
   * @param chunkType TODO
   * @return TODO
   */
  public CompletableFuture<IChunkType> addChunkType(IChunkType chunkType);

  /**
   * return the named chunktype. Case insensitive, but preserving
   * 
   * @param name TODO
   * @return TODO
   */
  public CompletableFuture<IChunkType> getChunkType(String name);

  /**
   * @return all the chunk types in this model
   */
  public CompletableFuture<Collection<IChunkType>> getChunkTypes();

  /**
   * create a chunk to later be inserted.
   * 
   * @param parent
   *            must not be null (duh)
   * @param name TODO
   * @return TODO
   */
  public CompletableFuture<IChunk> createChunk(IChunkType parent, String name);
  
  
  /**
   * request that this chunk be disposed.
   * 
   * @param chunk TODO
   */
  public void dispose(IChunk chunk);
  
  /**
   * code responsible for the setting up and maintaining of associative links,
   * may be null.
   * 
   * @return TODO
   */
  public IAssociativeLinkageSystem getAssociativeLinkageSystem();
  
  /**
   * the code responsible for linking memories. Can be null if this is unsupported.
   * Typically this will be called by a declarative learning module to install
   * custom version
   * 
   * @param linkageSystem TODO
   */
  public void setAssociativeLinkageSystem(IAssociativeLinkageSystem linkageSystem);

  /**
   * return a copy of source chunk
   * 
   * @param sourceChunk TODO
   * @return TODO
   */
  public CompletableFuture<IChunk> copyChunk(IChunk sourceChunk);

  public CompletableFuture<IChunk> copyChunk(IChunk sourceChunk,
      boolean copySubsymbolics);

  /**
   * add this chunk to the model and optionally check for duplicates so that it
   * can be merged if necessary
   * 
   * @param chunk TODO
   * @return a future wrapper of the actual chunk reference that was installed.
   *         if the chunk was actually merged, the original chunk is returned
   */
  public CompletableFuture<IChunk> addChunk(IChunk chunk);

  /**
   * because encoding might be async, we need a method to determine if a given
   * chunk is scheduled for encoding
   * 
   * @param chunk TODO
   * @return TODO
   */
  public boolean willEncode(IChunk chunk);

  /**
   * return the named chunk, case insensitive but preserving
   * 
   * @param name TODO
   * @return TODO
   */
  public CompletableFuture<IChunk> getChunk(String name);

  /**
   * return all chunks. This can be a <b>very</b> expensive operation
   * 
   * @return TODO
   */
  public CompletableFuture<Collection<IChunk>> getChunks();

  /**
   * return the number of chunks in the model. this might be an estimate
   * 
   * @return TODO
   */
  public long getNumberOfChunks();

  /**
   * search DM for all the chunks that match pattern, sorting using sorter, that
   * are above activationThreshold. If the request contains meta slots
   * (":slotName"), for instance if you reuse the ChunkTypePattern from a
   * retrieval request, they must be removed first.
   * 
   * @param request TODO
   * @param sorter
   *          sort order, may be null
   * @param filter
   *          filter function, may be null
   * @return TODO
   */
  public CompletableFuture<Collection<IChunk>> findExactMatches(
      ChunkTypeRequest request,
      Comparator<IChunk> sorter, IChunkFilter filter);

  /**
   * search DM for all the chunks that partially match
   * 
   * @param request TODO
   * @param sorter
   *            may be null
   * @param filter TODO
   * @return TODO
   */
  public CompletableFuture<Collection<IChunk>> findPartialMatches(
      ChunkTypeRequest request,
      Comparator<IChunk> sorter, IChunkFilter filter);

  /**
   * snag the busy chunk.<br>
   * <br>
   * <b>Note</b> : this should not be called by the declarative memory module
   * if the retrieval will access the future methods as it might result in
   * deadlock.
   * 
   * @return TODO
   */
  public IChunk getBusyChunk();

  /**
   * snag the empty chunk.<br>
   * <br>
   * <b>Note</b> : this should not be called by the declarative memory module
   * if the retrieval will access the future methods as it might result in
   * deadlock.
   * 
   * @return TODO
   */
  public IChunk getEmptyChunk();

  /**
   * snag the error chunk.<br>
   * <br>
   * <b>Note</b> : this should not be called by the declarative memory module
   * if the retrieval will access the future methods as it might result in
   * deadlock.
   * 
   * @return TODO
   */
  public IChunk getErrorChunk();

  /**
   * snag the free chunk.<br>
   * <br>
   * <b>Note</b> : this should not be called by the declarative memory module
   * if the retrieval will access the future methods as it might result in
   * deadlock.
   * 
   * @return TODO
   */
  public IChunk getFreeChunk();

  /**
   * snag the busy chunk.<br>
   * <br>
   * <b>Note</b> : this should not be called by the declarative memory module
   * if the retrieval will access the future methods as it might result in
   * deadlock.
   * 
   * @return TODO
   */
  public IChunk getFullChunk();

  /**
   * snag the new chunk.<br>
   * <br>
   * <b>Note</b> : this should not be called by the declarative memory module
   * if the retrieval will access the future methods as it might result in
   * deadlock.
   * 
   * @return TODO
   */
  public IChunk getNewChunk();

  /**
   * snag the requested chunk.<br>
   * <br>
   * <b>Note</b> : this should not be called by the declarative memory module
   * if the retrieval will access the future methods as it might result in
   * deadlock.
   * 
   * @return TODO
   */
  public IChunk getRequestedChunk();

  /**
   * snag the unrequested chunk.<br>
   * <br>
   * <b>Note</b> : this should not be called by the declarative memory module
   * if the retrieval will access the future methods as it might result in
   * deadlock.
   * 
   * @return TODO
   */
  public IChunk getUnrequestedChunk();

  /**
   * force all pending deferred operations to execute (dispose, encode, etc)
   */
  public void flush();

  public void addListener(IDeclarativeModuleListener listener, Executor executor);

  public void removeListener(IDeclarativeModuleListener listener);
}

package org.jactr.core.fluent;

import java.util.function.Function;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.action.IAction;

public interface IActionsBuilder extends IProductionAccessor {

	/**
	 * 
	 * @param buffer
	 * @param chunkTypeName
	 * @return
	 * @throws BuilderException
	 */
	public IActionSlotsBuilder add(String buffer, String chunkTypeName);
	public IActionSlotsBuilder add(String buffer, IChunkType chunkType);
	public IActionSlotsBuilder modify(String buffer);
	public IActionsBuilder remove(String buffer);
	public IActionsBuilder output(String message);
	public IActionsBuilder stop();
	
	public IActionsBuilder action(IAction customAction);
	public IActionsBuilder action(Function<IActionsBuilder,IActionsBuilder> builder);
	
}

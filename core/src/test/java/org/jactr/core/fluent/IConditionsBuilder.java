package org.jactr.core.fluent;

import java.util.function.Function;

import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.ICondition;

public interface IConditionsBuilder extends IProductionActionBuilder {

	/**
	 * Start creating a {@link ChunkTypeCondition}.
	 * 
	 * <p>A shortcut for {@code chunkType(buffer, dm.getChunkType(chunkTypeName).get()) }</p>
	 * 
	 * @param buffer The buffer to use for matching
	 * @param chunkTypeName the chunk type to match
	 * @return the slot builder to be used to add slots to the chunk type.
	 * @throws BuilderException if the named chunk type does not exist in declarative memory
	 */
	public IConditionSlotsBuilder chunkType(String buffer, String chunkTypeName);
	
	public IConditionSlotsBuilder chunkType(String buffer, IChunkType chunkType);
	
	public IConditionSlotsBuilder query(String buffer);
	
	public IConditionsBuilder condition(ICondition customCondition);
	
	public IConditionsBuilder condition(Function<IConditionsBuilder,IConditionsBuilder> builder);
	
}

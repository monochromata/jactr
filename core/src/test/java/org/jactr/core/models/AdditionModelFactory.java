package org.jactr.core.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.core.module.goal.six.DefaultGoalModule6;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;
import org.jactr.core.module.retrieval.six.DefaultRetrievalModule6;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.DefaultVariableConditionalSlot;

/**
 * A factory for the addition model.
 */
public class AdditionModelFactory extends AbstractModelFactory {

	private IChunkType chunkTypeAdd;
	private IChunkType chunkTypeCountOrder;
	private IChunk secondGoal;

	public AdditionModelFactory() {
		super("addition");
	}

	@Override
	protected void populateDeclarativeMemory(IDeclarativeModule dm) throws InterruptedException, ExecutionException {

		super.populateDeclarativeMemory(dm);

		createChunkTypeAddAndAddToDM(dm);
		createChunkTypeCountOrderAndAddToDM(dm);
		createChunkSecondGoalAndAddToDM(dm);
		createCountOrderChunkAndAddToDM(dm, "a", 0.0, 1.0);
		createCountOrderChunkAndAddToDM(dm, "b", 1.0, 2.0);
		createCountOrderChunkAndAddToDM(dm, "c", 2.0, 3.0);
		createCountOrderChunkAndAddToDM(dm, "d", 3.0, 4.0);
		createCountOrderChunkAndAddToDM(dm, "e", 4.0, 5.0);
		createCountOrderChunkAndAddToDM(dm, "f", 5.0, 6.0);
		createCountOrderChunkAndAddToDM(dm, "g", 6.0, 7.0);
		createCountOrderChunkAndAddToDM(dm, "h", 7.0, 8.0);
		createCountOrderChunkAndAddToDM(dm, "i", 8.0, 9.0);
		createCountOrderChunkAndAddToDM(dm, "j", 9.0, 10.0);
	}

	protected void createChunkTypeAddAndAddToDM(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		chunkTypeAdd = createChunkType(dm, "add", sct -> {
			sct.addSlot(new DefaultConditionalSlot("arg1", null));
			sct.addSlot(new DefaultConditionalSlot("arg2", null));
			sct.addSlot(new DefaultConditionalSlot("count", null));
			sct.addSlot(new DefaultConditionalSlot("sum", null));
		});
	}

	protected void createChunkTypeCountOrderAndAddToDM(IDeclarativeModule dm)
			throws InterruptedException, ExecutionException {
		chunkTypeCountOrder = createChunkType(dm, "count-order", sct -> {
			sct.addSlot(new DefaultConditionalSlot("first", null));
			sct.addSlot(new DefaultConditionalSlot("second", null));
		});
	}

	protected void createChunkSecondGoalAndAddToDM(IDeclarativeModule dm)
			throws InterruptedException, ExecutionException {
		secondGoal = createChunk(dm, chunkTypeAdd, "second-goal", sc -> {
			sc.addSlot(new DefaultConditionalSlot("arg1", 5.0d));
			sc.addSlot(new DefaultConditionalSlot("arg2", 2.0d));
			sc.addSlot(new DefaultConditionalSlot("count", null));
			sc.addSlot(new DefaultConditionalSlot("sum", null));
		});
	}

	protected IChunk createCountOrderChunkAndAddToDM(IDeclarativeModule dm, String name, double firstValue,
			double secondValue) throws InterruptedException, ExecutionException {
		return createChunk(dm, dm.getChunkType("count-order").get(), name, sc -> {
			sc.addSlot(new DefaultConditionalSlot("first", firstValue));
			sc.addSlot(new DefaultConditionalSlot("second", secondValue));
		});
	}

	@Override
	protected void populateProceduralMemory(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {

		super.populateProceduralMemory(dm, pm);

		createInitializeAdditionProductionAndAddToPM(dm, pm);
		createTerminateAdditionProductionAndAddToPM(dm, pm);
		createIncrementCountProductionAndAddToPM(dm, pm);
		createIncrementSumProductionAndAddToPM(dm, pm);
	}

	protected void createInitializeAdditionProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "initialize-addition", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", chunkTypeAdd,
						Arrays.asList(new DefaultVariableConditionalSlot("arg1", "=num1"),
								new DefaultVariableConditionalSlot("arg2", "=num2"),
								new DefaultConditionalSlot("sum", null))));
			
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("count", 0.0d),
						new DefaultVariableConditionalSlot("sum", "=num1"))));
			sp.addAction(new AddAction("retrieval", chunkTypeCountOrder,
				Arrays.asList(new DefaultVariableConditionalSlot("first", "=num1"))));
		});
	}

	protected void createTerminateAdditionProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "terminate-addition", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", chunkTypeAdd,
						Arrays.asList(new DefaultVariableConditionalSlot("arg1", "=num1"),
								new DefaultVariableConditionalSlot("arg2", "=num2"),
								new DefaultVariableConditionalSlot("count", "=num2"),
								new DefaultVariableConditionalSlot("sum", "=answer"))));
			
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("count", null))));
			sp.addAction(new OutputAction("=num1 + =num2 is =answer"));
		});
	}

	protected void createIncrementCountProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "increment-count", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", chunkTypeAdd,
						Arrays.asList(new DefaultVariableConditionalSlot("sum", "=sum"),
								new DefaultVariableConditionalSlot("count", "=count"))));
			sp.addCondition(new ChunkTypeCondition("retrieval", chunkTypeCountOrder,
						Arrays.asList(new DefaultVariableConditionalSlot("first", "=count"),
								new DefaultVariableConditionalSlot("second", "=newCount"),
								new DefaultConditionalSlot(":state", dm.getFreeChunk()))));
			
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultVariableConditionalSlot("count", "=newCount"))));
			sp.addAction(new AddAction("retrieval", chunkTypeCountOrder,
				Arrays.asList(new DefaultVariableConditionalSlot("first", "=sum"))));
			sp.addAction(new OutputAction("That was the =newCount finger"));
			sp.addAction(new OutputAction("Will try to retrieve a count-order first =sum"));
		});
	}

	protected void createIncrementSumProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "increment-sum", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", chunkTypeAdd,
						Arrays.asList(new DefaultVariableConditionalSlot("sum", "=sum"),
								new DefaultVariableConditionalSlot("count", "=count"),
								new DefaultVariableConditionalSlot("arg2", "=count"))));
			sp.addCondition(new ChunkTypeCondition("retrieval", chunkTypeCountOrder,
						Arrays.asList(new DefaultVariableConditionalSlot("first", "=sum"),
								new DefaultVariableConditionalSlot("second", "=newSum"),
								new DefaultConditionalSlot(":state", dm.getFreeChunk()))));
			
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultVariableConditionalSlot("sum", "=newSum"))));
			sp.addAction(new AddAction("retrieval", chunkTypeCountOrder,
				Arrays.asList(new DefaultVariableConditionalSlot("first", "=count"))));
			sp.addAction(new OutputAction("=newSum"));
			sp.addAction(new OutputAction("Will try to retrieve a count-order first =count"));
		});
	}

	@Override
	protected void setModelParameters(IModel model) {
		super.setModelParameters(model);
		model.setParameter("CycleSkipping", "true");
		model.setParameter("RealTime", "false");
		model.setParameter("RealTimeScalor", "1.0");
	}

	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(secondGoal);
	}

}

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
		chunkTypeAdd = createChunkType(dm, "add", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("arg1", null));
			sct.addSlot(new DefaultConditionalSlot("arg2", null));
			sct.addSlot(new DefaultConditionalSlot("count", null));
			sct.addSlot(new DefaultConditionalSlot("sum", null));
		});
	}

	protected void createChunkTypeCountOrderAndAddToDM(IDeclarativeModule dm)
			throws InterruptedException, ExecutionException {
		chunkTypeCountOrder = createChunkType(dm, "count-order", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("first", null));
			sct.addSlot(new DefaultConditionalSlot("second", null));
		});
	}

	protected void createChunkSecondGoalAndAddToDM(IDeclarativeModule dm)
			throws InterruptedException, ExecutionException {
		secondGoal = createChunk(dm, chunkTypeAdd, "second-goal", chunk -> {
			final ISymbolicChunk sc = chunk.getSymbolicChunk();
			sc.addSlot(new DefaultConditionalSlot("arg1", 5.0d));
			sc.addSlot(new DefaultConditionalSlot("arg2", 2.0d));
			sc.addSlot(new DefaultConditionalSlot("count", null));
			sc.addSlot(new DefaultConditionalSlot("sum", null));
		});
	}

	protected IChunk createCountOrderChunkAndAddToDM(IDeclarativeModule dm, String name, double firstValue,
			double secondValue) throws InterruptedException, ExecutionException {
		IChunk chunk = dm.createChunk(dm.getChunkType("count-order").get(), name).get();
		chunk.getSymbolicChunk().addSlot(new DefaultConditionalSlot("first", firstValue));
		chunk.getSymbolicChunk().addSlot(new DefaultConditionalSlot("second", secondValue));
		dm.addChunk(chunk);
		return chunk;
	}

	@Override
	protected void populateProceduralMemory(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {

		super.populateProceduralMemory(dm, pm);

		createInitializeAdditionProductionAndAddToPM(pm);
		createTerminateAdditionProductionAndAddToPM(pm);
		createIncrementCountProductionAndAddToPM(dm, pm);
		createIncrementSumProductionAndAddToPM(dm, pm);
	}

	protected void createInitializeAdditionProductionAndAddToPM(IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction initializeAddition = pm.createProduction("initialize-addition").get();
		initializeAddition.getSymbolicProduction()
				.addCondition(new ChunkTypeCondition("goal", chunkTypeAdd,
						Arrays.asList(new DefaultVariableConditionalSlot("arg1", "=num1"),
								new DefaultVariableConditionalSlot("arg2", "=num2"),
								new DefaultConditionalSlot("sum", null))));
		initializeAddition.getSymbolicProduction()
				.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("count", 0.0d),
						new DefaultVariableConditionalSlot("sum", "=num1"))));
		initializeAddition.getSymbolicProduction().addAction(new AddAction("retrieval", chunkTypeCountOrder,
				Arrays.asList(new DefaultVariableConditionalSlot("first", "=num1"))));
		pm.addProduction(initializeAddition);
	}

	protected void createTerminateAdditionProductionAndAddToPM(IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction terminateAddition = pm.createProduction("terminate-addition").get();
		terminateAddition.getSymbolicProduction()
				.addCondition(new ChunkTypeCondition("goal", chunkTypeAdd,
						Arrays.asList(new DefaultVariableConditionalSlot("arg1", "=num1"),
								new DefaultVariableConditionalSlot("arg2", "=num2"),
								new DefaultVariableConditionalSlot("count", "=num2"),
								new DefaultVariableConditionalSlot("sum", "=answer"))));
		terminateAddition.getSymbolicProduction()
				.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("count", null))));
		terminateAddition.getSymbolicProduction().addAction(new OutputAction("=num1 + =num2 is =answer"));
		pm.addProduction(terminateAddition);
	}

	protected void createIncrementCountProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction incrementCount = pm.createProduction("increment-count").get();
		incrementCount.getSymbolicProduction()
				.addCondition(new ChunkTypeCondition("goal", chunkTypeAdd,
						Arrays.asList(new DefaultVariableConditionalSlot("sum", "=sum"),
								new DefaultVariableConditionalSlot("count", "=count"))));
		incrementCount.getSymbolicProduction()
				.addCondition(new ChunkTypeCondition("retrieval", chunkTypeCountOrder,
						Arrays.asList(new DefaultVariableConditionalSlot("first", "=count"),
								new DefaultVariableConditionalSlot("second", "=newCount"),
								new DefaultConditionalSlot(":state", dm.getFreeChunk()))));
		incrementCount.getSymbolicProduction().addAction(
				new ModifyAction("goal", Arrays.asList(new DefaultVariableConditionalSlot("count", "=newCount"))));
		incrementCount.getSymbolicProduction().addAction(new AddAction("retrieval", chunkTypeCountOrder,
				Arrays.asList(new DefaultVariableConditionalSlot("first", "=sum"))));
		incrementCount.getSymbolicProduction().addAction(new OutputAction("That was the =newCount finger"));
		incrementCount.getSymbolicProduction()
				.addAction(new OutputAction("Will try to retrieve a count-order first =sum"));
		pm.addProduction(incrementCount);
	}

	protected void createIncrementSumProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction incrementSum = pm.createProduction("increment-sum").get();
		incrementSum.getSymbolicProduction()
				.addCondition(new ChunkTypeCondition("goal", chunkTypeAdd,
						Arrays.asList(new DefaultVariableConditionalSlot("sum", "=sum"),
								new DefaultVariableConditionalSlot("count", "=count"),
								new DefaultVariableConditionalSlot("arg2", "=count"))));
		incrementSum.getSymbolicProduction()
				.addCondition(new ChunkTypeCondition("retrieval", chunkTypeCountOrder,
						Arrays.asList(new DefaultVariableConditionalSlot("first", "=sum"),
								new DefaultVariableConditionalSlot("second", "=newSum"),
								new DefaultConditionalSlot(":state", dm.getFreeChunk()))));
		incrementSum.getSymbolicProduction().addAction(
				new ModifyAction("goal", Arrays.asList(new DefaultVariableConditionalSlot("sum", "=newSum"))));
		incrementSum.getSymbolicProduction().addAction(new AddAction("retrieval", chunkTypeCountOrder,
				Arrays.asList(new DefaultVariableConditionalSlot("first", "=count"))));
		incrementSum.getSymbolicProduction().addAction(new OutputAction("=newSum"));
		incrementSum.getSymbolicProduction()
				.addAction(new OutputAction("Will try to retrieve a count-order first =count"));
		pm.addProduction(incrementSum);
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

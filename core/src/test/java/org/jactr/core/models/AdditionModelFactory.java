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
		createProduction(dm, pm, "initialize-addition", pb -> {
			pb.conditions()
					.chunkType("goal", chunkTypeAdd)
					.slot("arg1").eqVariable("=num1")
					.slot("arg2").eqVariable("=num2")
					.slot("sum").eqNull()
			.actions()
				.modify("goal")
					.set("count").toDouble(0.0d)
					.set("sum").toVariable("=num1")
				.add("retrieval", chunkTypeCountOrder)
					.set("first").toVariable("=num1");
		});
	}

	protected void createTerminateAdditionProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "terminate-addition", pb -> {
			pb.conditions()
				.chunkType("goal", chunkTypeAdd)
					.slot("arg1").eqVariable("=num1")
					.slot("arg2").eqVariable("=num2")
					.slot("count").eqVariable("=num2")
					.slot("sum").eqVariable("=answer")
			.actions()
				.modify("goal")
					.set("count").toNull()
				.output("=num1 + =num2 is =answer");
		});
	}

	protected void createIncrementCountProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "increment-count", pb -> {
			pb.conditions()
				.chunkType("goal", chunkTypeAdd)
					.slot("sum").eqVariable("=sum")
					.slot("count").eqVariable("=count")
				.chunkType("retrieval", chunkTypeCountOrder)
					.slot("first").eqVariable("=count")
					.slot("second").eqVariable("=newCount")
					.slot(":state").eqChunk(dm.getFreeChunk())
			.actions()
				.modify("goal")
					.set("count").toVariable("=newCount")
				.add("retrieval", chunkTypeCountOrder)
					.set("first").toVariable("=sum")
				.output("That was the =newCount finger")
				.output("Will try to retrieve a count-order first =sum");
		});
	}

	protected void createIncrementSumProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "increment-sum", pb -> {
			pb.conditions()
				.chunkType("goal", chunkTypeAdd)
					.slot("sum").eqVariable("=sum")
					.slot("count").eqVariable("=count")
					.slot("arg2").eqVariable("=count")
				.chunkType("retrieval", chunkTypeCountOrder)
					.slot("first").eqVariable("=sum")
					.slot("second").eqVariable("=newSum")
					.slot(":state").eqChunk(dm.getFreeChunk())
			.actions()
				.modify("goal")
					.set("sum").toVariable("=newSum")
				.add("retrieval", chunkTypeCountOrder)
					.set("first").toVariable("=count")
				.output("=newSum")
				.output("Will try to retrieve a count-order first =count");
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

package org.jactr.core.models;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.DefaultVariableConditionalSlot;

public class CountModelFactory extends AbstractModelFactory {

	private IChunk firstGoal;
	private IChunkType countOrder;
	private IChunkType countFrom;
	private IChunk start;
	private IChunk counting;
	private IChunk stop;

	public CountModelFactory() {
		super("count");
	}

	@Override
	protected void populateDeclarativeMemory(IDeclarativeModule dm) throws InterruptedException, ExecutionException {

		super.populateDeclarativeMemory(dm);

		countOrder = dm.createChunkType(Collections.emptyList(), "count-order").get();
		countOrder.getSymbolicChunkType().addSlot(new DefaultConditionalSlot("first", null));
		countOrder.getSymbolicChunkType().addSlot(new DefaultConditionalSlot("second", null));
		dm.addChunkType(countOrder);

		countFrom = dm.createChunkType(Collections.emptyList(), "count-from").get();
		countFrom.getSymbolicChunkType().addSlot(new DefaultConditionalSlot("start", null));
		countFrom.getSymbolicChunkType().addSlot(new DefaultConditionalSlot("end", null));
		countFrom.getSymbolicChunkType().addSlot(new DefaultConditionalSlot("step", null));
		dm.addChunkType(countFrom);
		
		IChunkType chunk = dm.getChunkType("chunk").get();
		start = dm.createChunk(chunk, "start").get();
		dm.addChunk(start);
		counting = dm.createChunk(chunk, "counting").get();
		dm.addChunk(counting);
		stop = dm.createChunk(chunk, "stop").get();
		dm.addChunk(stop);

		// TODO: Why doesn't the order start at "a"?
		createCountOrderAndAddToDM(dm, "b", 1, 2);
		createCountOrderAndAddToDM(dm, "c", 2, 3);
		createCountOrderAndAddToDM(dm, "d", 3, 4);
		createCountOrderAndAddToDM(dm, "e", 4, 5);
		createCountOrderAndAddToDM(dm, "f", 5, 6);

		firstGoal = dm.createChunk(countFrom, "first-goal").get();
		firstGoal.getSymbolicChunk().addSlot(new DefaultConditionalSlot("start", 2.0d));
		firstGoal.getSymbolicChunk().addSlot(new DefaultConditionalSlot("end", 5.0d));
		firstGoal.getSymbolicChunk().addSlot(new DefaultConditionalSlot("step", start));
		dm.addChunk(firstGoal);
	}

	protected void createCountOrderAndAddToDM(IDeclarativeModule dm, String name, double first, double second)
			throws InterruptedException, ExecutionException {
		IChunk chunk = dm.createChunk(countOrder, "b").get();
		chunk.getSymbolicChunk().addSlot(new DefaultConditionalSlot("first", first));
		chunk.getSymbolicChunk().addSlot(new DefaultConditionalSlot("second", second));
		dm.addChunk(chunk);
	}

	@Override
	protected void populateProceduralMemory(IDeclarativeModule dm, IProceduralModule pm) throws InterruptedException, ExecutionException {

		super.populateProceduralMemory(dm, pm);

		createStartProductionAndAddToPM(dm, pm);
		createFailedProductionAndAddToPM(dm, pm);
		createIncrementProductionAndAddToPM(dm, pm);
		createStopProductionAndAddToPM(dm, pm);
	}

	protected void createStartProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction startProduction = pm.createProduction("start").get();
		startProduction.getSymbolicProduction()
				.addCondition(new ChunkTypeCondition("goal", countFrom,
						Arrays.asList(new DefaultVariableConditionalSlot("start", "=num1"),
								new DefaultVariableConditionalSlot("step", start))));
		startProduction.getSymbolicProduction()
				.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("step", counting))));
		startProduction.getSymbolicProduction().addAction(new AddAction("retrieval", countOrder,
				Arrays.asList(new DefaultVariableConditionalSlot("first", "=num1"))));
		startProduction.getSymbolicProduction().addAction(new OutputAction("Search for something start at =num1"));
		pm.addProduction(startProduction);
	}

	protected void createFailedProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction failedProduction = pm.createProduction("failed").get();
		failedProduction.getSymbolicProduction()
				.addCondition(new ChunkTypeCondition("goal", countFrom,
						Arrays.asList(new DefaultVariableConditionalSlot("start", "=num"),
								new DefaultVariableConditionalSlot("step", counting))));
		failedProduction.getSymbolicProduction().addCondition(new QueryCondition("retrieval",
				Arrays.asList(new DefaultConditionalSlot("state", dm.getErrorChunk()))));
		failedProduction.getSymbolicProduction().addAction(new RemoveAction("goal"));
		failedProduction.getSymbolicProduction()
				.addAction(new OutputAction("Awh, crap, I can't retrieve anything starting with =num "));
		pm.addProduction(failedProduction);
	}

	protected void createIncrementProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction incrementProduction = pm.createProduction("increment").get();
		incrementProduction.getSymbolicProduction().addCondition(new ChunkTypeCondition("goal", countFrom, Arrays.asList(
				new DefaultVariableConditionalSlot("start", "=num1"),
				new DefaultVariableConditionalSlot("end", "=num1"),
				new DefaultConditionalSlot("step", counting))));
		incrementProduction.getSymbolicProduction().addCondition(new ChunkTypeCondition("retrieval", countOrder, Arrays.asList(
				new DefaultVariableConditionalSlot("first", "=num1"),
				new DefaultVariableConditionalSlot("second", "=num2"))));
		incrementProduction.getSymbolicProduction().addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultVariableConditionalSlot("start", "=num2"))));
		incrementProduction.getSymbolicProduction().addAction(new AddAction("retrieval", countOrder, Arrays.asList(
				new DefaultVariableConditionalSlot("first", "=num2"))));
		incrementProduction.getSymbolicProduction().addAction(new OutputAction("=num1"));
		pm.addProduction(incrementProduction);
	}
	
	protected void createStopProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction stopProduction = pm.createProduction("stop").get();
		stopProduction.getSymbolicProduction().addCondition(new ChunkTypeCondition("goal", countFrom, Arrays.asList(
				new DefaultVariableConditionalSlot("start", "=num"),
				new DefaultVariableConditionalSlot("end", "=num"),
				new DefaultConditionalSlot("step", counting))));
		stopProduction.getSymbolicProduction().addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("stop", stop))));
		stopProduction.getSymbolicProduction().addAction(new OutputAction("Answer =num"));
		pm.addProduction(stopProduction);
	}
	
	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(firstGoal);
	}

}

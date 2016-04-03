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

		countOrder = createChunkType(dm, "count-order", sct -> {
			sct.addSlot(new DefaultConditionalSlot("first", null));
			sct.addSlot(new DefaultConditionalSlot("second", null));
		});
		dm.addChunkType(countOrder);

		countFrom = createChunkType(dm, "count-from", sct -> {
			sct.addSlot(new DefaultConditionalSlot("start", null));
			sct.addSlot(new DefaultConditionalSlot("end", null));
			sct.addSlot(new DefaultConditionalSlot("step", null));
		});
		dm.addChunkType(countFrom);
		
		IChunkType chunk = dm.getChunkType("chunk").get();
		start = createChunk(dm, chunk, "start");
		counting = createChunk(dm, chunk, "counting");
		stop = createChunk(dm, chunk, "stop");

		// TODO: Why doesn't the order start at "a"?
		createCountOrderAndAddToDM(dm, "b", 1, 2);
		createCountOrderAndAddToDM(dm, "c", 2, 3);
		createCountOrderAndAddToDM(dm, "d", 3, 4);
		createCountOrderAndAddToDM(dm, "e", 4, 5);
		createCountOrderAndAddToDM(dm, "f", 5, 6);

		firstGoal = createChunk(dm, countFrom, "first-goal", sc -> {
			sc.addSlot(new DefaultConditionalSlot("start", 2.0d));
			sc.addSlot(new DefaultConditionalSlot("end", 5.0d));
			sc.addSlot(new DefaultConditionalSlot("step", start));
		});
	}

	protected void createCountOrderAndAddToDM(IDeclarativeModule dm, String name, double first, double second)
			throws InterruptedException, ExecutionException {
		IChunk chunk = createChunk(dm, countOrder, "b", sc -> {
			sc.addSlot(new DefaultConditionalSlot("first", first));
			sc.addSlot(new DefaultConditionalSlot("second", second));
		});
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
		createProduction(dm, pm, "start", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", countFrom,
						Arrays.asList(new DefaultVariableConditionalSlot("start", "=num1"),
								new DefaultVariableConditionalSlot("step", start))));
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("step", counting))));
			sp.addAction(new AddAction("retrieval", countOrder,
				Arrays.asList(new DefaultVariableConditionalSlot("first", "=num1"))));
			sp.addAction(new OutputAction("Search for something start at =num1"));
		});
	}

	protected void createFailedProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "failed", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", countFrom,
						Arrays.asList(new DefaultVariableConditionalSlot("start", "=num"),
								new DefaultVariableConditionalSlot("step", counting))));
			sp.addCondition(new QueryCondition("retrieval",
				Arrays.asList(new DefaultConditionalSlot("state", dm.getErrorChunk()))));
			
			sp.addAction(new RemoveAction("goal"));
			sp.addAction(new OutputAction("Awh, crap, I can't retrieve anything starting with =num "));
		});
	}

	protected void createIncrementProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "increment", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", countFrom, Arrays.asList(
				new DefaultVariableConditionalSlot("start", "=num1"),
				new DefaultVariableConditionalSlot("end", "=num1"),
				new DefaultConditionalSlot("step", counting))));
			sp.addCondition(new ChunkTypeCondition("retrieval", countOrder, Arrays.asList(
				new DefaultVariableConditionalSlot("first", "=num1"),
				new DefaultVariableConditionalSlot("second", "=num2"))));
			
			sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultVariableConditionalSlot("start", "=num2"))));
			sp.addAction(new AddAction("retrieval", countOrder, Arrays.asList(
				new DefaultVariableConditionalSlot("first", "=num2"))));
			sp.addAction(new OutputAction("=num1"));
		});
	}
	
	protected void createStopProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "stop", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", countFrom, Arrays.asList(
				new DefaultVariableConditionalSlot("start", "=num"),
				new DefaultVariableConditionalSlot("end", "=num"),
				new DefaultConditionalSlot("step", counting))));
			
			sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("stop", stop))));
			sp.addAction(new OutputAction("Answer =num"));
		});
	}
	
	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(firstGoal);
	}

}

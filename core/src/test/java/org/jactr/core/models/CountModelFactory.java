package org.jactr.core.models;

import java.util.concurrent.ExecutionException;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.DefaultConditionalSlot;

public class CountModelFactory extends AbstractModelFactory {

	private IChunk firstGoal;
	private IChunkType countOrder;
	private IChunkType countFrom;
	private IChunk start;
	private IChunk counting;
	private IChunk stop;

	public CountModelFactory(ACTRRuntime runtime) {
		super(runtime, "count");
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
		createProduction(dm, pm, "start", pb -> {
			pb.conditions()
				.chunkType("goal", countFrom)
					.slot("start").eqVariable("=num1")
					.slot("step").eqChunk(start)
			.actions()
				.modify("goal")
					.set("step").toChunk(counting)
				.add("retrieval", countOrder)
					.set("first").toVariable("=num1")
				.output("Search for something start at =num1");
		});
	}

	protected void createFailedProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "failed", pb -> {
			pb.conditions()
				.chunkType("goal", countFrom)
					.slot("start").eqVariable("=num")
					.slot("step").eqChunk(counting)
				.query("retrieval")
					.slot("state").eqChunk(dm.getErrorChunk())
			.actions()
				.remove("goal")
				.output("Awh, crap, I can't retrieve anything starting with =num ");
		});
	}

	protected void createIncrementProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "increment", pb -> {
			pb.conditions()
				.chunkType("goal", countFrom)
					.slot("start").eqVariable("=num1")
					.slot("end").eqVariable("=num1")
					.slot("step").eqChunk(counting)
				.chunkType("retrieval", countOrder)
					.slot("first").eqVariable("=num1")
					.slot("second").eqVariable("=num2")
			.actions()
				.modify("goal")
					.set("start").toVariable("=num2")
				.add("retrieval", countOrder)
					.set("first").toVariable("=num2")
				.output("=num1");
		});
	}
	
	protected void createStopProductionAndAddToPM(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "stop", pb -> {
			pb.conditions()
				.chunkType("goal", countFrom)
					.slot("start").eqVariable("=num")
					.slot("end").eqVariable("=num")
					.slot("step").eqChunk(counting)
			.actions()
				.modify("goal")
					.set("stop").toChunk(stop)
				.output("Answer =num");
		});
	}
	
	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(firstGoal);
	}

}

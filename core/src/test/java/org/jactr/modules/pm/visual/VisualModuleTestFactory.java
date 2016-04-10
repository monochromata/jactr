package org.jactr.modules.pm.visual;

import java.util.concurrent.ExecutionException;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.fluent.BuilderException;
import org.jactr.core.fluent.IActionsBuilder;
import org.jactr.core.fluent.IConditionsBuilder;
import org.jactr.core.model.IModel;
import org.jactr.core.models.AbstractModelFactory;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.imaginal.six.DefaultImaginalModule6;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;

public class VisualModuleTestFactory extends AbstractModelFactory {

	private IChunk searching, found, starting, failed, succeeded;
	private IChunk testKind, testLessThan, testGreaterThan, testValue, testColor, testSize;
	private IChunkType goalCT, attendingTest;
	private IChunk goalChunk;
	
	public VisualModuleTestFactory() {
		this("visual-test");
	}
	
	protected VisualModuleTestFactory(String name) {
		super(name);
	}

	@Override
	protected void installModules(IModel model) {
		super.installModules(model);
		model.install(new DefaultImaginalModule6());
		model.install(configureVisualModule(new DefaultVisualModule6()));
	}

	protected DefaultVisualModule6 configureVisualModule(DefaultVisualModule6 visualModule) {
		// turned off the functional testing
		visualModule.setParameter("EnableVisualBufferStuff", "false");
		return visualModule;
	}

	@Override
	protected void populateDeclarativeMemory(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		super.populateDeclarativeMemory(dm);
		
		IChunkType chunk = dm.getChunkType("chunk").get();
		
		searching = createChunk(dm, chunk, "searching");
		found = createChunk(dm, chunk, "found");
		starting = createChunk(dm, chunk, "starting");
		failed = createChunk(dm, chunk, "failed");
		succeeded = createChunk(dm, chunk, "succeeded");
		
		testKind = createChunk(dm, chunk, "test-kind");
		testLessThan = createChunk(dm, chunk, "test-less-than");
		testGreaterThan = createChunk(dm, chunk, "test-greater, than");
		testValue = createChunk(dm, chunk, "test-value");
		testColor = createChunk(dm, chunk, "test-color");
		testSize = createChunk(dm, chunk, "test-size");
		
		goalCT = createChunkType(dm, "goal", sct -> {
			sct.addSlot(new DefaultConditionalSlot("status", starting));
			sct.addSlot(new DefaultConditionalSlot("stage", null));
		});
		
		attendingTest = createChunkType(dm, "attending-test", sct -> {
			sct.addSlot(new DefaultConditionalSlot("testValue", null));
		}, goalCT);
		
		goalChunk = createChunk(dm, attendingTest, "goal", sc -> {
			sc.addSlot(new DefaultConditionalSlot("testValue", "center"));
			sc.addSlot(new DefaultConditionalSlot("stage", testKind));
		});
		
	}

	@Override
	protected void populateProceduralMemory(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		super.populateProceduralMemory(dm, pm);
		
		createSearchKindProduction(dm, pm);
		createSearchLessThanProduction(dm, pm);
		createSearchGreaterThanProduction(dm, pm);
		createSearchColorProduction(dm, pm);
		createSearchSizeProduction(dm, pm);
		createSearchSizeSucceededProduction(dm, pm);
		createSearchMatchFailedProduction(dm, pm);
		createSearchFailedProduction(dm, pm);
		createSearchSucceededProduction(dm, pm);
		createEncodingSucceededProduction(dm, pm);
		createEncodingMatchFailedProduction(dm, pm);
		createEncodingFailedProduction(dm, pm);
	}

	protected void createSearchKindProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-kind", builder -> {
			// Search for text nearest to center
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(starting)
						.slot("stage").eqChunk(testKind)
					.condition(this::createSearchQueryCondition)
				.actions()
					.add("visual-location", "visual-location")
						.set(":attended").toNull()
						.set("nearest").toChunk("current")
						.set("kind").toChunkType("text")
					.modify("goal")
						.set("status").toChunk("searching")
					.action(this::createSearchKindExtraActions);
		});
	}
	
	protected IConditionsBuilder createSearchQueryCondition(IConditionsBuilder builder) {
		return builder
			.query("visual-location")
			.slot("state").eqChunk("free");
	}
	
	protected IActionsBuilder createSearchKindExtraActions(IActionsBuilder builder) {
		return builder;
	}

	protected void createSearchLessThanProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-less-than", builder -> {
			// Should find center
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(succeeded)
						.slot("stage").eqChunk(testKind)
					.condition(this::createSearchQueryCondition)
				.actions()
					.output("test-kind succeeded, searching less-than")
					.modify("goal")
						.set("stage").toChunk(testLessThan)
						.set("status").toChunk(searching)
						.set("testValue").toString("lowerLeft")
					.add("visual-location", "visual-location")
						.set(":attended").toNull()
						.set("screen-x").toChunk("less-than-current")
						.set("screen-y").toChunk("less-than-current")
					.action(this::createSearchLessThanExtraActions);
		});
	}
	
	protected IActionsBuilder createSearchLessThanExtraActions(IActionsBuilder builder) {
		return builder;
	}

	protected void createSearchGreaterThanProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-greater-than", builder -> {
			// Should find lowerLeft
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(succeeded)
						.slot("stage").eqChunk(testLessThan)
						.slot("stage").eqVariable("=variable")
					.condition(this::createSearchQueryCondition)
				.actions()
					.output("=variable succeeded, searching greater-than")
					.modify("goal")
						.set("stage").toChunk(testGreaterThan)
						.set("status").toChunk(searching)
						.set("testValue").toString("upperRight")
					.add("visual-location", "visual-location")
						.set(":attended").toNull()
						.set("screen-x").toChunk("greater-than-current")
						.set("screen-y").toChunk("greater-than-current")
					.action(this::createSearchGreaterThanExtraActions);
		});
	}
	
	protected IActionsBuilder createSearchGreaterThanExtraActions(IActionsBuilder builder) {
		return builder;
	}

	protected void createSearchColorProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-color", builder -> {
			// Should find upperRight
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(succeeded)
						.slot("stage").eqChunk(testGreaterThan)
					.condition(this::createSearchQueryCondition)
				.actions()
					.output("greater-than succeeded, searching for color")
					.modify("goal")
						.set("stage").toChunk(testColor)
						.set("status").toChunk(searching)
						.set("testValue").toString("lowerRight")
					.add("visual-location", "visual-location")
						.set(":attended").toNull()
						.set("color").toChunk("white")
					.action(this::createSearchColorExtraActions);
		});
	}
	
	protected IActionsBuilder createSearchColorExtraActions(IActionsBuilder builder) {
		return builder;
	}

	protected void createSearchSizeProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-size", builder -> {
			// Should find lowerMiddle - or lower right?
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(succeeded)
						.slot("stage").eqChunk(testColor)
					.condition(this::createSearchQueryCondition)
				.actions()
					.output("succeeded color search, looking for the largest")
					.modify("goal")
						.set("stage").toChunk(testSize)
						.set("status").toChunk(searching)
						.set("testValue").toString("lowerMiddle")
					.add("visual-location", "visual-location")
						.set(":attended").toNull()
						.set("size").toChunk("greater-than-current")
					.action(this::createSearchSizeExtraActions);
		});
	}
	
	protected IActionsBuilder createSearchSizeExtraActions(IActionsBuilder builder) {
		return builder;
	}

	protected void createSearchSizeSucceededProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-size-succeeded", builder -> {
			// Done with search - or should find lowerMiddle?
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(succeeded)
						.slot("stage").eqChunk(testSize)
				.actions()
					.output("size search succeeded, we are done!")
					.remove("goal")
					.remove("visual-location")
					.stop();
		});
	}

	protected void createSearchMatchFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-match-failed", builder -> {
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(searching)
						.slot("testValue").eqVariable("=value")
						.slot("stage").eqVariable("=stage")
					.chunkType("visual-location", "visual-location")
						.slot("value").eqVariable("=text")
						.slot("value").neqVariable("=value")
					.query("visual")
						.slot("state").eqChunk("free")
				.actions()
					.output("=stage failed, =text is not =value ")
					.remove("goal")
					.remove("visual-location");
		});
	}

	protected void createSearchFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-failed", builder -> {
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(searching)
						.slot("stage").eqVariable("=stage")
					.query("visual-location")
						.slot("state").eqChunk("error")
				.actions()
					.modify("goal")
						.set("status").toChunk(failed)
					.remove("goal")
					.remove("visual-location")
					.output("=stage failed. I couldnt find anything");
		});
	}

	protected void createSearchSucceededProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-succeeded", builder -> {
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(searching)
						.slot("testValue").eqVariable("=testValue")
					.chunkType("visual-location", "visual-location")
						.slot("screen-x").eqVariable("=x")
						.slot("screen-y").eqVariable("=y")
						.slot("value").eqVariable("=testValue")
					.query("visual")
						.slot("state").eqChunk("free")
				.actions()
					.output("Found =testValue at =x and =y shifting attention")
					.add("visual", "attend-to")
						.set("where").toVariable("=visual-location")
					.modify("visual-location")
					.modify("goal")
						.set("status").toChunk(found);
		});
	}

	protected void createEncodingSucceededProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "encoding-succeeded", builder -> {
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(found)
						.slot("testValue").eqVariable("=testValue")
					.chunkType("visual", "visual-object")
						.slot("value").eqVariable("=testValue")
				.actions()
					.output("Found =testValue visual chunk!")
					.modify("goal")
						.set("status").toChunk(succeeded)
					.remove("visual");
		});
	}

	protected void createEncodingMatchFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "encoding-match-failed", builder -> {
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(found)
						.slot("testValue").eqVariable("=testValue")
					.chunkType("visual", "visual-object")
						.slot("value").eqVariable("=value")
						.slot("value").neqVariable("=testValue")
				.actions()
					.output("Failed, =value but not =testValue visual chunk")
					.remove("goal")
					.remove("visual");
		});
	}

	protected void createEncodingFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "encoding-failed", builder -> {
			builder
				.conditions()
					.chunkType("goal", attendingTest)
						.slot("status").eqChunk(found)
					.query("visual")
						.slot("state").eqChunk("error")
				.actions()
					.output("Failed, encoding")
					.remove("goal")
					.remove("visual");
		});
	}
	
	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(goalChunk);
	}
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

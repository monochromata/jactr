package org.jactr.core.models;

import static org.jactr.core.slot.IConditionalSlot.NOT_EQUALS;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISymbolicProduction;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.DefaultVariableConditionalSlot;

public class SemanticModelFactory extends AbstractModelFactory {

	protected IChunkType attribute, object, property, isMember;

	protected IChunk shark, fish, salmon, animal, canary, bird, ostrich;
	protected IChunk dangerous, locomotion, category, edible, breathe, moves, skin, color, sings, flies, height, wings;
	protected IChunk _true, _false, swimming, gills, yellow, tall, flying, yes, no, pending;

	protected IChunk g1, g2, g3;

	public SemanticModelFactory(ACTRRuntime runtime) {
		super(runtime, "semantic");
	}

	@Override
	protected void populateDeclarativeMemory(IDeclarativeModule dm) throws InterruptedException, ExecutionException {

		super.populateDeclarativeMemory(dm);

		IChunkType chunk = dm.getChunkType("chunk").get();
		attribute = createChunkType(dm, "attribute", chunk);
		object = createChunkType(dm, "object", chunk);
		createPropertyChunkType(dm);
		createIsMemberChunkType(dm);

		shark = createChunk(dm, chunk, "shark");
		fish = createChunk(dm, chunk, "fish");
		salmon = createChunk(dm, chunk, "salmon");
		animal = createChunk(dm, chunk, "animal");
		canary = createChunk(dm, chunk, "canary");
		bird = createChunk(dm, chunk, "bird");
		ostrich = createChunk(dm, chunk, "ostrich");

		dangerous = createChunk(dm, chunk, "dangerous");
		locomotion = createChunk(dm, chunk, "locomotion");
		category = createChunk(dm, chunk, "category");
		edible = createChunk(dm, chunk, "edible");
		breathe = createChunk(dm, chunk, "breathe");
		moves = createChunk(dm, chunk, "moves");
		skin = createChunk(dm, chunk, "skin");
		color = createChunk(dm, chunk, "color");
		sings = createChunk(dm, chunk, "sings");
		flies = createChunk(dm, chunk, "flies");
		height = createChunk(dm, chunk, "height");
		wings = createChunk(dm, chunk, "wings");

		_true = createChunk(dm, chunk, "true");
		_false = createChunk(dm, chunk, "false");
		swimming = createChunk(dm, chunk, "swimming");
		gills = createChunk(dm, chunk, "gills");
		yellow = createChunk(dm, chunk, "yellow");
		tall = createChunk(dm, chunk, "tall");
		flying = createChunk(dm, chunk, "flying");
		yes = createChunk(dm, chunk, "yes");
		no = createChunk(dm, chunk, "no");
		pending = createChunk(dm, chunk, "pending");

		createProperty(dm, "p1", shark, dangerous, _true);
		createProperty(dm, "p2", shark, locomotion, swimming);
		createProperty(dm, "p3", shark, category, fish);

		createProperty(dm, "p4", salmon, edible, _true);
		createProperty(dm, "p5", salmon, locomotion, swimming);
		createProperty(dm, "p6", salmon, category, fish);

		createProperty(dm, "p7", fish, breathe, gills);
		createProperty(dm, "p8", fish, locomotion, swimming);
		createProperty(dm, "p9", fish, category, animal);

		createProperty(dm, "p10", animal, moves, _true);
		createProperty(dm, "p11", animal, skin, _true);

		createProperty(dm, "p12", canary, color, yellow);
		createProperty(dm, "p13", canary, sings, _true);
		createProperty(dm, "p14", canary, category, bird);

		createProperty(dm, "p15", ostrich, flies, _false);
		createProperty(dm, "p16", ostrich, height, tall);
		createProperty(dm, "p17", ostrich, category, bird);

		createProperty(dm, "p18", bird, wings, _true);
		createProperty(dm, "p19", bird, locomotion, flying);
		createProperty(dm, "p20", bird, category, animal);

		g1 = createIsMember(dm, "g1", canary, bird, null);
		g2 = createIsMember(dm, "g2", canary, animal, null);
		g3 = createIsMember(dm, "g3", canary, fish, null);
	}

	protected void createPropertyChunkType(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		property = createChunkType(dm, "property", sct -> {
			sct.addSlot(new DefaultConditionalSlot("object", null));
			sct.addSlot(new DefaultConditionalSlot("attribute", null));
			sct.addSlot(new DefaultConditionalSlot("value", null));
		});
	}

	protected void createIsMemberChunkType(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		isMember = createChunkType(dm, "is-member", sct -> {
			sct.addSlot(new DefaultConditionalSlot("object", null));
			sct.addSlot(new DefaultConditionalSlot("category", null));
			sct.addSlot(new DefaultConditionalSlot("judgement", null));
		});
	}

	
	@Override
	protected IChunk createChunk(IDeclarativeModule dm, IChunkType chunkType, final String name,
			final Consumer<ISymbolicChunk> configurator) throws InterruptedException, ExecutionException {
		final IChunk addedChunk = super.createChunk(dm, chunkType, name, configurator);
		// apply chunk parameters, after the chunk has been added to DM
		getChunkParameterConfiguration().accept(name, addedChunk);
		return addedChunk;
	}

	protected BiConsumer<String, IChunk> getChunkParameterConfiguration() {
		return (chunkName, chunk) -> {
		};
	}

	protected void createProperty(final IDeclarativeModule dm, final String name, final IChunk object,
			final IChunk attribute, final IChunk value) throws InterruptedException, ExecutionException {
		createChunk(dm, property, name, sc -> {
			sc.addSlot(new DefaultConditionalSlot("object", object));
			sc.addSlot(new DefaultConditionalSlot("attribute", attribute));
			sc.addSlot(new DefaultConditionalSlot("value", value));
		});
	}

	protected IChunk createIsMember(final IDeclarativeModule dm, final String name, final IChunk object,
			final IChunk category, final IChunk judgement) throws InterruptedException, ExecutionException {
		return createChunk(dm, isMember, name, sc -> {
			sc.addSlot(new DefaultConditionalSlot("object", object));
			sc.addSlot(new DefaultConditionalSlot("category", category));
			sc.addSlot(new DefaultConditionalSlot("judgement", judgement));
		});
	}

	@Override
	protected void populateProceduralMemory(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {

		createInitialRetrieveProduction(dm, pm);
		createDirectVerifyProduction(dm, pm);
		createChainCategoryProduction(dm, pm);
		createFailProduction(dm, pm);
	}

	protected void createProduction(IProceduralModule pm, String name, Consumer<ISymbolicProduction> slotConfiguration)
			throws InterruptedException, ExecutionException {
		final IProduction production = pm.createProduction(name).get();
		slotConfiguration.accept(production.getSymbolicProduction());
		getProductionParameterConfiguration().accept(production);
		pm.addProduction(production);
	}

	protected Consumer<IProduction> getProductionParameterConfiguration() {
		return production -> {
		};
	}

	protected void createInitialRetrieveProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(pm, "initial-retrieve", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", isMember,
					Arrays.asList(new DefaultVariableConditionalSlot("object", "=obj"),
							new DefaultVariableConditionalSlot("category", "=cat"),
							new DefaultVariableConditionalSlot("judgement", null))));
			sp.addCondition(new QueryCondition("retrieval",
					Arrays.asList(new DefaultConditionalSlot("state", dm.getFreeChunk()))));
			
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("judgement", pending))));
			sp.addAction(new AddAction("retrieval", property,
					Arrays.asList(new DefaultVariableConditionalSlot("object", "=obj"),
							new DefaultConditionalSlot("attribute", category),
							new DefaultConditionalSlot(":recently-retrieved", null))));
			sp.addAction(new OutputAction("trying to remember something about =obj "));
		});

	}

	protected void createDirectVerifyProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(pm, "direct-verify", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", isMember,
					Arrays.asList(new DefaultVariableConditionalSlot("object", "=obj"),
							new DefaultVariableConditionalSlot("category", "=cat"),
							new DefaultConditionalSlot("judgement", pending))));
			sp.addCondition(new ChunkTypeCondition("retrieval", property,
					Arrays.asList(new DefaultVariableConditionalSlot("object", "=obj"),
							new DefaultConditionalSlot("attribute", category),
							new DefaultVariableConditionalSlot("value", "=cat"))));
			
			sp.addAction(new RemoveAction("goal", Arrays.asList(new DefaultConditionalSlot("judgement", yes))));
			sp.addAction(new OutputAction("Yes, a =obj is a =cat"));
			sp.addAction(new RemoveAction("retrieval"));
		});
	}

	protected void createChainCategoryProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(pm, "chain-category", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", isMember,
					Arrays.asList(new DefaultVariableConditionalSlot("object", "=obj"),
							new DefaultVariableConditionalSlot("category", "=cat"),
							new DefaultConditionalSlot("judgement", pending))));
			sp.addCondition(new ChunkTypeCondition("retrieval", property,
					Arrays.asList(new DefaultVariableConditionalSlot("object", "=obj"),
							new DefaultConditionalSlot("attribute", category),
							new DefaultVariableConditionalSlot("value", "=val"),
							new DefaultVariableConditionalSlot("value", NOT_EQUALS, "=cat"))));
			sp.addCondition(new QueryCondition("retrieval",
					Arrays.asList(new DefaultConditionalSlot("state", dm.getFreeChunk()))));
			
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultVariableConditionalSlot("object", "=val"))));
			sp.addAction(new AddAction("retrieval", property,
					Arrays.asList(new DefaultVariableConditionalSlot("object", "=val"),
							new DefaultConditionalSlot("attribute", category))));
			sp.addAction(new OutputAction("=obj is a =val I wonder if =val is a =cat"));
			sp.addAction(new OutputAction("Im trying to remember something about =val"));
		});
	}

	protected void createFailProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(pm, "fail", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", isMember,
					Arrays.asList(new DefaultVariableConditionalSlot("object", "=obj"),
							new DefaultVariableConditionalSlot("category", "=cat"),
							new DefaultConditionalSlot("judgement", pending))));
			sp.addCondition(new QueryCondition("retrieval",
					Arrays.asList(new DefaultConditionalSlot("state", dm.getErrorChunk()))));
			
			sp.addAction(new RemoveAction("goal", Arrays.asList(new DefaultConditionalSlot("judgement", no))));
			sp.addAction(new OutputAction("No, a =obj is not a =cat"));
			sp.addAction(new RemoveAction("retrieval"));
		});
	}

	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(g3);
	}

}

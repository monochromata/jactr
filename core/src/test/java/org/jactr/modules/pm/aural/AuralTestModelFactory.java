package org.jactr.modules.pm.aural;

import static org.jactr.core.slot.IConditionalSlot.NOT_EQUALS;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.models.AbstractModelFactory;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.imaginal.six.DefaultImaginalModule6;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.action.StopAction;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.slot.DefaultVariableConditionalSlot;
import org.jactr.modules.pm.aural.six.DefaultAuralModule6;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;

public class AuralTestModelFactory extends AbstractModelFactory {

	protected IChunkType goalCT, attendingTest;
	protected IChunk searching, encoding, starting, testing, failed, succeeded;
	protected IChunk goal;

	public AuralTestModelFactory() {
		super("aural-test");
	}

	@Override
	protected void installModules(IModel model) {
		super.installModules(model);
		model.install(new DefaultImaginalModule6());
		model.install(new DefaultVisualModule6());
		DefaultAuralModule6 auralModule = new DefaultAuralModule6();
		auralModule.setParameter("EnableBufferStuff", "false");
		model.install(auralModule);
	}

	@Override
	protected void populateDeclarativeMemory(IDeclarativeModule dm) throws InterruptedException, ExecutionException {

		super.populateDeclarativeMemory(dm);

		final IChunkType chunkCT = dm.getChunkType("chunk").get();

		searching = createChunk(dm, chunkCT, "searching");
		encoding = createChunk(dm, chunkCT, "encoding");
		starting = createChunk(dm, chunkCT, "starting");
		testing = createChunk(dm, chunkCT, "testing");
		failed = createChunk(dm, chunkCT, "failed");
		succeeded = createChunk(dm, chunkCT, "succeeded");

		createGoalChunkType(dm);
		createAttendingTestChunkType(dm);

		createGoalChunk(dm);
	}

	protected void createGoalChunkType(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		goalCT = createChunkType(dm, "goal", sct -> {
			sct.addSlot(new DefaultConditionalSlot("status", starting));
			sct.addSlot(new DefaultConditionalSlot("kind", null));
		});
	}

	protected void createAttendingTestChunkType(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		attendingTest = createChunkType(dm, "attending-test", sct -> {
			sct.addSlot(new DefaultConditionalSlot("testValue", null));
		} , goalCT);
	}

	protected void createGoalChunk(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		final IChunkType tone = dm.getChunkType("tone").get();
		goal = createChunk(dm, attendingTest, "goal", sc -> {
			sc.addSlot(new DefaultConditionalSlot("testValue", "a"));
			sc.addSlot(new DefaultConditionalSlot("kind", tone));
		});
	}

	@Override
	protected void populateProceduralMemory(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {

		super.populateProceduralMemory(dm, pm);
		
		IChunkType tone = dm.getChunkType("tone").get();
		IChunkType digit = dm.getChunkType("digit").get();
		IChunkType word = dm.getChunkType("word").get();
		IChunkType speech = dm.getChunkType("speech").get();
		
		createHeardProduction(dm, pm, "heard-tone", tone, digit, "1");
		createHeardProduction(dm, pm, "heard-digit", digit, word, "foobar");
		createHeardProduction(dm, pm, "heard-word", word, speech, "hey you over there");
		createHeardSpeechProduction(dm, pm, speech);

		createSearchForSoundProduction(dm, pm);
		createSearchForSoundFailedProduction(dm, pm);
		createSearchForSoundSucceededProduction(dm, pm);

		createEncodingFailedProduction(dm, pm);
		createEncodingIncorrectKindProduction(dm, pm);
		createEncodingIncorrectContentProduction(dm, pm);
		createEncodingCorrectProduction(dm, pm);
	}

	protected void createHeardSpeechProduction(final IDeclarativeModule dm, final IProceduralModule pm, IChunkType speech)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "heard-speech", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest, Arrays.asList(
					new DefaultConditionalSlot("status", succeeded), new DefaultConditionalSlot("kind", speech))));
			
			sp.addAction(new OutputAction("All done"));
			sp.addAction(new RemoveAction("goal"));
			sp.addAction(new StopAction());
		});
	}

	protected void createHeardProduction(final IDeclarativeModule dm, final IProceduralModule pm, final String name,
			final IChunkType currentKind, final IChunkType nextKind, final String nextTestValue)
					throws InterruptedException, ExecutionException {
		createProduction(dm, pm, name, sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest, Arrays.asList(
					new DefaultConditionalSlot("status", succeeded), new DefaultConditionalSlot("kind", currentKind))));
			
			sp.addAction(new ModifyAction("goal",
					Arrays.asList(new DefaultConditionalSlot("status", starting),
							new DefaultConditionalSlot("kind", nextKind),
							new DefaultConditionalSlot("testValue", nextTestValue))));
		});
	}

	protected void createSearchForSoundProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType audioEvent = dm.getChunkType("audio-event").get();
		createProduction(dm, pm, "search-for-sound", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest,
					Arrays.asList(new DefaultConditionalSlot("status", starting),
							new DefaultVariableConditionalSlot("kind", "=kind"))));
			sp.addCondition(new QueryCondition("aural-location",
					Arrays.asList(new DefaultConditionalSlot("state", NOT_EQUALS, dm.getBusyChunk()))));

			sp.addAction(new AddAction("aural-location", audioEvent,
					Arrays.asList(new DefaultVariableConditionalSlot("kind", "=kind"),
							new DefaultConditionalSlot(":attended", null))));
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("status", searching))));
			sp.addAction(new OutputAction("Im listening for something new that is =kind"));
		});
	}

	protected void createSearchForSoundFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-for-sound-failed", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest,
					Arrays.asList(new DefaultConditionalSlot("status", searching),
							new DefaultVariableConditionalSlot("kind", "=kind"))));
			sp.addCondition(new QueryCondition("aural-location",
					Arrays.asList(new DefaultConditionalSlot("state", dm.getErrorChunk()))));

			sp.addAction(new OutputAction("Damn couldnt hear any =kind trying again"));
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("status", starting))));
		});
	}

	protected void createSearchForSoundSucceededProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType audioEvent = dm.getChunkType("audio-event").get();
		final IChunkType attendTo = dm.getChunkType("attend-to").get();
		createProduction(dm, pm, "search-for-sound-succeeded", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest,
					Arrays.asList(new DefaultConditionalSlot("status", searching),
							new DefaultVariableConditionalSlot("kind", "=kind"))));
			sp.addCondition(new ChunkTypeCondition("aural-location", audioEvent,
					Arrays.asList(new DefaultVariableConditionalSlot("kind", "=kind"))));

			sp.addAction(new AddAction("aural", attendTo,
					Arrays.asList(new DefaultVariableConditionalSlot("where", "=aural-location"))));
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("status", encoding))));
			sp.addAction(new OutputAction("Found =kind attending"));
		});
	}

	protected void createEncodingFailedProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "encoding-failed", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest,
					Arrays.asList(new DefaultConditionalSlot("status", encoding))));
			sp.addCondition(new QueryCondition("aural",
					Arrays.asList(new DefaultConditionalSlot("state", dm.getErrorChunk()))));
			
			sp.addAction(new OutputAction("Failed to encode sound"));
			sp.addAction(new RemoveAction("goal"));
		});
	}

	protected void createEncodingIncorrectKindProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType sound = dm.getChunkType("sound").get();
		createProduction(dm, pm, "encoding-incorrect-kind", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest,
					Arrays.asList(new DefaultConditionalSlot("status", encoding),
							new DefaultVariableConditionalSlot("kind", "=kind"),
							new DefaultVariableConditionalSlot("testValue", "=value"))));
			sp.addCondition(new QueryCondition("aural",
					Arrays.asList(new DefaultConditionalSlot("state", dm.getFreeChunk()))));
			sp.addCondition(new ChunkTypeCondition("aural", sound,
					Arrays.asList(new DefaultVariableConditionalSlot("kind", NOT_EQUALS, "=kind"))));
			
			sp.addAction(new OutputAction("incorrect kind"));
			sp.addAction(new RemoveAction("goal"));
		});
	}

	protected void createEncodingIncorrectContentProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType sound = dm.getChunkType("sound").get();
		createProduction(dm, pm, "encoding-incorrect-content", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest,
					Arrays.asList(new DefaultConditionalSlot("status", encoding),
							new DefaultVariableConditionalSlot("kind", "=kind"),
							new DefaultVariableConditionalSlot("testValue", "=value"))));
			sp.addCondition(new QueryCondition("aural",
					Arrays.asList(new DefaultConditionalSlot("state", dm.getFreeChunk()))));
			sp.addCondition(new ChunkTypeCondition("aural", sound,
					Arrays.asList(new DefaultVariableConditionalSlot("content", NOT_EQUALS, "=value"))));
			
			sp.addAction(new OutputAction("incorrect content"));
			sp.addAction(new RemoveAction("goal"));
		});
	}

	protected void createEncodingCorrectProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType sound = dm.getChunkType("sound").get();
		createProduction(dm, pm, "encoding-correct", sp -> {
			sp.addCondition(new ChunkTypeCondition("goal", attendingTest,
					Arrays.asList(new DefaultConditionalSlot("status", encoding),
							new DefaultVariableConditionalSlot("kind", "=kind"),
							new DefaultVariableConditionalSlot("testValue", "=value"))));
			sp.addCondition(new QueryCondition("aural",
					Arrays.asList(new DefaultConditionalSlot("state", dm.getFreeChunk()))));
			sp.addCondition(new ChunkTypeCondition("aural", sound,
					Arrays.asList(new DefaultVariableConditionalSlot("kind", "=kind"),
							new DefaultVariableConditionalSlot("content", "=value"))));
			
			sp.addAction(new OutputAction("I heard =value"));
			sp.addAction(new ModifyAction("goal", Arrays.asList(new DefaultConditionalSlot("status", succeeded))));
			sp.addAction(new RemoveAction("aural"));
			sp.addAction(new RemoveAction("aural-location"));
		});
	}

	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(goal);
	}
}

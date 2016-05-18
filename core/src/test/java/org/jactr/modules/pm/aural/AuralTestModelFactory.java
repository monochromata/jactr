package org.jactr.modules.pm.aural;

import java.util.concurrent.ExecutionException;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.models.AbstractModelFactory;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.imaginal.six.DefaultImaginalModule6;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.modules.pm.aural.six.DefaultAuralModule6;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;

public class AuralTestModelFactory extends AbstractModelFactory {

	protected IChunkType goalCT, attendingTest;
	protected IChunk searching, encoding, starting, testing, failed, succeeded;
	protected IChunk goal;

	public AuralTestModelFactory(ACTRRuntime runtime) {
		super(runtime, "aural-test");
	}

	@Override
	protected void installModules(IModel model) {
		super.installModules(model);
		model.install(new DefaultImaginalModule6(getRuntime()));
		model.install(new DefaultVisualModule6(getRuntime()));
		DefaultAuralModule6 auralModule = new DefaultAuralModule6(getRuntime());
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
		createProduction(dm, pm, "heard-speech", pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(succeeded)
					.slot("kind").eqChunkType(speech)
					
			.actions()
				.output("All done")
				.remove("goal")
				.stop();
		});
	}

	protected void createHeardProduction(final IDeclarativeModule dm, final IProceduralModule pm, final String name,
			final IChunkType currentKind, final IChunkType nextKind, final String nextTestValue)
					throws InterruptedException, ExecutionException {
		createProduction(dm, pm, name, pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(succeeded)
					.slot("kind").eqChunkType(currentKind)
					
			.actions()
				.modify("goal")
					.set("status").toChunk(starting)
					.set("kind").toChunkType(nextKind)
					.set("testValue").toString(nextTestValue);
		});
	}

	protected void createSearchForSoundProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType audioEvent = dm.getChunkType("audio-event").get();
		createProduction(dm, pm, "search-for-sound", pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(starting)
					.slot("kind").eqVariable("=kind")
				.query("aural-location")
					.slot("state").neqChunk(dm.getBusyChunk())
					
			.actions()
				.add("aural-location", audioEvent)
					.set("kind").toVariable("=kind")
					.set(":attended").toNull()
				.modify("goal")
					.set("status").toChunk(searching)
				.output("Im listening for something new that is =kind");
		});
	}

	protected void createSearchForSoundFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "search-for-sound-failed", pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(searching)
					.slot("kind").eqVariable("=kind")
				.query("aural-location")
					.slot("state").eqChunk(dm.getErrorChunk())
					
			.actions()
				.output("Damn couldnt hear any =kind trying again")
				.modify("goal")
					.set("status").toChunk(starting);
		});
	}

	protected void createSearchForSoundSucceededProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType audioEvent = dm.getChunkType("audio-event").get();
		final IChunkType attendTo = dm.getChunkType("attend-to").get();
		createProduction(dm, pm, "search-for-sound-succeeded", pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(searching)
					.slot("kind").eqVariable("=kind")
				.chunkType("aural-location", audioEvent)
					.slot("kind").eqVariable("=kind")
			
			.actions()
				.add("aural", attendTo)
					.set("where").toVariable("=aural-location")
				.modify("goal")
					.set("status").toChunk(encoding)
				.output("Found =kind attending");
		});
	}

	protected void createEncodingFailedProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "encoding-failed", pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(encoding)
				.query("aural")
					.slot("state").eqChunk(dm.getErrorChunk())
					
			.actions()
				.output("Failed to encode sound")
				.remove("goal");
		});
	}

	protected void createEncodingIncorrectKindProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType sound = dm.getChunkType("sound").get();
		createProduction(dm, pm, "encoding-incorrect-kind", pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(encoding)
					.slot("kind").eqVariable("=kind")
					.slot("testValue").eqVariable("=value")
				.query("aural")
					.slot("state").eqChunk(dm.getFreeChunk())
				.chunkType("aural", sound)
					.slot("kind").neqVariable("=kind")
					
			.actions()
				.output("incorrect kind")
				.remove("goal");
		});
	}

	protected void createEncodingIncorrectContentProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType sound = dm.getChunkType("sound").get();
		createProduction(dm, pm, "encoding-incorrect-content", pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(encoding)
					.slot("kind").eqVariable("=kind")
					.slot("testValue").eqVariable("=value")
				.query("aural")
					.slot("state").eqChunk(dm.getFreeChunk())
				.chunkType("aural", sound)
					.slot("content").neqVariable("=value")
			
			.actions()
				.output("incorrect content")
				.remove("goal");
		});
	}

	protected void createEncodingCorrectProduction(final IDeclarativeModule dm, final IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		final IChunkType sound = dm.getChunkType("sound").get();
		createProduction(dm, pm, "encoding-correct", pb -> {
			pb.conditions()
				.chunkType("goal", attendingTest)
					.slot("status").eqChunk(encoding)
					.slot("kind").eqVariable("=kind")
					.slot("testValue").eqVariable("=value")
				.query("aural")
					.slot("state").eqChunk(dm.getFreeChunk())
				.chunkType("aural", sound)
					.slot("kind").eqVariable("=kind")
					.slot("content").eqVariable("=value")
					
			.actions()
				.output("I heard =value")
				.modify("goal")
					.set("status").toChunk(succeeded)
				.remove("aural")
				.remove("aural-location");
		});
	}

	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(goal);
	}
}

package org.jactr.core.models;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.core.module.goal.six.DefaultGoalModule6;
import org.jactr.core.module.imaginal.six.DefaultImaginalModule6;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;
import org.jactr.core.module.retrieval.six.DefaultRetrievalModule6;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.core.utils.parameter.IParameterized;
import org.jactr.modules.pm.aural.six.DefaultAuralModule6;
import org.jactr.modules.pm.visual.six.DefaultVisualModule6;

/**
 * A factory for models that contain the following modules.
 * 
 * <ul>
 * <li>{@link DefaultDeclarativeModule6}</li>
 * <li>{@link DefaultProceduralModule6}</li>
 * <li>{@link DefaultGoalModule6}</li>
 * <li>{@link DefaultRetrievalModule6}</li>
 * </ul>
 */
public abstract class AbstractModelFactory implements ModelFactory {

	private final String modelName;

	private IChunkType chunk, command, color;

	protected AbstractModelFactory(String modelName) {
		this.modelName = modelName;
	}

	@Override
	public IModel createAndInitializeModel() throws Exception {
		IModel model = new BasicModel(modelName);
		installModules(model);
		populateDeclarativeMemory(model);
		populateProceduralMemory(model);
		setupBuffers(model);
		setModelParameters(model);
		model.initialize();
		return model;
	}

	protected void installModules(IModel model) {
		model.install(new DefaultDeclarativeModule6());
		model.install(new DefaultProceduralModule6());
		model.install(new DefaultGoalModule6());
		model.install(new DefaultRetrievalModule6());
	}

	protected void populateDeclarativeMemory(IModel model) throws InterruptedException, ExecutionException {
		populateDeclarativeMemory(model.getDeclarativeModule());
	}

	protected void populateDeclarativeMemory(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		if (dm.getModel().getModule(DefaultDeclarativeModule6.class) != null)
			installChunkTypesAndChunksForDefaultDeclarativeModule6(dm);
		if (dm.getModel().getModule(DefaultVisualModule6.class) != null)
			installChunkTypesAndChunksForDefaultVisualModule6(dm);
		if (dm.getModel().getModule(DefaultAuralModule6.class) != null)
			installChunkTypesAndChunksForDefaultAuralModule6(dm);
	}

	// TODO: Move to DefaultDeclarativeModule6
	// TODO: Does it matter that declarative.jactr and the other *.jactr files
	// included by the IASTParticipant instances declare a specific model name?
	protected void installChunkTypesAndChunksForDefaultDeclarativeModule6(IDeclarativeModule dm)
			throws InterruptedException, ExecutionException {

		chunk = createChunkType(dm, "chunk");
		command = createChunkType(dm, "command");
		createChunkType(dm, "clear", chunkType -> {
			chunkType.getSymbolicChunkType().addSlot(new DefaultConditionalSlot("all", false));
		} , command);
		createChunkType(dm, "attend-to", chunkType -> {
			chunkType.getSymbolicChunkType().addSlot(new DefaultConditionalSlot("where", null));
		} , command);

		createChunk(dm, chunk, "new");
		createChunk(dm, chunk, "free");
		createChunk(dm, chunk, "busy");
		createChunk(dm, chunk, "error");
		createChunk(dm, chunk, "full");
		createChunk(dm, chunk, "empty");
		createChunk(dm, chunk, "requested");
		createChunk(dm, chunk, "unrequested");
		createChunk(dm, chunk, "lowest");
		createChunk(dm, chunk, "highest");
		createChunk(dm, chunk, "reset");

		// buffer/module error codes
		createChunk(dm, chunk, "error-nothing-available");
		createChunk(dm, chunk, "error-nothing-matches");
		createChunk(dm, chunk, "error-no-longer-available");
		createChunk(dm, chunk, "error-changed-too-much");
		createChunk(dm, chunk, "error-invalid-index");
		createChunk(dm, chunk, "error-chunk-deleted");
		createChunk(dm, chunk, "error-unknown");
	}

	// TODO: Move to DefaultVisualModule6
	protected void installChunkTypesAndChunksForDefaultVisualModule6(IDeclarativeModule dm)
			throws InterruptedException, ExecutionException {

		color = createChunkType(dm, "color", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("red", null));
			sct.addSlot(new DefaultConditionalSlot("green", null));
			sct.addSlot(new DefaultConditionalSlot("blue", null));
			sct.addSlot(new DefaultConditionalSlot("alpha", null));
		});

		final IChunkType visualLocation = createChunkType(dm, "visual-location", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("screen-x", null));
			sct.addSlot(new DefaultConditionalSlot("screen-y", null));
			sct.addSlot(new DefaultConditionalSlot("distance", null));
			sct.addSlot(new DefaultConditionalSlot("color", null));
			sct.addSlot(new DefaultConditionalSlot("size", null));
			sct.addSlot(new DefaultConditionalSlot("kind", null));
			sct.addSlot(new DefaultConditionalSlot("nearest", null));
			sct.addSlot(new DefaultConditionalSlot("value", null));
		});

		final IChunkType setDefaultVisualSearch = createChunkType(dm, "set-default-visual-search", visualLocation);

		final IChunkType visualObject = createChunkType(dm, "visual-object", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("screen-pos", null));
			sct.addSlot(new DefaultConditionalSlot("value", null));
			sct.addSlot(new DefaultConditionalSlot("height", null));
			sct.addSlot(new DefaultConditionalSlot("width", null));
			sct.addSlot(new DefaultConditionalSlot("token", null));
			sct.addSlot(new DefaultConditionalSlot("type", null));
			sct.addSlot(new DefaultConditionalSlot("status", null));
			sct.addSlot(new DefaultConditionalSlot("color", null));
		});

		final IChunkType gui = createChunkType(dm, "gui", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("text", null));
			sct.addSlot(new DefaultConditionalSlot("enabled", false));
		} , visualObject);

		final IChunkType text = createChunkType(dm, "text", visualObject);

		final IChunkType emptySpace = createChunkType(dm, "empty-space", visualObject);

		final IChunkType cursor = createChunkType(dm, "cursor", visualObject);

		final IChunkType oval = createChunkType(dm, "oval", gui);

		final IChunkType line = createChunkType(dm, "line", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("other-pos", null));
			sct.addSlot(new DefaultConditionalSlot("end1-x", null));
			sct.addSlot(new DefaultConditionalSlot("end1-y", null));
			sct.addSlot(new DefaultConditionalSlot("end2-x", null));
			sct.addSlot(new DefaultConditionalSlot("end2-y", null));
		} , visualObject);

		final IChunkType phrase = createChunkType(dm, "phrase", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("objects", null));
			sct.addSlot(new DefaultConditionalSlot("words", null));
		} , visualObject);

		final IChunkType visionCommand = createChunkType(dm, "vision-command", dm.getChunkType("command").get());

		final IChunkType moveAttention = createChunkType(dm, "move-attention", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("screen-pos", null));
			sct.addSlot(new DefaultConditionalSlot("scale", null));
		} , visionCommand);

		final IChunkType startTracking = createChunkType(dm, "start-tracking", visionCommand);

		final IChunkType assgignFirst = createChunkType(dm, "assign-first", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("object", null));
			sct.addSlot(new DefaultConditionalSlot("location", null));
		} , visionCommand);

		final IChunkType visualConstant = createChunkType(dm, "visual-constant");

		// meta chunks
		createChunk(dm, visualConstant, "greater-than-current");
		createChunk(dm, visualConstant, "less-than-current");
		createChunk(dm, visualConstant, "current");
		createChunk(dm, visualConstant, "internal");
		createChunk(dm, visualConstant, "external");

		// color chunks
		createColorChunk(dm, "black", 0, 0, 0, 255);
		createColorChunk(dm, "darkGray", 64, 64, 64, 255);
		createColorChunk(dm, "gray", 128, 128, 128, 255);
		createColorChunk(dm, "lightGray", 192, 192, 192, 255);
		createColorChunk(dm, "white", 255, 255, 255, 255);
		createColorChunk(dm, "red", 255, 0, 0, 255);
		createColorChunk(dm, "blue", 0, 0, 255, 255);
		createColorChunk(dm, "green", 0, 255, 0, 255);
		createColorChunk(dm, "yellow", 255, 255, 0, 255);
		createColorChunk(dm, "orange", 255, 200, 0, 255);
		createColorChunk(dm, "magenta", 255, 0, 255, 255);
		createColorChunk(dm, "cyan", 0, 255, 255, 255);
	}

	protected IChunk createColorChunk(final IDeclarativeModule dm, final String name, final double red,
			final double green, final double blue, final double alpha) throws InterruptedException, ExecutionException {
		return createChunk(dm, color, name, chunk -> {
			final ISymbolicChunk sc = chunk.getSymbolicChunk();
			sc.addSlot(new DefaultConditionalSlot("red", red));
			sc.addSlot(new DefaultConditionalSlot("green", green));
			sc.addSlot(new DefaultConditionalSlot("blue", blue));
			sc.addSlot(new DefaultConditionalSlot("alpha", alpha));
		});
	}

	// TODO: Move to DefaultAuralModule6
	protected void installChunkTypesAndChunksForDefaultAuralModule6(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		
		final IChunkType audioEvent = createChunkType(dm, "audio-event", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("kind", null));
			sct.addSlot(new DefaultConditionalSlot("location", null));
			sct.addSlot(new DefaultConditionalSlot("onset", null));
			sct.addSlot(new DefaultConditionalSlot("offset", null));
			sct.addSlot(new DefaultConditionalSlot("pitch", null));
			// real location information, can't use heading/pitch
			sct.addSlot(new DefaultConditionalSlot("azimuth", null));
			sct.addSlot(new DefaultConditionalSlot("elevation", null));
		});

		final IChunkType sound = createChunkType(dm, "sound", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("content", null));
			sct.addSlot(new DefaultConditionalSlot("event", null));
			sct.addSlot(new DefaultConditionalSlot("kind", null));			
		});

		final IChunkType tone = createChunkType(dm, "tone", chunkType -> {
			final ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("pitch", null));
		}, sound);

		final IChunkType digit = createChunkType(dm, "digit", sound);
		final IChunkType speech = createChunkType(dm, "speech", sound);
		final IChunkType word = createChunkType(dm, "word", sound);

		final IChunkType auralCommand = createChunkType(dm, "aural-command", dm.getChunkType("command").get());

		// location chunks
		// TODO: Chunks with the same name were already contributed by DefaultVisualModule6.
		// Should a separate model be used per module?
		createChunk(dm, chunk, "external");
		createChunk(dm, chunk, "internal");
	}

	protected IChunkType createChunkType(IDeclarativeModule dm, String name, IChunkType... parents)
			throws InterruptedException, ExecutionException {
		return createChunkType(dm, name, null, parents);
	}

	protected IChunkType createChunkType(IDeclarativeModule dm, String name, Consumer<IChunkType> configurator,
			IChunkType... parents) throws InterruptedException, ExecutionException {
		final IChunkType ct = dm.createChunkType(Arrays.asList(parents), name).get();
		if (configurator != null)
			configurator.accept(ct);
		dm.addChunkType(ct);
		return ct;
	}

	protected IChunk createChunk(IDeclarativeModule dm, IChunkType chunkType, String name)
			throws InterruptedException, ExecutionException {
		return createChunk(dm, chunkType, name, null);
	}

	protected IChunk createChunk(IDeclarativeModule dm, IChunkType chunkType, String name,
			Consumer<IChunk> configurator) throws InterruptedException, ExecutionException {
		final IChunk chunk = dm.createChunk(chunkType, name).get();
		if (configurator != null)
			configurator.accept(chunk);
		dm.addChunk(chunk);
		return chunk;
	}

	protected void populateProceduralMemory(IModel model) throws InterruptedException, ExecutionException {
		populateProceduralMemory(model.getDeclarativeModule(), model.getProceduralModule());
	}

	protected void populateProceduralMemory(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
	}

	protected void setupBuffers(IModel model) {
		setupGoalBuffer(model);
		setupRetrievalBuffer(model);
		if (model.getModule(DefaultVisualModule6.class) != null)
			setupVisualBuffers(model);
		if (model.getModule(DefaultImaginalModule6.class) != null)
			setupImaginalBuffer(model);
		if (model.getModule(DefaultAuralModule6.class) != null)
			setupAuralBuffers(model);
	}

	// TODO: Move to DefaultGoalModule6
	protected void setupGoalBuffer(IModel model) {
		createBuffer(model, "goal", "1", "20", "false");
	}

	// TODO: Move to DefaultRetrievalModule6
	protected void setupRetrievalBuffer(IModel model) {
		createBuffer(model, "retrieval", "0", "0", "true");
	}

	// TODO: Move to DefaultVisualModule6
	protected void setupVisualBuffers(IModel model) {
		createBuffer(model, "visual-location", "0", "0", "true");
		createBuffer(model, "visual", "0", "0", "true");
	}

	// TODO: Move to DefaultImaginalModule6
	protected void setupImaginalBuffer(IModel model) {
		createBuffer(model, "imaginal", "0", "0", "true");
	}

	// TODO: Move to DefaultAuralModule6
	protected void setupAuralBuffers(IModel model) {
		createBuffer(model, "aural-location", "0", "0", "true");
		createBuffer(model, "aural", "0", "0", "true");
	}

	protected void createBuffer(final IModel model, final String name, final String activation, final String g, final String strictHarvestingEnabled) {
		final IParameterized buffer = (IParameterized)model.getActivationBuffer(name);
		buffer.setParameter("Activation", activation);
		buffer.setParameter("G", g);
		buffer.setParameter("StrictHarvestingEnabled", strictHarvestingEnabled);
	}
	
	protected void setModelParameters(IModel model) {
	}

}

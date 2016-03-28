package org.jactr.core.models;

import static java.lang.String.format;

import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.jactr.core.buffer.IActivationBuffer;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.model.basic.BasicModel;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.declarative.six.DefaultDeclarativeModule6;
import org.jactr.core.module.goal.six.DefaultGoalModule6;
import org.jactr.core.module.procedural.six.DefaultProceduralModule6;
import org.jactr.core.module.retrieval.six.DefaultRetrievalModule6;
import org.jactr.core.production.IProduction;
import org.jactr.core.utils.parameter.IParameterized;

public class FullSemanticModelFactory extends SemanticModelFactory {

	protected IChunk empty, _new, error, free, busy, unrequested, full, requested;

	@Override
	protected void installModules(IModel model) {
		// Replace super-class module configuration
		installDeclarativeModule(model);
		installProceduralModule(model);
		installGoalModule(model);
		installRetrievalModule(model);
	}

	protected void installDeclarativeModule(IModel model) {
		DefaultDeclarativeModule6 dm = new DefaultDeclarativeModule6();
		dm.setParameter("EnablePartialMatching", "false"); // TODO: Might previously have been configured as: dm.setParameter("PartialMatchingEnabled", "false"); 
		dm.setParameter("BaseLevelConstant", "0.0");
		dm.setParameter("ActivationNoise", "0.0");
		dm.setParameter("PermanentActivationNoise", "0.0"); // TODO: Might previously have been configured as: dm.setParameter("PermanentActivatioNoise", "0.0");
		dm.setParameter("MismatchPenalty", "0.0");
		dm.setParameter("MaximumDifference", "-10.0");
		dm.setParameter("MaximumSimilarity", "10.0");
		model.install(dm);
	}

	protected void installProceduralModule(IModel model) {
		DefaultProceduralModule6 pm = new DefaultProceduralModule6();
		pm.setParameter("ExpectedUtilityNoise", "0.0"); // TODO: Might previously have been configured as: pm.setParameter("ExpectedGainNoise", "0.0");
		pm.setParameter("DefaultProductionFiringTime", "0.05");
		pm.setParameter("NumberOfProductionsFired", "0");
		model.install(pm);
	}

	protected void installGoalModule(IModel model) {
		model.install(new DefaultGoalModule6());
	}

	protected void installRetrievalModule(IModel model) {
		DefaultRetrievalModule6 rm = new DefaultRetrievalModule6();
		rm.setParameter("RetrievalThreshold", "-Infinity");
		rm.setParameter("LatencyExponent", "1.0");
		rm.setParameter("LatencyFactor", "0.5");
		model.install(rm);
	}

	@Override
	protected void populateDeclarativeMemory(IDeclarativeModule dm) throws InterruptedException, ExecutionException {

		super.populateDeclarativeMemory(dm);

		empty = dm.getEmptyChunk();
		_new = dm.getNewChunk();
		error = dm.getErrorChunk();
		free = dm.getFreeChunk();
		busy = dm.getBusyChunk();
		unrequested = dm.getUnrequestedChunk();
		full = dm.getFullChunk();
		requested = dm.getRequestedChunk();

	}

	@Override
	protected BiConsumer<String, IChunk> getChunkParameterConfiguration() {
		return (chunkName, chunk) -> {
			super.getChunkParameterConfiguration().accept(chunkName, chunk);
			chunk.getSubsymbolicChunk().setParameter("CreationTime", "0.0");
			chunk.getSubsymbolicChunk().setParameter("TimesNeeded", "0");
			chunk.getSubsymbolicChunk().setParameter("TimesInContext", "0");
			chunk.getSubsymbolicChunk().setParameter("ReferenceCount", "1");
			chunk.getSubsymbolicChunk().setParameter("ReferenceTimes", "(0.0)");
			chunk.getSubsymbolicChunk().setParameter("BaseLevelActivation", "0.0");
			chunk.getSubsymbolicChunk().setParameter("SpreadingActivation", "0.0");
			// TODO: No longer supported: chunk.getSubsymbolicChunk().setParameter("SourceActivation", "0.0");
			chunk.getSubsymbolicChunk().setParameter("TotalActivation", "0.0");
			chunk.getSubsymbolicChunk().setParameter("Links", format("((%1$s 1 0.0))", chunkName));
			chunk.getSubsymbolicChunk().setParameter("CreationCycle", "0");
			chunk.getSubsymbolicChunk().setParameter("Similarities", format("((%1$s 1.0))", chunkName));
			chunk.getSubsymbolicChunk().setParameter("SimilarityActivation", "0.0");
		};
	}

	@Override
	protected Consumer<IProduction> getProductionParameterConfiguration() {
		return production -> {
			super.getProductionParameterConfiguration().accept(production);
			production.getSubsymbolicProduction().setParameter("P", "1.0");
			production.getSubsymbolicProduction().setParameter("C", "0.05");
			production.getSubsymbolicProduction().setParameter("CreationTime", "0.0");
			production.getSubsymbolicProduction().setParameter("FiringTime", "0.05");
			production.getSubsymbolicProduction().setParameter("EffortCount", "0");
			production.getSubsymbolicProduction().setParameter("EffortTimes", "()");
			production.getSubsymbolicProduction().setParameter("SuccessCount", "0");
			production.getSubsymbolicProduction().setParameter("SuccessTimes", "()");
			production.getSubsymbolicProduction().setParameter("FailureCount", "0");
			production.getSubsymbolicProduction().setParameter("FailureTimes", "()");
			production.getSubsymbolicProduction().setParameter("PriorSuccessCount", "1");
			production.getSubsymbolicProduction().setParameter("PriorFailureCount", "0");
			production.getSubsymbolicProduction().setParameter("PriorEffortCount", "0.05");
			production.getSubsymbolicProduction().setParameter("Success", "true");
			production.getSubsymbolicProduction().setParameter("Failure", "false");
			production.getSubsymbolicProduction().setParameter("CreationCycle", "0");
		};
	}

	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		configureBuffer(model, "goal", "1.0", "20.0");
		configureBuffer(model, "retrieval", "0.0", "0.0");
	}

	protected void configureBuffer(IModel model, String name, String activation, String g) {
		IActivationBuffer goal = model.getActivationBuffer(name);
		((IParameterized) goal).setParameter("Activation", activation);
		((IParameterized) goal).setParameter("G", g);
	}

	@Override
	protected void setModelParameters(IModel model) {
		super.setModelParameters(model);
		model.setParameter("EnableUnusedCycleSkipping", "true");
	}

}

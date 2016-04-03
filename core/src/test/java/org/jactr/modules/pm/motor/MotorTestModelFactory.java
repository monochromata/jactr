package org.jactr.modules.pm.motor;

import static java.lang.String.format;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections.list.SetUniqueList;
import org.jactr.core.chunk.IChunk;
import org.jactr.core.chunk.ISymbolicChunk;
import org.jactr.core.chunktype.IChunkType;
import org.jactr.core.chunktype.ISymbolicChunkType;
import org.jactr.core.model.IModel;
import org.jactr.core.models.AbstractModelFactory;
import org.jactr.core.module.asynch.IAsynchronousModule;
import org.jactr.core.module.declarative.IDeclarativeModule;
import org.jactr.core.module.imaginal.six.DefaultImaginalModule6;
import org.jactr.core.module.procedural.IProceduralModule;
import org.jactr.core.production.IProduction;
import org.jactr.core.production.ISymbolicProduction;
import org.jactr.core.production.VariableBindings;
import org.jactr.core.production.action.AddAction;
import org.jactr.core.production.action.ModifyAction;
import org.jactr.core.production.action.OutputAction;
import org.jactr.core.production.action.RemoveAction;
import org.jactr.core.production.action.StopAction;
import org.jactr.core.production.condition.CannotMatchException;
import org.jactr.core.production.condition.ChunkTypeCondition;
import org.jactr.core.production.condition.ICondition;
import org.jactr.core.production.condition.QueryCondition;
import org.jactr.core.slot.DefaultConditionalSlot;
import org.jactr.modules.pm.motor.command.translators.AbstractManualTranslator;
import org.jactr.modules.pm.motor.command.translators.PeckRecoilTranslator;
import org.jactr.modules.pm.motor.command.translators.PeckTranslator;
import org.jactr.modules.pm.motor.command.translators.PunchTranslator;
import org.jactr.modules.pm.motor.six.DefaultMotorModule6;

public class MotorTestModelFactory extends AbstractModelFactory {

	private IChunkType goal, sequence;
	private IChunk starting, retrieving, motorStarted, motorCompleted, finished;
	private IChunkType punch, peck, peckRecoil;
	private IChunk index, left, right;
	private IChunk g1;
	
	public MotorTestModelFactory() {
		super("motor-test");
	}

	@Override
	protected void installModules(IModel model) {
		super.installModules(model);
		model.install(new DefaultImaginalModule6());
		model.install(configureMotorModule(new DefaultMotorModule6()));
	}
	
	protected DefaultMotorModule6 configureMotorModule(DefaultMotorModule6 motorModule) {
	    motorModule.setParameter(IAsynchronousModule.STRICT_SYNCHRONIZATION_PARAM, "true");
	    motorModule.setParameter(AbstractMotorModule.ENABLE_PARALLEL_MUSCLES_PARAM, "false");
	    motorModule.setParameter(AbstractManualTranslator.MINIMUM_FITTS_TIME,"0.1");
	    motorModule.setParameter(AbstractManualTranslator.MINIMUM_MOVEMENT_TIME,"0.05");
	    motorModule.setParameter(AbstractManualTranslator.PECK_FITTS_COEFFICIENT,"0.075");
	    motorModule.setParameter(PunchTranslator.class.getName(), "true");
	    motorModule.setParameter(PeckTranslator.class.getName(), "true");
	    motorModule.setParameter(PeckRecoilTranslator.class.getName(), "true");
	    return motorModule;
	}

	@Override
	protected void populateDeclarativeMemory(IDeclarativeModule dm) throws InterruptedException, ExecutionException {
		
		super.populateDeclarativeMemory(dm);
		
		final IChunkType chunkCT = dm.getChunkType("chunk").get();
		
		sequence = createChunkType(dm, "sequence", chunkType -> {
			ISymbolicChunkType sct = chunkType.getSymbolicChunkType();
			sct.addSlot(new DefaultConditionalSlot("order", null));
			sct.addSlot(new DefaultConditionalSlot("command", null));
			sct.addSlot(new DefaultConditionalSlot("finger", null));
			sct.addSlot(new DefaultConditionalSlot("hand", null));
			sct.addSlot(new DefaultConditionalSlot("r", null));
			sct.addSlot(new DefaultConditionalSlot("theta", null));
		});
		
		starting = createChunk(dm, chunkCT, "starting");
		retrieving = createChunk(dm, chunkCT, "retrieving");
		motorStarted = createChunk(dm, chunkCT, "motor-started");
		motorCompleted = createChunk(dm, chunkCT, "motor-completed");
		finished = createChunk(dm, chunkCT, "finished");
		
		// goal chunk type that defines the sequence of firing
		goal = createChunkType(dm, "goal", chunkType -> {
			chunkType.getSymbolicChunkType().addSlot(
					new DefaultConditionalSlot("state", starting));
		});
		
		punch = dm.getChunkType("punch").get();
		peck = dm.getChunkType("peck").get();
		peckRecoil = dm.getChunkType("peck-recoil").get();

		index = dm.getChunk("index").get();
		left = dm.getChunk("left").get();
		right = dm.getChunk("right").get();
		
		createChunk(dm, sequence, "punch-j", chunk -> {
			ISymbolicChunk sc = chunk.getSymbolicChunk();
			sc.addSlot(new DefaultConditionalSlot("order", 1.0));
			sc.addSlot(new DefaultConditionalSlot("command", punch));
			sc.addSlot(new DefaultConditionalSlot("finger", index));
			sc.addSlot(new DefaultConditionalSlot("hand", right));
		});
		
		createChunk(dm, sequence, "peck-g", chunk -> {
			ISymbolicChunk sc = chunk.getSymbolicChunk();
			sc.addSlot(new DefaultConditionalSlot("order", 2.0));
			sc.addSlot(new DefaultConditionalSlot("command", peck));
			sc.addSlot(new DefaultConditionalSlot("finger", index));
			sc.addSlot(new DefaultConditionalSlot("hand", left));
			sc.addSlot(new DefaultConditionalSlot("r", 1.0));
			sc.addSlot(new DefaultConditionalSlot("theta", 0.0));
		});
		
		createChunk(dm, sequence, "punch-g", chunk -> {
			ISymbolicChunk sc = chunk.getSymbolicChunk();
			sc.addSlot(new DefaultConditionalSlot("order", 3.0));
			sc.addSlot(new DefaultConditionalSlot("command", punch));
			sc.addSlot(new DefaultConditionalSlot("finger", index));
			sc.addSlot(new DefaultConditionalSlot("hand", left));
		});
		
		createChunk(dm, sequence, "peck-recoil-h", chunk -> {
			ISymbolicChunk sc = chunk.getSymbolicChunk();
			sc.addSlot(new DefaultConditionalSlot("order", 4.0));
			sc.addSlot(new DefaultConditionalSlot("command", peckRecoil));
			sc.addSlot(new DefaultConditionalSlot("finger", index));
			sc.addSlot(new DefaultConditionalSlot("hand", right));
			sc.addSlot(new DefaultConditionalSlot("r", 1.0));
			sc.addSlot(new DefaultConditionalSlot("theta", 3.14));
		});
		
		g1 = createChunk(dm, goal, "g1");
	}

	@Override
	protected void populateProceduralMemory(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		
		super.populateProceduralMemory(dm, pm);
		
		createStartProduction(dm, pm);
		createRetrieveNextMovementProduction(dm, pm);
		createRetrievalFailedProduction(dm, pm);
		createMovementCompletedProduction(dm, pm);
		createMovementFailedProduction(dm, pm);
		createStartPunchProduction(dm, pm);
		createStartPeckProduction(dm, pm);
		createStartPeckRecoilProduction(dm, pm);
	}
	
	// Create the production that starts up the test
	protected void createStartProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction production = pm.createProduction("start").get();
		ISymbolicProduction sp = production.getSymbolicProduction();
		sp.addCondition(new ChunkTypeCondition("goal", goal, Arrays.asList(
				new DefaultConditionalSlot("state", starting))));
		sp.addCondition(new QueryCondition("retrieval", Arrays.asList(
				new DefaultConditionalSlot("state", dm.getFreeChunk()))));
		
		sp.addAction(new AddAction("retrieval", sequence, Arrays.asList(
				new DefaultConditionalSlot("order", 1.0))));
		sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("state", retrieving))));
		sp.addAction(new OutputAction("Trying to retrieve first movement"));
		pm.addProduction(production);
	}
	
	// post completion production
	// which launches another retrieval of the next movement
	protected void createRetrieveNextMovementProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction production = pm.createProduction("retrieve-next-movement").get();
		ISymbolicProduction sp = production.getSymbolicProduction();
		sp.addCondition(new ChunkTypeCondition("goal", goal, Arrays.asList(
				new DefaultConditionalSlot("state", motorCompleted))));
		sp.addCondition(new QueryCondition("retrieval", Arrays.asList(
				new DefaultConditionalSlot("state", dm.getFreeChunk()))));
		sp.addCondition(new ChunkTypeCondition("retrieval", sequence, Arrays.asList(
				new DefaultConditionalSlot("order", "=index"))));
		sp.addCondition(new IncrementingCondition("=index", "=next"));
		
		sp.addAction(new AddAction("retrieval", sequence, Arrays.asList(
				new DefaultConditionalSlot("order", "=next"))));
		sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("state", retrieving))));
		sp.addAction(new OutputAction("Trying to retrieve =index movement"));
		pm.addProduction(production);
	}
	
	// failed to find a sequence, we are done
	protected void createRetrievalFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction production = pm.createProduction("retrieval-failed").get();
		ISymbolicProduction sp = production.getSymbolicProduction();
		sp.addCondition(new ChunkTypeCondition("goal", goal, Arrays.asList(
				new DefaultConditionalSlot("state", retrieving))));
		sp.addCondition(new QueryCondition("retrieval", Arrays.asList(
				new DefaultConditionalSlot("state", dm.getErrorChunk()))));
		
		sp.addAction(new RemoveAction("retrieval"));
		sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("state", "finished"))));
		sp.addAction(new OutputAction("Could not find the next movement sequence. Finished"));
		sp.addAction(new StopAction());
		
		pm.addProduction(production);
	}
	
	// general movement end production
	protected void createMovementCompletedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction production = pm.createProduction("movement-completed").get();
		ISymbolicProduction sp = production.getSymbolicProduction();
		sp.addCondition(new ChunkTypeCondition("goal", goal, Arrays.asList(
				new DefaultConditionalSlot("state", motorStarted))));
		sp.addCondition(new ChunkTypeCondition("retrieval", sequence, Arrays.asList(
				new DefaultConditionalSlot("command", "=command"))));
		sp.addCondition(new QueryCondition("motor", Arrays.asList(
				new DefaultConditionalSlot("state", dm.getFreeChunk()))));
		
		sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("state", motorCompleted))));
		// keep the command around for the next production
		sp.addAction(new ModifyAction("retrieval"));
		sp.addAction(new OutputAction("Completed command =command defined by =retrieval"));
		pm.addProduction(production);
	}
	
	// general movement failed production
	protected void createMovementFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction production = pm.createProduction("movement-failed").get();
		ISymbolicProduction sp = production.getSymbolicProduction();
		sp.addCondition(new ChunkTypeCondition("goal", goal, Arrays.asList(
				new DefaultConditionalSlot("state", motorStarted))));
		sp.addCondition(new ChunkTypeCondition("retrieval", sequence, Arrays.asList(
				new DefaultConditionalSlot("command", "=command"))));
		sp.addCondition(new QueryCondition("motor", Arrays.asList(
				new DefaultConditionalSlot("state", dm.getErrorChunk()))));
		
		sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("state", finished))));
		sp.addAction(new OutputAction("Failed to execute command =command defined by =retrieval"));
		pm.addProduction(production);
	}
	
	// got a command sequence, lets do something with it
	
	protected void createStartPunchProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction p = pm.createProduction("start-punch").get();
		ISymbolicProduction sp = p.getSymbolicProduction();
		sp.addCondition(new ChunkTypeCondition("goal", goal, Arrays.asList(
				new DefaultConditionalSlot("state", retrieving))));
		sp.addCondition(new ChunkTypeCondition("retrieval", sequence, Arrays.asList(
				new DefaultConditionalSlot("command", punch),
				new DefaultConditionalSlot("finger", "=finger"),
				new DefaultConditionalSlot("hand", "=hand"))));
		sp.addCondition(new QueryCondition("motor", Arrays.asList(
				new DefaultConditionalSlot("state", dm.getFreeChunk()))));
		
		sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("state", motorStarted))));
		sp.addAction(new AddAction("motor", punch, Arrays.asList(
				new DefaultConditionalSlot("finger", "=finger"),
				new DefaultConditionalSlot("hand", "=hand"))));
		// Keep it around for a bit
		sp.addAction(new ModifyAction("retrieval"));
		sp.addAction(new OutputAction("Punching =hand =finger"));
		pm.addProduction(p);
	}
	
	protected void createStartPeckProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction p = pm.createProduction("start-peck").get();
		ISymbolicProduction sp = p.getSymbolicProduction();
		sp.addCondition(new ChunkTypeCondition("goal", goal, Arrays.asList(
				new DefaultConditionalSlot("state", retrieving))));
		sp.addCondition(new ChunkTypeCondition("retrieval", sequence, Arrays.asList(
				new DefaultConditionalSlot("command", peck),
				new DefaultConditionalSlot("finger", "=finger"),
				new DefaultConditionalSlot("hand", "=hand"),
				new DefaultConditionalSlot("r", "=r"),
				new DefaultConditionalSlot("theta", "=theta"))));
		sp.addCondition(new QueryCondition("motor", Arrays.asList(
				new DefaultConditionalSlot("state", dm.getFreeChunk()))));
		
		sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("state", motorStarted))));
		sp.addAction(new AddAction("motor", peck, Arrays.asList(
				new DefaultConditionalSlot("finger", "=finger"),
				new DefaultConditionalSlot("hand", "=hand"),
				new DefaultConditionalSlot("r", "=r"),
				new DefaultConditionalSlot("theta", "=theta"))));
		// Keep it around for a bit
		sp.addAction(new ModifyAction("retrieval"));
		sp.addAction(new OutputAction("Pecking =hand =finger =r along =theta"));
		pm.addProduction(p);
	}
	
	protected void createStartPeckRecoilProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		IProduction p = pm.createProduction("start-peck-recoil").get();
		ISymbolicProduction sp = p.getSymbolicProduction();
		sp.addCondition(new ChunkTypeCondition("goal", goal, Arrays.asList(
				new DefaultConditionalSlot("state", retrieving))));
		sp.addCondition(new ChunkTypeCondition("retrieval", sequence, Arrays.asList(
				new DefaultConditionalSlot("command", peckRecoil),
				new DefaultConditionalSlot("finger", "=finger"),
				new DefaultConditionalSlot("hand", "=hand"),
				new DefaultConditionalSlot("r", "=r"),
				new DefaultConditionalSlot("theta", "=theta"))));
		sp.addCondition(new QueryCondition("motor", Arrays.asList(
				new DefaultConditionalSlot("state", dm.getFreeChunk()))));
		
		sp.addAction(new ModifyAction("goal", Arrays.asList(
				new DefaultConditionalSlot("state", motorStarted))));
		sp.addAction(new AddAction("motor", peckRecoil, Arrays.asList(
				new DefaultConditionalSlot("finger", "=finger"),
				new DefaultConditionalSlot("hand", "=hand"),
				new DefaultConditionalSlot("r", "=r"),
				new DefaultConditionalSlot("theta", "=theta"))));
		// Keep it around for a bit
		sp.addAction(new ModifyAction("retrieval"));
		sp.addAction(new OutputAction("Pecking and recoil =hand =finger =r along =theta"));
		pm.addProduction(p);
	}
		
	@Override
	protected void setupBuffers(IModel model) {
		super.setupBuffers(model);
		model.getActivationBuffer("goal").addSourceChunk(g1);
	}

	@Override
	protected void setModelParameters(IModel model) {
		super.setModelParameters(model);
		model.setParameter("EnablePersistentExecution", "true");
	}
	
	private static class IncrementingCondition implements ICondition {

		private final String incomingVariable;
		private final String outgoingVariable;
		
		private IncrementingCondition(String incomingVariable, String outgoingVariable) {
			this.incomingVariable = incomingVariable.toLowerCase();
			this.outgoingVariable = outgoingVariable.toLowerCase();
		}
		
		@Override
		public void dispose() {
		}

		@Override
		public ICondition clone(IModel model, VariableBindings variableBindings) throws CannotMatchException {
			return new IncrementingCondition(incomingVariable, outgoingVariable);
		}

		@Override
		public int bind(IModel model, VariableBindings variableBindings, boolean isIterative)
				throws CannotMatchException {
			if(!variableBindings.isBound(incomingVariable))
				throw new CannotMatchException(format("Variable %1$s is not bound", incomingVariable));
			double oldValue = (double)variableBindings.get(incomingVariable);
			variableBindings.bind(outgoingVariable, oldValue + 1);
			return 0;
		}
	}
	
}

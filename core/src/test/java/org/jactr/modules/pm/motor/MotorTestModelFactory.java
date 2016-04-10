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
		
		sequence = createChunkType(dm, "sequence", sct -> {
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
		goal = createChunkType(dm, "goal", sct -> {
			sct.addSlot(new DefaultConditionalSlot("state", starting));
		});
		
		punch = dm.getChunkType("punch").get();
		peck = dm.getChunkType("peck").get();
		peckRecoil = dm.getChunkType("peck-recoil").get();

		index = dm.getChunk("index").get();
		left = dm.getChunk("left").get();
		right = dm.getChunk("right").get();
		
		createChunk(dm, sequence, "punch-j", sc -> {
			sc.addSlot(new DefaultConditionalSlot("order", 1.0));
			sc.addSlot(new DefaultConditionalSlot("command", punch));
			sc.addSlot(new DefaultConditionalSlot("finger", index));
			sc.addSlot(new DefaultConditionalSlot("hand", right));
		});
		
		createChunk(dm, sequence, "peck-g", sc -> {
			sc.addSlot(new DefaultConditionalSlot("order", 2.0));
			sc.addSlot(new DefaultConditionalSlot("command", peck));
			sc.addSlot(new DefaultConditionalSlot("finger", index));
			sc.addSlot(new DefaultConditionalSlot("hand", left));
			sc.addSlot(new DefaultConditionalSlot("r", 1.0));
			sc.addSlot(new DefaultConditionalSlot("theta", 0.0));
		});
		
		createChunk(dm, sequence, "punch-g", sc -> {
			sc.addSlot(new DefaultConditionalSlot("order", 3.0));
			sc.addSlot(new DefaultConditionalSlot("command", punch));
			sc.addSlot(new DefaultConditionalSlot("finger", index));
			sc.addSlot(new DefaultConditionalSlot("hand", left));
		});
		
		createChunk(dm, sequence, "peck-recoil-h", sc -> {
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
		createProduction(dm, pm, "start", pb -> {
			pb.conditions()
				.chunkType("goal", goal)
					.slot("state").eqChunk(starting)
				.query("retrieval")
					.slot("state").eqChunk(dm.getFreeChunk())
					
			.actions()
				.add("retrieval", sequence)
					.set("order").toDouble(1.0)
				.modify("goal")
					.set("state").toChunk(retrieving)
				.output("Trying to retrieve first movement");
		});
	}
	
	// post completion production
	// which launches another retrieval of the next movement
	protected void createRetrieveNextMovementProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "retrieve-next-movement", pb -> {
			pb.conditions()
				.chunkType("goal", goal)
					.slot("state").eqChunk(motorCompleted)
				.query("retrieval")
					.slot("state").eqChunk(dm.getFreeChunk())
				.chunkType("retrieval", sequence)
					.slot("order").eqVariable("=index")
				.condition(new IncrementingCondition("=index", "=next"))
				
			.actions()
				.add("retrieval", sequence)
					.set("order").toVariable("=next")
				.modify("goal")
					.set("state").toChunk(retrieving)
				.output("Trying to retrieve =index movement");
		});
	}
	
	// failed to find a sequence, we are done
	protected void createRetrievalFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "retrieval-failed", pb -> {
			pb.conditions()
				.chunkType("goal", goal)
					.slot("state").eqChunk(retrieving)
				.query("retrieval")
					.slot("state").eqChunk(dm.getErrorChunk())
					
			.actions()
				.remove("retrieval")
				.modify("goal")
					.set("state").toChunk(finished)
				.output("Could not find the next movement sequence. Finished")
				.stop();
		});
	}
	
	// general movement end production
	protected void createMovementCompletedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "movement-completed", pb -> {
			pb.conditions()
				.chunkType("goal", goal)
					.slot("state").eqChunk(motorStarted)
				.chunkType("retrieval", sequence)
					.slot("command").eqVariable("=command")
				.query("motor")
					.slot("state").eqChunk(dm.getFreeChunk())
			
			.actions()
				.modify("goal")
					.set("state").toChunk(motorCompleted)
				// keep the command around for the next production
				.modify("retrieval")
				.output("Completed command =command defined by =retrieval");
		});
	}
	
	// general movement failed production
	protected void createMovementFailedProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "movement-failed", pb -> {
			pb.conditions()
				.chunkType("goal", goal)
					.slot("state").eqChunk(motorStarted)
				.chunkType("retrieval", sequence)
					.slot("command").eqVariable("=command")
				.query("motor")
					.slot("state").eqChunk(dm.getErrorChunk())
			
			.actions()
				.modify("goal")
					.set("state").toChunk(finished)
				.output("Failed to execute command =command defined by =retrieval");
		});
	}
	
	// got a command sequence, lets do something with it
	
	protected void createStartPunchProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "start-punch", pb -> {
			pb.conditions()
				.chunkType("goal", goal)
					.slot("state").eqChunk(retrieving)
				.chunkType("retrieval", sequence)
					.slot("command").eqChunkType(punch)
					.slot("finger").eqVariable("=finger")
					.slot("hand").eqVariable("=hand")
				.query("motor")
					.slot("state").eqChunk(dm.getFreeChunk())
			
			.actions()
				.modify("goal")
					.set("state").toChunk(motorStarted)
				.add("motor", punch)
					.set("finger").toVariable("=finger")
					.set("hand").toVariable("=hand")
				// Keep it around for a bit
				.modify("retrieval")
				.output("Punching =hand =finger");
		});
	}
	
	protected void createStartPeckProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "start-peck", pb -> {
			pb.conditions()
				.chunkType("goal", goal)
					.slot("state").eqChunk(retrieving)
				.chunkType("retrieval", sequence)
					.slot("command").eqChunkType(peck)
					.slot("finger").eqVariable("=finger")
					.slot("hand").eqVariable("=hand")
					.slot("r").eqVariable("=r")
					.slot("theta").eqVariable("=theta")
				.query("motor")
					.slot("state").eqChunk(dm.getFreeChunk())
					
			.actions()
				.modify("goal")
					.set("state").toChunk(motorStarted)
				.add("motor", peck)
					.set("finger").toVariable("=finger")
					.set("hand").toVariable("=hand")
					.set("r").toVariable("=r")
					.set("theta").toVariable("=theta")
				// Keep it around for a bit
				.modify("retrieval")
				.output("Pecking =hand =finger =r along =theta");
		});
	}
	
	protected void createStartPeckRecoilProduction(IDeclarativeModule dm, IProceduralModule pm)
			throws InterruptedException, ExecutionException {
		createProduction(dm, pm, "start-peck-recoil", pb -> {
			pb.conditions()
				.chunkType("goal", goal)
					.slot("state").eqChunk(retrieving)
				.chunkType("retrieval", sequence)
					.slot("command").eqChunkType(peckRecoil)
					.slot("finger").eqVariable("=finger")
					.slot("hand").eqVariable("=hand")
					.slot("r").eqVariable("=r")
					.slot("theta").eqVariable("=theta")
				.query("motor")
					.slot("state").eqChunk(dm.getFreeChunk())
			
			.actions()
				.modify("goal")
					.set("state").toChunk(motorStarted)
				.add("motor", peckRecoil)
					.set("finger").toVariable("=finger")
					.set("hand").toVariable("=hand")
					.set("r").toVariable("=r")
					.set("theta").toVariable("=theta")
				// Keep it around for a bit
				.modify("retrieval")
				.output("Pecking and recoil =hand =finger =r along =theta");
		});
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

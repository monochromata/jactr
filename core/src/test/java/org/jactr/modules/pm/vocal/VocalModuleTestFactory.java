package org.jactr.modules.pm.vocal;

import org.jactr.core.fluent.IActionSlotsBuilder;
import org.jactr.core.fluent.IActionsBuilder;
import org.jactr.core.fluent.IConditionsBuilder;
import org.jactr.core.model.IModel;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.modules.pm.visual.VisualModuleTestFactory;
import org.jactr.modules.pm.vocal.six.DefaultVocalModule6;

public class VocalModuleTestFactory extends VisualModuleTestFactory {

	public VocalModuleTestFactory(ACTRRuntime runtime) {
		super(runtime, "vocal-test");
	}

	@Override
	protected void installModules(IModel model) {
		super.installModules(model);
		model.install(new DefaultVocalModule6(getRuntime()));
	}
	
	@Override
	protected IConditionsBuilder createSearchQueryCondition(IConditionsBuilder builder) {
		return builder
			.query("visual")
				.slot("state").eqChunk("free")
			.query("vocal")
				.slot("state").eqChunk("free");
	}

	@Override
	protected IActionsBuilder createSearchKindExtraActions(IActionsBuilder builder) {
		return speakSearchingBasedOn(builder, "kind");
	}

	@Override
	protected IActionsBuilder createSearchLessThanExtraActions(IActionsBuilder builder) {
		return speakSearchingBasedOn(builder, "less-than");
	}

	@Override
	protected IActionsBuilder createSearchGreaterThanExtraActions(IActionsBuilder builder) {
		return speakSearchingBasedOn(builder, "greater-than");
	}

	@Override
	protected IActionsBuilder createSearchColorExtraActions(IActionsBuilder builder) {
		return speakSearchingBasedOn(builder, "color");
	}

	@Override
	protected IActionsBuilder createSearchSizeExtraActions(IActionsBuilder builder) {
		return speakSearchingBasedOn(builder, "size");
	}

	protected IActionSlotsBuilder speakSearchingBasedOn(IActionsBuilder builder, String searchBasis) {
		return builder.add("vocal", "speak")
				.set("string").toString("Searching based on "+searchBasis);
	}
}

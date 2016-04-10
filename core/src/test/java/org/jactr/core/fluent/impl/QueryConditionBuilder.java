package org.jactr.core.fluent.impl;

import org.jactr.core.production.condition.QueryCondition;

public class QueryConditionBuilder extends AbstractConditionBuilder {

	public QueryConditionBuilder(String buffer, DefaultConditionsBuilder conditionsBuilder) {
		super(buffer, conditionsBuilder);
	}

	@Override
	protected void doComplete() {
		getConditionsBuilder().conditionInternal(new QueryCondition(getBuffer(), getSlots()));
	}
	
}
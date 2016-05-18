/*
 * Created on Jul 7, 2006 Copyright (C) 2001-5, Anthony Harrison anh23@pitt.edu
 * (jactr.org) This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or (at your option) any later version. This library is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details. You should have
 * received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jactr.modules.pm.aural;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.net.message.credentials.PlainTextCredentials;
import org.commonreality.net.provider.INetworkingProvider;
import org.commonreality.net.service.IClientService;
import org.commonreality.net.transport.ITransportProvider;
import org.commonreality.netty.NettyNetworkingProvider;
import org.commonreality.netty.protocol.NOOPProtocol;
import org.commonreality.netty.transport.LocalTransportProvider;
import org.commonreality.participant.impl.AbstractParticipant;
import org.commonreality.reality.IReality;
import org.commonreality.reality.control.RealitySetup;
import org.commonreality.sensors.ISensor;
import org.commonreality.sensors.xml.XMLSensor;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.models.IModelFactory;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.reality.connector.CommonRealityConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.DebugController;
import org.jactr.modules.AbstractModuleTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuralModuleTest extends AbstractModuleTest {
	/**
	 * logger definition
	 */
	static public final Log LOGGER = LogFactory.getLog(AuralModuleTest.class);

	String[] _productionSequence = { "search-for-sound-succeeded", "encoding-correct", "heard-tone",
			"search-for-sound-succeeded", "encoding-correct", "heard-digit", "search-for-sound-succeeded",
			"encoding-correct", "heard-word", "search-for-sound-succeeded", "encoding-correct", "heard-speech" };

	String[] _failedProductions = { "encoding-failed", "encoding-incorrect-kind", "encoding-incorrect-content",
			"encoding-match-failed" };

	String[] _ignoreProductions = { "search-for-sound-failed", "search-for-sound" };

	int _productionFireCount = 0;

	@Override
	protected IModelFactory createModelFactory() {
		return new AuralTestModelFactory(_runtime);
	}

	protected ISensor createSensor(INetworkingProvider netProvider) throws Exception {
		return createXMLSensor(_cr, netProvider, "org/jactr/modules/pm/aural/sensorData.xml");
	}

	protected String getModelName() {
		return "aural-test";
	}
	
	@Test
	public void test() throws Exception {
		assertThat(_runtime.getModels().size(), equalTo(1));
		IModel model = _runtime.getModels().iterator().next();
		assertThat(model, notNullValue());

		// will be null until we start
		assertThat(_runtime.getConnector().getAgent(model), nullValue());

		model.getProceduralModule().addListener(new ProceduralModuleListenerAdaptor() {

			@Override
			public void conflictSetAssembled(ProceduralModuleEvent pme) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("conflict set : " + pme.getProductions());
			}

			@Override
			public void productionWillFire(ProceduralModuleEvent pme) {
				IProduction production = pme.getProduction();
				LOGGER.debug(production + " is about to run, checking");
				testProduction(production);

			}
		}, ExecutorServices.INLINE_EXECUTOR);

		_controller.start().get();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("model run has started");

		_controller.complete().get();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Model run has completed");
		assertThat("Not all the productions have fired", _productionSequence.length, equalTo(_productionFireCount));

		model.dispose();
	}

	public void testProduction(IProduction production) {
		String pName = production.getSymbolicProduction().getName();

		for (String name : _ignoreProductions)
			if (name.equals(pName))
				return;

		// fail any in the _failedProductions
		for (String name : _failedProductions) {
			if (name.equals(pName)) {
				fail("production " + name + " should not have fired. should have been "
						+ _productionSequence[_productionFireCount]);
			}
		}
		if (!_productionSequence[_productionFireCount].equals(pName)) {
			fail(pName + " was fired out of sequence, expecting " + _productionSequence[_productionFireCount]);
		}
		_productionFireCount++;
	}
}

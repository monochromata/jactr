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

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.commonreality.agents.IAgent;
import org.commonreality.net.message.credentials.PlainTextCredentials;
import org.commonreality.net.protocol.IProtocolConfiguration;
import org.commonreality.net.provider.INetworkingProvider;
import org.commonreality.net.service.IClientService;
import org.commonreality.net.transport.ITransportProvider;
import org.commonreality.netty.NettyNetworkingProvider;
import org.commonreality.netty.protocol.NOOPProtocol;
import org.commonreality.netty.transport.LocalTransportProvider;
import org.commonreality.participant.impl.AbstractParticipant;
import org.commonreality.reality.IReality;
import org.commonreality.reality.control.RealitySetup;
import org.commonreality.reality.impl.DefaultReality;
import org.commonreality.sensors.ISensor;
import org.commonreality.sensors.xml.XMLSensor;
import org.jactr.core.concurrent.ExecutorServices;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.module.procedural.event.ProceduralModuleEvent;
import org.jactr.core.module.procedural.event.ProceduralModuleListenerAdaptor;
import org.jactr.core.production.IProduction;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.reality.connector.CommonRealityConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.DebugController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AuralModuleTest {
	/**
	 * logger definition
	 */
	static public final Log LOGGER = LogFactory.getLog(AuralModuleTest.class);

	ACTRRuntime _runtime;

	private IReality _reality;

	DebugController _controller;

	String[] _productionSequence = { "search-for-sound-succeeded", "encoding-correct", "heard-tone",
			"search-for-sound-succeeded", "encoding-correct", "heard-digit", "search-for-sound-succeeded",
			"encoding-correct", "heard-word", "search-for-sound-succeeded", "encoding-correct", "heard-speech" };

	String[] _failedProductions = { "encoding-failed", "encoding-incorrect-kind", "encoding-incorrect-content",
			"encoding-match-failed" };

	String[] _ignoreProductions = { "search-for-sound-failed", "search-for-sound" };

	int _productionFireCount = 0;

	@Before
	public void setUp() throws Exception {
		_runtime = ACTRRuntime.getRuntime();//new ACTRRuntime();
		_reality = getReality();
		_runtime.setConnector(new CommonRealityConnector());
		_runtime.setController(new DebugController());
		
		final IModel model = new AuralTestModelFactory().createAndInitializeModel();
		_runtime.addModel(model);
		
		final DefaultModelLogger logger = new DefaultModelLogger();
		logger.setParameter("all", "err");
		model.install(logger);
		
		_controller = (DebugController) _runtime.getController();
	}
	
	@After
	public void tearDown() {
		_runtime = null;
		_reality = null;
		_controller = null;
	}

	protected IReality getReality() throws Exception {
		INetworkingProvider netProvider = new NettyNetworkingProvider();
		IReality reality = createReality(netProvider);
		ISensor xmlSensor = createXMLSensor(netProvider);
		IAgent actrAgent = createACTRAgent(netProvider);
		new RealitySetup(reality, Arrays.asList(xmlSensor), Arrays.asList(actrAgent)).run();
		return reality;
	}

	protected IAgent createACTRAgent(INetworkingProvider netProvider) throws Exception {
		IAgent actrAgent = new ACTRAgent();
		ITransportProvider transport = createTransport(netProvider);
		((AbstractParticipant) actrAgent).addClientService(netProvider.newClient(), transport,
				createProtocol(netProvider), createAddress(transport));
		actrAgent.setCredentials(new PlainTextCredentials("agent", "pass"));
		Map<String, String> properties = new HashMap<>();
		properties.put("ACTRAgent.ModelName", "aural-test");
		actrAgent.configure(properties);
		return actrAgent;
	}

	protected ISensor createXMLSensor(INetworkingProvider netProvider) throws Exception {
		ISensor xmlSensor = new XMLSensor();
		final IClientService client = netProvider.newClient();
		final ITransportProvider transport = createTransport(netProvider);
		((AbstractParticipant) xmlSensor).addClientService(client, transport, createProtocol(netProvider),
				createAddress(transport));
		xmlSensor.setCredentials(new PlainTextCredentials("sensor", "pass"));
		Map<String, String> properties = new HashMap<>();
		properties.put("XMLSensor.DataURI", "org/jactr/modules/pm/aural/sensorData.xml");
		xmlSensor.configure(properties);
		return xmlSensor;
	}

	protected IReality createReality(INetworkingProvider netProvider) {
		IReality reality = new DefaultReality();
		final ITransportProvider transport = createTransport(netProvider);
		final SocketAddress address = transport.createAddress("1400");
		((AbstractParticipant) reality).addServerService(netProvider.newServer(), transport,
				createProtocol(netProvider), address);
		boolean wantsClockControl = false;
		reality.add(new PlainTextCredentials("sensor", "pass"), wantsClockControl);
		reality.add(new PlainTextCredentials("agent", "pass"), wantsClockControl);
		return reality;
	}

	protected SocketAddress createAddress(final ITransportProvider transport) {
		return transport.createAddress("1400");
	}

	protected IProtocolConfiguration createProtocol(INetworkingProvider netProvider) {
		return netProvider.getProtocol("protocol.noop");//NOOPProtocol.class.getName());
	}

	protected ITransportProvider createTransport(INetworkingProvider netProvider) {
		return netProvider.getTransport("transport.noop");//LocalTransportProvider.class.getName());
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

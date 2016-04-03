package org.jactr.modules;

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.commonreality.agents.IAgent;
import org.commonreality.net.message.credentials.PlainTextCredentials;
import org.commonreality.net.protocol.IProtocolConfiguration;
import org.commonreality.net.provider.INetworkingProvider;
import org.commonreality.net.service.IClientService;
import org.commonreality.net.transport.ITransportProvider;
import org.commonreality.netty.NettyNetworkingProvider;
import org.commonreality.participant.impl.AbstractParticipant;
import org.commonreality.reality.IReality;
import org.commonreality.reality.control.RealitySetup;
import org.commonreality.reality.impl.DefaultReality;
import org.commonreality.sensors.ISensor;
import org.commonreality.sensors.keyboard.DefaultKeyboardSensor;
import org.commonreality.sensors.xml.XMLSensor;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.models.IModelFactory;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.reality.connector.CommonRealityConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.debug.DebugController;
import org.junit.After;
import org.junit.Before;

/**
 * Provides utility methods for testing jACT-R modules.
 */
public abstract class AbstractModuleTest {

	protected ACTRRuntime _runtime;
	protected IReality _reality;
	protected DebugController _controller;

	@Before
	public void setUp() throws Exception {
		_runtime = ACTRRuntime.getRuntime(); // new ACTRRuntime();
		_reality = getReality();
		_runtime.setConnector(new CommonRealityConnector());
		_controller = new DebugController();
		_runtime.setController(_controller);
		final IModel model = createModelFactory().createAndInitializeModel();
		_runtime.addModel(model);

		final DefaultModelLogger logger = new DefaultModelLogger();
		logger.setParameter("all", "err");
		model.install(logger);
	}

	protected IReality getReality() throws Exception {
		final NettyNetworkingProvider netProvider = new NettyNetworkingProvider();
		final IReality reality = createReality(netProvider);
		final ISensor keyboardSensor = createSensor(netProvider);
		final IAgent actrAgent = createACTRAgent(netProvider, getModelName());
		setup(reality, keyboardSensor, actrAgent);
		return reality;
	}

	protected abstract IModelFactory createModelFactory();

	protected abstract ISensor createSensor(final INetworkingProvider netProvider) throws Exception;

	protected abstract String getModelName();

	@After
	public void tearDown() throws Exception {
		_runtime = null;
		_reality = null;
		_controller = null;
	}

	protected static IReality createReality(INetworkingProvider netProvider) {
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

	protected static SocketAddress createAddress(final ITransportProvider transport) {
		return transport.createAddress("1400");
	}

	protected static IProtocolConfiguration createProtocol(INetworkingProvider netProvider) {
		return netProvider.getProtocol("protocol.noop");// NOOPProtocol.class.getName());
	}

	protected static ITransportProvider createTransport(INetworkingProvider netProvider) {
		return netProvider.getTransport("transport.noop");// LocalTransportProvider.class.getName());
	}

	protected static IAgent createACTRAgent(INetworkingProvider netProvider, String modelName) throws Exception {
		IAgent actrAgent = new ACTRAgent();
		ITransportProvider transport = createTransport(netProvider);
		((AbstractParticipant) actrAgent).addClientService(netProvider.newClient(), transport,
				createProtocol(netProvider), createAddress(transport));
		actrAgent.setCredentials(new PlainTextCredentials("agent", "pass"));
		Map<String, String> properties = new HashMap<>();
		properties.put("ACTRAgent.ModelName", modelName);
		actrAgent.configure(properties);
		return actrAgent;
	}

	protected static ISensor createXMLSensor(INetworkingProvider netProvider, String sensorDataURI) throws Exception {
		Map<String, String> properties = new HashMap<>();
		properties.put("XMLSensor.DataURI", sensorDataURI);
		return configureSensor(netProvider, new XMLSensor(), properties);
	}

	protected static ISensor createKeyboardSensor(INetworkingProvider netProvider) throws Exception {
		return configureSensor(netProvider, new DefaultKeyboardSensor());
	}

	protected static ISensor configureSensor(INetworkingProvider netProvider, ISensor sensor) throws Exception {
		return configureSensor(netProvider, sensor, Collections.emptyMap());
	}

	protected static ISensor configureSensor(INetworkingProvider netProvider, ISensor sensor,
			Map<String, String> properties) throws Exception {
		final IClientService client = netProvider.newClient();
		final ITransportProvider transport = createTransport(netProvider);
		((AbstractParticipant) sensor).addClientService(client, transport, createProtocol(netProvider),
				createAddress(transport));
		sensor.setCredentials(new PlainTextCredentials("sensor", "pass"));
		sensor.configure(properties);
		return sensor;
	}

	/**
	 * Connects a reality, a sensor and an ACT-R agent.
	 * 
	 * @param reality
	 *            the reality
	 * @param sensor
	 *            the sensor
	 * @param actrAgent
	 *            the agent
	 */
	protected static void setup(IReality reality, ISensor sensor, IAgent actrAgent) {
		new RealitySetup(reality, Arrays.asList(sensor), Arrays.asList(actrAgent)).run();
	}

}
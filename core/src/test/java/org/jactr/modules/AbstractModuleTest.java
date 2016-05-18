package org.jactr.modules;

import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.commonreality.agents.IAgent;
import org.commonreality.net.message.credentials.PlainTextCredentials;
import org.commonreality.net.protocol.IProtocolConfiguration;
import org.commonreality.net.provider.INetworkingProvider;
import org.commonreality.net.service.IClientService;
import org.commonreality.net.transport.ITransportProvider;
import org.commonreality.netty.NettyNetworkingProvider;
import org.commonreality.participant.impl.AbstractParticipant;
import org.commonreality.reality.CommonReality;
import org.commonreality.reality.IReality;
import org.commonreality.reality.control.RealitySetup;
import org.commonreality.reality.impl.DefaultReality;
import org.commonreality.sensors.ISensor;
import org.commonreality.sensors.keyboard.DefaultKeyboardSensor;
import org.commonreality.sensors.speech.DefaultSpeechSensor;
import org.commonreality.sensors.xml.XMLSensor;
import org.jactr.core.logging.impl.DefaultModelLogger;
import org.jactr.core.model.IModel;
import org.jactr.core.models.IModelFactory;
import org.jactr.core.reality.ACTRAgent;
import org.jactr.core.reality.connector.CommonRealityConnector;
import org.jactr.core.runtime.ACTRRuntime;
import org.jactr.core.runtime.controller.IController;
import org.jactr.core.runtime.controller.debug.DebugController;
import org.junit.After;
import org.junit.Before;

/**
 * Provides utility methods for testing jACT-R modules.
 */
public abstract class AbstractModuleTest {

	protected static final boolean NO_CLOCK_CONTROL_DESIRED = false;
	protected ACTRRuntime _runtime;
	protected IReality _reality;
	protected CommonReality _cr;
	protected IController _controller;

	@Before
	public void setUp() throws Exception {
		getReality();
		_runtime = new ACTRRuntime(_cr, ACTRRuntime.DEFAULT_WORKING_DIRECTORY);
		_runtime.setConnector(new CommonRealityConnector(_runtime));
		_controller = createController();
		_runtime.setController(_controller);
		final IModel model = createModelFactory().createAndInitializeModel();
		_runtime.addModel(model);

		final DefaultModelLogger logger = new DefaultModelLogger(_runtime);
		logger.setParameter("all", "err");
		model.install(logger);
	}

	protected IController createController() {
		return new DebugController(_runtime);
	}

	protected void getReality() throws Exception {
		final NettyNetworkingProvider netProvider = new NettyNetworkingProvider();
		createReality(netProvider);
		final List<ISensor> sensor = createSensors(netProvider);
		final IAgent actrAgent = createACTRAgent(_cr, netProvider, getModelName());
		setup(_cr, sensor, actrAgent);
	}

	protected abstract IModelFactory createModelFactory();

	protected List<ISensor> createSensors(final INetworkingProvider netProvider) throws Exception {
		return Arrays.asList(createSensor(netProvider));
	}
	
	protected abstract ISensor createSensor(final INetworkingProvider netProvider) throws Exception;

	protected abstract String getModelName();

	@After
	public void tearDown() throws Exception {
		_runtime = null;
		_reality = null;
		_controller = null;
	}

	protected void createReality(INetworkingProvider netProvider) {
		_reality = DefaultReality.newInstanceThatNeedsToBePreparedWithACommonReality();
		_cr = new CommonReality(_reality);
		((DefaultReality)_reality).prepare(_cr);
		final ITransportProvider transport = createTransport(netProvider);
		final SocketAddress address = transport.createAddress("1400");
		((AbstractParticipant)_reality).addServerService(netProvider.newServer(), transport,
				createProtocol(netProvider), address);
		_reality.add(new PlainTextCredentials("sensor", "pass"), NO_CLOCK_CONTROL_DESIRED);
		_reality.add(new PlainTextCredentials("agent", "pass"), NO_CLOCK_CONTROL_DESIRED);
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

	protected static IAgent createACTRAgent(CommonReality cr, INetworkingProvider netProvider, String modelName) throws Exception {
		IAgent actrAgent = new ACTRAgent(cr);
		ITransportProvider transport = createTransport(netProvider);
		((AbstractParticipant) actrAgent).addClientService(netProvider.newClient(), transport,
				createProtocol(netProvider), createAddress(transport));
		actrAgent.setCredentials(new PlainTextCredentials("agent", "pass"));
		Map<String, String> properties = new HashMap<>();
		properties.put("ACTRAgent.ModelName", modelName);
		actrAgent.configure(properties);
		return actrAgent;
	}

	protected static ISensor createXMLSensor(CommonReality cr, INetworkingProvider netProvider, String sensorDataURI) throws Exception {
		Map<String, String> properties = new HashMap<>();
		properties.put("XMLSensor.DataURI", sensorDataURI);
		return configureSensor(netProvider, new XMLSensor(cr), properties);
	}
	
	protected static ISensor createSpeechSensor(CommonReality cr, INetworkingProvider netProvider) throws Exception {
		return configureSensor(netProvider, new DefaultSpeechSensor(cr));
	}

	protected static ISensor createKeyboardSensor(CommonReality cr, INetworkingProvider netProvider) throws Exception {
		return configureSensor(netProvider, new DefaultKeyboardSensor(cr));
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
	protected static void setup(CommonReality reality, List<ISensor> sensors, IAgent actrAgent) {
		new RealitySetup(reality, sensors, Arrays.asList(actrAgent)).run();
	}

}
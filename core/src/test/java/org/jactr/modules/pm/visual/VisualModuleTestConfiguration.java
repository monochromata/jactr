package org.jactr.modules.pm.visual;

import java.util.Arrays;
import java.util.List;

import org.commonreality.net.message.credentials.PlainTextCredentials;
import org.commonreality.net.provider.INetworkingProvider;
import org.commonreality.reality.IReality;
import org.commonreality.sensors.ISensor;
import org.jactr.core.models.IModelFactory;
import org.jactr.core.runtime.controller.DefaultController;
import org.jactr.core.runtime.controller.IController;
import org.jactr.modules.AbstractModuleTest;

public abstract class VisualModuleTestConfiguration extends AbstractModuleTest {

	public VisualModuleTestConfiguration() {
		super();
	}

	@Override
	protected IModelFactory createModelFactory() {
		return new VisualModuleTestFactory();
	}

	@Override
	protected IController createController() {
		return new DefaultController();
	}

	@Override
	protected IReality createReality(INetworkingProvider netProvider) {
		final IReality reality = super.createReality(netProvider);
		reality.add(new PlainTextCredentials("sensor2", "pass"), NO_CLOCK_CONTROL_DESIRED);
		return reality;
	}

	@Override
	protected List<ISensor> createSensors(INetworkingProvider netProvider) throws Exception {
		return Arrays.asList(
				createXMLSensor(netProvider, "org/jactr/modules/pm/visual/sensorData.xml"),
				setCredentials(createSpeechSensor(netProvider))); // using sensor:pass instead of sensor2:pass for the 2nd sensor, too
	}

	protected ISensor setCredentials(ISensor sensor) {
		sensor.setCredentials(new PlainTextCredentials("sensor2", "pass"));
		return sensor;
	}

	@Override
	protected ISensor createSensor(INetworkingProvider netProvider) throws Exception {
		throw new UnsupportedOperationException("Use createSensors(INetworkingProvider) instead");
	}

	@Override
	protected String getModelName() {
		return "visual-test";
	}

}
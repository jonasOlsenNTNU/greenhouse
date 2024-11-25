package no.ntnu.controlpanel;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class ControlPanelLogicTest {

    private ControlPanelLogic logic;
    private GreenhouseEventListener listenerMock;
    private CommunicationChannel communicationChannelMock;
    private CommunicationChannelListener communicationChannelListenerMock;
    private SensorActuatorNodeInfo nodeInfo;

    @BeforeEach
    void setUp(){
        logic =  new ControlPanelLogic();
        listenerMock = mock(GreenhouseEventListener.class);
        communicationChannelMock = mock(CommunicationChannel.class);
        communicationChannelListenerMock = mock(CommunicationChannelListener.class);
    }

    @Test
    void TestAddListener() {
        logic.addListener(listenerMock);
        logic.onNodeAdded(new SensorActuatorNodeInfo(1));
        verify(listenerMock, times(1)).onNodeAdded(any(SensorActuatorNodeInfo.class));
    }

    @Test
    void TestOnNodeAdded() {
        logic.addListener(listenerMock);
        logic.onNodeAdded(new SensorActuatorNodeInfo(2));
        logic.onNodeAdded(nodeInfo);
        verify(listenerMock).onNodeAdded(eq(nodeInfo));
    }

    @Test
    void testOnNodeRemoved() {
        logic.addListener(listenerMock);
        logic.onNodeAdded(new SensorActuatorNodeInfo(2));
        logic.onNodeRemoved(2);
        verify(listenerMock).onNodeRemoved(2);
    }

    @Test
    void TestOnSensorData() {
        logic.addListener(listenerMock);
        List<SensorReading> validReadings = new ArrayList<>();
        validReadings.add(new SensorReading("temperature", 22.5 , "Celsius"));

        logic.onSensorData(4, validReadings);
        verify(listenerMock).onSensorData(4, validReadings);

        List<SensorReading> invalidReadings = new ArrayList<>();
        invalidReadings.add(new SensorReading("humidity", 22.5, "Celsius"));

        logic.onSensorData(5, invalidReadings);
        assertThrows(IllegalArgumentException.class, () -> logic.onSensorData(5, invalidReadings));
    }

    @Test
    void testOnActuatorStateChanged() {
        logic.addListener(listenerMock);

        logic.onActuatorStateChanged(1,1,false);
        verify(listenerMock).onActuatorStateChanged(eq(1),eq(1),eq(false));

        assertThrows(NumberFormatException.class, ()
                -> logic.onActuatorStateChanged(Integer.parseInt("abc"),1,true));
    }

    @Test
    void testActuatorUpdated() {
        logic.setCommunicationChannel(communicationChannelMock);
        logic.addListener(listenerMock);

        Actuator actuator1 = new Actuator(2,"window", 1);
        actuator1.toggle();
        logic.actuatorUpdated(1, actuator1);
        verify(communicationChannelMock).sendActuatorChange(eq(1), eq(2),eq(true));
        verify(listenerMock).onActuatorStateChanged(eq(1),eq(2),eq(true));
    }

    @Test
    void onCommunicationChannelClosed() {
    }
}
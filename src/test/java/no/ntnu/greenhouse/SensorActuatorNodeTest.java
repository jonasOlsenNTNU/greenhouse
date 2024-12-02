package no.ntnu.greenhouse;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.listeners.greenhouse.SensorListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SensorActuatorNodeTest {
    private SensorActuatorNode node;
    private ActuatorListener actuatorListenerMock;
    private CommunicationChannelListener communicationChannelListenerMock;
    private NodeStateListener nodeStateListenerMock;
    private SensorListener sensorListenerMock;

    @BeforeEach
    void setUp() {
        node = new SensorActuatorNode(1);
        actuatorListenerMock = mock(ActuatorListener.class);
        communicationChannelListenerMock = mock(CommunicationChannelListener.class);
        nodeStateListenerMock = mock(NodeStateListener.class);
        sensorListenerMock = mock(SensorListener.class);

//        NodeCommunicationChannel communicationChannelMock = mock(NodeCommunicationChannel.class);
//        node.setCommunicationChannel(communicationChannelMock);

        node.addActuatorListener(actuatorListenerMock);
        node.addStateListener(nodeStateListenerMock);
        node.addSensorListener(sensorListenerMock);
    }
    @Test
    void testAddSensors(){
        Sensor sensorTemplate = mock(Sensor.class);
        when(sensorTemplate.createClone()).thenReturn(sensorTemplate);
        when(sensorTemplate.getType()).thenReturn("temperature");

        node.addSensors(sensorTemplate, 3);
        List<Sensor> sensors = node.getSensors();
        assertEquals(3, sensors.size());
    }
    @Test
    void testAddActuators(){
        Actuator actuator = mock(Actuator.class);
        node.addActuator(actuator);

        verify(actuator).setListener(node);
        assertEquals(1, node.getActuators().size());
    }
//    @Test
//    void testStartAndStop(){
//        node.start();
//        assertTrue(node.isRunning());
//        verify(nodeStateListenerMock).onNodeReady(node);
//
//        node.stop();
//        assertFalse(node.isRunning());
//        verify(nodeStateListenerMock).onNodeStopped(node);
//    }
    @Test
    void testToggleActuator(){
        Actuator actuator = mock(Actuator.class);
        when(actuator.getId()).thenReturn(1);
        node.addActuator(actuator);

        node.toggleActuator(1);
        verify(actuator).toggle();
    }
    @Test
    void testSetActuator(){
        Actuator actuator = mock(Actuator.class);
        when(actuator.getId()).thenReturn(1);
        node.addActuator(actuator);

        node.setActuator(1,true);
        verify(actuator).set(true);
    }
//    @Test
//    void testGenerateNewSensorValues() {
//        Sensor sensor = mock(Sensor.class);
//        when(sensor.getType()).thenReturn("temperature");
//        node.addSensors(sensor, 1);
//
//        node.generateNewSensorValues();
//        verify(sensor).addRandomNoise();
//        verify(sensorListenerMock).sensorsUpdated(node.getSensors());
//    }

//    @Test
//    void testApplyActuatorImpact() {
//        Sensor sensor = mock(Sensor.class);
//        when(sensor.getType()).thenReturn("temperature");
//        node.addSensors(sensor, 1);
//
//        node.applyActuatorImpact("temperature", 5.0);
//        verify(sensor).applyImpact(5.0);
//    }

    @Test
    void testOnCommunicationChannelClosed() {
        node.onCommunicationChannelClosed();
        verify(communicationChannelListenerMock, never()).onCommunicationChannelClosed();
        assertFalse(node.isRunning());
    }
}
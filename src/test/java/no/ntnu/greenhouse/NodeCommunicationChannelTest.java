package no.ntnu.greenhouse;

import no.ntnu.message.Splitters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.mockito.Mockito.*;

class NodeCommunicationChannelTest {
    private NodeCommunicationChannel communicationChanel;
    private SensorActuatorNode nodeMock;
    private PrintWriter socketWriterMock;

    @BeforeEach
    void setUp() {
        nodeMock = mock(SensorActuatorNode.class);
        communicationChanel = spy(new NodeCommunicationChannel(nodeMock));

        socketWriterMock = mock(PrintWriter.class);
        communicationChanel.socketWriter = socketWriterMock;
    }
    @Test
    void testSendDisconnectMessage(){
        communicationChanel.sendConnectionMessage(false);

        verify(socketWriterMock).println("server#NodeConnectionMessage!false*0");
    }
    @Test
    void testSendNodeInfo(){
        Actuator actuatorMock = mock(Actuator.class);
        when(nodeMock.getId()).thenReturn(1);
        when(actuatorMock.getId()).thenReturn(1);
        when(actuatorMock.isOn()).thenReturn(false);

        ActuatorCollection actuators = new ActuatorCollection();
        actuators.add(actuatorMock);

        when(nodeMock.getActuators()).thenReturn(actuators);

        String clientHandlerId = "client-123";
        communicationChanel.sendNodeInfo(clientHandlerId);

        String expectedMessage = "controlpanel,client-123#AddNodeMessage!1*1,null,false";
        verify(socketWriterMock).println(expectedMessage);
    }

    @Test
    void testActuatorUpdated(){
        Actuator actuatorMock = mock(Actuator.class);
        when(actuatorMock.getId()).thenReturn(2);
        when(actuatorMock.isOn()).thenReturn(true);

        communicationChanel.actuatorUpdated(1,actuatorMock);

        String expectedMessage = "controlpanel#ActuatorUpdateMessage!0*2*true";
        verify (socketWriterMock).println(expectedMessage);

    }
    @Test
    void testSendConnectionMessage() {
        communicationChanel.sendConnectionMessage(true);
        String expectedConnectMessage = "server" + Splitters.MESSAGE_SPLITTER + "NodeConnectionMessage"
                + Splitters.TYPE_SPLITTER + "true"
                + "*" + nodeMock.getId();
        String expectedDisconnectMessage = "server" + Splitters.MESSAGE_SPLITTER + "NodeConnectionMessage"
                + Splitters.TYPE_SPLITTER + "false"
                + "*" + nodeMock.getId();
        verify(socketWriterMock).println(expectedConnectMessage);

        communicationChanel.sendConnectionMessage(false);
        verify(socketWriterMock).println(expectedDisconnectMessage);
    }
    @Test
    void testOnNodeReady(){
        Actuator actuatorMock = mock(Actuator.class);
        when(actuatorMock.getId()).thenReturn(3);
        when(actuatorMock.getType()).thenReturn("window");
        when(actuatorMock.isOn()).thenReturn(true);

        ActuatorCollection actuator = new ActuatorCollection();
        actuator.add(actuatorMock);
        when(nodeMock.getActuators()).thenReturn(actuator);

        communicationChanel.onNodeReady(nodeMock);

        String expectedMessage = "controlpanel#AddNodeMessage!0*3,window,true";
        verify(socketWriterMock).println(expectedMessage);
    }
    @Test
    void testSensorsUpdated() {
        Sensor sensorMock = mock(Sensor.class);
        when(sensorMock.getType()).thenReturn("temperature");
        when(sensorMock.getReading()).thenReturn(new SensorReading("temperature",22.5,"Celsius"));

        List<Sensor> sensors = List.of(sensorMock);
        communicationChanel.sensorsUpdated(sensors);

        String expectedMessage = "controlpanel#SensorUpdateMessage!0*temperature,22.5,Celsius,";
        verify(socketWriterMock).println(expectedMessage);
    }
}
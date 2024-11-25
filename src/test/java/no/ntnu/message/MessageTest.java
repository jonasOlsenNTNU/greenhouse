package no.ntnu.message;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.message.controlpanel.ActuatorChangeMessage;
import no.ntnu.message.controlpanel.ControlPanelConnectionMessage;
import no.ntnu.message.controlpanel.RequestNodesMessage;
import no.ntnu.message.greenhouse.ActuatorUpdateMessage;
import no.ntnu.message.greenhouse.AddNodeMessage;
import no.ntnu.message.greenhouse.SensorUpdateMessage;
import no.ntnu.message.server.RemoveNodeMessage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

class MessageTest {

    @Test
    void testActuatorChangeMessage(){
        ActuatorChangeMessage message = new ActuatorChangeMessage(1,2,true);
        String expected = "node,1#ActuatorChangeMessage!2\\*true";
        assertEquals(expected,message.getMessageString());
    }
    @Test
    void testControlPanelConnectionMessage(){
        ControlPanelConnectionMessage connectMessage = new ControlPanelConnectionMessage(true);
        String expectedConnectMessage = "server#ControlPanelConnectionMessage!true";
        assertEquals(expectedConnectMessage, connectMessage.getMessageString());

        ControlPanelConnectionMessage disconnectMessage = new ControlPanelConnectionMessage(false);
        String expectedDisconnectMessage = "server#ControlPanelConnectionMessage!false";
        assertEquals(expectedDisconnectMessage, disconnectMessage.getMessageString());
    }
    @Test
    void testRequestNodesMessage(){
        RequestNodesMessage requestNodesMessage = new RequestNodesMessage();
        String expectedRequest = "server#RequestNodesMessage";
        assertEquals(expectedRequest, requestNodesMessage.getMessageString());
    }
    @Test
    void testActuatorUpdateMessage(){
        ActuatorUpdateMessage message = new ActuatorUpdateMessage(1,2,true);
        String expected = "controlpanel#ActuatorUpdateMessage!1\\*2\\*true";
        assertEquals(expected, message.getMessageString());
    }
    @Test
    void testAddNodeMessage(){
        Actuator actuator1 = new Actuator(1,"window",1);
        Actuator actuator2 = new Actuator(2,"fan",1);
        ActuatorCollection actuatorsTestCollection = new ActuatorCollection();
        actuatorsTestCollection.add(actuator1);
        actuatorsTestCollection.add(actuator2);

        AddNodeMessage message = new AddNodeMessage(1,actuatorsTestCollection);
        String expected = "controlpanel#AddNodeMessage!1\\*1,window,false;2,fan,false";
        assertEquals(expected, message.getMessageString());
    }
    @Test
    void testSensorUpdateMessage(){
        Sensor sensor1 = new Sensor("temperature",0,100,25.5,"Celsius");
        Sensor sensor2 = new Sensor("humidity", 0, 100, 45.0,"Percent");
        List<Sensor> sensorCollection =  new ArrayList<>();
        sensorCollection.add(sensor1);
        sensorCollection.add(sensor2);

        SensorUpdateMessage sensorUpdateMessage = new SensorUpdateMessage(1,sensorCollection);
        String expected = "controlpanel#SensorUpdateMessage!1\\*temperature,25.5,Celsius,;humidity,45.0,Percent,";
        assertEquals(expected, sensorUpdateMessage.getMessageString());
    }
    @Test
    void testRemoveNodeMessage(){
        RemoveNodeMessage removeNodeMessage = new RemoveNodeMessage(1);
        String expected = "controlpanel#RemoveNodeMessage!1";
        assertEquals(expected, removeNodeMessage.getMessageString());
    }
}

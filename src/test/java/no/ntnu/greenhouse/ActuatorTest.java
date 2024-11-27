package no.ntnu.greenhouse;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.tools.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ActuatorTest {
    @Test
    void testActuatorInitialization() {
        Actuator actuator = new Actuator(1,"heater",2);
        Logger.info("Actuator : " + actuator);
        assertEquals("heater", actuator.getType());
        assertEquals(2, actuator.getNodeId());
        assertEquals(1,actuator.getId());
        assertFalse(actuator.isOn());
    }
    @Test
    void testToggle(){
        Actuator actuator = new Actuator(1,"heater",2);
        actuator.toggle();
        assertTrue(actuator.isOn());
    }
    @Test
    void testTurnOnAndOff(){
        Actuator actuator = new Actuator(1,"heater",2);
        actuator.turnOn();
        assertTrue(actuator.isOn());
        actuator.turnOff();
        assertFalse(actuator.isOn());
    }
    @Test
    void testListenerNotification(){
        ActuatorListener listenerMock = mock(ActuatorListener.class);
        Actuator actuator  = new Actuator(1,"heater",2);
        actuator.setListener(listenerMock);
        actuator.toggle();
        verify(listenerMock).actuatorUpdated(eq(2), eq(actuator));
    }

    @Test
    void testApplyImpact(){
        SensorActuatorNode nodeMock = mock(SensorActuatorNode.class);
        Actuator actuator1 = new Actuator(1,"heater",2);
        actuator1.setImpact("temperature",5.0);

        actuator1.turnOn();
        actuator1.applyImpact(nodeMock);
        verify(nodeMock).applyActuatorImpact("temperature", 5);

        actuator1.turnOff();
        actuator1.applyImpact(nodeMock);
        verify(nodeMock).applyActuatorImpact("temperature", -5);
    }

    @Test
    void testCreateClone(){
        Actuator actuator1 = new Actuator(1,"heater",2);
        actuator1.setImpact("brightness",10.0);
        Actuator clone = actuator1.createClone();
        assertEquals(actuator1.getId(), clone.getId());
        assertEquals(actuator1.getType(), clone.getType());
        assertEquals(actuator1.getNodeId(), clone.getNodeId());
        assertFalse(actuator1.isOn());
        assertFalse(clone.isOn());
    }


}
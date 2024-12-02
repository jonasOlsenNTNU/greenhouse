package no.ntnu.controlpanel;

import no.ntnu.greenhouse.SensorReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ControlPanelMessageHandlerTest {
  private ControlPanelMessageHandler messageHandler;
  private ControlPanelLogic logicMock;
  @BeforeEach
    void setUp() {
        logicMock = mock(ControlPanelLogic.class);
        messageHandler = new ControlPanelMessageHandler(logicMock);
    }
  @Test
  void testAddNodeMessage() {
    ControlPanelLogic logic = mock(ControlPanelLogic.class);
    ControlPanelMessageHandler handler = new ControlPanelMessageHandler(logic);

    String validMessage = "AddNodeMessage!1*2,window,true;3,fan,false;";
    handler.handleMessage(validMessage);
    verify(logic).onNodeAdded(any(SensorActuatorNodeInfo.class));

    String invalidMessage = "AddNodeMessage!abc*2,window,true;3,fan,false;";
    assertThrows(NumberFormatException.class, () -> handler.handleMessage(invalidMessage));
  }
  @Test
  void testRemoveNodeMessage() {
    ControlPanelLogic logic = mock(ControlPanelLogic.class);
    ControlPanelMessageHandler handler = new ControlPanelMessageHandler(logic);

    String validMessage = "RemoveNodeMessage!1";

    messageHandler.handleMessage(validMessage);
    verify(logicMock).onNodeRemoved(1);

    String invalidMessage = "RemoveNodeMessage!abc";
      assertThrows(NumberFormatException.class, () -> handler.handleMessage(invalidMessage));
  }
  @Test
  void testActuatorUpdateMessage(){
      ControlPanelLogic logic = mock(ControlPanelLogic.class);
      ControlPanelMessageHandler handler = new ControlPanelMessageHandler(logic);

      String validMessage = "ActuatorUpdateMessage!1*2*true";
      handler.handleMessage(validMessage);

      verify(logic).onActuatorStateChanged(eq(1), eq(2), eq(true));

      String invalidMessage = "ActuatorUpdateMessage!abc*2*true";
      assertThrows(NumberFormatException.class, () -> handler.handleMessage(invalidMessage));
  }
  @Test
  void testSensorUpdateMessage(){
      ControlPanelLogic logicMock = mock(ControlPanelLogic.class);
      ControlPanelMessageHandler handler = new ControlPanelMessageHandler(logicMock);

      String validMessage = "SensorUpdateMessage!1*temperature,25.5,Celsius;humidity,45.0,Percent";
      handler.handleMessage(validMessage);

      ArrayList<SensorReading> expectedReadings = new ArrayList<>();
      expectedReadings.add(new SensorReading("temperature", 25.5, "Celsius"));
      expectedReadings.add(new SensorReading("humidity", 45.0, "Percent"));

      verify(logicMock).onSensorData(eq(1), eq(expectedReadings));

      String invalidMessage = "SensorUpdateMessage!1*humidity,25.5,Celsius";
      handler.handleMessage(invalidMessage);

      assertThrows(IllegalArgumentException.class, () -> handler.handleMessage(invalidMessage));
  }

}
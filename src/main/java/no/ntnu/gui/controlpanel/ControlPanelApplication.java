package no.ntnu.gui.controlpanel;


import java.util.*;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.SensorActuatorNodeInfo;
import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.gui.common.ActuatorPane;
import no.ntnu.gui.common.SensorPane;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.controlpanel.GreenhouseEventListener;
import no.ntnu.tools.Logger;

/**
 * Run a control panel with a graphical user interface (GUI), with JavaFX.
 */
public class ControlPanelApplication extends Application implements GreenhouseEventListener,
    CommunicationChannelListener {
  private static ControlPanelLogic logic;
  private static final int WIDTH = 500;
  private static final int HEIGHT = 400;
  private static CommunicationChannel channel;

  private TabPane nodeTabPane;
  private Scene mainScene;
  private final Map<Integer, SensorPane> sensorPanes = new HashMap<>();
  private final Map<Integer, ActuatorPane> actuatorPanes = new HashMap<>();
  private final Map<Integer, SensorActuatorNodeInfo> nodeInfo = new HashMap<>();
  private final Map<Integer, Tab> nodeTabs = new HashMap<>();
  private VBox globalTabVBox;

  private Set<String> actuatorTypes = new HashSet<>();
  /**
   * Application entrypoint for the GUI of a control panel.
   * Note - this is a workaround to avoid problems with JavaFX not finding the modules!
   * We need to use another wrapper-class for the debugger to work.
   *
   * @param logic   The logic of the control panel node
   * @param channel Communication channel for sending control commands and receiving events
   */
  public static void startApp(ControlPanelLogic logic, CommunicationChannel channel) {
    if (logic == null) {
      throw new IllegalArgumentException("Control panel logic can't be null");
    }
    ControlPanelApplication.logic = logic;
    ControlPanelApplication.channel = channel;
    Logger.info("Running control panel GUI...");
    launch();
  }

  @Override
  public void start(Stage stage) {
    if (channel == null) {
      throw new IllegalStateException(
          "No communication channel. See the README on how to use fake event spawner!");
    }

    stage.setMinWidth(WIDTH);
    stage.setMinHeight(HEIGHT);
    stage.setTitle("Control panel");
    mainScene = new Scene(createEmptyContent(), WIDTH, HEIGHT);
    stage.setScene(mainScene);
    stage.show();
    logic.addListener(this);
    logic.setCommunicationChannelListener(this);
    if (!channel.open()) {
      logic.onCommunicationChannelClosed();
    }
    channel.sendControlPanelConnectionMessage(true);
    channel.sendNodeRequestMessage();
  }

  private static Label createEmptyContent() {
    Label l = new Label("Waiting for node data...");
    l.setAlignment(Pos.CENTER);
    return l;
  }

  /**
   * Method called when a new node is added to the control panel UI. It adds a new tab for the
   * node and sets listeners for the actuators of the node.
   *
   * @param nodeInfo A SensorActuatorNodeInfo object containing information about the new node
   */
  @Override
  public void onNodeAdded(SensorActuatorNodeInfo nodeInfo) {
    Platform.runLater(() -> {
      addNodeTab(nodeInfo);
      for (Actuator actuator : nodeInfo.getActuators()) {
        actuator.setListener(logic);
        String type = actuator.getType();
        if (actuatorTypes.add(type)) { // Adds type if not present, returns true if it's new.
          Logger.info("Adding new actuator type to Global tab: " + type);
          addGlobalActuatorControls(type);
        }
      }
    });
  }

  @Override
  public void onNodeRemoved(int nodeId) {
    Tab nodeTab = nodeTabs.get(nodeId);
    if (nodeTab != null) {
      Platform.runLater(() -> {
        removeNodeTab(nodeId, nodeTab);
        forgetNodeInfo(nodeId);
        if (nodeInfo.isEmpty()) {
          removeNodeTabPane();
        }
      });
      Logger.info("Node " + nodeId + " removed");
    } else {
      Logger.error("Can't remove node " + nodeId + ", there is no Tab for it");
    }
  }

  private void removeNodeTabPane() {
    mainScene.setRoot(createEmptyContent());
    nodeTabPane = null;
  }

  @Override
  public void onSensorData(int nodeId, List<SensorReading> sensors) {
    Logger.info("Sensor data from node " + nodeId);
    SensorPane sensorPane = sensorPanes.get(nodeId);
    if (sensorPane != null) {
      sensorPane.update(sensors);
    } else {
      Logger.error("No sensor section for node " + nodeId);
    }
  }

  @Override
  public void onActuatorStateChanged(int nodeId, int actuatorId, boolean isOn) {
    String state = isOn ? "ON" : "off";
    Logger.info("actuator[" + actuatorId + "] on node " + nodeId + " is " + state);
    ActuatorPane actuatorPane = actuatorPanes.get(nodeId);
    if (actuatorPane != null) {
      Actuator actuator = getStoredActuator(nodeId, actuatorId);
      if (actuator != null) {
        if (isOn) {
          actuator.turnOn();
        } else {
          actuator.turnOff();
        }
        actuatorPane.update(actuator);
      } else {
        Logger.error(" actuator not found");
      }
    } else {
      Logger.error("No actuator section for node " + nodeId);
    }
  }

  @Override
  public void onActuatorTypeToggle(String type, boolean isOn) {
    channel.sendBroadcastActuatorChange(type,isOn);
  }


  private Actuator getStoredActuator(int nodeId, int actuatorId) {
    Actuator actuator = null;
    SensorActuatorNodeInfo nodeInfo = this.nodeInfo.get(nodeId);
    if (nodeInfo != null) {
      actuator = nodeInfo.getActuator(actuatorId);
    }
    return actuator;
  }

  private void forgetNodeInfo(int nodeId) {
    sensorPanes.remove(nodeId);
    actuatorPanes.remove(nodeId);
    nodeInfo.remove(nodeId);
  }

  private void removeNodeTab(int nodeId, Tab nodeTab) {
    nodeTab.getTabPane().getTabs().remove(nodeTab);
    nodeTabs.remove(nodeId);
  }

  private void addNodeTab(SensorActuatorNodeInfo nodeInfo) {
    if (nodeTabPane == null) {
      nodeTabPane = new TabPane();
      mainScene.setRoot(nodeTabPane);
      nodeTabPane.getTabs().add(createGlobalTab());
    }
    Tab nodeTab = nodeTabs.get(nodeInfo.getId());
    if (nodeTab == null) {
      this.nodeInfo.put(nodeInfo.getId(), nodeInfo);
      nodeTabPane.getTabs().add(createNodeTab(nodeInfo));
    } else {
      Logger.info("Duplicate node spawned, ignore it");
    }
  }

  private Tab createNodeTab(SensorActuatorNodeInfo nodeInfo) {
    Tab tab = new Tab("Node " + nodeInfo.getId());
    SensorPane sensorPane = createEmptySensorPane();
    sensorPanes.put(nodeInfo.getId(), sensorPane);
    ActuatorPane actuatorPane = new ActuatorPane(nodeInfo.getActuators());
    actuatorPanes.put(nodeInfo.getId(), actuatorPane);
    tab.setContent(new VBox(sensorPane, actuatorPane));
    nodeTabs.put(nodeInfo.getId(), tab);
    return tab;
  }
  private static SensorPane createEmptySensorPane() {
    return new SensorPane();
  }
  private Tab createGlobalTab() {
    Tab tab = new Tab("Global");
    globalTabVBox = new VBox();
    globalTabVBox.setSpacing(20); // Sets spacing between each HBox containing buttons.
    globalTabVBox.setPadding(new Insets(20, 20, 20, 20)); // Adds padding around the VBox (top, right, bottom, left).

    // Initial actuator buttons.
    for (String type : actuatorTypes) {
      addGlobalActuatorControls(type);
    }

    tab.setContent(globalTabVBox);
    return tab;
  }
  private void addGlobalActuatorControls(String type) {
    HBox buttonBox = new HBox();
    buttonBox.setSpacing(10); // Sets spacing between elements in the HBox.

    Label typeLabel = new Label(type + "s : ");

    Button turnOnButton = new Button("On");
    turnOnButton.setOnAction(event -> {
      Logger.info("Broadcasting Turn On for " + type);
      logic.onActuatorTypeToggle(type, true);
    });

    Button turnOffButton = new Button("Off");
    turnOffButton.setOnAction(event -> {
      Logger.info("Broadcasting Turn Off for " + type);
      logic.onActuatorTypeToggle(type, false);
    });

    buttonBox.getChildren().addAll(typeLabel, turnOnButton, turnOffButton);
    globalTabVBox.getChildren().add(buttonBox);
  }

  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication closed, closing the GUI");
    Platform.runLater(Platform::exit);
  }
}

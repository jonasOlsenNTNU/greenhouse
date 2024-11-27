package no.ntnu.greenhouse;

import java.util.*;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.listeners.greenhouse.SensorListener;
import no.ntnu.message.greenhouse.AddNodeMessage;
import no.ntnu.tools.Logger;

/**
 * Represents one node with sensors and actuators.
 */
public class SensorActuatorNode implements ActuatorListener, CommunicationChannelListener {
  // How often to generate new sensor values, in seconds.
  private static final long SENSING_DELAY = 5000;
  public final int nodeId;

  private final List<Sensor> sensors = new LinkedList<>();
  public final ActuatorCollection actuators = new ActuatorCollection();

  private final List<SensorListener> sensorListeners = new LinkedList<>();
  private final List<ActuatorListener> actuatorListeners = new LinkedList<>();
  private final List<NodeStateListener> stateListeners = new LinkedList<>();
  private final NodeCommunicationChannel communicationChannel = new NodeCommunicationChannel(this);

  private Timer sensorReadingTimer;

  private boolean running;
  private final Random random = new Random();

  /**
   * Create a sensor/actuator node. Note: the node itself does not check whether the ID is unique.
   * This is done at the greenhouse-level.
   *
   * @param id A unique ID of the node
   */
  public SensorActuatorNode(int id) {
    this.nodeId = id;
    this.running = false;
    this.addSensorListener(communicationChannel);
    this.addActuatorListener(communicationChannel);
    this.addStateListener(communicationChannel);
  }
  /** //TODO
   * Find and return a list of actuators based on the given type.
   *
   * @param type The type of actuators to search for.
   * @return A list of Actuator objects with the specified type.
   */
  public List<Actuator> findActuatorByType(String type){
    Logger.info("The type that is being searched for is " + type);
    List<Actuator> matchingActuators = new ArrayList<>();
    for (Actuator actuator : actuators) {
      Logger.info("Actuators being searched for are " + actuator.getType());
      if (actuator.getType().equalsIgnoreCase((type))) {
        Logger.info("The list of matching actuators is: " + matchingActuators);
        matchingActuators.add(actuator);
      }
    }
    return matchingActuators;
  }
  /**
   * Get the unique ID of the node.
   *
   * @return the ID
   */
  public int getId() {
    return nodeId;
  }

  /**
   * Add sensors to the node.
   *
   * @param template The template to use for the sensors. The template will be cloned.
   *                 This template defines the type of sensors, the value range, value
   *                 generation algorithms, etc.
   * @param n        The number of sensors to add to the node.
   */
  public void addSensors(Sensor template, int n) {
    if (template == null) {
      throw new IllegalArgumentException("Sensor template is missing");
    }
    String type = template.getType();
    if (type == null || type.isEmpty()) {
      throw new IllegalArgumentException("Sensor type missing");
    }
    if (n <= 0) {
      throw new IllegalArgumentException("Can't add a negative number of sensors");
    }

    for (int i = 0; i < n; ++i) {
      sensors.add(template.createClone());
    }
  }

  /**
   * Add an actuator to the node.
   *
   * @param actuator The actuator to add
   */
  public void addActuator(Actuator actuator) {
    actuator.setListener(this);
    actuators.add(actuator);
    Logger.info("Created " + actuator.getType() + "[" + actuator.getId() + "] on node " + nodeId);
  }

  /**
   * Register a new listener for sensor updates.
   *
   * @param listener The listener which will get notified every time sensor values change.
   */
  public void addSensorListener(SensorListener listener) {
    if (!sensorListeners.contains(listener)) {
      sensorListeners.add(listener);
    }
  }

  /**
   * Register a new listener for actuator updates.
   *
   * @param listener The listener which will get notified every time actuator state changes.
   */
  public void addActuatorListener(ActuatorListener listener) {
    if (!actuatorListeners.contains(listener)) {
      actuatorListeners.add(listener);
    }
  }

  /**
   * Register a new listener for node state updates.
   *
   * @param listener The listener which will get notified when the state of this node changes
   */
  public void addStateListener(NodeStateListener listener) {
    if (!stateListeners.contains(listener)) {
      stateListeners.add(listener);
    }
  }


  /**
   * Start simulating the sensor node's operation.
   */
  public void start() {
    if (!running) {
      startPeriodicSensorReading();
      running = true;
      notifyStateChanges(true);
    }
  }

  /**
   * Initiate communication with the server.
   */
  public void startCommunication() {
    this.communicationChannel.open();
    this.communicationChannel.sendConnectionMessage(true);
  }

  /**
   * Stop communication with the server.
   */
  public void stopCommunication() {
    this.communicationChannel.sendConnectionMessage(false);
    this.communicationChannel.closeConnection();
  }

  /**
   * Stop simulating the sensor node's operation.
   */
  public void stop() {
    if (running) {
      Logger.info("-- Stopping simulation of node " + nodeId);
      stopPeriodicSensorReading();
      running = false;
      notifyStateChanges(false);
    }
  }

  /**
   * Check whether the node is currently running.
   *
   * @return True if it is in a running-state, false otherwise
   */
  public boolean isRunning() {
    return running;
  }

  private void startPeriodicSensorReading() {
    sensorReadingTimer = new Timer();
    TimerTask newSensorValueTask = new TimerTask() {
      @Override
      public void run() {
        generateNewSensorValues();
      }
    };
    long randomStartDelay = random.nextLong(SENSING_DELAY);
    sensorReadingTimer.scheduleAtFixedRate(newSensorValueTask, randomStartDelay, SENSING_DELAY);
  }

  private void stopPeriodicSensorReading() {
    if (sensorReadingTimer != null) {
      sensorReadingTimer.cancel();
    }
  }

  /**
   * Generate new sensor values and send a notification to all listeners.
   */
  public void generateNewSensorValues() {
    Logger.infoNoNewline("Node #" + nodeId);
    addRandomNoiseToSensors();
    notifySensorChanges();
    debugPrint();
  }

  private void addRandomNoiseToSensors() {
    for (Sensor sensor : sensors) {
      sensor.addRandomNoise();
    }
  }

  private void debugPrint() {
    for (Sensor sensor : sensors) {
      Logger.infoNoNewline(" " + sensor.getReading().getFormatted());
    }
    Logger.infoNoNewline(" :");
    actuators.debugPrint();
    Logger.info("");
  }

  /**
   * Toggle an actuator attached to this device.
   *
   * @param actuatorId The ID of the actuator to toggle
   * @throws IllegalArgumentException If no actuator with given configuration is found on this node
   */
  public void toggleActuator(int actuatorId) {
    Actuator actuator = getActuator(actuatorId);
    if (actuator == null) {
      throw new IllegalArgumentException("actuator[" + actuatorId + "] not found on node " + nodeId);
    }
    actuator.toggle();
  }

  private Actuator getActuator(int actuatorId) {
    return actuators.get(actuatorId);
  }

  private void notifySensorChanges() {
    for (SensorListener listener : sensorListeners) {
      listener.sensorsUpdated(sensors);
    }
  }

  @Override
  public void actuatorUpdated(int nodeId, Actuator actuator) {
    actuator.applyImpact(this);
    notifyActuatorChange(actuator);
  }

  private void notifyActuatorChange(Actuator actuator) {
    String onOff = actuator.isOn() ? "ON" : "off";
    Logger.info(" => " + actuator.getType() + " on node " + nodeId + " " + onOff);
    for (ActuatorListener listener : actuatorListeners) {
      listener.actuatorUpdated(nodeId, actuator);
    }
  }


  /**
   * Notify the listeners that the state of this node has changed.
   *
   * @param isReady When true, let them know that this node is ready;
   *                when false - that this node is shut down
   */
  private void notifyStateChanges(boolean isReady) {
    Logger.info("Notify state changes for node " + nodeId);
    for (NodeStateListener listener : stateListeners) {
      if (isReady) {
        listener.onNodeReady(this);
      } else {
        listener.onNodeStopped(this);
      }
    }
  }

  /**
   * An actuator has been turned on or off. Apply an impact from it to all sensors of given type.
   *
   * @param sensorType The type of sensors affected
   * @param impact     The impact to apply
   */
  public void applyActuatorImpact(String sensorType, double impact) {
    for (Sensor sensor : sensors) {
      if (sensor.getType().equals(sensorType)) {
        sensor.applyImpact(impact);
      }
    }
  }

  /**
   * Get all the sensors available on the device.
   *
   * @return List of all the sensors
   */
  public List<Sensor> getSensors() {
    return sensors;
  }

  /**
   * Get all the actuators available on the node.
   *
   * @return A collection of the actuators
   */
  public ActuatorCollection getActuators() {
    return this.actuators;
  }


  @Override
  public void onCommunicationChannelClosed() {
    Logger.info("Communication channel closed for node " + nodeId);
    stop();
  }

  /**
   * Set an actuator to a desired state.
   *
   * @param actuatorId ID of the actuator to set.
   * @param on         Whether it should be on (true) or off (false)
   */
  public void setActuator(int actuatorId, boolean on) {
    Actuator actuator = getActuator(actuatorId);
    if (actuator != null) {
      actuator.set(on);
    }
  }

  /**
   * Set all actuators to desired state.
   *
   * @param on Whether the actuators should be on (true) or off (false)
   */
  public void setAllActuators(boolean on) {
    for (Actuator actuator : actuators) {
      actuator.set(on);
    }
  }

  public void onNodeInfoRequest(String clientHandlerID) {
    this.communicationChannel.sendNodeInfo(clientHandlerID);
  }
}

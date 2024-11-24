package no.ntnu.greenhouse;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.server.ClientHandler;
import no.ntnu.server.Server;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
  private final Map<Integer, SensorActuatorNode> nodes = new HashMap<>();

  private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>();
  private final boolean fake;
  private boolean isRunning;
  private Server server;
  /**
   * Create a greenhouse simulator.
   *
   * @param fake When true, simulate a fake periodic events instead of creating
   *             socket communication
   */
  public GreenhouseSimulator(boolean fake) {
    this.fake = fake;
  }

  /**
   * Initialise the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    createNode(1, 2, 1, 0, 0);
    createNode(1, 0, 0, 2, 1);
    createNode(2, 0, 0, 0, 0);
    Logger.info("Greenhouse initialized");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    SensorActuatorNode node = DeviceFactory.createNode(
        temperature, humidity, windows, fans, heaters);
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    initiateCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.start();
    }
    for (PeriodicSwitch periodicSwitch : periodicSwitches) {
      periodicSwitch.start();
    }

    Logger.info("Simulator started");
  }

  private void initiateCommunication() {
    if (fake) {
      initiateFakePeriodicSwitches();
    } else {
      initiateRealCommunication();
    }
  }

  private void initiateRealCommunication() {
   if(server == null||!server.isRunning()){
     server  = new Server();
     initiateFakePeriodicSwitches();
     server.start();
     Logger.info("Communication initiated and the server is now listening for connections.");
   }else{
     Logger.info("The server is already running and listening for connections.");
   }
  }

  private void initiateFakePeriodicSwitches() {
    periodicSwitches.add(new PeriodicSwitch("Window DJ", nodes.get(1), 2, 20000));
    periodicSwitches.add(new PeriodicSwitch("Heater DJ", nodes.get(2), 7, 8000));
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  public void stop() {
    stopCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.stop();
    }
  }

  /**
   * Stop the communication with all nodes in the greenhouse.
   * This method iterates through all the nodes and stops the communication with each one.
   * It first checks if the node is currently running before stopping it.
   * After successfully stopping the communication with each node, a log message is generated.
   */
  private void stopCommunication() {
    Logger.info("Stopping the communication with nodes");
    //Here we iterate through all the nodes and stop the communication with each
    for(SensorActuatorNode node : nodes.values()){
      if(node.isRunning()){
        node.stop();
        Logger.info("Communication with node "+node.getId()+" has been successfully stopped");
      }
    }
    Logger.info("All nodes communications have been stopped");
  }

  /**
   * Stops the communication with a specified node in the greenhouse.
   * If the communication mode is set to fake, stops all periodic switches.
   *
   * @param nodeId The id of the node to stop the communication with
   */
  private void stopCommunicationById(int nodeId) {
    SensorActuatorNode node = nodes.get(nodeId);
    if (fake) {
      for (PeriodicSwitch periodicSwitch : periodicSwitches) {
        periodicSwitch.stop();
      }
    } else {
      //Stopping the communication between the greenhouse and a node.
      if(node != null && node.isRunning()){
        Logger.info("--Stopping the communication with node " + nodeId);
        node.stop();
        Logger.info("Communication with the node " + nodeId + "has been stopped.");
      }
      else{
        Logger.info("No active communication with node " + nodeId + "or node has already been stopped.");
      }
    }
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (SensorActuatorNode node : nodes.values()) {
      node.addStateListener(listener);
    }
  }
}

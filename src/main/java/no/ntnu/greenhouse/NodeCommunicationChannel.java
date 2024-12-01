package no.ntnu.greenhouse;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.listeners.greenhouse.SensorListener;
import no.ntnu.message.greenhouse.ActuatorUpdateMessage;
import no.ntnu.message.greenhouse.AddNodeMessage;
import no.ntnu.message.greenhouse.NodeConnectionMessage;
import no.ntnu.message.greenhouse.SensorUpdateMessage;
import no.ntnu.server.Client;
import no.ntnu.tools.Logger;

import java.util.List;

/**
 * A TCP Client designed for SensorActuator nodes.
 * <p></p>Uses a NodeMessageHandler to handle incoming messages.
 */
public class NodeCommunicationChannel extends Client implements ActuatorListener, NodeStateListener, SensorListener {
    private final SensorActuatorNode node;

    /**
     * Constructor for a NodeCommunicationChannel.
     * @param node The SensorActuatorNode this client belongs to.
     */
    public NodeCommunicationChannel(SensorActuatorNode node) {
        this.node = node;
        this.setMessageHandler(new NodeMessageHandler(this.node));
    }

    /**
     * Notify the server when the node is connecting/disconnecting.
     * @param connecting True if connecting, false if disconnecting.
     */
    public void sendConnectionMessage(boolean connecting) {
        String message = new NodeConnectionMessage(node.getId(), connecting).getMessageString();
        this.sendMessageToServer(message);
    }

    /**
     * Send information about this node when requested.
     * @param clientHandlerID The ID of the client that requested the information.
     */
    public void sendNodeInfo(String clientHandlerID) {
        this.sendMessageToServer(new AddNodeMessage(
                node.getId(), node.getActuators(), clientHandlerID).getMessageString());
    }

    /**
     * Notify the server that an actuator has been updated with new information.
     *
     * @param nodeId The ID of the node to which the actuator belongs.
     * @param actuator The updated Actuator object with new information.
     */
    @Override
    public void actuatorUpdated(int nodeId, Actuator actuator) {
        this.sendMessageToServer(new ActuatorUpdateMessage(
                this.node.getId(), actuator.getId(), actuator.isOn()).getMessageString());
    }

    /**
     * Notifies the server that a SensorActuatorNode is ready.
     *
     * @param node The SensorActuatorNode that is ready.
     */
    @Override
    public void onNodeReady(SensorActuatorNode node) {
        this.sendMessageToServer(new AddNodeMessage(
                node.getId(), node.getActuators()).getMessageString());
    }

    /**
     * Notifies when a SensorActuatorNode has stopped.
     *
     * @param node The SensorActuatorNode that has stopped.
     */
    @Override
    public void onNodeStopped(SensorActuatorNode node) {
        Logger.info("Node on thread: " + Thread.currentThread().getName() + " has stopped.");
    }

    /**
     * Notifies the communication channel that the sensors have been updated and sends a message to the server with the updated sensor information.
     *
     * @param sensors A list of Sensor objects containing the updated sensor readings.
     */
    @Override
    public void sensorsUpdated(List<Sensor> sensors) {
        this.sendMessageToServer(new SensorUpdateMessage(
                this.node.getId(), sensors).getMessageString());
    }
}

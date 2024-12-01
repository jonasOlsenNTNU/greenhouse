package no.ntnu.controlpanel;

import no.ntnu.message.controlpanel.ActuatorChangeMessage;
import no.ntnu.message.controlpanel.ControlPanelConnectionMessage;
import no.ntnu.message.controlpanel.RequestNodesMessage;
import no.ntnu.message.controlpanel.UpdateActuatorByTypeMessage;
import no.ntnu.server.Client;
import no.ntnu.tools.Logger;

import java.io.IOException;

/**
 * A TCP Client designed for control panels.
 * <p></p>Uses a ControlPanelMessageHandler to handle incoming messages.
 * <p>Uses a ControlPanelLogic to interact with the control panel.
 */
public class ControlPanelCommunicationChannel extends Client implements CommunicationChannel{
    private final ControlPanelLogic logic;

    /**
     * Constructor for a ControlPanelCommunicationChannel.
     * @param logic The ControlPanelLogic this client belongs to.
     * @throws IllegalArgumentException if logic == null.
     */
    public ControlPanelCommunicationChannel(ControlPanelLogic logic) throws IllegalArgumentException {
        if (logic == null) {
            throw new IllegalArgumentException("ControlPanelCommunicationChannel requires a valid" +
                    "ControlPanelLogic.");
        }
        this.logic = logic;
        this.setMessageHandler(new ControlPanelMessageHandler(logic));
        this.connected = false;
    }

    /**
     * Sends a control panel connection message to the server.
     * If connecting is true, it indicates that the control panel is connecting; if false, it indicates disconnection.
     *
     * @param connecting True if connecting, false if disconnecting
     */
    @Override
    public void sendControlPanelConnectionMessage(boolean connecting) {
        this.sendMessageToServer(new ControlPanelConnectionMessage(connecting).getMessageString());
    }

    /**
     * Closes the connection with the server by setting the 'connected' flag to false,
     * closing the socket, and notifying the logic object that the communication channel is closed.
     */
    @Override
    public void closeConnection() {
        this.connected = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            Logger.error("Error occurred while closing connection: " + e.getMessage());
        }
        this.logic.onCommunicationChannelClosed();
    }

    /**
     * Sends a message to the server to change the state of an actuator.
     *
     * @param nodeId The ID of the node to which the actuator belongs. Must be 0 or higher.
     * @param actuatorId The ID of the actuator to change. Must be 0 or higher.
     * @param isOn True if the actuator should be turned on, false if it should be turned off.
     */
    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        if (nodeId >= 0) {
            if (actuatorId >= 0) {
                this.sendMessageToServer(new ActuatorChangeMessage(nodeId, actuatorId, isOn).getMessageString());
            } else {
                Logger.error("ActuatorId: " + actuatorId + "is not valid. Must be 0 or higher.");
            }
        } else {
            Logger.error("NodeId: " + nodeId + " is not valid. Must be 0 or higher.");
        }
    }

    /**
     * Sends a node request message to the server.
     * <p>
     * This method creates a new RequestNodesMessage and sends it to the server using the sendMessageToServer method.
     * The RequestNodesMessage contains information about the sender, which can be the control panel or a server.
     * </p>
     */
    @Override
    public void sendNodeRequestMessage() {
        this.sendMessageToServer(new RequestNodesMessage().getMessageString());
    }

    @Override
    public void sendBroadcastActuatorChange(String actuatorType, boolean isOn) {
        this.sendMessageToServer(new UpdateActuatorByTypeMessage(actuatorType,isOn).getMessageString());
    }
}

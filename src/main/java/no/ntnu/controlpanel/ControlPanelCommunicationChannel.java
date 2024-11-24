package no.ntnu.controlpanel;

import no.ntnu.message.controlpanel.ActuatorChangeMessage;
import no.ntnu.message.controlpanel.ControlPanelConnectionMessage;
import no.ntnu.message.controlpanel.RequestNodesMessage;
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

    @Override
    public void sendControlPanelConnectionMessage(boolean connecting) {
        this.sendMessageToServer(new ControlPanelConnectionMessage(connecting).getMessageString());
    }

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

    @Override
    public void sendNodeRequestMessage() {
        this.sendMessageToServer(new RequestNodesMessage().getMessageString());
    }
}

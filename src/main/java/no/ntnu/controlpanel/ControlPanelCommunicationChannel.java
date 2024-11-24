package no.ntnu.controlpanel;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.message.controlpanel.ActuatorChangeMessage;
import no.ntnu.message.controlpanel.ControlPanelConnectionMessage;
import no.ntnu.server.Client;
import no.ntnu.tools.Logger;

import java.io.IOException;

/**
 * A communication channel for a control panel.
 * Connects to a server through a TCP socket.
 * Sends and receives messages through the input and output streams.
 */
public class ControlPanelCommunicationChannel extends Client implements CommunicationChannel{
    private final ControlPanelLogic logic;
    public ControlPanelCommunicationChannel(ControlPanelLogic logic) {
        this.logic = logic;
        this.setMessageHandler(new ControlPanelMessageHandler(logic));
        this.connected = false;
    }

    public void sendControlPanelConnectionMessage(boolean connecting) {
        this.sendMessageToServer(new ControlPanelConnectionMessage(connecting).getMessageString());
    }

    @Override
    public void closeConnection() {
        this.connected = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            Logger.error("Error occured while closing connection: " + e.getMessage());
        }
        this.logic.onCommunicationChannelClosed();
    }

    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        this.sendMessageToServer(new ActuatorChangeMessage(nodeId, actuatorId, isOn).getMessageString());
    }

}

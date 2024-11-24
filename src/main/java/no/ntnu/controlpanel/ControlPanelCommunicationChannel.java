package no.ntnu.controlpanel;

import no.ntnu.message.Splitters;
import no.ntnu.message.controlpanel.ActuatorChangeMessage;
import no.ntnu.tools.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A communication channel for a control panel.
 * Connects to a server through a TCP socket.
 * Sends and receives messages through the input and output streams.
 */
public class ControlPanelCommunicationChannel implements CommunicationChannel {
    private final ControlPanelLogic logic;
    private final ControlPanelMessageHandler messageHandler;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private Socket socket;
    private boolean connected;
    public ControlPanelCommunicationChannel(ControlPanelLogic logic) {
        this.logic = logic;
        this.messageHandler = new ControlPanelMessageHandler(logic);
        this.connected = false;
    }

   @Override
    public boolean open() {
        if (!connectToServer()) {
            return false;
        }
        if (!initializeIOStreams()) {
            return false;
        }
        Thread receiverThread = new Thread(this::handleConnection);
        receiverThread.start();
        return true;
    }

    /**
     * Try to connect to the server.
     * @return True if successful, false if not.
     */
    private boolean connectToServer() {
        boolean success = false;
        try {
            this.socket = new Socket("localhost", 8585);
            System.out.println("Succesfully connected to the server");
            success = true;
        } catch (IOException e) {
            Logger.error("Could not connect to the server: " + e.getMessage());
        }
        return success;
    }

    /**
     * Initialize the input and output streams from the socket.
     * @return True if successful, false if not.
     */
    private boolean initializeIOStreams() {
        boolean success = false;
        try {
            this.socketWriter = new PrintWriter(socket.getOutputStream(), true);
            this.socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.connected = true;
            success = true;
        } catch (Exception e) {
            Logger.error("Error occured while initializing IO streams: " + e.getMessage());
        }
        return success;
    }

    /**
     * While the connection is open; try to read messages from the server.
     * If a message is received, the message body is sent to the messageHandler.
     */
    private void handleConnection() {
        while(this.connected) {
            String serverMessage = readServerMessage();
            if (serverMessage != null) {
                this.messageHandler.handleMessage(serverMessage.split(Splitters.MESSAGE_SPLITTER)[1]);
            }
        }
        this.closeConnection();
    }

    /**
     * Close the socket connection to the server.
     */
    private void closeConnection() {
        this.connected = false;
        try {
            this.socket.close();
        } catch (IOException e) {
            Logger.error("Error occured while closing connection: " + e.getMessage());
        }
        this.logic.onCommunicationChannelClosed();
    }

    /**
     * Attempts to read a serialized message from the sockets input stream.
     * Connected is set to false if the socket is disconnected and throws an exception.
     * @return The serialized message received from the input stream.
     */
    private String readServerMessage() {
        String serverMessage = null;
        try {
            serverMessage = socketReader.readLine();
        } catch (IOException e) {
            Logger.error("Input stream from client ended");
            this.connected = false;
        }
        return serverMessage;
    }


    @Override
    public void sendActuatorChange(int nodeId, int actuatorId, boolean isOn) {
        sendMessageToServer(new ActuatorChangeMessage(nodeId, actuatorId, isOn).getMessageString());
    }

    /**
     * Send a serialized message to the server.
     * @param message A serialized message. See protocol.md for formatting information.
     */
    public void sendMessageToServer(String message) {
        this.socketWriter.println(message);
    }
}

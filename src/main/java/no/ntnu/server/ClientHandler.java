package no.ntnu.server;



import no.ntnu.tools.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * A handler for a single client socket.
 * Has 3 responsibilities:
 * -Reading from the socket input stream.
 * -Writing to the socket output stream.
 * -Routing input stream using the message head.
 */
public class ClientHandler extends Thread {

    private final Server server;
    private final Socket clientSocket;
    private BufferedReader socketReader;
    private PrintWriter socketWriter;
    private boolean clientConnected;
    private int clientNumber;

    /**
     * Constructor for a ClientHandler
     * @param clientSocket A connected Socket belonging to the client.
     * @param server The Server this object handles clients for.
     */
    public ClientHandler(Socket clientSocket, Server server, int clientNumber) {
        this.clientSocket = clientSocket;
        this.server = server;
        this.clientConnected = true;
        this.clientNumber = clientNumber;
    }

    /**
     * Call ClientHandler.start() to start and run the thread.
     * Initializes the IO streams and starts handling the client.
     * If initialization fails, the thread finishes.
     */
    @Override
    public void run() {
        if (!initializeIOStreams()) {
            return;
        }
        Logger.info("ClientHandler started on thread: " + Thread.currentThread().getName());
        handleClient();
    }

    /**
     * Initialize the Input/Output streams.
     * @return If the initialization was successful.
     */
    private boolean initializeIOStreams() {
        boolean success = false;
        try {
            socketReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            socketWriter = new PrintWriter(clientSocket.getOutputStream(), true);
            success = true;
        } catch (Exception e) {
            Logger.error("Could not initialize IO streams: " + e.getMessage());
        }
        return success;
    }

    /**
     * Handle the client.
     * Reads from input stream while the client is connected.
     * Removes the client from the server otherwise.
     */
    private void handleClient() {
        while (this.clientConnected) {
            String clientMessage = readClientMessage();
            if (clientMessage != null) {
                if(server.messageHandler.isServerMessage(clientMessage)){
                    server.messageHandler.handleServerMessage(clientMessage, this);
                } else {
                    this.server.messageHandler.handleMessage(clientMessage);
                }
            }
        }
        removeClient();
    }

    /**
     * Method to remove the client with a specific client ID.
     * Removes the node associated with the client ID from the server's nodes map
     * and the control panel associated with this client handler from the server's control panels map.
     * Logs messages indicating whether the node and control panel were found and removed successfully.
     */
    private void removeClient() {
        String clientId = Integer.toString(this.clientNumber);


        if (server.getNodes().containsKey(clientId)) {
            server.removeNode(clientId);
            Logger.info("Node with client ID " + clientId + " removed.");
        } else {
            Logger.info("Node with client ID " + clientId + " not found.");
        }

        if (server.getControlPanels().containsValue(ClientHandler.this)) {
            server.removeControlPanel(ClientHandler.this);
            Logger.info("Control panel for client handler removed.");
        } else {
            Logger.info("Control panel for client handler not found.");
        }
    }


    /**
     * Attempts to read a serialized message from the sockets input stream.
     * Updates isConnected if the socket is disconnected and throws an exception.
     * @return The serialized message received from the input stream.
     */
    private String readClientMessage() {
        String clientMessage = null;
        try {
            clientMessage = socketReader.readLine();
        } catch (IOException e) {
            Logger.error("Inputstream from client ended");
            this.clientConnected = false;
        }
        return clientMessage;
    }

    /**
     * Send a serialized message to the client via the output stream.
     * @param message The serialized message to be sent.
     */
    public void sendMessageToClient(String message) {
        socketWriter.println(message);
        Logger.info("Client message sent " + message);
    }

    public int getClientNumber() {
        return  this.clientNumber;
    }
}

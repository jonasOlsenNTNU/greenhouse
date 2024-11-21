package no.ntnu.server;

import no.ntnu.tools.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A server that handles communication between greenhouse nodes and control panels.
 * Accepts incoming TCP connections on port 8585.
 */
public class Server {
    private HashMap<String, ClientHandler> nodes;
    private ArrayList<ClientHandler> controlpanels;
    private ServerSocket serverSocket;
    private boolean isRunning;
    private static final int portNumber = 8585;

    /**
     * Normal constructor for a server object.
     * Call Server.start() to start the server.
     */
    public Server() {
        this.nodes = new HashMap<>();
        this.controlpanels = new ArrayList<>();
        this.isRunning = false;
    }

    /**
     * Starts the server by opening a listening socket.
     * The server accepts incoming connections until it stops running.
     */
    public void start() {
        this.serverSocket = openListeningSocket(portNumber);
        while(isRunning) {
            Socket clientSocket = acceptNextClientConnection();
            if (clientSocket != null) {
                Logger.info("Client connected from " + clientSocket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clientHandler.start();
            }
        }
        Logger.info("The server has stopped running: isRunning set to false.");
    }

    /**
     * Listen for a new incoming connection and accept it.
     * While listening for a connection the thread is blocked.
     *
     * @return The connected client socket if successful. Null if not.
     */
    private Socket acceptNextClientConnection() {
        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            Logger.error("Could not accept client connection: " + e.getMessage());
        }
        return clientSocket;
    }

    /**
     * Create a ServerSocket for the server.
     * If successful isRunning is set to true.
     *
     * @param port The port number for incoming connections.
     * @return A ServerSocket if successful. Null if not.
     */
    private ServerSocket openListeningSocket(int port) {
        ServerSocket listeningSocket = null;
        try {
            listeningSocket = new ServerSocket(port);
            this.isRunning = true;
        } catch (IOException e) {
            Logger.error("Could not create ServerSocket" + e.getMessage());
            this.isRunning = false;
        }
        return listeningSocket;
    }

    /**
     * Add a ClientHandler for a node to the map of nodes.
     * @param id ID of the node.
     * @param nodeClientHandler ClientHandler for the node.
     */
    public void addNewNode(String id, ClientHandler nodeClientHandler) {
        this.nodes.put(id, nodeClientHandler);
    }

    /**
     * Add a ClientHandler for a control panel to the list of control panels.
     * @param cPanelClientHandler ClientHandler for the control panel.
     */
    public void addNewControlPanel(ClientHandler cPanelClientHandler) {
        this.controlpanels.add(cPanelClientHandler);
    }

    /**
     * Remove a ClientHandler for a node from the map of nodes.
     * @param id ID of the node.
     */
    public void removeNode(String id) {
        this.nodes.remove(id);
    }

    /**
     * Remove a ClientHandler for a controlpanel from the list of controlpanels.
     * @param cPanelClientHandler ClientHandler for the control panel.
     */
    public void removeControlPanel(ClientHandler cPanelClientHandler) {
        this.controlpanels.remove(cPanelClientHandler);
    }

    /**
     * Send a message to a single node.
     * @param id ID of the receiver node.
     * @param message A serialized Message.
     */
    public void sendMessageToNode(String id, String message) {
        this.nodes.get(id).sendMessageToClient(message);
    }

    /**
     * Send a message to all control panels.
     * @param message A serialized Message.
     */
    public void sendMessageToAllControlPanels(String message) {
        for (ClientHandler c : controlpanels) {
            c.sendMessageToClient(message);
        }
    }

}

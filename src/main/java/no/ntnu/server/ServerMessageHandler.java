package no.ntnu.server;

import no.ntnu.message.Splitters;
import no.ntnu.message.controlpanel.RequestNodesMessage;
import no.ntnu.message.server.RemoveNodeMessage;
import no.ntnu.tools.Logger;

import static no.ntnu.message.Splitters.BODY_SPLITTER;

public class ServerMessageHandler implements MessageHandler {

    private final Server server;
    public ServerMessageHandler(Server server) {
        this.server = server;
    }

    /**
     * Processes the incoming message and delegates it to the appropriate method based on the message header.
     *
     * @param message The incoming message to be processed.
     */
    @Override
    public void handleMessage(String message) {
        String[] splitMessage = message.split(Splitters.MESSAGE_SPLITTER);
        String[] head = splitMessage[0].split(Splitters.HEAD_SPLITTER);
        switch (head[0]) {
            //Note: Case "server" requires the clientHandler.
            //Send to handleServerMessage from the ClientHandler directly.
            case "node" -> server.sendMessageToNode(head[1], message);
            case "controlpanel" -> {
                //Throws an exception if the message does not have a single recipient.
                try {
                    int clientHandlerID = Integer.parseInt(head[1]);
                    server.sendMessageToControlPanel(clientHandlerID, message);
                } catch (Exception e) {
                    server.sendMessageToAllControlPanels(message);
                }
            }
            default -> Logger.error("Message from client not valid: " + head[0]);
        }
    }

    /**
     * Checks if the given message is a server message based on the message type.
     *
     * @param message The message to be checked.
     * @return true if the message type is "server", false otherwise.
     */
    public boolean isServerMessage(String message) {
        String type = message.split(Splitters.MESSAGE_SPLITTER)[0].split(Splitters.HEAD_SPLITTER)[0];
        return type.equals("server");
    }

    /**
     * Processes a server message received from a client and takes appropriate actions based on the message type.
     *
     * @param message The server message received from the client.
     * @param clientHandler The ClientHandler associated with the client who sent the message.
     */
    public void handleServerMessage(String message, ClientHandler clientHandler) {
        String[] bodySplit = message.split(Splitters.MESSAGE_SPLITTER)[1]
                .split(Splitters.TYPE_SPLITTER);
        String type = bodySplit[0];
        switch (type) {
            case "NodeConnectionMessage" -> {
                String[] data = bodySplit[1].split(BODY_SPLITTER);
                String nodeID = data[1];
                if (data[0].equals("true")) {
                    server.addNewNode(nodeID, clientHandler);
                } else if (data[0].equals("false")){
                    this.sendRemoveNodeMessage(nodeID);
                    server.removeNode(data[1]);
                } else {
                    Logger.error("Boolean 'connecting' not valid");
                }
            }
            case "ControlPanelConnectionMessage" -> {
                String[] data = bodySplit[1].split(BODY_SPLITTER);
                if (data[0].equals("true")) {
                    server.addNewControlPanel(clientHandler);
                } else if (data[0].equals("false")) {
                    server.removeControlPanel(clientHandler);
                } else {
                    Logger.error("Boolean 'connecting' not valid");
                }
            }
            case "UpdateActuatorByTypeMessage" -> server.sendMessageToAllNodes(message);
            case "RequestNodesMessage" -> server.sendMessageToAllNodes(new RequestNodesMessage(
                    clientHandler.getClientNumber()).getMessageString());
            default -> Logger.error("Message type not found: " + type);
        }
    }

    /**
     * Sends a remove node message to all control panels in the server.
     *
     * @param nodeID The ID of the node to be removed.
     */
    private void sendRemoveNodeMessage(String nodeID) {
        int id = Integer.parseInt(nodeID);
        server.sendMessageToAllControlPanels(new RemoveNodeMessage(id).getMessageString());
    }

}

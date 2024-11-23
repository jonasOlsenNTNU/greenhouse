package no.ntnu.server;

import no.ntnu.message.Splitters;
import no.ntnu.message.server.RemoveNodeMessage;
import no.ntnu.tools.Logger;

public class ServerMessageHandler implements MessageHandler {

    private final Server server;
    public ServerMessageHandler(Server server) {
        this.server = server;
    }

    @Override
    public void handleMessage(String message) {
        String[] splitMessage = message.split(Splitters.MESSAGE_SPLITTER);
        String[] head = splitMessage[0].split(Splitters.HEAD_SPLITTER);
        switch (head[0]) {
            //Note: Case "server" requires the clientHandler.
            //Send to handleServerMessage from the ClientHandler directly.
            case "node" -> server.sendMessageToNode(head[1], message);
            case "controlpanel" -> server.sendMessageToAllControlPanels(message);
            default -> Logger.error("Message from client not valid: " + head[0]);
        }
    }

    public boolean isServerMessage(String message) {
        String type = message.split(Splitters.MESSAGE_SPLITTER)[0].split(Splitters.HEAD_SPLITTER)[0];
        // TODO: Remove logger after testing
        Logger.info("Message type: " + type);
        return type.equals("server");
    }

    public void handleServerMessage(String message, ClientHandler clientHandler) {
        String[] bodySplit = message.split(Splitters.MESSAGE_SPLITTER)[1]
                .split(Splitters.TYPE_SPLITTER);
        String type = bodySplit[0];
        String[] data = bodySplit[1].split(Splitters.BODY_SPLITTER);
        //TODO: Remove logger after testing
        Logger.info(data[0]);
        switch (type) {
            case "NodeConnectionMessage" -> {
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
                if (data[0].equals("true")) {
                    server.addNewControlPanel(clientHandler);
                } else if (data[0].equals("false")) {
                    server.removeControlPanel(clientHandler);
                } else {
                    Logger.error("Boolean 'connecting' not valid");
                }
            }
            default -> Logger.error("Message type not found: " + type);
        }
    }
    private void sendRemoveNodeMessage(String nodeID) {
        int id = Integer.parseInt(nodeID);
        server.sendMessageToAllControlPanels(new RemoveNodeMessage(id).getMessageString());
    }

}

package no.ntnu.greenhouse;

import no.ntnu.message.greenhouse.AddNodeMessage;
import no.ntnu.message.greenhouse.NodeConnectionMessage;
import no.ntnu.server.Client;

/**
 * Communication channel for a node.
 */
public class NodeCommunicationChannel extends Client {
    private SensorActuatorNode node;
    public NodeCommunicationChannel(SensorActuatorNode node) {
        this.node = node;
        this.setMessageHandler(new NodeMessageHandler(this.node));
    }

    public void sendConnectionMessage(boolean connecting) {
        String message = new NodeConnectionMessage(node.getId(), connecting).getMessageString();
        this.sendMessageToServer(message);
    }

    public void broadcastNodeInfo() {
        this.sendMessageToServer(new AddNodeMessage(
                node.getId(), node.getActuators()).getMessageString());
    }
    public void sendNodeInfo(String clientHandlerID) {
        this.sendMessageToServer(new AddNodeMessage(
                node.getId(), node.getActuators(), clientHandlerID).getMessageString());
    }

}

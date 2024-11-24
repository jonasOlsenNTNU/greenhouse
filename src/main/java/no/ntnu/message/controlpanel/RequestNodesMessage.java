package no.ntnu.message.controlpanel;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

public class RequestNodesMessage implements Message {

    private final String head;
    private final String body;

    /**
     * Constructor for a RequestNodesMessage.
     * This message can be sent by a control panel to request information about all connected nodes.
     */
    public RequestNodesMessage() {
        this.head = "server";
        this.body = "RequestNodesMessage";
    }

    /**
     * Constructor for a RequestNodesMessage.
     * This message can be sent by a server to a node. Contains information about sender.
     * @param clientHandlerID ID of the client that requested node information.
     */
    public RequestNodesMessage(int clientHandlerID) {
        this.head = "node";
        this.body = "RequestNodesMessage" + Splitters.TYPE_SPLITTER + clientHandlerID;
    }

    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

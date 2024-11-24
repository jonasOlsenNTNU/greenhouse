package no.ntnu.message.controlpanel;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

public class RequestNodesMessage implements Message {

    private final String head;
    private final String body;

    /**
     * Constructor for a RequestNodesMessage.
     */
    public RequestNodesMessage() {
        this.head = "server";
        this.body = "RequestNodesMessage";
    }

    public RequestNodesMessage(int clientHandlerID) {
        this.head = "node";
        this.body = "RequestNodesMessage" + Splitters.BODY_SPLITTER + clientHandlerID;
    }

    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

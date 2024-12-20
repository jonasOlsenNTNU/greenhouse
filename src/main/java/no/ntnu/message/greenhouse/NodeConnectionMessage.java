package no.ntnu.message.greenhouse;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

/**
 * A message readable by the server.
 * Gives information to the server about the connection.
 */
public class NodeConnectionMessage implements Message {

    private final String head;
    private final String body;

    /**
     * Constructor for a NodeConnectionMessage.
     *
     * @param nodeID ID of the node
     * @param connecting True if the node is connecting, false if disconnecting
     */
    public NodeConnectionMessage(int nodeID, boolean connecting) {
        this.head = "server";
        this.body = "NodeConnectionMessage"
                + Splitters.TYPE_SPLITTER + connecting
                + Splitters.BODY_SPLITTER + nodeID;
    }

    /**
     * Get the full message string of the NodeConnectionMessage.
     *
     * @return the concatenated head and body elements of the message separated by MESSAGE_SPLITTER
     */
    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

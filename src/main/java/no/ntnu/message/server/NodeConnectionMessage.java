package no.ntnu.message.server;

import no.ntnu.message.common.Message;

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
    //TODO: splitters should be managed by an enum file according to the protocol
    public NodeConnectionMessage(int nodeID, boolean connecting) {
        this.head = "server";
        this.body = "node"+ "," + connecting + "," + nodeID;
    }

    @Override
    public String getMessageString() {
        return this.head + "#" + this.body;
    }
}

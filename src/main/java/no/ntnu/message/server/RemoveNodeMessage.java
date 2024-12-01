package no.ntnu.message.server;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

/**
 * A message for triggering the GreenHouseListeners.onNodeRemoved event.
 * Sent by the server when a node has disconnected.
 */
public class RemoveNodeMessage implements Message {
    private final String head;
    private final String body;

    /**
     * Constructs a RemoveNodeMessage with the specified node ID.
     *
     * @param nodeID The ID of the node to be removed.
     */
    public RemoveNodeMessage(int nodeID) {
        this.head = "controlpanel";
        this.body = "RemoveNodeMessage"
                + Splitters.TYPE_SPLITTER + nodeID;
    }

    /**
     * Get the message string representing the combination of the head and body of the message.
     *
     * @return The concatenated string of the head and body using MESSAGE_SPLITTER.
     */
    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

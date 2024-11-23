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

    public RemoveNodeMessage(int nodeID) {
        this.head = "controlpanel";
        this.body = "RemoveNodeMessage"
                + Splitters.TYPE_SPLITTER + nodeID;
    }

    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

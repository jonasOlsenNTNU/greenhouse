package no.ntnu.message.controlpanel;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

public class ControlPanelConnectionMessage implements Message {

    private final String head;
    private final String body;

    /**
     * Constructor for a ControlPanelConnectionMessage.
     *
     * @param connecting True if the node is connecting, false if disconnecting
     */
    public ControlPanelConnectionMessage(boolean connecting) {
        this.head = "server";
        this.body = "ControlPanelConnectionMessage"
                + Splitters.TYPE_SPLITTER + connecting;
    }

    /**
     * Get the message as a serialized string.
     * Used to send the message to a socket output stream.
     *
     * @return The serialized message to be sent.
     */
    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

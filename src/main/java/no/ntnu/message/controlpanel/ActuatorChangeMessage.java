package no.ntnu.message.controlpanel;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

public class ActuatorChangeMessage implements Message {

    private final String head;
    private final String body;

    /**
     * Constructs an ActuatorChangeMessage object representing a message to change the state of an actuator.
     *
     * @param nodeId The ID of the node the actuator belongs to
     * @param actuatorId The ID of the actuator to change
     * @param isOn True if the actuator should be turned on, false if it should be turned off
     */
    public ActuatorChangeMessage (int nodeId, int actuatorId, boolean isOn) {
        this.head = "node"
                + Splitters.HEAD_SPLITTER + nodeId;
        this.body = "ActuatorChangeMessage"
                + Splitters.TYPE_SPLITTER + actuatorId
                + Splitters.BODY_SPLITTER + isOn;
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

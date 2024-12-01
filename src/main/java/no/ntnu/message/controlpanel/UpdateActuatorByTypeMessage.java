package no.ntnu.message.controlpanel;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

public class UpdateActuatorByTypeMessage implements Message {

    private final String head ;
    private final String body;

    /**
     * Represents a message used to update an actuator by type.
     * The head field contains information about the message sender, and the body contains the message content.
     */
    public UpdateActuatorByTypeMessage(String actuatorType, boolean isOn) {
        this.head = "server";
        this.body = "UpdateActuatorByTypeMessage"
        + Splitters.TYPE_SPLITTER + actuatorType
        + Splitters.BODY_SPLITTER + isOn;

    }

    /**
     * Retrieves the message as a string by concatenating the head and body with a predefined message splitter.
     *
     * @return The concatenated message string with the head and body separated by the message splitter.
     */
    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

package no.ntnu.message.greenhouse;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

/**
 * A message for triggering the onActuatorStateChanged event on a GreenHouseListener.
 */
public class ActuatorUpdateMessage implements Message {
    private final String head;
    private final String body;

    /**
     * Constructor for the ActuatorUpdateMessage
     * @param nodeID ID of the node the actuator belongs to.
     * @param actuatorID ID of the actuator.
     * @param isOn The new state of the actuator.
     */
    public ActuatorUpdateMessage(int nodeID, int actuatorID, boolean isOn) {
        this.head = "controlpanel";
        this.body = "ActuatorUpdateMessage"
                + Splitters.TYPE_SPLITTER + nodeID
                + Splitters.BODY_SPLITTER + actuatorID
                + Splitters.BODY_SPLITTER + isOn;
        //int nodeId, int actuatorId, boolean isOn
    }

    /**
     * Get the message string composed of the head and body parts separated by a splitter.
     *
     * @return The concatenated message head and body.
     */
    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

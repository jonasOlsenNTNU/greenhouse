package no.ntnu.message.greenhouse;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

/**
 * A message for triggering the GreenhouseEventListener.onNodeAdded event
 * Contains information about a new node.
 * Used to add a node to a controlpanel.
 */
public class AddNodeMessage implements Message {
    private final String head;
    private final String body;

    /**
     * Constructor for an AddNodeMessage.
     * @param nodeID ID of the node sending the message.
     * @param actuators A collection of actuators on the node.
     */
    public AddNodeMessage(int nodeID, ActuatorCollection actuators) {
        this.head = "controlpanel";
        StringBuilder actuatorsString = new StringBuilder();
        for (Actuator actuator : actuators) {
            actuatorsString.append(actuator.getId()).append(Splitters.VALUES_SPLITTER);
            actuatorsString.append(actuator.getType()).append(Splitters.VALUES_SPLITTER);
            actuatorsString.append(actuator.isOn()).append(Splitters.VALUES_SPLITTER);
        }
        actuatorsString.deleteCharAt(actuatorsString.length());
        this.body = "AddNodeMessage"
                + Splitters.TYPE_SPLITTER + nodeID
                + Splitters.BODY_SPLITTER + actuatorsString;
    }

    @Override
    public String getMessageString() {
        return this.head + "#" + this.body;
    }
}

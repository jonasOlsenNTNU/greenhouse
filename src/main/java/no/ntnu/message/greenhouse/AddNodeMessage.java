package no.ntnu.message.greenhouse;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.message.common.Message;

import java.util.Iterator;

/**
 * A message readable by a GreenhouseEventListener.
 * Contains information about a new node.
 * Used to add a node to a controlpanel.
 */

//TODO: splitters should be managed by an enum file according to the protocol
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
            actuatorsString.append(actuator.toString()).append(",");
        }
        actuatorsString.deleteCharAt(actuatorsString.length());
        this.body = nodeID + "," + actuatorsString;
    }

    @Override
    public String getMessageString() {
        return this.head + "#" + this.body;
    }
}

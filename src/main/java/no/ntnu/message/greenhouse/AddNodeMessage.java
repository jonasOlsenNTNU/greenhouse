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
    private String body;

    /**
     * Constructor for an AddNodeMessage.
     * This message should be sent to all control panels when a node has connected to the server.
     * @param nodeID ID of the node sending the message.
     * @param actuators A collection of actuators on the node.
     */
    public AddNodeMessage(int nodeID, ActuatorCollection actuators) {
        this.head = "controlpanel";
        this.createBody(nodeID, actuators);
    }
    /**
     * Constructor for an AddNodeMessage.
     * This message should be a response to a control panel that requested nodes.
     * @param nodeID ID of the node sending the message.
     * @param actuators A collection of actuators on the node.
     * @param clientHandlerID clientHandlerID of the control panel that requested this node.
     */
    public AddNodeMessage(int nodeID, ActuatorCollection actuators, String clientHandlerID) {
        this.head = "controlpanel" + Splitters.HEAD_SPLITTER + clientHandlerID;
        this.createBody(nodeID, actuators);
    }

    private void createBody(int nodeID, ActuatorCollection actuators) {
        StringBuilder actuatorsString = new StringBuilder();
        for (Actuator actuator : actuators) {
            actuatorsString.append(actuator.getId()).append(Splitters.VALUES_SPLITTER);
            actuatorsString.append(actuator.getType()).append(Splitters.VALUES_SPLITTER);
            actuatorsString.append(actuator.isOn()).append(Splitters.LIST_SPLITTER);
        }
        if (actuatorsString.length() > 2) {
            actuatorsString.deleteCharAt(actuatorsString.length() - 1);
        }
        this.body = "AddNodeMessage"
                + Splitters.TYPE_SPLITTER + nodeID
                + Splitters.BODY_SPLITTER + actuatorsString;
    }

    @Override
    public String getMessageString() {
        return this.head + "#" + this.body;
    }
}

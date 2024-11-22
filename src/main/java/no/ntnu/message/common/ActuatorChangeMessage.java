package no.ntnu.message.common;

import no.ntnu.greenhouse.Actuator;

/**
 * A message readable by an ActuatorListener.
 * If received by a node it can be interpreted as a command.
 * If received by a controlpanel it can be interpreted as an update of status.
 */
//TODO: splitters should be managed by an enum file according to the protocol
public class ActuatorChangeMessage implements Message {

    private String head;
    private String body;

    /**
     * The constructor for an ActuatorChangeMessage
     * @param receiver Either "node" or "controlpanel"
     * @param actuator The actuator affected by the change.
     */
    public ActuatorChangeMessage(String receiver, Actuator actuator) {
        this.head = receiver + actuator.getNodeId();
        this.body = actuator.getId() + "," + actuator.isOn();
        //TODO: The body is currently not readable by an ActuatorListener because
        // the listener requires an actual Actuator
        // It is currently not possible to recreate an actuator with the actuator.toString() method.
    }

    @Override
    public String getMessageString() {
        return this.head + "#" + this.body;
    }
}

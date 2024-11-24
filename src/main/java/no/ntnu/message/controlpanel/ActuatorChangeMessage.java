package no.ntnu.message.controlpanel;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

public class ActuatorChangeMessage implements Message {

    private final String head;
    private final String body;

    public ActuatorChangeMessage (int nodeId, int actuatorId, boolean isOn) {
        this.head = "node"
                + Splitters.HEAD_SPLITTER + nodeId;
        this.body = "ActuatorChangeMessage"
                + Splitters.TYPE_SPLITTER + actuatorId
                + Splitters.BODY_SPLITTER + isOn;
    }

    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

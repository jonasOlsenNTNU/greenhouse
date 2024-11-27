package no.ntnu.message.greenhouse;

import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

public class UpdateActuatorByTypeMessage implements Message {

    private final String head ;
    private final String body;

    public UpdateActuatorByTypeMessage() {
        this.head = "server";
        this.body = "UpdateActuatorByTypeMessage";
    }

    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

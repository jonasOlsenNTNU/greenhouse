package no.ntnu.greenhouse;

import no.ntnu.message.Splitters;
import no.ntnu.server.MessageHandler;
import no.ntnu.tools.Logger;

/**
 * Handles messages coming from the server
 */
public class NodeMessageHandler implements MessageHandler {
    private final SensorActuatorNode node;
    public NodeMessageHandler(SensorActuatorNode node) {
        this.node = node;
    }

    @Override
    public void handleMessage(String messageBody) {
        //Actuator change message
        String[] splitMessage = messageBody.split(Splitters.MESSAGE_SPLITTER);
        String type = splitMessage[1].split(Splitters.TYPE_SPLITTER)[0];
        //Add more message types here to handle them.
        switch (type) {
            case "ActuatorChangeMessage" -> this.handleActuatorChangeMessage(splitMessage[1]);
            default -> Logger.error("Message type could not be identified.");
        }
    }

    private void handleActuatorChangeMessage(String bodyData) {
        String[] values = bodyData.split(Splitters.BODY_SPLITTER);
        int actuatorID = Integer.parseInt(values[0]);
        boolean isOn = Boolean.parseBoolean(values[1]);
        //Update actuator
        node.setActuator(actuatorID, isOn);
    }
}

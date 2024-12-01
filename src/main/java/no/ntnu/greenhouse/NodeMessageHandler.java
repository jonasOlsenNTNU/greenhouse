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

    /**
     * Decides the appropriate handling method based on the type of the incoming message.
     *
     * @param messageBody The message body of a serialized message.
     */
    @Override
    public void handleMessage(String messageBody) {
        //Actuator change message
        String[] bodySplit = messageBody.split(Splitters.TYPE_SPLITTER);
        String type = bodySplit[0];
        //Add more message types here to handle them.
        Logger.error("Message received: " + messageBody + " type: " + type);
        switch (type) {
            case "ActuatorChangeMessage" -> this.handleActuatorChangeMessage(bodySplit[1]);
            case "RequestNodesMessage" -> this.handleRequestNodesMessage(bodySplit[1]);
            case "UpdateActuatorByTypeMessage" -> this.handleActuatorByTypeMessage(bodySplit[1]);
            default -> Logger.error("Message type could not be identified.");
        }
    }

    private void handleActuatorByTypeMessage(String bodyData) {
        Logger.info(bodyData);
        String[] values = bodyData.split(Splitters.BODY_SPLITTER);
        String actuatorType = values[0];
        boolean isOn = Boolean.parseBoolean(values[1]);
        Logger.info("The Actuator to change is " + actuatorType + " isOn: " + isOn);
        node.setActuatorType(actuatorType,isOn);
    }

    /**
     * Handles an actuator change message by updating the actuator state in the node.
     *
     * @param bodyData The serialized data containing actuator ID and state.
     */
    private void handleActuatorChangeMessage(String bodyData) {
        String[] values = bodyData.split(Splitters.BODY_SPLITTER);
        int actuatorID = Integer.parseInt(values[0]);
        boolean isOn = Boolean.parseBoolean(values[1]);
        //Update actuator
        node.setActuator(actuatorID, isOn);
    }

    /**
     * Handles a request for node information by sending node info to the specified client handler.
     *
     * @param clientHandlerID The ID of the client handler requesting node information.
     */
    private void handleRequestNodesMessage(String clientHandlerID) {
        this.node.onNodeInfoRequest(clientHandlerID);
    }
}

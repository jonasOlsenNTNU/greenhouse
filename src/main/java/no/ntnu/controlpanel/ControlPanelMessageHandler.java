package no.ntnu.controlpanel;


import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.message.Splitters;
import no.ntnu.tools.Logger;
import no.ntnu.tools.Parser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class responsible for parsing and handling incoming serialized messages.
 */
public class ControlPanelMessageHandler {
    private final ControlPanelLogic logic;
    public ControlPanelMessageHandler(ControlPanelLogic logic) {
        this.logic = logic;
    }

    /**
     * Handle an incoming serialized message.
     * <p> Uses the "type" from the message header to decide the correct handling method. </p>
     * <p> Only passes on the data in the message body. </p>
     * @param messageBody The message body of a serialized message.
     */
    public void handleMessage(String messageBody) {
        Logger.info("Received message: "+ messageBody);
        String[] splitMessage = messageBody.split(Splitters.MESSAGE_SPLITTER);

        if (splitMessage.length < 2) {
            Logger.error("Invalid message format." + messageBody);
            return;
        }
        String type = splitMessage[0].split(Splitters.TYPE_SPLITTER)[0];
        Logger.info("Type: " + type);
        switch (type) {
            case "AddNodeMessage" -> this.handleAddNodeMessage(splitMessage[1]);
            case "RemoveNodeMessage" -> this.handleRemoveNodeMessage(splitMessage[1]);
            case "ActuatorUpdateMessage" -> this.handleActuatorUpdateMessage(splitMessage[1]);
            case "SensorUpdateMessage" -> this.handleSensorUpdateMessage(splitMessage[1]);
            default -> Logger.error("Message type could not be identified.");
        }
    }

    /**
     * Parse an AddNodeMessage and trigger the GreenhouseEventListener.onNodeRemoved event.
     * @param bodyData A string containing the data section from the message body.
     */
    private void handleAddNodeMessage(String bodyData) {
        //bodyValues[0] = nodeID -- bodyValues[1] = list of actuators
        String[] bodyValues = bodyData.split(Splitters.BODY_SPLITTER);
        int nodeID = Parser.parseIntegerOrError(bodyValues[0], "Invalid nodeID");
        SensorActuatorNodeInfo nodeInfo = new SensorActuatorNodeInfo(nodeID);
        //Add the collection of actuators to the node.
        String[] actuators = bodyValues[1].split(Splitters.LIST_SPLITTER);
        for (String actuator : actuators) {
            String[] values = actuator.split(Splitters.VALUES_SPLITTER);
            //Parse values to correct data types.
            int actuatorID = Parser.parseIntegerOrError(values[0], "Invalid id: " + nodeID);
            String type = values[1].trim();
            boolean isOn = Boolean.parseBoolean(values[2]);
            //Create new Actuator, set it to the correct state and add it to the node.
            Actuator a = new Actuator(actuatorID, type, nodeID);
            a.set(isOn);
            nodeInfo.addActuator(a);
        }
        this.logic.onNodeAdded(nodeInfo);
    }

    /**
     * Parse a RemoveNodeMessage and trigger the GreenhouseEventListener.onNodeRemoved event.
     * @param bodyData A string containing the data section from the message body.
     */
    private void handleRemoveNodeMessage(String bodyData) {
        int nodeID = Integer.parseInt(bodyData);
        this.logic.onNodeRemoved(nodeID);
        Logger.info("Removed node: " + nodeID);
    }

    /**
     * Parse an ActuatorUpdateMessage and trigger the GreenhouseEventListener.onNodeRemoved event.
     * @param bodyData A string containing the data section from the message body.
     */
    private void handleActuatorUpdateMessage(String bodyData) {
        String[] values = bodyData.split(Splitters.BODY_SPLITTER);
        int nodeID = Integer.parseInt(values[0]);
        int actuatorID = Integer.parseInt(values[1]);
        boolean isOn = Boolean.parseBoolean(values[2]);
        this.logic.onActuatorStateChanged(nodeID, actuatorID, isOn);
        Logger.info("Actuator state changed: " + nodeID + ", " + actuatorID + ", " + isOn);
    }

    /**
     * Parse a SensorUpdateMessage and trigger the GreenhouseEventListener.onSensorData event.
     * @param bodyData A string containing the data section from the message body.
     */
    private void handleSensorUpdateMessage(String bodyData) {
        //values[0] = nodeID -- values[1] = list of sensor readings
        String[] values = bodyData.split(Splitters.BODY_SPLITTER);
        int nodeID = Integer.parseInt(values[0]);
        //Parse all sensor readings and add them to a list of sensor readings.
        ArrayList<SensorReading> sensors = new ArrayList<>();
        String[] sensorReadings = values[1].split(Splitters.LIST_SPLITTER);
        for (String reading : sensorReadings) {
            String[] sensorInfo = reading.split(Splitters.VALUES_SPLITTER);
            String type = sensorInfo[0];
            double value = Parser.parseDoubleOrError(sensorInfo[1]
                    , "Value could not be parsed as a double");
            String unit = sensorInfo[2];
            SensorReading newSensorReading = new SensorReading(type, value, unit);
            sensors.add(newSensorReading);
        }
        this.logic.onSensorData(nodeID, sensors);
    }
}

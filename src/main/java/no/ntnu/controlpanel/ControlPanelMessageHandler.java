package no.ntnu.controlpanel;


import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.gui.controlpanel.ControlPanelApplication;
import no.ntnu.message.Splitters;
import no.ntnu.server.MessageHandler;
import no.ntnu.tools.Logger;
import no.ntnu.tools.Parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class responsible for parsing and handling incoming serialized messages.
 * <p>Has methods for handling all the different types of messages a control panel
 * could receive and interacts with the ControlPanelLogic accordingly.</p>
 */
public class ControlPanelMessageHandler implements MessageHandler {
    private final ControlPanelLogic logic;
    private Actuator actuator;
    private CommunicationChannel communicationChannel;
    private ActuatorCollection actuatorCollection;
    private SensorActuatorNode sensorActuatorNode;
    private ControlPanelApplication controlPanelApplication;

    /**
     * Constructor for a ControlPanelMessageHandler.
     * @param logic The control panel the message handler processes messages for.
     * @throws IllegalArgumentException If logic == null
     */
    public ControlPanelMessageHandler(ControlPanelLogic logic) throws IllegalArgumentException {
        if (logic != null) {
            this.logic = logic;
        } else {
            throw new IllegalArgumentException("ControlPanelLogic requires a ControlPanelLogic to function.");
        }
    }

    /**
     * Handle an incoming serialized message.
     * <p> Uses the "type" from the message header to decide the correct handling method. </p>
     * <p> Sends message body to the correct handler. </p>
     * @param messageBody The message body of a serialized message.
     * @throws IllegalArgumentException if messageBody is null or blank.
     */
    public void handleMessage(String messageBody) throws IllegalArgumentException {
        if (messageBody == null) {
            throw new IllegalArgumentException("Cannot handle a message that is null.");
        } else if (messageBody.isBlank()) {
            throw new IllegalArgumentException("Cannot handle blank message.");
        }
        String[] bodySplit = messageBody.split(Splitters.TYPE_SPLITTER);
        String type = bodySplit[0];
        switch (type) {
            case "AddNodeMessage" -> this.handleAddNodeMessage(bodySplit[1]);
            case "RemoveNodeMessage" -> this.handleRemoveNodeMessage(bodySplit[1]);
            case "ActuatorUpdateMessage" -> this.handleActuatorUpdateMessage(bodySplit[1]);
            case "SensorUpdateMessage" -> this.handleSensorUpdateMessage(bodySplit[1]);
            case "UpdateActuatorByTypeMessage" -> this.handleUpdateActuatorByTypeMessage(bodySplit[1]);
            default -> Logger.error("Message type '" + type + "' could not be identified.");
        }
    }

    /**
     * Handles the update of actuators by type based on the provided message data.
     * Parses the message body to extract actuator type and status, then finds all actuators
     * of the specified type and sends a command to change their status accordingly.
     *
     * @param bodyData a string containing the data section from the message body in the format "actuatorType!isOn"
     */
    private void handleUpdateActuatorByTypeMessage(String bodyData) {
        String[] bodyValues = bodyData.split(Splitters.BODY_SPLITTER);
        Logger.info("The server came here");
        try {
            String type = bodyValues[0];
            boolean isOn = Boolean.parseBoolean(bodyValues[1]);
            this.logic.onActuatorTypeToggle(type,isOn);
        } catch (Exception e) {
            Logger.info("Actuator update failed: " + e.getMessage());
        }
    }

    /**
     * Parse an AddNodeMessage and trigger the GreenhouseEventListener.onNodeRemoved event.
     * <p>Logs an error if data is not valid.</p>
     * @param bodyData A string containing the data section from the message body.
     */
    private void handleAddNodeMessage(String bodyData) {
        //bodyValues[0] = nodeID -- bodyValues[1] = list of actuators
        String[] bodyValues = bodyData.split(Splitters.BODY_SPLITTER);
        int nodeID = Parser.parseIntegerOrError(bodyValues[0], "Invalid nodeID");
        SensorActuatorNodeInfo nodeInfo = new SensorActuatorNodeInfo(nodeID);
        //Try to add a collection of actuators to the node.
        //Throws an Exception if the node has no actuators
        try {
            String[] actuators = bodyValues[1].split(Splitters.LIST_SPLITTER);
            for (String actuator : actuators) {
                String[] values = actuator.split(Splitters.VALUES_SPLITTER);
                //Parse values to correct data types.
                int actuatorID = Parser.parseIntegerOrError(values[0], "Invalid id.");
                String type = values[1];
                boolean isOn = Boolean.parseBoolean(values[2]);
                //Create new Actuator, set it to the correct state and add it to the node.
                Actuator a = new Actuator(actuatorID, type, nodeID);
                a.set(isOn);
                nodeInfo.addActuator(a);
            }
        } catch (Exception e) {
            Logger.info("Node has no actuators.");
        }

        this.logic.onNodeAdded(nodeInfo);
    }

    /**
     * Parse a RemoveNodeMessage and trigger the GreenhouseEventListener.onNodeRemoved event.
     * @param bodyData A string containing the data section from the message body.
     */
    private void handleRemoveNodeMessage(String bodyData) {
        try {
            int nodeID = Integer.parseInt(bodyData);
            this.logic.onNodeRemoved(nodeID);
        } catch (NumberFormatException e) {
            Logger.error("Received invalid RemoveNodeMessage: Invalid nodeID: " + bodyData);
        }
    }

    /**
     * Parse an ActuatorUpdateMessage and trigger the GreenhouseEventListener.onNodeRemoved event.
     * <p>Logs an error if data is not valid</p>
     * @param bodyData A string containing the data section from the message body.
     */
    private void handleActuatorUpdateMessage(String bodyData) {
        String[] values = bodyData.split(Splitters.BODY_SPLITTER);
        try {
            int nodeID = Integer.parseInt(values[0]);
            int actuatorID = Integer.parseInt(values[1]);
            boolean isOn = Boolean.parseBoolean(values[2]);
            this.logic.onActuatorStateChanged(nodeID, actuatorID, isOn);
        } catch (Exception e) {
            Logger.error("Exception " + e + " was thrown due to invalid ActuatorUpdateMessage: "
                    + bodyData);
        }
    }

    /**
     * Parse a SensorUpdateMessage and trigger the GreenhouseEventListener.onSensorData event.
     * <p>Logs an error if data is not valid.</p>
     * @param bodyData A string containing the data section from the message body.
     */
    private void handleSensorUpdateMessage(String bodyData) {
        //values[0] = nodeID -- values[1] = list of sensor readings
        String[] values = bodyData.split(Splitters.BODY_SPLITTER);
        if(values.length < 2) {
            Logger.error("bodyData for SensorUpdateMessage is not valid: " + bodyData);
        }

        int nodeID;
        try {
            nodeID = Integer.parseInt(values[0]);
        } catch (Exception e) {
            Logger.error("Exception " + e + " was thrown due to invalid nodeID in "
                    + "SensorUpdateMessage: " + bodyData);
            return;
        }
        //Parse all sensor readings and add them to a list of sensor readings.
        ArrayList<SensorReading> sensors = new ArrayList<>();
        String[] sensorReadings = values[1].split(Splitters.LIST_SPLITTER);
        for (String reading : sensorReadings) {
            String[] sensorInfo = reading.split(Splitters.VALUES_SPLITTER);
            if (sensorInfo.length < 3) {
                Logger.error("SensorReading: " + Arrays.toString(sensorInfo) + " is not valid.");
                break;
            }
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

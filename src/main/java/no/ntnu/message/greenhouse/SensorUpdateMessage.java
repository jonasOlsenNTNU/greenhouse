package no.ntnu.message.greenhouse;

import no.ntnu.greenhouse.SensorReading;
import no.ntnu.message.common.Message;

import java.util.List;

/**
 * A message readable by a GreenhouseEventListener
 * Specifically by the onSensorData method.
 */
//TODO: splitters should be managed by an enum file according to the protocol
public class SensorUpdateMessage implements Message {

    private final String head;
    private final String body;

    /**
     * Constructor for a SensorUpdateMessage.
     * @param nodeId ID of the node sending the message.
     * @param sensors A list of sensor readings.
     */
    public SensorUpdateMessage(int nodeId, List<SensorReading> sensors) {
        this.head = "controlpanel";
        StringBuilder readings = new StringBuilder();
        for (SensorReading reading : sensors) {
            readings.append(reading.toString()).append(",");
        }
        readings.deleteCharAt(readings.length());
        this.body = nodeId + readings.toString();
    }

    @Override
    public String getMessageString() {
        return this.head + "#" + this.body;
    }
}

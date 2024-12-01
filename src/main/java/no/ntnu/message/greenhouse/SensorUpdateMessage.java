package no.ntnu.message.greenhouse;

import no.ntnu.greenhouse.Sensor;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.message.Message;
import no.ntnu.message.Splitters;

import java.util.List;

/**
 * A message for triggering the GreenhouseEventListener.onSensorData event.
 */
public class SensorUpdateMessage implements Message {
    private final String head;
    private final String body;

    /**
     * Constructor for a SensorUpdateMessage.
     * @param nodeId ID of the node sending the message.
     * @param sensors A list of sensor readings.
     */
    public SensorUpdateMessage(int nodeId, List<Sensor> sensors) {
        this.head = "controlpanel";
        StringBuilder readings = new StringBuilder();
        for (Sensor sensor : sensors) {
            SensorReading reading = sensor.getReading();
            readings.append(reading.getType()).append(Splitters.VALUES_SPLITTER);
            readings.append(reading.getValue()).append(Splitters.VALUES_SPLITTER);
            readings.append(reading.getUnit()).append(Splitters.VALUES_SPLITTER);
            readings.append(Splitters.LIST_SPLITTER);
        }
        readings.deleteCharAt(readings.length() - 1);
        this.body = "SensorUpdateMessage"
                + Splitters.TYPE_SPLITTER + nodeId
                + Splitters.BODY_SPLITTER + readings.toString();
    }

    /**
     * Gets the concatenated message string by combining the head and body with a message splitter.
     *
     * @return The concatenated message string.
     */
    @Override
    public String getMessageString() {
        return this.head + Splitters.MESSAGE_SPLITTER + this.body;
    }
}

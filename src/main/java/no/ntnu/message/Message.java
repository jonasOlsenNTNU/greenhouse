package no.ntnu.message;

/**
 * Message that can be transmitted over a socket connection.
 * See protocol.md for formatting details.
 */
public interface Message {

    /**
     * Get the message as a serialized string.
     * Used to send the message to a socket output stream.
     * @return The serialized message to be sent.
     */
    String getMessageString();
}

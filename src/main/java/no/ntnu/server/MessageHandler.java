package no.ntnu.server;

public interface MessageHandler {

    /**
     * Handle an incoming serialized message.
     * <p> Uses the "type" from the message header to decide the correct handling method. </p>
     * <p> Only passes on the data in the message body. </p>
     * @param messageBody The message body of a serialized message.
     */
    void handleMessage(String messageBody);
}

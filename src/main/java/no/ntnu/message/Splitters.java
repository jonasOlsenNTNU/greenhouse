package no.ntnu.message;

/**
 * A class containing global constants related to Message-formatting.
 */
public final class Splitters {

    /**
     * Separates the head and body of a message.
     * Example: head#body
     */
    public static final String MESSAGE_SPLITTER = "#";
    /**
     * Separates values in the message head.
     * Example: receiver,nodeID
     */
    public static final String HEAD_SPLITTER = ",";
    /**
     * Separates values in the message body.
     * Example: nodeID*actuatorID*isOn
     */
    public static final String BODY_SPLITTER = "*";
    /**
     * Separates the type and rest of the message body.
     * Example: type!body_values
     */
    public static final String TYPE_SPLITTER = "!";
    /**
     * Separates values that are in a list format in the message body.
     * Example: value1;value2;value3
     * Example: {value1, value2, value3};{value4, value5, value6}
     */
    public static final String LIST_SPLITTER = ";";

    public static final String VALUES_SPLITTER = ",";
    private Splitters() {}
}

package no.ntnu.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SplittersTest {

    @Test
    void testMessageSplitter(){
        String message =  "head#body";
        String[] parts =  message.split(Splitters.MESSAGE_SPLITTER);
        assertEquals(2,parts.length);
        assertEquals("head",parts[0]);
        assertEquals("body",parts[1]);
    }
    @Test
    void testHeaderSplitter(){
        String head  = "receiver,nodeID";
        String[] headers = head.split(Splitters.HEAD_SPLITTER);
        assertEquals(2, headers.length);
        assertEquals("receiver", headers[0]);
        assertEquals("nodeID", headers[1]);
    }
    @Test
    void testBodySplitter(){
        String body = "data1*data2*data3";
        String[] bodyParts = body.split(Splitters.BODY_SPLITTER);
        assertEquals(3, bodyParts.length);
        assertEquals("data1", bodyParts[0]);
        assertEquals("data2", bodyParts[1]);
        assertEquals("data3", bodyParts[2]);
    }
    @Test
    void testTypeSplitter(){
        String message = "type!body_values";
        String[] typeParts = message.split(Splitters.TYPE_SPLITTER);
        assertEquals(2, typeParts.length);
        assertEquals("type", typeParts[0]);
        assertEquals("body_values", typeParts[1]);
    }
    @Test
    void testListSplitter(){
        String list = "value1;value2;value3";
        String[] values = list.split(Splitters.LIST_SPLITTER);
        assertEquals(3, values.length);
        assertEquals("value1", values[0]);
        assertEquals("value2", values[1]);
        assertEquals("value3", values[2]);
    }
    @Test
    void testValueSplitter(){
        String values = "value1,value2,value3";
        String[] parts = values.split(Splitters.VALUES_SPLITTER);
        assertEquals(3, parts.length);
        assertEquals("value1", parts[0]);
        assertEquals("value2", parts[1]);
        assertEquals("value3", parts[2]);
    }
}
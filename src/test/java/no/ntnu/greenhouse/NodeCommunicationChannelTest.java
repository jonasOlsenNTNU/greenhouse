package no.ntnu.greenhouse;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class NodeCommunicationChannelTest {
    private NodeCommunicationChannel chanel;
    private SensorActuatorNode nodeMock;
    private PrintWriter writerMock;

    @BeforeEach
    void setUp() {
        nodeMock = mock(SensorActuatorNode.class);
        writerMock = mock(PrintWriter.class);

        NodeCommunicationChannel channel = Mockito.spy(new NodeCommunicationChannel(nodeMock));
        channel.setSocketWriter(writerMock);
    }
}
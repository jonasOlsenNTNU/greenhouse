package no.ntnu.greenhouse;

import no.ntnu.server.Client;

public class NodeCommunicationChannel extends Client {
    private SensorActuatorNode node;
    public NodeCommunicationChannel(SensorActuatorNode node) {
        this.node = node;
    }

}

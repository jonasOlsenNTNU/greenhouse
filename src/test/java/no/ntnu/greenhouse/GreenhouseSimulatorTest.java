package no.ntnu.greenhouse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GreenhouseSimulatorTest {
    private GreenhouseSimulator testSimulator;

    @BeforeEach
    void setUp(){
        testSimulator = new GreenhouseSimulator(true);
    }
    @Test
    void testInitialize() {
        testSimulator.initialize();
        assertEquals(1,testSimulator.getNodes().size(),"The simulator should create 3 nodes.");
    }
    @Test
    void testStartAndStopFakeMode(){
        testSimulator.initialize();
        testSimulator.start();
        testSimulator.getNodes().values().forEach(node -> assertTrue(node.isRunning(),
                "Node should be running"));
        testSimulator.stop();
        testSimulator.getNodes().values().forEach(node -> assertFalse(node.isRunning()));
    }

}
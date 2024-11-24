package no.ntnu.run;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.tools.Logger;

/**
 * Entrypoint for running the Greenhouse Simulator.
 */
public class GreenHouseRun {

    public static void main(String[] args) {
        Logger.info("Starting Greenhouse Simulator...");

        boolean fake = false; //Set to true for fake communication.

        if (args.length == 1 && "fake".equalsIgnoreCase(args[0])) {
            fake = true;
            Logger.info("Running Greenhouse Simulator in FAKE mode.");
        }

        GreenhouseSimulator greenhouseSimulator = new GreenhouseSimulator(fake);

        // Initialize the greenhouse: set up nodes and actuators
        greenhouseSimulator.initialize();

        // Start the greenhouse simulation: begin server and node operations
        greenhouseSimulator.start();

        Logger.info("Greenhouse Simulator is running.");

        // Add shutdown hook to ensure the greenhouse simulator stops cleanly
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Logger.info("Stopping Greenhouse Simulator...");
            greenhouseSimulator.stop();
            Logger.info("Greenhouse Simulator has stopped.");
        }));
    }
}

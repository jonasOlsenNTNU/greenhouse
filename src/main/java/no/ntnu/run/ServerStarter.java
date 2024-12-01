package no.ntnu.run;

import no.ntnu.server.Server;

/**
 * Class to start the server for handling communication between greenhouse nodes and control panels.
 */
public class ServerStarter {

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

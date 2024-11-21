package no.ntnu.run;

import no.ntnu.server.Server;

public class ServerStarter {

    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }
}

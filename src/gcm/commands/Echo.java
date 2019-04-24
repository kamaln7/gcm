package gcm.commands;

import gcm.server.Server;

public class Echo extends Command {
    public static String NAME = "echo";

    public Echo(String msg) {
        super();
        this.args = msg;
    }

    public Echo() {

    }

    @Override
    public void runOnServer(Server server) {
        server.sendToAllClients(this.args);
    }
}

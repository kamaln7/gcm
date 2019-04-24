package gcm.server;

import gcm.commands.Command;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;

public class Server extends AbstractServer {
    private Settings settings;
    private ChatIF chatIF;

    public Server(Settings settings, ChatIF chatIF) {
        super(settings.port);

        this.settings = settings;
        this.chatIF = chatIF;
    }

    public void start() throws IOException {
        int port = this.settings.port;
        this.chatIF.display("Starting server on port " + port);
        this.listen();
    }

    @Override
    public void listen(int port) {
        this.setPort(port);
        try {
            this.listen();
        } catch (Exception e) {
            // yeah idk
        }
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        this.chatIF.displayf("client msg: %s\nisCommand: %s", msg, msg instanceof Command);
    }

    public void handleMessageFromServerConsole(String msg) {
        this.chatIF.displayf("server console msg: %s", msg);
    }
}

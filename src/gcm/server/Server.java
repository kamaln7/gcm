package gcm.server;

import com.beust.jcommander.JCommander;
import gcm.ChatIF;
import gcm.commands.Command;
import gcm.commands.Echo;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Server extends AbstractServer {
    private Settings settings;
    private ChatIF chatIF;

    public Server(Settings settings, ChatIF chatIF) {
        super(settings.port);

        this.settings = settings;
        this.chatIF = chatIF;
    }

    public void start() throws IOException {
        this.chatIF.displayf("Starting server on port %s", this.settings.port);
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
        if (msg instanceof Command) {
            Command cmd = (Command) msg;
            cmd.runOnServer(this);
        }
    }

    public void handleMessageFromServerConsole(String msg) {

    }
}

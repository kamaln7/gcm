package gcm.client;

import gcm.ChatIF;
import gcm.commands.Command;
import gcm.commands.Echo;
import ocsf.client.AbstractClient;

import java.io.EOFException;
import java.io.IOException;
import java.util.UUID;

public class Client extends AbstractClient {
    private Settings settings;
    private ChatIF chatIF;

    public Client(Settings settings, ChatIF chatIF) {
        super(settings.host, settings.port);

        this.settings = settings;
        this.chatIF = chatIF;
    }

    public void start() throws IOException {
        this.chatIF.displayf("Connecting to server at %s:%d", this.settings.host, this.settings.port);
        this.openConnection();
    }

    @Override
    protected void connectionClosed() {
        this.chatIF.display("Connection closed");
        System.exit(0);
    }

    @Override
    protected void connectionException(Exception e) {
        if (e instanceof EOFException) {
            this.connectionClosed();
            return;
        }

        this.chatIF.display("ERR: connection exception");
        e.printStackTrace();
        System.exit(1);
    }

    @Override
    protected void connectionEstablished() {
        this.chatIF.display("Connected");
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        this.chatIF.displayf("server msg: %s\nisCommand: %s", msg, msg instanceof Command);
        if (!(msg instanceof Command)) {
            return;
        }

        Command cmd = (Command) msg;
        cmd.clientOnReply.accept(cmd);
    }

    public void handleMessageFromClientConsole(String msg) {
        this.chatIF.displayf("sending msg to server: %s", msg);
        try {
            Echo cmd = new Echo(msg);
            cmd.id = UUID.randomUUID().toString();

            this.sendToServer(cmd);
            this.chatIF.display("sent message");
        } catch (IOException e) {
            this.chatIF.display("ERR: couldn't send message");
            e.printStackTrace();
        }
    }
}

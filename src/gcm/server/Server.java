package gcm.server;

import gcm.ChatIF;
import gcm.commands.*;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Server extends AbstractServer {
    private Settings settings;
    private ChatIF chatIF;
    private HashMap<String, ConnectionToClient> clientConnections = new HashMap<>();

    // <-- commands
    private static HashMap<Class<? extends Request>, Class<? extends gcm.commands.Command<? extends gcm.commands.Request, ? extends gcm.commands.Response>>> requestToCommandMap = new HashMap<>();

    static {
        requestToCommandMap.put(BroadcastCommandRequest.class, BroadcastCommand.class);
        requestToCommandMap.put(EchoCommandRequest.class, EchoCommand.class);
    }
    // --> commands

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
    protected void clientConnected(ConnectionToClient client) {
        String id = UUID.randomUUID().toString();
        client.setInfo("id", id);
        this.clientConnections.put(id, client);

        this.chatIF.displayf("Client [%s] connected, assigned id [%s]", client, id);
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
        this.chatIF.displayf("Received msg from [%s]: %s\nisRequest: %s", client, msg, msg instanceof Request);
        if (!(msg instanceof Request)) {
            return;
        }

        if (!requestToCommandMap.containsKey(msg.getClass())) {
            this.chatIF.displayf("Received unknown request [%s] from client [%s]", msg.getClass(), client);
            try {
                client.sendToClient("ERR: Unknown request " + msg.getClass());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            Request req = (Request) msg;
            Response res = requestToCommandMap.get(msg.getClass()).newInstance().New(this).runOnServer(req, client);
            client.sendToClient(res);
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessageFromServerConsole(String msg) {
        this.chatIF.displayf("server console commands are not implemented");
    }
}

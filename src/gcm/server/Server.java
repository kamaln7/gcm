package gcm.server;

import com.google.gson.Gson;
import gcm.ChatIF;
import gcm.commands.Command;
import gcm.commands.Request;
import gcm.commands.Response;
import gcm.common.GsonSingleton;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class Server extends AbstractServer {
    private Gson gson = GsonSingleton.GsonSingleton().gson;
    private Settings settings;
    private ChatIF chatIF;
    private HashMap<String, ConnectionToClient> clientConnections = new HashMap<>();

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

        try {
            Request request = (Request) msg;
            Command cmd = request.command.newInstance();

            Object output = cmd.runOnServer(request, this, client);
            Response response = new Response(request.id, request.command, gson.toJson(output));
            client.sendToClient(response);
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    public void handleMessageFromServerConsole(String msg) {
        this.chatIF.displayf("server console commands are not implemented");
    }
}

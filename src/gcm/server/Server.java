package gcm.server;

import com.google.gson.Gson;
import gcm.ChatIF;
import gcm.commands.Command;
import gcm.commands.Output;
import gcm.commands.Request;
import gcm.commands.Response;
import gcm.common.GsonSingleton;
import gcm.database.models.Model;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;

public class Server extends AbstractServer {
    private Gson gson = GsonSingleton.GsonSingleton().gson;

    private Settings settings;
    private ChatIF chatIF;
    private HashMap<String, ConnectionToClient> clientConnections = new HashMap<>();
    private Connection db;

    public Server(Settings settings, ChatIF chatIF) throws SQLException {
        super(settings.port);
        this.chatIF.display("Initializing server");

        this.settings = settings;
        this.chatIF = chatIF;

        this.chatIF.display("Connecting to the database");
        this.db = DriverManager.getConnection(this.settings.connectionString);
        Model.setDb(this.db);
    }

    public void start() throws IOException {
        this.chatIF.displayf("Starting OCSF server on port %s", this.settings.port);
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
            this.chatIF.displayf("couldn't set port to %d", port);
        }
    }

    @Override
    protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
        this.chatIF.displayf("Received msg from [%s]: %s", client, msg);
        if (!(msg instanceof Request)) {
            return;
        }

        // The message is of type Request
        // process in new thread
        (new Thread(() -> {
            try {
                Request request = (Request) msg;
                Command cmd = request.command.newInstance();

                // run the command
                Exception exception = null;
                Output output = null;
                try {
                    output = cmd.runOnServer(request, this, client);
                } catch (Exception e) {
                    exception = e;
                }

                // return command output to client as a Response
                Response response = new Response(request, output, exception);
                client.sendToClient(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        })).start();
    }

    public void handleMessageFromServerConsole(String msg) {
        this.chatIF.displayf("server console commands are not implemented");
    }
}

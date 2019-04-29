package gcm.client;

import com.google.gson.Gson;
import gcm.ChatIF;
import gcm.commands.BroadcastCommand;
import gcm.commands.Request;
import gcm.commands.Response;
import gcm.common.GsonSingleton;
import ocsf.client.AbstractClient;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class Client extends AbstractClient {
    private Gson gson = GsonSingleton.GsonSingleton().gson;
    private Settings settings;
    private ChatIF chatIF;
    private HashMap<String, CompletableFuture<Response>> pendingCommands;

    public Client(Settings settings, ChatIF chatIF) {
        super(settings.host, settings.port);

        this.settings = settings;
        this.chatIF = chatIF;
        this.pendingCommands = new HashMap<>();
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
        this.chatIF.displayf("server msg: %s\nisResponse: %s", msg, msg instanceof Response);
        if (!(msg instanceof Response)) {
            return;
        }

        (new Thread(() -> {
            Response response = (Response) msg;
            String id = response.id;
            if (!this.pendingCommands.containsKey(id)) {
                return;
            }

            this.pendingCommands.get(id).complete(response);
            this.pendingCommands.remove(id);
        })).start();
    }

    public void handleMessageFromClientConsole(String msg) {
        (new Thread(() -> {
            this.chatIF.displayf("sending msg to server: %s", msg);
            try {
                String id = UUID.randomUUID().toString();
                BroadcastCommand.Input input = new BroadcastCommand.Input("hello there");
                Request request = new Request(id, BroadcastCommand.class, gson.toJson(input));
                CompletableFuture<Response> responseF = new CompletableFuture<>();
                this.pendingCommands.put(id, responseF);
                this.sendToServer(request);

                Response response = responseF.get();
                this.chatIF.display("got response for " + response.id);
            } catch (Exception e) {
                this.chatIF.display("ERR: couldn't send message");
                e.printStackTrace();
            }
        })).start();
    }
}

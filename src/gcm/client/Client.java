package gcm.client;

import gcm.ChatIF;
import gcm.commands.*;
import ocsf.client.AbstractClient;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Client extends AbstractClient {
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

        Response response = (Response) msg;
        String id = response.getClass().cast(response).id;
        if (!this.pendingCommands.containsKey(id)) {
            return;
        }

        this.pendingCommands.get(id).complete(response);
        this.pendingCommands.remove(id);
    }

    public void handleMessageFromClientConsole(String msg) {
        (new Thread(() -> {
            this.chatIF.displayf("sending msg to server: %s", msg);
            try {
                String id = UUID.randomUUID().toString();
                Request request;
                if (msg.startsWith("bb")) {
                    request = new BroadcastCommandRequest(id, msg);
                    CompletableFuture<Response> response = new CompletableFuture<>();
                    this.pendingCommands.put(id, response);
                    this.sendToServer(request);
                    BroadcastCommandResponse r = (BroadcastCommandResponse) response.get();
                    this.chatIF.displayf("broadcast command response is %s %s", r, r.ok);
                } else {
                    request = new EchoCommandRequest(id, msg);
                    CompletableFuture<Response> response = new CompletableFuture<>();
                    this.pendingCommands.put(id, response);
                    this.sendToServer(request);
                    EchoCommandResponse r = ((EchoCommandResponse) response.get());
                    this.chatIF.displayf("echo command response is %s %s", r, r.ok);
                }
            } catch (IOException | InterruptedException | ExecutionException e) {
                this.chatIF.display("ERR: couldn't send message");
                e.printStackTrace();
            }
        })).start();
    }
}

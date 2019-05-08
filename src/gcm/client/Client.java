package gcm.client;

import com.google.gson.Gson;
import gcm.ChatIF;
import gcm.commands.FindUserByIdCommand;
import gcm.commands.Input;
import gcm.commands.Request;
import gcm.commands.Response;
import gcm.common.GsonSingleton;
import ocsf.client.AbstractClient;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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
            Integer id = Integer.parseInt(msg);
            this.chatIF.displayf("finding user with id %d", id);
            try {
                Input input = new FindUserByIdCommand.Input(id);
                Response response = this.sendInputAndWaitForResponse(input);
                FindUserByIdCommand.Output output = response.getOutput(FindUserByIdCommand.Output.class);

                this.chatIF.displayf("got response for %s status: %s", response.id, output.user);
            } catch (Exception e) {
                this.chatIF.display("ERR: couldn't send message");
                e.printStackTrace();
            }
        })).start();
    }

    private Response sendInputAndWaitForResponse(Input input) throws InterruptedException, ExecutionException, IOException {
        Request request = new Request(input);
        return this.sendRequestAndWaitForResponse(request);
    }

    private Response sendRequestAndWaitForResponse(Request request) throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<Response> response = new CompletableFuture<>();
        this.pendingCommands.put(request.id, response);
        this.sendToServer(request);
        return response.get();
    }
}

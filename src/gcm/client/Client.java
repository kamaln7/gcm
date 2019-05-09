package gcm.client;

import com.google.gson.Gson;
import gcm.ChatIF;
import gcm.commands.FindUserByIdCommand;
import gcm.commands.Input;
import gcm.commands.Request;
import gcm.commands.Response;
import gcm.common.GsonSingleton;
import gcm.database.models.User;
import ocsf.client.AbstractClient;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Client extends AbstractClient {
    private Gson gson = GsonSingleton.GsonSingleton().gson;
    private Settings settings;
    private HashMap<String, CompletableFuture<Response>> pendingCommands;


    public ChatIF chatIF;

    public Client(Settings settings, ChatIF chatIF) {
        super(settings.host, settings.port);

        this.settings = settings;
        this.chatIF = chatIF;
        this.pendingCommands = new HashMap<>();
    }

    // connects to the server
    public void start() throws IOException {
        this.chatIF.displayf("Connecting to server at %s:%d", this.settings.host, this.settings.port);
        this.openConnection();
    }

    @Override
    protected void connectionClosed() {
        this.chatIF.display("Connection to server closed");
        System.exit(0);
    }

    @Override
    protected void connectionException(Exception e) {
        if (e instanceof EOFException) {
            // this means the server shut down
            this.connectionClosed();
            return;
        }

        this.chatIF.display("ERR: Server connection exception");
        e.printStackTrace();
        System.exit(1);
    }

    @Override
    protected void connectionEstablished() {
        this.chatIF.display("Connected to server");
    }

    @Override
    protected void handleMessageFromServer(Object msg) {
        this.chatIF.displayf("Server sent msg: %s", msg);
        if (!(msg instanceof Response)) {
            return;
        }

        // received a Response from the server
        (new Thread(() -> {
            Response response = (Response) msg;

            // get the command Request id and find its CompletableFuture
            String id = response.id;
            if (!this.pendingCommands.containsKey(id)) {
                return;
            }

            // return the Response to the caller function in Client
            // and remove it from the pendingCommands map
            this.pendingCommands.remove(id).complete(response);
        })).start();
    }

    public void handleMessageFromClientConsole(String msg) {
        // received a message from the client console
        (new Thread(() -> {
            Integer id = -1;
            try {
                id = Integer.parseInt(msg);
                this.chatIF.displayf("finding user with id %d", id);
                Input input = new FindUserByIdCommand.Input(id);
                Response response = this.sendInputAndWaitForResponse(input);
                FindUserByIdCommand.Output output = response.getOutput(FindUserByIdCommand.Output.class);

                this.chatIF.displayf("got response for %s status: %s", response.id, output.user);
            } catch (User.NotFound e) {
                this.chatIF.displayf("user with id %d wasn't found", id);
            } catch (Exception e) {
                this.chatIF.display("ERR: couldn't send message");
                e.printStackTrace();
            }
        })).start();
    }

    // Takes an Input, creates a Request object and calls sendRequestAndWaitForResponse()
    public Response sendInputAndWaitForResponse(Input input) throws InterruptedException, ExecutionException, IOException {
        Request request = new Request(input);
        return this.sendRequestAndWaitForResponse(request);
    }

    // sends the request to the server and waits for a response then returns it
    private Response sendRequestAndWaitForResponse(Request request) throws IOException, ExecutionException, InterruptedException {
        CompletableFuture<Response> response = new CompletableFuture<>();
        this.pendingCommands.put(request.id, response);
        this.sendToServer(request);
        return response.get();
    }
}

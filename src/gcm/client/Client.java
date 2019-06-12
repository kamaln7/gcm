package gcm.client;

import com.beust.jcommander.internal.Lists;
import com.google.gson.Gson;
import com.sun.javafx.stage.StageHelper;
import gcm.ChatIF;
import gcm.client.bin.ClientGUI;
import gcm.client.controllers.ConnectionSettingsController;
import gcm.commands.Input;
import gcm.commands.LogoutUserCommand;
import gcm.commands.Request;
import gcm.commands.Response;
import gcm.common.GsonSingleton;
import javafx.application.Platform;
import javafx.stage.Stage;
import ocsf.client.AbstractClient;

import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class Client extends AbstractClient {
    private final long TimeoutTime = 10;
    private final TimeUnit TimeoutUnit = TimeUnit.SECONDS;
    private final String TimeoutMessage = String.format("Command timed out after waiting %s %s. No response from server.", TimeoutTime, TimeoutUnit);

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

        Platform.runLater(() -> {
            try {
                List<Stage> stages = Lists.newArrayList(StageHelper.getStages());
                stages.forEach(s -> s.close());

                Stage newStage = new Stage();
                ClientGUI.setPrimaryStage(newStage);
                ConnectionSettingsController.loadView(newStage);
                ClientGUI.showErrorTryAgain("Connection to server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
        (new Thread(() -> {
            this.chatIF.displayf("Server sent msg: %s", msg);
            if (!(msg instanceof Response)) {
                return;
            }

            // received a Response from the server
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
        chatIF.display("client console commands aren't supported");
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

        // time out if server never sends a response
        Executors.newScheduledThreadPool(1).schedule(() -> {
            if (!response.isDone()) {
                response.completeExceptionally(new CancellationException(TimeoutMessage));
                response.cancel(true);
            }
        }, TimeoutTime, TimeoutUnit);
        return response.get();
    }

    public void stop() throws IOException {
        this.closeConnection();
    }

    public void logout() throws InterruptedException, ExecutionException, IOException {
        this.sendInputAndWaitForResponse(new LogoutUserCommand.Input());
    }

    public Settings getSettings() {
        return settings;
    }
}

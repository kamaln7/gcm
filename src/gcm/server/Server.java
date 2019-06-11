package gcm.server;

import com.google.gson.Gson;
import gcm.ChatIF;
import gcm.commands.Command;
import gcm.commands.Output;
import gcm.commands.Request;
import gcm.commands.Response;
import gcm.common.GsonSingleton;
import gcm.database.models.Model;
import gcm.database.models.ServerJob;
import gcm.database.models.User;
import gcm.exceptions.AlreadyLoggedIn;
import gcm.server.jobs.Job;
import gcm.server.jobs.SubscriptionExpiryNotification;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Server extends AbstractServer {
    private Gson gson = GsonSingleton.GsonSingleton().gson;

    private Settings settings;
    private ChatIF chatIF;
    private HashMap<String, ConnectionToClient> clientConnections = new HashMap<>();
    private ArrayList<Integer> loggedInUserIds = new ArrayList<>();
    private Connection db;

    private static final List<Class<? extends Job>> registeredJobs = new ArrayList<Class<? extends Job>>() {
        {
            add(SubscriptionExpiryNotification.class);
        }
    };

    public Server(Settings settings, ChatIF chatIF) throws Exception {
        super(settings.port);
        this.settings = settings;
        this.chatIF = chatIF;

        this.chatIF.display("Initializing server");
        this.chatIF.displayf("Using directory [%s] for files", this.getFilesPath());
        Path filesPathDir = Paths.get(this.getFilesPath());
        if (!Files.exists(filesPathDir)) {
            this.chatIF.displayf("Created directory [%s] because it didn't exist", filesPathDir);
            Files.createDirectory(filesPathDir);
        }

        this.chatIF.display("Connecting to the database");

        // MySQL connection
        this.db = DriverManager.getConnection(this.settings.connectionString);
        Model.setDb(this.db);

        // scheduled jobs
        scheduleJobs();
    }

    private void scheduleJobs() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        for (Class<? extends Job> jobC : registeredJobs) {
            Job job = jobC.getDeclaredConstructor(Server.class).newInstance(this);
            workJob(executor, job);
        }
    }

    private void workJob(ExecutorService executor, Job job) {
        (new Thread(() -> {
            // find the last time the job was run and find out how much we need to wait until the next execution
            try {
                long nextSleep;
                try {
                    ServerJob sj = ServerJob.findLatestByName(job.getName());
                    Date now = new Date(),
                            lastRunDate = sj.getCreatedAt();
                    // how long ago the last time this job was run
                    long diff = TimeUnit.SECONDS.convert(now.getTime() - lastRunDate.getTime(), TimeUnit.MILLISECONDS);

                    // convert delay from seconds to milliseconds
                    nextSleep = (diff < job.getInterval()) ? (job.getInterval() - diff) * 1000 : 0;
                } catch (ServerJob.NotFound notFound) {
                    nextSleep = 0;
                }

                while (true) {
                    if (nextSleep > 0) {
                        Thread.sleep(nextSleep);
                    }
                    Future<Void> future = executor.submit(job);
                    future.get();
                    nextSleep = job.getInterval() * 1000;
                }
            } catch (Exception e) {
                System.err.printf("Exception while working job %s", job.getName());
                e.printStackTrace();
            }
        })).start();
    }

    public ChatIF getChatIF() {
        return chatIF;
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
    protected synchronized void clientDisconnected(ConnectionToClient client) {
        logout(client);
        this.chatIF.displayf("Client [%s] [id=%s] disconnected", client, client.getInfo("id"));
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
                this.chatIF.displayf("Sent msg to [%s]: %s", client, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        })).start();
    }

    public void handleMessageFromServerConsole(String msg) {
        this.chatIF.displayf("server console commands are not implemented");
    }

    public void login(ConnectionToClient client, User user) throws AlreadyLoggedIn {
        Integer id = user.getId();
        if (loggedInUserIds.contains(id)) {
            throw new AlreadyLoggedIn();
        }

        client.setInfo("userId", id);
        loggedInUserIds.add(id);
    }

    public void logout(ConnectionToClient client) {
        Integer id = (Integer) client.getInfo("userId");
        chatIF.displayf("Client [%s] logged out userId=%s", client, String.valueOf(id));
        if (id == null) {
            return;
        }

        loggedInUserIds.removeAll(Arrays.asList(id));
        client.setInfo("userId", null);
    }

    public String getFilesPath() {
        return this.settings.filesPath;
    }
}

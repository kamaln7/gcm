package gcm.server.bin;

import com.beust.jcommander.JCommander;
import gcm.server.ChatIF;
import gcm.server.Server;
import gcm.server.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole implements ChatIF {
    private Server server;

    public static void main(String[] cliArgs) {
        // parse cli args
        Args args = new Args();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(cliArgs);

        ServerConsole serverConsole = new ServerConsole(args);

        try {
            serverConsole.start();
        } catch (IOException e) {
            serverConsole.display("ERR: couldn't start server");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void start() throws IOException {
        // start server
        this.server.start();
        // accept console commands
        this.accept();
    }

    private void accept() {
        try {
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true) {
                message = fromConsole.readLine();
                server.handleMessageFromServerConsole(message);
            }
        } catch (Exception ex) {
            this.display("Unexpected error while reading from console!");
        }
    }

    public ServerConsole(Args args) {
        this.server = new Server(
                new Settings(
                        args.getPort(),
                        args.getConnectionString()
                ),
                this
        );
    }

    @Override
    public void display(String message) {
        System.out.println("> " + message);
    }
}
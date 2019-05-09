package gcm.server.bin;

import com.beust.jcommander.JCommander;
import gcm.ChatIF;
import gcm.server.Server;
import gcm.server.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ServerConsole implements ChatIF {
    private Server server;

    public static void main(String[] cliArgs) {
        // read cli args
        Args args = new Args();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(cliArgs);

        // create server and start listening for connections
        try {
            ServerConsole serverConsole = new ServerConsole(args);
            serverConsole.start();
        } catch (Exception e) {
            System.err.println("ERR: couldn't start server");
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
                this.server.handleMessageFromServerConsole(message);
            }
        } catch (Exception ex) {
            this.display("Unexpected error while reading from console!");
        }
    }

    public ServerConsole(Args args) throws Exception {
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
        System.out.println("# " + message);
    }
}
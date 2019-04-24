package gcm.client.bin;


import com.beust.jcommander.JCommander;
import gcm.client.Client;
import gcm.ChatIF;
import gcm.client.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ClientConsole implements ChatIF {
    private Client client;

    public static void main(String[] cliArgs) {
        // parse cli args
        Args args = new Args();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(cliArgs);

        ClientConsole clientConsole = new ClientConsole(args);

        try {
            clientConsole.start();
        } catch (IOException e) {
            clientConsole.display("ERR: couldn't start server");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void start() throws IOException {
        // start server
        this.client.start();
        // accept console commands
        this.accept();
    }

    private void accept() {
        try {
            BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
            String message;

            while (true) {
                message = fromConsole.readLine();
                this.client.handleMessageFromClientConsole(message);
            }
        } catch (Exception ex) {
            this.display("Unexpected error while reading from console!");
        }
    }

    public ClientConsole(Args args) {
        this.client = new Client(
                new Settings(
                        args.getHost(),
                        args.getPort()
                ),
                this
        );
    }

    @Override
    public void display(String message) {
        System.out.println("> " + message);
    }
}
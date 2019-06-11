package gcm.client.bin;


import com.beust.jcommander.JCommander;
import gcm.ChatIF;
import gcm.client.Client;
import gcm.client.Settings;
import javafx.application.Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientConsole implements ChatIF {
    private Client client;

    public static void main(String[] cliArgs) {
        // read cli args
        Args args = new Args();
        JCommander.newBuilder()
                .addObject(args)
                .build()
                .parse(cliArgs);

        // create and start client console
        // connects to server on start
        ClientConsole clientConsole = new ClientConsole(args);

        clientConsole.display("Starting GUI");
        ClientGUI.setClient(clientConsole.client);
        Application.launch(ClientGUI.class);
    }

    private void start() {
        try {
            // start server
            this.client.start();
            // accept console commands
            this.accept();
        } catch (IOException e) {
            display("ERR: couldn't start server");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void accept() {
        (new Thread(() -> {
            // this reads input from the command line console and sends it to the Client
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
        })).start();
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
        System.out.printf("|%s> %s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), message);
    }
}
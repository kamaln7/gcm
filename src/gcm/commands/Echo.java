package gcm.commands;

import gcm.server.Server;

import java.io.Serializable;

public class Echo extends Command implements Serializable {
    public class Input implements Serializable {
        public String message;

        public Input(String message) {
            this.message = message;
        }
    }

    public class Output implements Serializable {
        public String message;

        public Output(String message) {
            this.message = message;
        }
    }

    public Input input;
    public Output output;

    public Echo(String msg) {
        super();
        this.input = new Input(msg);
    }

    @Override
    public void runOnServer(Server server) {
        Output output = new Output(this.input.message);
        this.output = output;
        server.sendToAllClients("Echo command requested: " + this.input.message);
    }
}

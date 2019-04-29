package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class BroadcastCommand implements Command {
    public static class Input {
        public String message;

        public Input(String message) {
            this.message = message;
        }
    }

    public static class Output {
        public Boolean ok;

        public Output(Boolean ok) {
            this.ok = ok;
        }
    }

    @Override
    public Object runOnServer(Request request, Server server, ConnectionToClient client) {
        Input input = gson.fromJson(request.input, Input.class);
        server.sendToAllClients("Broadcasted: " + input.message);

        return new Output(true);
    }
}

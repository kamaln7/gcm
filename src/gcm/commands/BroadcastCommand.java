package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class BroadcastCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String message;

        public Input(String message) {
            this.message = message;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Boolean ok;

        public Output(Boolean ok) {
            this.ok = ok;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) {
        Input input = request.getInput(Input.class);
        server.sendToAllClients("Broadcasted: " + input.message);

        return new Output(true);
    }
}

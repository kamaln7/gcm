package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class LogoutUserCommand implements Command {
    public static class Input extends gcm.commands.Input {
    }

    public static class Output extends gcm.commands.Output {
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) {
        server.logout(client);

        return new Output();
    }
}

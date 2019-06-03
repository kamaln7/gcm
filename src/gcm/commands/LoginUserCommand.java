package gcm.commands;

import gcm.database.models.User;
import gcm.exceptions.AlreadyLoggedIn;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class LoginUserCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String username, password;

        public Input(String username, String password) {
            this.username = username;
            this.password = password;
        }
    }

    public static class Output extends gcm.commands.Output {
        public User user;

        public Output(User user) {
            this.user = user;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        User user = User.login(input.username, input.password);

        try {
            server.login(client, user);
        } catch (AlreadyLoggedIn e) {
            server.getChatIF().displayf("Client [%s] tried to log in as id=%d username=%s but refused because already logged in.", client, user.getId(), user.getUsername());
            throw e;
        }

        server.getChatIF().displayf("Client [%s] logged in as id=%d username=%s", client, user.getId(), user.getUsername());

        return new Output(user);
    }
}

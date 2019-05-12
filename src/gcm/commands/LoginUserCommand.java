package gcm.commands;

import gcm.database.models.User;
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

        client.setInfo("userId", user.getId());

        return new Output(user);
    }
}

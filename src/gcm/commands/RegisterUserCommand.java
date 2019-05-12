package gcm.commands;

import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class RegisterUserCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String username, password, email, phone;

        public Input(String username, String password, String email, String phone) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
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

        User user = new User(input.username, input.password, input.email, input.phone);
        user.register();

        client.setInfo("userId", user.getId());

        return new Output(user);
    }
}

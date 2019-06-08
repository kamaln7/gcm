package gcm.commands;

import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class RegisterUserCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String username, password, email, phone, first_name, last_name;

        public Input(String username, String password, String email, String phone, String first_name, String last_name) {
            this.username = username;
            this.password = password;
            this.email = email;
            this.phone = phone;
            this.first_name = first_name;
            this.last_name = last_name;
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

        User user = new User(input.username, input.password, input.email, input.phone, input.first_name, input.last_name);
        user.register();

        client.setInfo("userId", user.getId());

        return new Output(user);
    }
}

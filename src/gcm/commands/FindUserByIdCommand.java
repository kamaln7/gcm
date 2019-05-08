package gcm.commands;

import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class FindUserByIdCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer id;

        public Input(Integer id) {
            this.id = id;
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
        User user = User.findById(input.id);

        return new Output(user);
    }
}

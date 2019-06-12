package gcm.commands;

import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class ShowUserInfoCommand implements Command {

    public static class Input extends gcm.commands.Input {
    }

    public class Output extends gcm.commands.Output {

        public List<User> usersList;

        public Output(List<User> users) {
            this.usersList = users;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        List<User> users = User.findAllUsersWithCounts();

        return new Output(users);
    }
}
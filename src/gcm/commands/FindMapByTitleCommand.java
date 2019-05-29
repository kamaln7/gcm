package gcm.commands;

import gcm.database.models.Map;
import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class FindMapByTitleCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String title;

        public Input(String title) {
            this.title = title;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Map map;

        public Output(Map map) {
            this.map = map;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        Map map = Map.findByTitle(input.title);

        return new Output(map);
    }
}

package gcm.commands;

import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class UpdateMapTitleAndDescriptionCommand implements Command {
    public static class Input extends gcm.commands.Input {
        private Map map;

        public Input(Map map) {
            this.map = map;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Output() {
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        input.map.updateDescriptionAndTitle();

        return new Output();
    }
}

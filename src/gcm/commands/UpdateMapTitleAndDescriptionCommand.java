package gcm.commands;

import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

/**
 * used when user edit map
 */
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

    /**
     * runs the command on the server
     * @param request
     * @param server
     * @param client
     * @return
     * @throws Exception
     */
    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        input.map.updateDescriptionAndTitle();

        return new Output();
    }
}

package gcm.commands;

import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class GetAllMapsWithCityTitleCommand implements Command {
    public static class Input extends gcm.commands.Input {
    }

    public static class Output extends gcm.commands.Output {
        public List<Map> maps;

        public Output(List<Map> maps) {
            this.maps = maps;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        List<Map> maps = Map.findAllWithCityTitle();

        return new Output(maps);
    }
}

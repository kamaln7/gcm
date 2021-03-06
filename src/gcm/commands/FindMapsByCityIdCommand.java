package gcm.commands;

import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class FindMapsByCityIdCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer cityId;

        public Input(Integer cityId) {
            this.cityId = cityId;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<Map> maps;

        public Output(List<Map> maps) {
            this.maps = maps;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<Map> maps = Map.findAllByCityId(input.cityId);

        return new Output(maps);
    }
}

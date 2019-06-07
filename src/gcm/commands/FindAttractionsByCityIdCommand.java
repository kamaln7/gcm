package gcm.commands;

import gcm.database.models.Attraction;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class FindAttractionsByCityIdCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer cityId;

        public Input(Integer cityId) {
            this.cityId = cityId;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<Attraction> attractions;

        public Output(List<Attraction> attractions) {
            this.attractions = attractions;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<Attraction> attractions = Attraction.findAllByCityId(input.cityId);

        return new Output(attractions);
    }
}

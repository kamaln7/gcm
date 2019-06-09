package gcm.commands;

import gcm.database.models.Map;
import gcm.database.models.Tour;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class FindToursByCityIdCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer cityId;

        public Input(Integer cityId) {
            this.cityId = cityId;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<Tour> tours;

        public Output(List<Tour> tours) {
            this.tours = tours;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<Tour> tours = Tour.findAllByCityId(input.cityId);

        return new Output(tours);
    }
}

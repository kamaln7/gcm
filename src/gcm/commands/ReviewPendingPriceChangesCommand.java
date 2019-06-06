package gcm.commands;

import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class ReviewPendingPriceChangesCommand implements Command {
    public static class Input extends gcm.commands.Input {
    }

    public static class Output extends gcm.commands.Output {
        public List<City> result;


        public Output(List<City> result) {
            this.result = result;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        List<City> result = City.findUnapproved();
        return new Output(result);
    }
}

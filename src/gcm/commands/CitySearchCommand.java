package gcm.commands;

import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class CitySearchCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String searchQuery;

        public Input(String searchQuery) {
            this.searchQuery = searchQuery;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<City> cities;

        public Output(List<City> cities) {
            this.cities = cities;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<City> cities = City.searchByName(input.searchQuery);

        return new Output(cities);
    }
}

package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class FindCityCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String cityName;
        public String countryName;

        public Input(String city, String country) {
            this.cityName = city; this.countryName=country;
        }
    }

    public static class Output extends gcm.commands.Output {
        public City city;

        public Output(City city) {
            this.city = city;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        City city = City.findCity(input.cityName, input.countryName);

        return new Output(city);
    }
}

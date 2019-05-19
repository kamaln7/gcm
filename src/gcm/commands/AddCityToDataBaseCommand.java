package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class AddCityToDataBaseCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String name, country;

        public Input(String name, String country) {
            this.name = name;
            this.country = country;
        }
    }

    public static class Output extends gcm.commands.Output {
        public City city;

        public Output(City city) {
            this.city = city;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception{
        Input input = request.getInput(Input.class);

        City city = new City(input.name, input.country);
        city.insert();

        return new Output(city);
    }
}

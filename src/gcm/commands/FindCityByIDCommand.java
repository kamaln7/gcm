package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.Subscription;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.Date;

public class FindCityByIDCommand implements Command {
    public static class Input extends gcm.commands.Input {
        private Integer  id;
        private Date to_date,from_date;

        public Input(Integer id) {
            this.id = id;


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
        City city = City.findById(input.id);

        return new Output(city);
    }
}

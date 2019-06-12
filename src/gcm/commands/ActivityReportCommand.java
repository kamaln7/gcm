package gcm.commands;

import gcm.database.models.*;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityReportCommand implements Command {

    public static class Input extends gcm.commands.Input {
        public Date from, to;

        public Input(Date fromDate, Date toDate) {
            this.from = fromDate;
            this.to = toDate;
        }
    }

    public class Output extends gcm.commands.Output {

        public List<City> cities;

        public Output(List<City> cities) {
            this.cities = cities;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        List<City> cities = City.findAllWithCount(input.from,input.to);

        return new Output(cities);
    }
}

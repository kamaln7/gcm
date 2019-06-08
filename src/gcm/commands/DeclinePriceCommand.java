package gcm.commands;

import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class DeclinePriceCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public int id;

        public Input(int id){
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
        City city = City.declinePrice(input.id) ;

        return new Output(city);
    }
}

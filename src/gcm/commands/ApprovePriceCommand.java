package gcm.commands;

import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.sql.Array;
import java.sql.ResultSet;

public class ApprovePriceCommand implements Command {
    public static class Input extends gcm.commands.Input {



    }

    public static class Output extends gcm.commands.Output {
        public String[] result;


        public Output(String[] result) {
            this.result= result;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        String[] result = City.findUnapproved();

        return new Output(result);
    }
}

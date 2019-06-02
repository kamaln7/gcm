package gcm.commands;

import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ApprovePriceCommand implements Command {
    public static class Input extends gcm.commands.Input {
      /*  public ArrayList result;
        public Input(){this.result = new ArrayList();}*/

    }

    public static class Output extends gcm.commands.Output {
        public ArrayList result;


        public Output(ArrayList result) {
            this.result= result;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        ArrayList result = City.findUnapproved();

        return new Output(result);
    }
}

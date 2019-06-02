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
        public ArrayList<City> result;


        public Output(ArrayList<City> result) {
            this.result= result;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        ArrayList<City> result = City.findUnapproved();
        System.out.println("amin run on server");
        for (int i = 0; i <result.size() ; i++) {
            System.out.println(result.get(i).getName());
        }
        return new Output(result);
    }
}

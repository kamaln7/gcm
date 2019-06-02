package gcm.commands;

import gcm.database.models.City;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.sql.ResultSet;

public class ApprovePriceCommand implements Command {
    public static class Input extends gcm.commands.Input {


        public Input() {

        }
    }

    public static class Output extends gcm.commands.Output {
        public ResultSet rs;


        public Output(ResultSet rs) {
            this.rs= rs;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        ResultSet rs = City.findUnapproved();

        return new Output(rs);
    }
}

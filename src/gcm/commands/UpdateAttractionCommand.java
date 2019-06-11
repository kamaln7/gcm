package gcm.commands;

import com.sun.accessibility.internal.resources.accessibility;
import gcm.database.models.Attraction;
import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class UpdateAttractionCommand implements Command {
    public static class Input extends gcm.commands.Input {
        String attraction_type, attraction_location, description;
        boolean accessibility;
        int attraction_id;
        public Input(int attraction_id, String attraction_type, String attraction_location, boolean accessibility,String description) {
            this.attraction_type = attraction_type;
            this.attraction_location = attraction_location;
            this.description = description;
            this.accessibility = accessibility;
            this.attraction_id = attraction_id;
        }
    }

    public static class Output extends gcm.commands.Output {

    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        Attraction.updateAttraction(input.attraction_id,input.attraction_type, input.attraction_location, input.description, input.accessibility);
        return new Output();
    }
}

package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.View;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

/**
 * used to add view to the database
 */
public class AddViewCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public int user_id, model_id;
        private String model;


        public Input(int user_id, int model_id, String model) {
            this.user_id = user_id;
            this.model_id = model_id;
            this.model = model;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Output() {
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception{
        Input input = request.getInput(Input.class);

        View view = new View(input.user_id,input.model_id, input.model);
        view.insert();

        return new Output();
    }
}

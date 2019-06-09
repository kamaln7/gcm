package gcm.commands;

import gcm.database.models.Download;
import gcm.database.models.View;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class AddDownloadCommand implements Command {
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

        Download download = new Download(input.user_id,input.model_id, input.model);
        download.insert();

        return new Output();
    }
}

package gcm.commands;

import gcm.database.models.Map;
import gcm.database.models.User;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class AddMapCommand implements Command {
    public static class Input extends gcm.commands.Input {
        private Integer one_off_price, subscription_price;
        private String title, description, version, img;

        public Input(String title, String description, String version, int one_off_price, int subscription_price, String img) {
            this.title = title;
            this.description = description;
            this.version = version;
            this.one_off_price = one_off_price;
            this.subscription_price = subscription_price;
            this.img=img;

        }
    }

    public static class Output extends gcm.commands.Output {
        public Map map;

        public Output(Map map) {
            this.map = map;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        Map map = new Map(input.one_off_price,input.subscription_price,input.title,input.description,input.version, input.img);
        map.insert();


        return new Output(map);
    }
}

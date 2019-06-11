package gcm.commands;

import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * used to add map to the database
 */
public class AddMapCommand implements Command {
    public static class Input extends gcm.commands.Input {
        private String title, description, version;
        int cityId;
        private byte[] img;

        public Input(String title, String description, String version, byte[] img, Integer cityId) {
            this.title = title;
            this.description = description;
            this.version = version;
            this.img = img;
            this.cityId = cityId;
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

        String imageName = UUID.randomUUID().toString() + ".jpg";
        Files.write(Paths.get(server.getFilesPath(), imageName), input.img);

        Map map = new Map(input.title, input.description, input.version, imageName, input.cityId);
        map.insert();

        return new Output(map);
    }
}

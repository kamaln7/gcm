package gcm.commands;

import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.File;
import java.nio.file.Files;

public class ReadMapImageById implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer id;

        public Input(Integer id) {
            this.id = id;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Map map;
        public byte[] imgBytes;

        public Output(Map map, byte[] imgBytes) {
            this.map = map;
            this.imgBytes = imgBytes;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        Map map = Map.findById(input.id);
        File file = new File(server.getFilesPath(), map.getImg());

        byte[] img = Files.readAllBytes(file.toPath());

        return new Output(map, img);
    }
}

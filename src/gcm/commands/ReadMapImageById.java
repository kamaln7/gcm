package gcm.commands;

import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.File;
import java.nio.file.Files;

public class ReadMapImageById implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer id;
        public Boolean withUnapproved = false;

        public Input(Integer id) {
            this.id = id;
        }

        public Input(Integer id, Boolean withUnapproved) {
            this.id = id;
            this.withUnapproved = withUnapproved;
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

        String imagePath = map.getImgPathToRead(input.withUnapproved);
        File file = new File(server.getFilesPath(), imagePath);
        byte[] img = Files.readAllBytes(file.toPath());

        return new Output(map, img);
    }
}

package gcm.commands;

import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.File;
import java.nio.file.Files;

public class FindMapByTitleAndVersionCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String title, version;

        public Input(String title, String version) {
            this.title = title;
            this.version = version;
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
        Map map = Map.findByTitleAndVersion(input.title, input.version);
        File file = new File(map.getImg());

        byte[] img= Files.readAllBytes(file.toPath());


        return new Output(map,img);
    }
}

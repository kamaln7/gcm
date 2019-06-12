package gcm.commands;

import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.File;
import java.nio.file.Files;

public class ReadMapImageByPath implements Command {
    public static class Input extends gcm.commands.Input {
        public String path;

        public Input(String path) {
            this.path = path;
        }
    }

    public static class Output extends gcm.commands.Output {
        public byte[] imgBytes;

        public Output(byte[] imgBytes) {
            this.imgBytes = imgBytes;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        File file = new File(server.getFilesPath(), input.path);
        byte[] img = Files.readAllBytes(file.toPath());

        return new Output(img);
    }
}

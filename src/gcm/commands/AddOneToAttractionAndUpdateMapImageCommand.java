package gcm.commands;

import gcm.database.models.Attraction;
import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class AddOneToAttractionAndUpdateMapImageCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer mapId;
        Attraction attraction;
        byte[] new_map_image;

        public Input(Integer mapId, Attraction attraction, byte[] new_map_image) {
            this.mapId = mapId;
            this.attraction = attraction;
            this.new_map_image = new_map_image;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Output() {
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        //store the img in the server
        String imageName = UUID.randomUUID().toString();
        Files.write(Paths.get(server.getFilesPath(), imageName), input.new_map_image);

        Map mapToUpdate = Map.findById(input.mapId);
        //delete the old file from the server
        File file = new File(server.getFilesPath(), mapToUpdate.getImg());
        file.delete();

        mapToUpdate.updateImage(imageName);

        //here we add attraction Id and map Id to many-many Table

        return new Output();
    }
}

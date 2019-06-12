package gcm.commands;

import gcm.database.models.Map;
import gcm.database.models.MapAttraction;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * used to add attraction to the map
 */
public class AddExistingAttractionAndUpdateMapImageCommand implements Command {
    private Boolean createdNewFile = false;

    public static class Input extends gcm.commands.Input {
        public Integer mapId, attractionId;
        byte[] new_map_image;

        public Input(Integer mapId, Integer attractionId, byte[] new_map_image) {
            this.mapId = mapId;
            this.attractionId = attractionId;
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

        // check if already exists
        try {
            MapAttraction.findByIds(input.mapId, input.attractionId);
            throw new MapAttraction.AlreadyExists();
        } catch (MapAttraction.NotFound e) {
        }

        Map map = Map.findById(input.mapId);

        //store the img in the server
        String imageName = getImagePathToWrite(map);
        Files.write(Paths.get(server.getFilesPath(), imageName), input.new_map_image);
        if (createdNewFile) {
            map.updateImage(imageName);
        }

        //here we add attraction Id and map Id to many-many Table
        MapAttraction mapAttraction = new MapAttraction(map.getId(), input.attractionId);
        mapAttraction.insert();

        return new Output();
    }

    private String getImagePathToWrite(Map map) {
        String mapImgNew = map.getImgNew();
        if (mapImgNew != null) return mapImgNew;

        createdNewFile = true;
        return UUID.randomUUID().toString() + ".jpg";
    }
}

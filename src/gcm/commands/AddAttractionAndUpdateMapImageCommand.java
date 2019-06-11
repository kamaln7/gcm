package gcm.commands;

import gcm.database.models.Attraction;
import gcm.database.models.Map;
import gcm.database.models.MapAttraction;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * used to create new attraction and add it to the map
 */
public class AddAttractionAndUpdateMapImageCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer mapId;
        public boolean accessibility;
        public String attraction_type, attraction_name, getAttraction_location, description;
        byte[] new_map_image;

        public Input(Integer mapId, String attraction_type, String attraction_name, String getAttraction_location, byte[] new_map_image, boolean accessibility, String description) {
            this.mapId = mapId;
            this.attraction_type = attraction_type;
            this.attraction_name = attraction_name;
            this.getAttraction_location = getAttraction_location;
            this.new_map_image = new_map_image;
            this.accessibility = accessibility;
            this.description = description;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Attraction attraction;

        public Output(Attraction attraction) {
            this.attraction = attraction;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        //store the img in the server
        String imageName = UUID.randomUUID().toString() + ".jpg";
        Files.write(Paths.get(server.getFilesPath(), imageName), input.new_map_image);

        Map mapToUpdate = Map.findById(input.mapId);
        //delete the old file from the server
        File file = new File(server.getFilesPath(), mapToUpdate.getImg());
        file.delete();

        mapToUpdate.updateImage(imageName);

        Attraction attraction = new Attraction(mapToUpdate.getCityId(), input.attraction_name, input.description, input.attraction_type, input.getAttraction_location, input.accessibility);
        attraction.insert();

        MapAttraction mapAttraction = new MapAttraction(mapToUpdate.getId(), attraction.getId());
        mapAttraction.insert();

        return new Output(attraction);
    }
}

package gcm.commands;

import gcm.database.models.Attraction;
import gcm.database.models.Map;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class AddAttractionAndUpdateMapImageCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public String map_title, map_version, attraction_type, attraction_name, getAttraction_location;
        byte[] new_map_image;

        public Input(String map_title, String map_version, String attraction_type, String attraction_name, String getAttraction_location, byte[] new_map_image) {
            this.map_title = map_title;
            this.map_version = map_version;
            this.attraction_type = attraction_type;
            this.attraction_name = attraction_name;
            this.getAttraction_location = getAttraction_location;
            this.new_map_image = new_map_image;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Map map;

        public Output(Map map) {
            this.map = map;
        }

        public Output() {
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);
        //store the img in the server
        String imageName = UUID.randomUUID().toString();
        Files.write(Paths.get(server.getFilesPath(), imageName), input.new_map_image);

        Map mapToUpdate = Map.findByTitleAndVersion(input.map_title, input.map_version);
        //delete the old file from the server
        File file = new File(server.getFilesPath(), mapToUpdate.getImg());
        file.delete();


        mapToUpdate.updateImage(imageName, input.map_version, input.map_title);

        Attraction attraction = new Attraction(input.attraction_name, mapToUpdate.getCityId(), input.attraction_type, input.getAttraction_location);
        attraction.insert();

        return new Output();
    }
}

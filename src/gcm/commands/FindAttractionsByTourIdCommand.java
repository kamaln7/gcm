package gcm.commands;

import gcm.database.models.Attraction;
import gcm.database.models.Tour;
import gcm.database.models.TourAttraction;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

import java.util.List;

public class FindAttractionsByTourIdCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public Integer tourID;

        public Input(Integer tourID) {
            this.tourID = tourID;
        }
    }

    public static class Output extends gcm.commands.Output {
        public List<Attraction> attractions;
        public List<TourAttraction> tourAttractionList;

        public Output(List<Attraction> attractions, List<TourAttraction> tourAttractionList) {
            this.attractions = attractions;
            this.tourAttractionList = tourAttractionList;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception {
        Input input = request.getInput(Input.class);

        List<Attraction> attractions = TourAttraction.findAttractionsByTourId(input.tourID);
        List<TourAttraction> tourAttractionList = TourAttraction.findTourAttractionsByTourId(input.tourID);
        return new Output(attractions, tourAttractionList);
    }
}

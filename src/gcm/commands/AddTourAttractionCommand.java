package gcm.commands;

import gcm.database.models.Tour;
import gcm.database.models.TourAttraction;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

public class AddTourAttractionCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public int tour_id, attraction_id, index;
        public String time;


        public Input(int tour_id, int attraction_id, int index, String time) {
            this.tour_id = tour_id;
            this.attraction_id = attraction_id;
            this.index = index;
            this.time = time;
        }
    }

    public static class Output extends gcm.commands.Output {
        TourAttraction tourAttraction;

        public Output(TourAttraction tourAttraction) {
            this.tourAttraction = tourAttraction;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception{
        Input input = request.getInput(Input.class);

        TourAttraction tourAttraction =new TourAttraction(input.tour_id, input.attraction_id, input.index, input.time);

        tourAttraction.insert();

        return new Output(tourAttraction);
    }
}

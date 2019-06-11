package gcm.commands;

import gcm.database.models.City;
import gcm.database.models.Tour;
import gcm.server.Server;
import ocsf.server.ConnectionToClient;

/**
 * used to add tour to the database
 */
public class AddTourCommand implements Command {
    public static class Input extends gcm.commands.Input {
        public int city_id;
        public String description;

        public Input(int city_id, String description) {
            this.city_id = city_id;
            this.description = description;
        }
    }

    public static class Output extends gcm.commands.Output {
        public Tour tour;

        public Output(Tour tour) {
            this.tour = tour;
        }
    }

    @Override
    public Output runOnServer(Request request, Server server, ConnectionToClient client) throws Exception{
        Input input = request.getInput(Input.class);

        Tour tour = new Tour(input.city_id, input.description);

        tour.insert();

        return new Output(tour);
    }
}
